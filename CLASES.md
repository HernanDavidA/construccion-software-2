# Por qué existe cada clase de NexusMarket

NexusMarket intermedia entre compradores y vendedores. La especificación pide usuarios, vendedores, compradores, bodegas, catálogo, inventario distribuido, carrito, pedidos, facturación, logística, devoluciones, reembolsos y reportes. El código separa esas reglas de Spring, HTTP y MongoDB.

Los identificadores (paquetes, clases, archivos, enums y rutas) están en inglés. Los mensajes que ve quien usa la API siguen en español: excepciones de negocio y el cuerpo de error `codigo` / `mensaje`.

```
HTTP  -->  controller  -->  inbound port  -->  application service
                                      |
                                      v
                               domain model
                                      |
                                      v
                         outbound port  -->  MongoDB adapter
```

El encabezado `X-User-Id` identifica al actor (RG-01). La especificación deja fuera el mecanismo técnico de autenticación, así que el adaptador web resuelve el usuario y el dominio solo exige que exista, esté activo y tenga un único rol.

## Cómo levantarlo

Hace falta Java 21 y MongoDB en `mongodb://localhost:27017/nexusmarket`.

```powershell
$env:JAVA_HOME = "C:\Program Files\Android\Android Studio\jbr"
.\mvnw.cmd spring-boot:run
```

Al arrancar, si la base está vacía, se crea el administrador `user-admin` (`admin@nexusmarket.local`). Las demás operaciones envían `X-User-Id: user-admin`.

Son públicas `POST /api/buyers`, `GET /api/catalog` y `GET /api/products/{id}`. El resto exige el encabezado.

En Spring Boot 4, `spring-boot-starter-mongodb` solo trae el driver. `spring-boot-starter-data-mongodb` aporta los repositorios del adaptador de persistencia.

La documentación de los 33 servicios está en [SERVICIOS.md](SERVICIOS.md).

## Reglas que viven en el dominio

| Regla de la especificación | Dónde queda |
| --- | --- |
| RG-01 operación con usuario autenticado | `AccessPolicy` y `CurrentUser` |
| RG-02 un solo rol por usuario | `User` guarda un único `Role` y no tiene método para cambiarlo |
| RG-03 nadie administra fuera de su rol | `AccessPolicy.requireRole` en cada caso de uso |
| Correo y documento únicos | `RegisterUserService` |
| El vendedor no se auto-registra | `RegisterSellerService` solo acepta administrador |
| El comprador no ve otros compradores ni inventario | servicios de compradores, inventario y pedidos |
| Bodega del marketplace o del vendedor | `Warehouse` |
| Producto físico o digital | `ProductType`; el digital se cierra al pagar |
| Inventario atado a producto y bodega | `Inventory` |
| Sin existencias negativas | `Inventory.receive`, `reserve`, `adjust`, `returnStock` |
| No reservar inventario dañado o inexistente | `Inventory.reserve` |
| Pedido finalizado no se modifica | `Order.advanceTo` |
| Factura al validar el pago | `Invoice.issue` dentro de `RegisterPaymentService` |
| Devolución y reembolso | `RequestReturnService`, `ReviewReturnService`, `ProcessRefundService` |

## Excepciones

| Clase | Por qué existe | Qué hace |
| --- | --- | --- |
| `BusinessRuleException` | Las reglas (stock, estados, unicidad) no son errores técnicos | Interrumpe la operación. El adaptador web responde 422 con `codigo` `REGLA_NEGOCIO` |
| `ResourceNotFoundException` | Distingue "no existe" de "no está permitido" | Un id no está en el puerto de salida. Responde 404 `NO_ENCONTRADO` |
| `AccessDeniedException` | Materializa RG-03 y el usuario bloqueado | El rol no puede ejecutar esa acción. Responde 403 `ACCESO_DENEGADO` |
| `UnauthenticatedException` | Materializa RG-01 | No hay actor o el id no corresponde a un usuario. Responde 401 `NO_AUTENTICADO` |

## Catálogos

Cada enum existe para que un estado o tipo no sea un texto libre.

| Clase | Valores | Qué representa |
| --- | --- | --- |
| `Role` | `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR` | Un usuario tiene exactamente uno |
| `UserStatus` | `ACTIVE`, `BLOCKED` | Si la identidad puede operar |
| `BuyerCommercialStatus` | `ENABLED`, `SUSPENDED` | Si el comprador puede comprar |
| `WarehouseType` | `MARKETPLACE`, `SELLER` | Bodega de la plataforma o de un vendedor |
| `ProductType` | `PHYSICAL`, `DIGITAL` | El físico exige inventario y envío; el digital se entrega al pagar |
| `ProductStatus` | `PUBLISHED`, `SUSPENDED`, `DISCONTINUED` | El alta nace suspendido hasta que el vendedor publica |
| `InventoryMovementType` | `INBOUND`, `RESERVATION`, `SALE_OUTBOUND`, `ADJUSTMENT`, `RETURN` | Cada cambio de stock deja rastro |
| `InventoryCondition` | `AVAILABLE`, `DAMAGED` | Lo dañado no se reserva |
| `OrderStatus` | `PENDING_PAYMENT`, `PAID`, `SHIPPED`, `COMPLETED` | `COMPLETED` es inmutable |
| `InvoiceStatus` | `ISSUED`, `VOIDED` | Se anula cuando el reembolso se procesa |
| `ShipmentStatus` | `PREPARING`, `PACKED`, `SHIPPED`, `IN_TRANSIT`, `DELIVERED` | Cadena logística |
| `ReturnStatus` | `REQUESTED`, `APPROVED`, `REJECTED` | Una devolución activa por pedido |
| `RefundStatus` | `PENDING`, `PROCESSED` | El reembolso nace al aprobar la devolución |

## Modelo

| Clase | Por qué existe | Qué hace |
| --- | --- | --- |
| `EmailAddress` | El correo no puede guardarse con mayúsculas o espacios distintos | Normaliza y valida el formato |
| `RequiredText` | Varios agregados rechazan texto vacío con el mismo criterio | Exige un valor y devuelve el mensaje en español |
| `VariantIds` | Dos variantes son la misma solo si su id coincide | Normaliza y compara identificadores de variante |
| `Address` | Comprador, bodega y envío comparten domicilio | Guarda calle, ciudad y referencia |
| `ProductVariant` | Un producto se vende por talla, color u otra variante | Identidad de la variante dentro del producto |
| `User` | RG-02 pide un solo rol. Operador, administrador y supervisor son este tipo | Normaliza el correo, guarda un único rol y solo permite cambiar el estado. Rechaza los roles de comprador y vendedor |
| `Buyer` | El comprador es un usuario con datos de entrega | Extiende `User` con rol `BUYER`, dirección principal y estado comercial |
| `Seller` | El vendedor es un usuario con razón social, no un perfil aparte | Extiende `User` con rol `SELLER` y nombre comercial |
| `Warehouse` | El inventario vive en bodegas del marketplace o del vendedor | `SELLER` exige `sellerId`; `MARKETPLACE` lo prohíbe |
| `Product` | El catálogo distingue físico y digital, y nace suspendido | Valida que la variante exista y que el producto pertenezca al vendedor |
| `Inventory` | El stock no puede ser negativo ni reservarse si está dañado | `receive`, `reserve`, `saleOutbound`, `adjust`, `returnStock`, `markCondition` |
| `InventoryMovement` | Cada cambio de existencias debe poder auditarse | Registro inmutable del tipo, la cantidad y el pedido relacionado |
| `CartItem` / `Cart` | El comprador arma la compra antes de comprometer stock | Agrega, quita y vacía líneas sin crear un pedido |
| `OrderLine` | La línea congela precio, variante, bodega y tipo al confirmar | El pedido no depende de cambios posteriores del catálogo |
| `Order` | El pedido tiene transiciones cerradas y no se edita al finalizar | `PENDING_PAYMENT` → `PAID` → `SHIPPED` → `COMPLETED`; el digital salta de `PAID` a `COMPLETED` |
| `Invoice` | La factura nace al validar el pago y se anula con el reembolso | `issue` asigna número `FAC-`; `voidInvoice` pasa a `VOIDED` |
| `Shipment` | El producto físico necesita una cadena logística propia | `prepare` → `pack` → `dispatch` → `markInTransit` → `deliver` |
| `ReturnRequest` | Solo un pedido finalizado puede devolverse, y una sola vez en curso | `approve` y `reject` |
| `Refund` | El dinero se devuelve después de aprobar, no al solicitar | `process` marca el reembolso y habilita anular la factura |
| `SellerOnboarding` | Dar de alta un vendedor crea al vendedor y su primera bodega juntos | Agrupa el resultado de `RegisterSellerService` |
| `AdminReport` | El administrador y el supervisor consultan operación, no mutan pedidos | Pedidos por estado, stock por bodega y ventas facturadas |

## Política y comandos

`AccessPolicy` es la única política de dominio compartida. Recibe el actor y los roles permitidos. Si no hay actor lanza `UnauthenticatedException`. Si está bloqueado o el rol no está en la lista, lanza `AccessDeniedException`.

Los comandos (`RegisterBuyer`, `UpdateAddresses`, `RegisterStaff`, `RegisterSeller`, `RegisterWarehouse`, `RegisterProduct`, `ReceiveStock`, `AdjustStock`, `AddCartItem`, `RequestReturn`) existen para que el controlador no arme entidades a medias. Son datos de entrada ya validados en su forma, y el servicio decide si la operación es legal.

## Puertos

Un puerto de entrada (`domain.port.inbound`) es el contrato de un caso de uso. Hay uno por servicio de aplicación, por ejemplo `ConfirmOrderUseCase` o `RegisterPaymentUseCase`. El controlador depende de esa interfaz, no de la clase concreta.

Un puerto de salida (`domain.port.outbound`) es lo que el dominio necesita persistir: `UserRepository`, `BuyerRepository`, `SellerRepository`, `WarehouseRepository`, `ProductRepository`, `InventoryRepository`, `CartRepository`, `OrderRepository`, `InvoiceRepository`, `ShipmentRepository`, `ReturnRepository`, `RefundRepository`. Ninguno menciona MongoDB.

## Servicios de aplicación

Hay 33 clases `@Service` en `application.service`. Cada una implementa un puerto de entrada, salvo `RegisterUserService`, que concentra la unicidad de correo y documento para el alta de personal, compradores y vendedores. `InventoryAuthorization` es un `@Component` de apoyo: limita al vendedor a sus productos y a sus bodegas o a las del marketplace; el operador logístico opera cualquiera.

El detalle de comportamiento, roles y rutas está en [SERVICIOS.md](SERVICIOS.md).

## Adaptador web

| Clase | Por qué existe | Qué hace |
| --- | --- | --- |
| `CurrentUser` | RG-01 se resuelve en el borde HTTP | Lee `X-User-Id` y carga el `User` |
| `ApiError` | La salida de error queda en español aunque el código esté en inglés | Cuerpo `codigo` y `mensaje` |
| `WebExceptionHandler` | Traduce excepciones de dominio a HTTP | 422, 404, 403 y 401 |
| `UserController` | Alta y consulta de personal | `/api/users` |
| `BuyerController` | Registro público y perfil del comprador | `/api/buyers` |
| `SellerController` | Alta de vendedor solo por administrador | `/api/sellers` |
| `WarehouseController` | Bodegas | `/api/warehouses` |
| `CatalogController` | Alta, publicación y catálogo público | `/api/products` y `/api/catalog` |
| `InventoryController` | Ingresos, ajustes, condición y movimientos | `/api/inventory` |
| `CartController` | Carrito del comprador | `/api/cart` |
| `OrderController` | Confirmar, pagar, entregar y consultar | `/api/orders` |
| `InvoiceController` | Consulta de facturas | `/api/invoices` |
| `ShipmentController` | Empaque, despacho y tránsito | `/api/shipments` |
| `ReturnController` | Solicitud, revisión y reembolso | `/api/returns` |
| `ReportController` | Reportes de administrador y supervisor | `/api/reports` |

## Persistencia

Cada agregado del dominio tiene un documento Mongo, salvo el comprador y el vendedor: son subclases de `User` y se guardan en la colección `users`, con la dirección o el nombre comercial según el rol. El resto usa su documento (`WarehouseDocument`, `ProductDocument`, `InventoryDocument`, `InventoryMovementDocument`, `CartDocument`, `OrderDocument`, `InvoiceDocument`, `ShipmentDocument`, `ReturnRequestDocument`, `RefundDocument`), un `MongoRepository` de Spring Data y un adaptador que implementa el puerto de salida. `BuyerRepository` y `SellerRepository` leen esa misma colección y filtran por rol. Los embebidos `AddressDocument`, `VariantDocument`, `CartItemDocument` y `OrderLineDocument` evitan colecciones para datos que no se consultan solos. Los índices únicos de correo y documento de identidad viven en el adaptador de usuarios, no en el dominio.

## Configuración

| Clase | Por qué existe | Qué hace |
| --- | --- | --- |
| `Application` | Punto de arranque de Spring Boot | Excluye el usuario técnico de Spring Security para no generar una contraseña que la especificación no pide |
| `DomainConfiguration` | `AccessPolicy` no debe conocer Spring | Publica la política como bean |
| `SecurityConfig` | La autorización real es `AccessPolicy`, no el filtro de Spring | Deja pasar las peticiones y apaga CSRF, HTTP Basic y formulario |
| `InitialAdminLoader` | Siempre debe existir un administrador para crear vendedores y personal | Si `nexusmarket.admin.auto-create` es verdadero y el id no existe, inserta `user-admin` |
