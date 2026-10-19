# Especificación de correcciones — NexusMarket (Spring Boot)

> Documento para entregar a Cursor. Describe **qué corregir, dónde, cómo comprobarlo y qué NO tocar**.
> Cada error se reprodujo ejecutando los servicios reales del proyecto (capas `domain` y `application`) con repositorios falsos que copian los objetos como lo hace Mongo.

---

## 0. Contexto y reglas del juego

**Proyecto:** `construccion-software-2` · Spring Boot 4.1.1 · Java 21 · MongoDB (`localhost:27017`) · arquitectura hexagonal.

**Rutas** (todas bajo `src/main/java/com/nexusmarket/`):

| Capa | Ruta |
|---|---|
| Entidades, comandos, puertos, `AccessPolicy`, excepciones | `domain/…` |
| Los 33 servicios + `RegisterUserService` + `InventoryAuthorization` | `application/service/` |
| Controladores, adaptadores de Mongo, configuración | `infrastructure/…` |
| Documentación de servicios | `SERVICIOS.md` (raíz del proyecto) |

### Reglas que NO se pueden romper
1. **`domain` no puede importar Spring, Lombok ni Mongo.** Hoy hay 0 imports; debe seguir en 0.
2. **`application` solo depende de `domain`** (y de `@Service`/`@Component`). No importa nada de `infrastructure`.
3. **No cambiar reglas de negocio** que no estén en esta especificación. Las que dependen de una decisión del usuario están en la sección 4 y **no se implementan** sin confirmación.
4. **No renombrar** clases, paquetes, endpoints ni códigos de error (`REGLA_NEGOCIO`, `NO_ENCONTRADO`, `ACCESO_DENEGADO`, `NO_AUTENTICADO`).
5. Los errores de negocio se lanzan con `BusinessRuleException` y mensajes **en español**, como el resto del proyecto.
6. Trabajar en una **rama nueva**, con un commit por error corregido.
7. **No declarar algo como hecho sin mostrar la salida real** de la compilación y de las pruebas.

### Orden de trabajo
`E-01` → `E-02` → `E-03` → `E-04`. Los errores `E-05` y `E-06` son opcionales y se hacen al final.

---

## 1. Errores críticos (corregir obligatoriamente)

### E-01 · Usuarios huérfanos al registrar comprador y vendedor

| | |
|---|---|
| **Severidad** | **Alta** (corrompe datos) |
| **Dónde** | `application/service/RegisterBuyerService.java`, `RegisterSellerService.java`, `RegisterUserService.java` |

**Problema.** Ambos servicios llaman primero a `RegisterUserService.register(...)`, que **guarda el `User`** en la base de datos. Solo después construyen el perfil (`new Buyer(...)`, `new Warehouse(...)`), y esos constructores validan los datos. Si la validación falla, el `User` ya quedó guardado:

- **Comprador** sin dirección principal → responde 422, pero el correo y el documento **quedan ocupados**. Al reintentar con datos correctos devuelve “El correo electrónico debe ser único”.
- **Vendedor** con bodega inválida (sin dirección) → quedan guardados el `User` **y** el `Seller`, **sin bodega**. Eso contradice la regla de `SERVICIOS.md`: “un vendedor sin bodega no puede recibir mercancía”.

**Reproducción (Given / When / Then).**
- *Given* un repositorio vacío. *When* `RegisterBuyerService.register(new RegisterBuyer("Ana", "ana@x.com", "DOC-1", null, List.of()))`. *Then* lanza `BusinessRuleException` **y** `users.existsByEmail("ana@x.com")` es `true` (debería ser `false`).
- *Given* un administrador activo. *When* `RegisterSellerService.register(admin, new RegisterSeller("Dueño", "v@x.com", "DOC-2", "Tienda", "Bodega", null))`. *Then* lanza `BusinessRuleException`, pero existe un `User` y un `Seller` (deberían no existir).

**Comportamiento esperado.** Si algo falla, **no se guarda nada**.

**Cambio sugerido (mínimo, sin tocar puertos).**
1. En `RegisterUserService`, separar en dos pasos:
   - `User prepare(fullName, email, identityDocument, role)`: normaliza el correo, valida unicidad de correo y documento y **construye** el `User` (con su `UUID`) **sin guardar**.
   - `User register(...)`: llama a `prepare(...)` y guarda. (Se conserva para quien ya lo usa.)
   - Un método para guardar un `User` ya preparado (por ejemplo `save(User)`), o inyectar `UserRepository` en los servicios que lo necesiten.
2. `RegisterBuyerService`: `prepare` → construir `Buyer` (valida dirección y estado comercial) → **recién entonces** guardar `User` y después `Buyer`.
3. `RegisterSellerService`: `prepare` → construir `Seller` y `Warehouse` (validan datos) → **recién entonces** guardar `User`, `Seller` y `Warehouse`, en ese orden.
4. `RegisterStaffService` sigue funcionando igual (solo crea `User`).

**Criterios de aceptación (pruebas nuevas).**
- Comprador con `primaryAddress = null` → `BusinessRuleException`, `existsByEmail` y `existsByIdentityDocument` quedan en `false`, y **un segundo intento con datos válidos y el mismo correo se registra bien**.
- Comprador con dirección con `line` o `city` vacíos → igual que arriba.
- Vendedor con `warehouseAddress = null` → no se guarda `User`, ni `Seller`, ni `Warehouse`.
- El flujo feliz de comprador y de vendedor sigue guardando perfil y bodega (el vendedor devuelve `SellerOnboarding` con su bodega de tipo `SELLER`).

**No hacer.** No agregar `@Transactional` como solución (la persistencia es MongoDB sin transacciones configuradas). No cambiar los puertos `outbound`.

---

### E-02 · Validación de correo demasiado débil

| | |
|---|---|
| **Severidad** | **Alta** (regla de identidad) |
| **Dónde** | `domain/model/EmailAddress.java` (lo usa `User` y `RegisterUserService`) |

**Problema.** `EmailAddress.normalize` solo comprueba que el texto **contenga `@`**. Valores como `"@"`, `"a@"`, `"@b.com"` o `"a@b"` se aceptan como correo válido. Comprobado: `"@"` se registra sin error.

**Comportamiento esperado.** El correo debe tener la forma `texto@dominio.tld`: sin espacios, sin partes vacías y con al menos un punto en el dominio.

**Cambio sugerido.** En `normalize`, después de recortar y comprobar que no esté vacío, validar con este patrón (en una constante `Pattern`, en el propio `EmailAddress`; es Java estándar, sin dependencias):

```
^[^@\s]+@[^@\s.]+(\.[^@\s.]+)+$
```

Mantener: `trim()`, `toLowerCase(Locale.ROOT)` y el mensaje actual “El correo electrónico es obligatorio y debe ser válido”.

**Criterios de aceptación (pruebas nuevas, en `BusinessRulesTest` o una clase nueva de dominio).**
- **Se rechazan** (con `BusinessRuleException`): `"@"`, `"a@"`, `"@b.com"`, `"a@b"`, `"a b@c.com"`, `"a@b..com"`, `""`, `" "` y `null`.
- **Se aceptan**: `"ana@x.com"`, `"A.B+c@mail.co.uk"` (queda en minúsculas) y **`"admin@nexusmarket.local"`**.
- ⚠️ **Importante:** el administrador inicial se crea con `admin@nexusmarket.local` (`application.properties`). Debe seguir pasando la validación; si no, `InitialAdminLoader` fallará al arrancar.

**No hacer.** No cambiar el contrato de `normalize` (sigue recibiendo y devolviendo `String`).

---

### E-03 · La documentación `SERVICIOS.md` no coincide con el código

| | |
|---|---|
| **Severidad** | **Media-alta** (es lo primero que se nota en una revisión) |
| **Dónde** | `SERVICIOS.md` y `application/service/QueryWarehousesService.java` |

**Problema.** Hay afirmaciones del documento que el código no cumple. Se comprobó ejecutando cada caso.

| # | `SERVICIOS.md` dice | El código hace | Qué corregir |
|---|---|---|---|
| 1 | `ChangeProductStatusService`: “vendedor dueño, administrador” | `requireRole(SELLER)`: el administrador recibe **403** | **Corregir el documento**: solo el vendedor dueño |
| 2 | `RegisterWarehouseService`: “administrador, operador logístico” | `requireRole(ADMINISTRATOR)`: el operador recibe **403** | **Corregir el documento**: solo el administrador |
| 3 | `QueryWarehousesService`: “el vendedor ve las suyas y las del marketplace” | `list` usa `findBySellerId`: solo ve **las suyas**; `get` de una bodega del marketplace da 403 | **Corregir el código** (ver abajo) |
| 4 | `QueryOrdersService`: “comprador (los propios), administrador, supervisor” | También permite **vendedor** (pedidos con sus productos) y **operador logístico** (todos) | Corregir el documento |
| 5 | `QueryInvoicesService`: “comprador (las propias), administrador, supervisor” | También permite **vendedor** (facturas de pedidos con sus productos) | Corregir el documento |
| 6 | `QueryShipmentService`: “operador, comprador dueño, administrador” | También permite **supervisor** | Corregir el documento |
| 7 | `QueryReturnsService`: “comprador (las propias), administrador” | También permite **supervisor** | Corregir el documento |
| 8 | `QueryUsersService`: “administrador, supervisor” | `get` lo puede usar **cualquier rol sobre sí mismo**; `list` solo administrador y supervisor | Corregir el documento |
| 9 | `ConfirmOrderService`: “si alguna línea es física y el comprador no tiene dirección principal, la operación falla” | Esa comprobación **nunca se ejecuta**: `Buyer` ya exige dirección principal al construirse | Quitar la frase del documento **y** el `if` muerto en `ConfirmOrderService` |

**Por qué el criterio “corregir el documento” en los casos 1, 2 y 4–8.** Esos comportamientos del código son los coherentes con las reglas ya aplicadas en el proyecto (RG-03, un vendedor solo gestiona lo suyo, solo el administrador crea bodegas). El caso 3 es distinto: hoy un vendedor **puede recibir stock en una bodega del marketplace** (`InventoryAuthorization` lo permite) pero **no puede verla**; eso sí es una incoherencia del código.

**Cambio de código para el caso 3** (`QueryWarehousesService`):
- `list(actor)`: si el actor es `SELLER`, devolver **sus bodegas + las de tipo `MARKETPLACE`**. Para el resto de roles, sin cambios.
- `get(actor, id)`: si el actor es `SELLER`, permitir la bodega si es suya **o** si es de tipo `MARKETPLACE`; negar (403) las bodegas de otros vendedores.

**Criterios de aceptación.**
- Un vendedor lista y consulta una bodega del marketplace (antes: lista vacía / 403).
- Un vendedor **sigue sin poder** ver la bodega de otro vendedor (403).
- El resto de roles se comporta igual que antes.
- `SERVICIOS.md` queda con los roles exactos que permite el código en cada servicio (revisar la tabla “El resto de los servicios” fila por fila).

---

## 2. Errores a corregir después (importancia media)

### E-04 · Un vendedor puede recibir stock en bodegas que no puede consultar (cubierto por E-03, caso 3)
No requiere trabajo adicional si E-03 se resuelve; solo verificar que la prueba del caso 3 pasa.

### E-05 · Persistencia sin orden seguro en `ConfirmOrderService` y `RegisterPaymentService` *(opcional)*

| | |
|---|---|
| **Severidad** | Baja-media |
| **Dónde** | `ConfirmOrderService`, `RegisterPaymentService` |

**Problema.** Sin transacciones, un fallo a mitad de camino deja datos a medias:
- `ConfirmOrderService` guarda el **pedido** y **después** los inventarios reservados.
- `RegisterPaymentService` guarda inventarios y factura **antes** de guardar el pedido en `PAID`.

**Cambio sugerido (sin transacciones).** Reordenar para que el último guardado sea el que “confirma” la operación: guardar primero inventarios y movimientos, y **al final** el pedido. Todas las validaciones y los cambios en memoria deben terminar **antes** del primer `save`.

**Criterio de aceptación.** Las pruebas existentes del flujo completo siguen pasando; el orden de las llamadas a `save` queda con el pedido al final (verificable con un fake que registre el orden).

**No hacer.** No introducir transacciones de Mongo ni `@Version` en este paso: es otra decisión (ver 4.5).

### E-06 · Mensaje de pruebas: las dos pruebas de Spring dependen de MongoDB *(informativo)*
`ApplicationTests` y `UserControllerTest` usan `@SpringBootTest`. Antes de ejecutar `./mvnw test`, tener MongoDB en `localhost:27017` (por ejemplo `docker run -p 27017:27017 mongo`). Si fallan por conexión, **reportarlo sin modificarlas**.

---

## 3. Pruebas: cómo escribirlas

- Poner las pruebas nuevas en `src/test/java/com/nexusmarket/` (por ejemplo `application/service/…` y `domain/model/…`), con JUnit 5.
- Para probar servicios, **escribir repositorios falsos en memoria** que implementen los puertos `outbound`.
- ⚠️ **Los falsos deben copiar el objeto al guardar y al leer** (crear una instancia nueva con el constructor completo), porque Mongo devuelve un objeto nuevo en cada lectura. Un falso que devuelve la misma instancia **oculta** justo los errores de esta especificación.
- No usar Mongo ni el contexto de Spring en estas pruebas: deben correr en milisegundos.
- Cada error corregido lleva **al menos una prueba que falla antes del arreglo y pasa después**.

---

## 4. Decisiones pendientes — NO implementar sin confirmación del usuario

Son reglas de negocio que cambian el comportamiento. Cursor debe **listarlas al usuario y esperar respuesta**, no decidir.

| Id | Tema | Comportamiento actual | Opciones | Recomendación |
|---|---|---|---|---|
| 4.1 | **Devoluciones** | Solo con el pedido `COMPLETED`. La versión anterior las permitía con el pedido enviado **o** entregado | A) dejarlo así · B) permitir también `SHIPPED` | Confirmar con la especificación original |
| 4.2 | **Producto DISCONTINUED** | `ChangeProductStatus` permite volver a `PUBLISHED` | A) dejarlo · B) prohibir salir de `DISCONTINUED` (`Product.changeStatus` lanza `BusinessRuleException`) | B |
| 4.3 | **Productos de un vendedor bloqueado** | Siguen vendiéndose; el vendedor no tiene estado propio | A) dejarlo · B) al agregar al carrito y al confirmar, rechazar productos cuyo vendedor tenga el `User` bloqueado · C) suspender sus productos al bloquear | B |
| 4.4 | **`X-User-Id` no autentica** | Spring Security está en `permitAll`: quien conozca un id actúa como ese usuario | A) dejarlo y documentarlo como límite académico · B) tokens (JWT) | A para la entrega; B solo si el profesor lo exige |
| 4.5 | **Concurrencia** | Dos compradores simultáneos podrían reservar la última unidad (leer y luego escribir) | A) dejarlo y documentarlo · B) `@Version` en `InventoryDocument` | A para la entrega |

---

## 5. Definición de “terminado”

1. `./mvnw -q compile` sin errores.
2. Las pruebas nuevas (E-01, E-02, E-03) **pasan**, y se muestra la salida real.
3. Las pruebas que ya existían (`BusinessRulesTest` y las demás) siguen pasando.
4. Comprobación de la regla de dependencias, mostrando el resultado:
   - `domain` sin imports de `org.springframework`, `lombok` ni `mongodb`.
   - `application` sin imports de `infrastructure`.
5. `SERVICIOS.md` coincide con el código (E-03).
6. Entregar al usuario: lista de archivos modificados (`git diff --stat`) y la lista de decisiones pendientes de la sección 4.
7. Si algo no se pudo hacer o verificar, **decirlo explícitamente**.
