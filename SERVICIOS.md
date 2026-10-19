# Servicios de NexusMarket

Hay 33 servicios de aplicación en `com.nexusmarket.application.service`. Cada uno implementa un puerto de entrada, salvo `RegisterUserService`, que crea el personal y comprueba que el correo y el documento estén libres. `InventoryAuthorization` no es un caso de uso: solo decide si un actor puede tocar un producto en una bodega.

El actor llega en el encabezado `X-User-Id`. Los mensajes de error salen en español.

## Los que sostienen el negocio

Estos ocho servicios cubren el ciclo de una compra: alta del vendedor, existencias, pedido, pago, despacho, entrega, devolución y reembolso.

### RegisterSellerService

Solo el administrador puede ejecutarlo (`POST /api/sellers`). Un vendedor no se auto-registra.

En una sola operación crea dos cosas, porque un vendedor sin bodega no puede recibir mercancía:

1. Un `Seller`, que ya es el usuario con rol `SELLER`. `RegisterUserService` solo comprueba que el correo y el documento estén libres; no guarda un usuario genérico.
2. Una `Warehouse` de tipo `SELLER` ligada a ese vendedor.

La bodega se construye antes de guardar. Si su dirección es inválida, no se ocupa el correo. Devuelve `SellerOnboarding` con el vendedor y la bodega.

### ReceiveStockService

Lo usan el vendedor y el operador logístico (`POST /api/inventory/receipts`).

Rechaza productos digitales: no tienen inventario. Exige que la variante exista. `InventoryAuthorization` impide que un vendedor cargue stock de otro vendedor o en una bodega que no sea suya ni del marketplace. El ingreso suma existencias y deja un movimiento `INBOUND`. El dominio impide que el saldo quede negativo.

`AdjustStockService` y `MarkInventoryConditionService` siguen la misma autorización. Un ajuste corrige el saldo; marcar `DAMAGED` saca esas unidades de lo que se puede reservar.

### ConfirmOrderService

Lo ejecuta un comprador habilitado (`POST /api/orders/confirm`).

Recorre el carrito. Cada producto debe estar `PUBLISHED` y la variante debe existir. Si la línea es física, busca inventario `AVAILABLE` que cubra la cantidad, reserva esas unidades con un movimiento `RESERVATION` y anota la bodega en la línea. El pedido nace en `PENDING_PAYMENT`, se vacía el carrito y el precio de cada línea queda congelado. La dirección principal ya es obligatoria al crear el comprador.

### RegisterPaymentService

Lo ejecuta el comprador dueño del pedido o un administrador (`POST /api/orders/{id}/payment`). No hay pasarela: la especificación deja el cobro técnico fuera de alcance, y este servicio registra que el pago ya se validó.

Pasa el pedido a `PAID`. En cada línea física convierte la reserva en salida de venta (`SALE_OUTBOUND`). Emite una factura con número `FAC-` y estado `ISSUED`. Si el pedido tiene algún producto físico, crea el envío en `PREPARING` con la dirección principal. Si todas las líneas son digitales, el pedido pasa directo a `COMPLETED` y no hay envío. Un pedido que ya tiene factura no se puede pagar otra vez.

### DispatchShipmentService

Lo ejecuta el operador logístico (`POST /api/shipments/{id}/dispatch`), después de que `PackShipmentService` dejó el envío en `PACKED`.

`Shipment.dispatch` avanza el envío y `Order.advanceTo(SHIPPED)` mueve el pedido. Un pedido finalizado rechaza cualquier cambio. `MarkShipmentInTransitService` solo actualiza el envío a `IN_TRANSIT`; no toca el pedido.

### ConfirmDeliveryService

Lo ejecuta el comprador dueño o el operador logístico (`POST /api/orders/{id}/delivery`).

Exige que el pedido tenga producto físico: el digital ya quedó finalizado al pagar. El envío debe poder entregarse desde `SHIPPED` o `IN_TRANSIT`. Entonces el envío pasa a `DELIVERED` y el pedido a `COMPLETED`. A partir de ahí `Order.advanceTo` rechaza cualquier transición.

### ReviewReturnService

Lo ejecuta el administrador (`POST /api/returns/{id}/approve` y `POST /api/returns/{id}/reject`).

La solicitud previa la hace el comprador en `RequestReturnService`, y solo sobre un pedido `COMPLETED` que no tenga ya otra devolución distinta de `REJECTED`.

Al aprobar, el servicio reingresa el stock físico con un movimiento `RETURN`, deja la devolución en `APPROVED` y crea un `Refund` en `PENDING` por el total de la factura. Rechazar solo cambia el estado a `REJECTED` y no mueve inventario ni dinero.

### ProcessRefundService

Lo ejecuta el administrador (`POST /api/returns/{id}/refund`).

Solo procede si la devolución está `APPROVED` y ya existe el reembolso pendiente. Marca el reembolso `PROCESSED` y anula la factura (`VOIDED`). Una factura ya anulada no se puede anular otra vez.

## El resto de los servicios

| Servicio | Rol | Ruta | Qué resuelve |
| --- | --- | --- | --- |
| `RegisterUserService` | lo usa el alta de personal | — | Unicidad de correo y documento, y persistencia del personal |
| `RegisterStaffService` | administrador | `POST /api/users/staff` | Operador logístico, administrador o supervisor. No crea vendedores |
| `ChangeUserStatusService` | administrador | `PATCH /api/users/{id}/status` | Activa o bloquea. Un bloqueado no pasa `AccessPolicy` |
| `QueryUsersService` | cualquiera sobre sí mismo; listado: administrador, supervisor | `GET /api/users`, `GET /api/users/{id}` | Consulta de identidades |
| `RegisterBuyerService` | público | `POST /api/buyers` | Un `Buyer` habilitado, guardado de una vez |
| `UpdateBuyerAddressesService` | comprador | `PUT /api/buyers/addresses` | Dirección principal y alternativas del propio perfil |
| `ChangeBuyerCommercialStatusService` | administrador | `PATCH /api/buyers/{id}/commercial-status` | Habilita o suspende compras sin borrar al usuario |
| `QueryBuyersService` | comprador (el propio), administrador, supervisor | `GET /api/buyers/me`, `GET /api/buyers/{id}`, `GET /api/buyers` | Un comprador no lista a los demás |
| `QuerySellersService` | vendedor (el propio), administrador, supervisor | `GET /api/sellers`, `GET /api/sellers/{id}` | El listado completo es solo de administrador y supervisor |
| `RegisterWarehouseService` | administrador | `POST /api/warehouses` | Bodega `MARKETPLACE` o `SELLER` con las reglas de `sellerId` |
| `QueryWarehousesService` | administrador, operador, supervisor, vendedor | `GET /api/warehouses` | El vendedor ve las suyas y las del marketplace |
| `RegisterProductService` | vendedor | `POST /api/products` | Alta en `SUSPENDED` y asignación de ids de variante |
| `ChangeProductStatusService` | vendedor dueño | `PATCH /api/products/{id}/status` | Publicar, suspender o descontinuar |
| `BrowseCatalogService` | público en el catálogo; vendedor en los suyos | `GET /api/catalog`, `GET /api/products/{id}`, `GET /api/products/mine` | El catálogo público solo muestra `PUBLISHED` |
| `AdjustStockService` | vendedor, operador logístico | `POST /api/inventory/adjustments` | Corrección de saldo con movimiento `ADJUSTMENT` |
| `MarkInventoryConditionService` | vendedor, operador logístico | `POST /api/inventory/{id}/condition` | `AVAILABLE` o `DAMAGED` |
| `QueryInventoryService` | vendedor, operador, administrador, supervisor | `GET /api/inventory`, `GET /api/inventory/{id}/movements` | El comprador no consulta existencias |
| `ManageCartService` | comprador | `GET/POST/DELETE /api/cart` | Arma el carrito sin reservar stock |
| `QueryOrdersService` | comprador (los propios), vendedor (con sus productos), operador logístico, administrador, supervisor | `GET /api/orders`, `GET /api/orders/{id}` | Un comprador no ve pedidos ajenos |
| `QueryInvoicesService` | comprador (las propias), vendedor (pedidos con sus productos), administrador, supervisor | `GET /api/invoices`, `GET /api/invoices/order/{orderId}` | La factura no se emite aquí; solo se consulta |
| `PackShipmentService` | operador logístico | `POST /api/shipments/{id}/pack` | `PREPARING` → `PACKED` |
| `MarkShipmentInTransitService` | operador logístico | `POST /api/shipments/{id}/transit` | `SHIPPED` → `IN_TRANSIT` |
| `QueryShipmentService` | operador, comprador dueño, administrador, supervisor | `GET /api/shipments/order/{orderId}` | Seguimiento del envío de un pedido |
| `RequestReturnService` | comprador dueño | `POST /api/returns` | Solo pedido `COMPLETED` y una devolución activa |
| `QueryReturnsService` | comprador (las propias), administrador, supervisor | `GET /api/returns/order/{orderId}` | Historial de la posventa |
| `QueryAdminReportService` | administrador, supervisor | `GET /api/reports` | Pedidos por estado, stock por bodega y ventas facturadas |

## Cómo se encadenan

```
RegisterSellerService
        |
        v
RegisterProductService --> ChangeProductStatusService (PUBLISHED)
        |
        v
ReceiveStockService
        |
        v
ManageCartService --> ConfirmOrderService (reserva, PENDING_PAYMENT)
        |
        v
RegisterPaymentService (salida de venta, factura FAC-, envío o COMPLETED si es digital)
        |
        v
PackShipmentService --> DispatchShipmentService (pedido SHIPPED) --> MarkShipmentInTransitService
        |
        v
ConfirmDeliveryService (pedido COMPLETED, ya no se modifica)
        |
        v
RequestReturnService --> ReviewReturnService (reingreso + reembolso pendiente)
        |
        v
ProcessRefundService (reembolso procesado y factura anulada)
```
