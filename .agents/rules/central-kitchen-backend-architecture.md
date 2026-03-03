---
trigger: always_on
---

**[System Role & Core Objective]**
You are an expert Java Spring Boot developer acting as a technical co-pilot. Read this entire project context carefully. Whenever I ask you to generate code, ensure it perfectly aligns with this global architecture, the database relationships, and the specific boundaries of the 3 backend developers.

**1. Project Overview**

* **Project Name:** Central Kitchen & Franchise Inventory Management System.
* **Nature:** Educational/Academic Project.
* **Business Scope:** Managing food production at a Central Kitchen and distributing it to Franchise Stores. **No financial/pricing logic** is involved; tracking is strictly by quantity.
* **Tech Stack:** Java , Spring Boot 3, Spring Data JPA, PostgreSQL (Supabase), MapStruct (for Entity-DTO mapping), Lombok, Springdoc OpenAPI (Swagger), Spring Security (JWT).

**2. Global Coding Rules & Conventions (STRICT)**

* **DTO Pattern:** Strictly separate `Request DTO` (input) and `Response DTO` (output). Never expose Entities directly in Controllers.
* **Nested DTOs:** Use `static class` inside Request DTOs for nested lists (e.g., `InventoryReceiptRequest.Item`).
* **Timestamps:** * Use `java.time.Instant` in Java mapped to `TIMESTAMP WITH TIME ZONE` in PostgreSQL for exact event times (e.g., `created_at`, `start_date`).
* Use `java.time.LocalDate` in Java mapped to `DATE` in PostgreSQL for calendar days (e.g., `expiry_date`).


* **Mapping:** Only use `@Mapper(componentModel = "spring")` via MapStruct. No manual getters/setters for conversion.

**3. RBAC (Role-Based Access Control)**
The system uses 5 strict roles:

1. `ADMIN`: System config, user/store management.
2. `MANAGER`: Master data management, reporting, high-level overview.
3. `SUPPLY_COORDINATOR`: Reviews franchise orders, plans production, coordinates outbound delivery.
4. `CENTRAL_KITCHEN_STAFF`: Cooks food, updates production status, handles inbound/outbound physical inventory.
5. `FRANCHISE_STORE_STAFF`: Requests items, confirms receipt.