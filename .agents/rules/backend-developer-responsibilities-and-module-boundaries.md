---
trigger: always_on
---

# backend developer responsibilities and strict module boundaries

This project is divided into 3 backend developers.
Each developer has strict functional boundaries.
The AI must NEVER generate code outside the scope of the developer explicitly specified in the prompt.

---

# developer 1 – system & order flow module

## responsibility

Build the authentication foundation and manage the franchise order request flow.

## 1. authentication & user management

* login (email, password)
* logout
* generate jwt token
* create / update / deactivate user accounts

authorization:

* only ADMIN can manage users

---

## 2. franchise store management

* create / update / view franchise stores

authorization:

* ADMIN → create/update
* all other roles → view only

---

## 3. store order management (franchise → kitchen)

* create store order
* track order status (PENDING, APPROVED, COMPLETED, CANCELLED)
* confirm order completion when goods are received

authorization:

* FRANCHISE_STORE_STAFF → create, track, confirm completion
* SUPPLY_COORDINATOR, MANAGER → approve / cancel orders

---

# developer 2 – master data, production & inbound module (this is my role)

## responsibility

Manage core master data, handle production workflow, and manage inbound inventory into the central warehouse.

---

## 1. master data management (category & product)

* create / update categories
* create / update products (name, image, unit of measure)
* view product list (used by stores for ordering and kitchen for production)

authorization:

* MANAGER → create/update
* all 5 roles → view

---

## 2. manufacturing order management

* create manufacturing order based on approved store orders

  * product
  * planned quantity
* update production progress

  * PLANNED → COOKING → COMPLETED

authorization:

* SUPPLY_COORDINATOR → create manufacturing orders
* CENTRAL_KITCHEN_STAFF → update production status

---

## 3. product batch & inbound inventory management

* automatic batch creation (hidden system logic):

  * when a manufacturing order becomes COMPLETED
  * system auto-generates a ProductBatch
  * initial status = WAITING_FOR_STOCK
* create inventory receipt:

  * kitchen staff verifies actual cooked quantity
  * update ProductBatch.currentQuantity
  * change batch status to AVAILABLE

authorization:

* CENTRAL_KITCHEN_STAFF

---

# developer 3 – outbound, fefo & reporting module

## responsibility

Handle outbound warehouse operations using FEFO logic and generate analytical reports.

---

## 1. export note management (fefo algorithm)

* create export note based on approved store order
* automatically scan ProductBatch records with status AVAILABLE
* apply FEFO (first expired first out)
* deduct quantity from earliest expiry batches first

authorization:

* SUPPLY_COORDINATOR

---

## 2. inventory transaction ledger (immutable log)

* automatically log every inventory movement:

  * IMPORT
  * EXPORT
  * DISPOSAL
* no update or delete is allowed

authorization:

* MANAGER → view only

---

## 3. reports & analytics

* central warehouse stock overview
* near-expiry batch alert report
* store consumption summary report

authorization:

* stock overview → MANAGER, SUPPLY_COORDINATOR, CENTRAL_KITCHEN_STAFF
* expiry alerts → MANAGER, SUPPLY_COORDINATOR
* consumption summary → MANAGER, ADMIN

---

# very important instruction for the ai

When generating code:

* Respect strict module boundaries.
* If the prompt specifies DEV 2, only generate:

  * category
  * product
  * manufacturing order
  * product batch
  * inventory receipt
* Never generate authentication, store order, export, or reporting logic unless explicitly instructed.

