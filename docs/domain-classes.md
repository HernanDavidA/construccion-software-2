# NexusMarket domain classes

This document explains **why each class exists** and **what it does**, aligned with the *Business Functional Specification*. The project is a Java domain model: there is no persistence, API, or graphical interface (those are out of scope).

When an attribute or status catalog **does not appear in the spec**, it is marked as **inferred**.

---

## Startup

### `NexusMarketApp`

- **Why:** Lets you verify that the Maven project compiles and starts (`mvn compile`, `mvn exec:java`).
- **Role:** Entry point with no business logic. Prints that the domain is loaded.

---

## Users (OBJ-01, OBJ-02, OBJ-03, RG-01, RG-02, RG-03)

Each participant has **a single role**. Inheritance is used so the role is fixed in the subclass and cannot be changed.

### `User` (abstract)

- **Covers:** OBJ-01, RG-01, RG-02. Validation 11 (identity document and email unique at platform level; here they must exist and the email must be valid; global uniqueness requires a future repository).
- **Why:** Every operation is executed by an identified user. It is the authentication and identification base of the marketplace.
- **Role:** Holds identifier, identity document, full name, email, role, and operational status. Allows activating or blocking the user.

### `Role`

- **Why:** The spec defines five participants and requires a unique role per user (RG-02).
- **Role:** Catalog `BUYER`, `SELLER`, `LOGISTICS_OPERATOR`, `ADMINISTRATOR`, `SUPERVISOR`.

### `UserStatus`

- **Why:** The user has an operational condition (Active, Blocked, etc.).
- **Role:** Catalog `ACTIVE`, `BLOCKED`.

### `Buyer`

- **Covers:** OBJ-03. Restriction: does not manage other buyers or inventory.
- **Why:** Purchases products and takes part in cart, orders, returns, and refunds.
- **Role:** Extends `User` with a required primary address, additional addresses, and commercial status. `canBuy()` requires an active user and enabled commercial status.

### `CommercialStatus`

- **Why:** The spec requires a buyer “commercial status” to make purchases, without listing values.
- **Role:** **Inferred** catalog `ENABLED`, `SUSPENDED`.

### `Address`

- **Why:** The buyer has a usual delivery location and may have secondary locations.
- **Role:** Represents a delivery address (line, city, region, postal code, country).

### `Seller`

- **Covers:** OBJ-02. Rule: does not self-register; the administrator onboards them.
- **Why:** Responsible for registering products and inventory (together with the logistics operator).
- **Role:** Extends `User` with business name and seller status (**inferred**: the spec does not detail seller-specific attributes).

### `SellerStatus`

- **Why:** It is necessary to distinguish whether the supplier is operational.
- **Role:** **Inferred** catalog `ACTIVE`, `SUSPENDED`.

### `Administrator`

- **Why:** In the responsibility matrix they register sellers; in the onboarding flow they also create the seller and their first warehouse.
- **Role:** Extends `User` with administrator role. `onboardSeller(...)` creates an active seller (no self-registration).

### `LogisticsOperator`

- **Why:** In charge of warehouses and dispatches; takes part in inventory, orders, and shipments.
- **Role:** Extends `User` with operator role. Adds no attributes: the spec does not detail them.

### `Supervisor`

- **Why:** Inquiry and operational follow-up profile (RG-03: does not manage outside their role).
- **Role:** Extends `User` with supervisor role. OBJ-12 (reports) is left for a future query layer.

---

## Catalog and warehouses (OBJ-04, OBJ-05)

### `Warehouse`

- **Covers:** OBJ-04.
- **Why:** Inventory is physical and distributed; a storage place must exist. Marketplace warehouses and seller warehouses are distinguished.
- **Role:** Identifies the space, its type, location, and, if it belongs to a seller, the owning seller. Factories `ofMarketplace` and `ofSeller`.

### `WarehouseType`

- **Why:** The spec classifies marketplace warehouses vs. seller warehouses.
- **Role:** `MARKETPLACE`, `SELLER`.

### `Product`

- **Covers:** OBJ-05.
- **Why:** The catalog distinguishes physical goods (inventory and dispatch) and digital goods (immediate delivery after payment).
- **Role:** Name, type, publication status, owning seller, price (**inferred**: needed for invoicing), and variant list.

### `ProductType`

- **Why:** Physical vs. digital changes inventory and logistics.
- **Role:** `PHYSICAL`, `DIGITAL`.

### `ProductStatus`

- **Why:** The spec defines Published, Suspended, or Discontinued.
- **Role:** Those three values. Only a `PUBLISHED` product enters the cart.

### `ProductVariant`

- **Why:** The spec includes differences of color, size, model, etc.
- **Role:** A concrete variant of a product; requires at least one differentiating attribute.

---

## Inventory (OBJ-06)

### `Inventory`

- **Covers:** OBJ-06 and the critical inventory validation.
- **Why:** Stock is **mandatory** bound to a product and a warehouse. No negative stock. Nonexistent or damaged stock cannot be reserved. Applies only to physical products.
- **Role:** Quantity and stock status; operations `receive`, `reserve`, `issueForSale`, `adjust`, and `returnStock`.

### `StockStatus`

- **Why:** The validation mentions “Damaged” inventory; there is also stock reservation.
- **Role:** `AVAILABLE`, `DAMAGED`, `RESERVED`.

### `InventoryMovement`

- **Why:** The spec lists movements: Receipt, Reservation, Sale issue, Adjustment, and Return.
- **Role:** Traces a change (type, quantity, date, note) against an inventory.

### `InventoryMovementType`

- **Why:** Fixes the movement catalog from the spec.
- **Role:** `RECEIPT`, `RESERVATION`, `SALE_ISSUE`, `ADJUSTMENT`, `RETURN`.

---

## Commercial operation (OBJ-07, OBJ-08, OBJ-09)

### `Cart`

- **Covers:** OBJ-07. First state of the order cycle (“Cart: provisional selection”).
- **Why:** The buyer builds the purchase before committing.
- **Role:** Groups items and delivery address. `confirmOrder` creates an `Order` in pending payment and clears the cart.

### `CartItem`

- **Why:** The cart is a list of products (and variants) with quantity.
- **Role:** Provisional line; only published products are allowed.

### `Order`

- **Covers:** OBJ-08. Validation: a completed order is not modified.
- **Why:** It is the formal commercial commitment and the central process of the system.
- **Role:** Cycle `CART` → `PENDING_PAYMENT` → `PAID` → `SHIPPED` → `DELIVERED_COMPLETED`. Calculates the total, associates invoice and shipment, and allows returns **after** shipping or delivery (post-sale, not order editing). Digital-only orders close with `completeDigitalDelivery()` after payment.

### `OrderStatus`

- **Why:** The spec defines the order status cycle.
- **Role:** The five cycle statuses.

### `OrderItem`

- **Why:** When confirming, lines stop being provisional and must keep price and type.
- **Role:** Snapshot of name, type, variant, quantity, and unit price; calculates subtotal.

### `Invoice`

- **Covers:** OBJ-09.
- **Why:** Every paid purchase generates commercial sale information.
- **Role:** Number, order, issue date, and total amount. **Inferred attributes:** the spec has no invoicing table.

---

## Logistics and post-sale (OBJ-10, OBJ-11)

The spec names shipments, returns, and refunds without attribute tables. The following is the **coherent minimum** for the flow (packing, dispatch, transport, close, and post-sale).

### `Shipment`

- **Covers:** OBJ-10.
- **Why:** Physical products require packing, dispatch, and transport. The logistics operator executes that operation.
- **Role:** Created on an order that requires shipment. Transitions from preparation to shipped (with tracking number), in transit, and delivered; confirming delivery completes the order.

### `ShipmentStatus`

- **Why:** A logistics cycle parallel to the order is needed.
- **Role:** **Inferred** catalog `IN_PREPARATION`, `SHIPPED`, `IN_TRANSIT`, `DELIVERED`.

### `ReturnRequest`

- **Covers:** OBJ-11. Matrix: buyer and administrator on refunds; the return is the previous step.
- **Why:** The scope includes return management as post-sale linked to an order.
- **Role:** Request with a reason; it is approved or rejected; if approved it can generate a refund and be completed.

### `ReturnStatus`

- **Role:** **Inferred** catalog `REQUESTED`, `APPROVED`, `REJECTED`, `COMPLETED`.

### `Refund`

- **Covers:** OBJ-11.
- **Why:** The scope includes refunds; the matrix assigns them to buyer and administrator.
- **Role:** Originates from an approved return, with amount taken from the order. It can be processed or rejected.

### `RefundStatus`

- **Role:** **Inferred** catalog `PENDING`, `PROCESSED`, `REJECTED`.

---

## What was not modeled (on purpose)

| Objective / topic | Reason |
| --- | --- |
| OBJ-12 Administrative reports | Consolidated query, not a business entity. Requires a query or persistence layer. |
| Global uniqueness of email and document | Without storage there is no central registry; classes do require that they exist. |
| Technical authentication, UI, APIs, database | Outside the scope of the specification and of this Maven project. |

---

## How to verify

```bash
mvn compile
mvn exec:java
```
