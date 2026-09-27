# NexusMarket application services

This document explains **why each service exists** and **what it does**, aligned with the *Business Functional Specification*. It is the application-layer companion of [domain-classes.md](domain-classes.md).

Services live inside a **hexagonal** layout: inbound ports (`port/in`), outbound ports (`port/out`), adapters (`adapter/in`, `adapter/out`) and bootstrap. Each service implements one inbound use-case port and depends only on domain types plus outbound ports.

When a rule **does not appear in the spec**, it is marked as **inferred**.

---

## Hexagon

```
com.nexusmarket
  domain/                  entities and local invariants
  application/
    <context>/port/in      driving ports (use cases)
    <context>/port/out     driven ports (repositories)
    <context>/service      use-case implementations
  adapter/
    in/api                 NexusMarketApi (driving adapter)
    out/persistence/memory in-memory repositories (driven adapters)
  bootstrap/               composition root
```

The five contexts are the same as the domain: `users`, `catalog`, `inventory`, `commerce`, `logistics`.

---

## Users (OBJ-01, OBJ-02, OBJ-03, RG-01, RG-02, RG-03)

### `RegisterBuyerService`

- **Port:** `RegisterBuyerUseCase`
- **Covers:** OBJ-03, validation 11.
- **Why:** A buyer must exist as an identified participant before cart, orders, returns or refunds. Uniqueness of email and document cannot be checked inside the entity.
- **Role:** Creates a `Buyer` with `ACTIVE` user status and `ENABLED` commercial status, required primary address. Rejects duplicate email or identity document.

### `OnboardSellerService`

- **Port:** `OnboardSellerUseCase`
- **Covers:** OBJ-02.
- **Why:** Sellers do not self-register. The administrator incorporates them.
- **Role:** Actor must be an active `Administrator`. Delegates to `Administrator.onboardSeller`. Enforces unique email and document. Resulting seller is `ACTIVE` / `ACTIVE`.

### `RegisterAdministratorService`

- **Port:** `RegisterAdministratorUseCase`
- **Covers:** OBJ-01, RG-02.
- **Why:** Onboarding of sellers and resolution of returns need an administrator.
- **Role:** Registers an active administrator with unique identity. Role is fixed in the subclass.

### `RegisterLogisticsOperatorService`

- **Port:** `RegisterLogisticsOperatorUseCase`
- **Covers:** OBJ-10.
- **Why:** Physical dispatch is executed by a logistics operator.
- **Role:** Registers an active operator with unique identity.

### `RegisterSupervisorService`

- **Port:** `RegisterSupervisorUseCase`
- **Covers:** RG-03.
- **Why:** The spec includes a supervisor profile. This role must exist even if it does not mutate data.
- **Role:** Registers an active supervisor with unique identity. This role is **not** an actor on write use cases.

### `ChangeUserStatusService`

- **Port:** `ChangeUserStatusUseCase`
- **Covers:** OBJ-01.
- **Why:** The platform must be able to activate or block any participant.
- **Role:** Loads any `User` and calls `activate()` or `block()`.

### `ChangeBuyerCommercialStatusService`

- **Port:** `ChangeBuyerCommercialStatusUseCase`
- **Why:** Purchases depend on commercial status, not only operational status.
- **Role:** Sets `ENABLED` or `SUSPENDED` on a `Buyer`. That value drives `canBuy()`.

### `ChangeSellerStatusService`

- **Port:** `ChangeSellerStatusUseCase`
- **Why:** **Inferred.** A supplier that is not operational must not publish or receive stock.
- **Role:** Sets `ACTIVE` or `SUSPENDED` on a `Seller`. Publish and receive services read this status.

### `AddBuyerAddressService`

- **Port:** `AddBuyerAddressUseCase`
- **Why:** The buyer may have secondary delivery locations.
- **Role:** Adds an additional `Address` to the owning buyer.

---

## Catalog (OBJ-04, OBJ-05)

### `CreateMarketplaceWarehouseService`

- **Port:** `CreateMarketplaceWarehouseUseCase`
- **Covers:** OBJ-04.
- **Why:** Inventory is physical and distributed; the marketplace owns its own storage spaces.
- **Role:** Creates a warehouse with `Warehouse.ofMarketplace`.

### `CreateSellerWarehouseService`

- **Port:** `CreateSellerWarehouseUseCase`
- **Covers:** OBJ-04.
- **Why:** Seller warehouses must be tied to an operational seller.
- **Role:** Seller must exist and have `SellerStatus.ACTIVE`. Creates `Warehouse.ofSeller`.

### `RegisterProductService`

- **Port:** `RegisterProductUseCase`
- **Covers:** OBJ-05.
- **Why:** The catalog is owned by sellers. A product starts unpublished so it does not enter carts until review.
- **Role:** Seller must be active. Price ≥ 0 (entity). Registers the product as `SUSPENDED`.

### `PublishProductService`

- **Port:** `PublishProductUseCase`
- **Covers:** OBJ-05.
- **Why:** Only published products enter the cart.
- **Role:** Seller still active; product not `DISCONTINUED`. Sets `PUBLISHED`.

### `ChangeProductPublicationService`

- **Port:** `ChangeProductPublicationUseCase`
- **Covers:** OBJ-05.
- **Why:** The spec allows Suspended and Discontinued as catalog states.
- **Role:** Accepts only `SUSPENDED` or `DISCONTINUED`.

### `AddProductVariantService`

- **Port:** `AddProductVariantUseCase`
- **Covers:** OBJ-05.
- **Why:** Color, size and model differences belong to the product.
- **Role:** Adds a `ProductVariant`. The entity requires at least one differentiating attribute.

---

## Inventory (OBJ-06)

### `OpenInventoryService`

- **Port:** `OpenInventoryUseCase`
- **Covers:** OBJ-06.
- **Why:** Stock is mandatory bound to one product and one warehouse. Buyers do not manage inventory (OBJ-03).
- **Role:** Actor is an active seller or logistics operator. Product must be physical. The product+warehouse pair is unique. Initial quantity ≥ 0.

### `ReceiveStockService`

- **Port:** `ReceiveStockUseCase`
- **Covers:** OBJ-06 (`RECEIPT` movement).
- **Why:** Incoming stock is an operational movement, not a constructor concern after the inventory exists.
- **Role:** Actor seller or operator, both active. Product seller not suspended. Rejects receipts on `DAMAGED` inventory. Calls `Inventory.receive`.

### `ReserveStockService`

- **Port:** `ReserveStockUseCase`
- **Covers:** OBJ-06 (`RESERVATION` movement).
- **Why:** Stock can be held independently of the cart (adjustments, manual holds). Confirmation of an order also reserves through `ConfirmOrderService`.
- **Role:** Actor seller or operator. Entity rejects damaged or insufficient stock.

### `AdjustStockService`

- **Port:** `AdjustStockUseCase`
- **Covers:** OBJ-06 (`ADJUSTMENT` movement).
- **Why:** Physical counts can differ from system quantity.
- **Role:** Actor seller or operator. Resulting quantity ≥ 0. Calls `Inventory.adjust`.

### `MarkInventoryDamagedService`

- **Port:** `MarkInventoryDamagedUseCase`
- **Covers:** critical inventory validation (“Damaged”).
- **Why:** Damaged stock must not be reserved.
- **Role:** Actor seller or operator. Calls `Inventory.markDamaged`.

`issueForSale` and `returnStock` are **not** standalone services: payment keeps the reservation already taken at confirmation; a processed refund restocks through `ResolveReturnAndRefundService`.

---

## Commerce (OBJ-07, OBJ-08, OBJ-09)

### `OpenCartService`

- **Port:** `OpenCartUseCase`
- **Covers:** OBJ-07.
- **Why:** The buyer builds a provisional selection before committing.
- **Role:** Buyer must `canBuy()`. Reuses the open cart if one exists; otherwise creates `Cart.newFor`.

### `AddItemToCartService`

- **Port:** `AddItemToCartUseCase`
- **Covers:** OBJ-07.
- **Why:** Lines are provisional. Physical availability is checked so the cart does not promise nonexistent stock.
- **Role:** Product must be `PUBLISHED` (entity). Physical items need enough `AVAILABLE` stock (query only, no reservation yet). Buyer eligibility is enforced by `Cart.addItem`.

### `ConfirmOrderService`

- **Port:** `ConfirmOrderUseCase`
- **Covers:** OBJ-08, OBJ-06.
- **Why:** Confirming the cart is the first formal commitment. Physical stock must be reserved so it cannot be sold twice.
- **Role:** Cart not empty and buyer `canBuy()`. Reserves each physical line, then `cart.confirmOrder`. Persists the empty cart and the new order in `PENDING_PAYMENT`.

### `RegisterOrderPaymentService`

- **Port:** `RegisterOrderPaymentUseCase`
- **Covers:** OBJ-09.
- **Why:** Every paid purchase generates an invoice.
- **Role:** Calls `order.registerPayment`, which creates the `Invoice`. Physical quantity was already reserved at confirmation; this service does not decrement stock again.

### `CompleteDigitalOrderService`

- **Port:** `CompleteDigitalOrderUseCase`
- **Covers:** OBJ-08 (digital delivery after payment).
- **Why:** Digital-only orders have no shipment. They close after payment.
- **Role:** Order must be `PAID` and must not `requiresShipment()`. Calls `completeDigitalDelivery()`.

---

## Logistics (OBJ-10, OBJ-11)

### `CreateShipmentService`

- **Port:** `CreateShipmentUseCase`
- **Covers:** OBJ-10.
- **Why:** Physical orders need packing and dispatch executed by a logistics operator.
- **Role:** Actor is an active `LogisticsOperator`. Order must be `PAID`. The `Shipment` constructor requires `requiresShipment()` and attaches itself to the order.

### `DispatchShipmentService`

- **Port:** `DispatchShipmentUseCase`
- **Covers:** OBJ-10.
- **Why:** Dispatch needs a tracking number (guía) and moves the order to `SHIPPED`.
- **Role:** Calls `shipment.ship(trackingNumber)`.

### `ConfirmShipmentDeliveryService`

- **Port:** `ConfirmShipmentDeliveryUseCase`
- **Covers:** OBJ-10.
- **Why:** Confirming delivery is what completes a physical order.
- **Role:** Calls `shipment.confirmDelivery()`, which completes the order.

### `RequestReturnService`

- **Port:** `RequestReturnUseCase`
- **Covers:** OBJ-11.
- **Why:** Post-sale starts with a buyer request linked to a shipped or delivered order.
- **Role:** Actor must be the order’s buyer. Reason is required. `ReturnRequest` registers itself on the order.

### `ResolveReturnAndRefundService`

- **Port:** `ResolveReturnAndRefundUseCase`
- **Covers:** OBJ-11.
- **Why:** Buyer and administrator participate in refunds; the administrator decides.
- **Role:** Actor is an active `Administrator`. Rejects the return, or approves it and then processes or rejects the refund. A processed refund restocks physical lines via `Inventory.returnStock`.

---

## Outbound ports

Owned children (`Address`, `CartItem`, `OrderItem`, `Invoice`, `ProductVariant`, `InventoryMovement`, `Refund`) are saved with their aggregate.

| Port | Methods | Aggregate |
| --- | --- | --- |
| `UserRepository` | `save`, `findById`, `existsByEmail`, `existsByIdentityDocument` | All user types; uniqueness (validation 11) |
| `WarehouseRepository` | `save`, `findById` | Warehouse |
| `ProductRepository` | `save`, `findById` | Product + variants |
| `InventoryRepository` | `save`, `findById`, `findByProductAndWarehouse`, `findByProductId` | Inventory + movements |
| `CartRepository` | `save`, `findById`, `findOpenByBuyerId` | Cart + items |
| `OrderRepository` | `save`, `findById` | Order + items + invoice |
| `ShipmentRepository` | `save`, `findById` | Shipment |
| `ReturnRequestRepository` | `save`, `findById` | Return + refund |

In-memory implementations live in `adapter/out/persistence/memory`.

---

## What was not modeled (on purpose)

| Topic | Reason |
| --- | --- |
| OBJ-12 Administrative reports | Query layer, not a write use case. |
| REST / JPA adapters | Out of current scope. Memory adapters close the hexagon for compilation and wiring. |

---

## How to verify

```bash
mvn compile
mvn exec:java
```

Entry point: `com.nexusmarket.bootstrap.NexusMarketApp` (also reachable from `com.nexusmarket.NexusMarketApp`).
