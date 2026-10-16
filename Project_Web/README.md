# FastFood MIS — Full-Stack Web Application

A complete commercial fast food chain business platform and management information system (MIS) migrated and expanded from the original Java console/stream project (`BaiTapNhom`).

Built with **Java Spring Boot 3** (backend) and **React 18 + TypeScript + Vite** (frontend).

---

## 🌟 Key Features

### 1. Customer-Facing Ordering Experience
- **Interactive Homepage:** Hero banner, current promotions, featured dishes with ratings, branch locator, value propositions.
- **Dynamic Menu & Catalog:** Filter by category, branch, price range, search by food name, sort by price.
- **Full Shopping Cart:** Quantity adjustments, subtotal calculations, local persistence.
- **Smart Checkout Workflow:** Real-time promotion discount calculations, branch selection, payment method simulation (Cash, Bank Transfer, E-Wallet, Card).
- **Order Confirmation & Tracking:** Instant order receipt with items, discount breakdown, branch instructions.
- **Customer Account & Order History:** View past orders, status, item breakdown, and profile.

### 2. Enterprise Admin & Staff MIS Dashboard
- **Executive Dashboard:** Live metrics for 11 tables, order status pipeline, branch revenue distribution, payment method split, low-stock warnings.
- **Orders & Point of Sale (POS):** View orders, drill down into order line-items and payment tickets, execute state machine order transitions (`CHO_XAC_NHAN` → `DA_XAC_NHAN` → `DANG_CHUAN_BI` → `SAN_SANG` → `DA_HOAN_THANH` / `DA_HUY`), create new in-store POS orders.
- **Food & Categories Management:** Full CRUD on food items and categories, price updates, active/discontinued soft-toggling.
- **Inventory & Branch Stock:** Branch stock monitoring, low-stock threshold filtering (< 10 units), instant stock quantity adjustments.
- **Promotions & Discount Calculator:** Create and pause promotional codes, minimum order threshold, percentage off, discount cap (`giamToiDa`), built-in discount testing sandbox.
- **Employees & Accounts:** Headcount per branch, role badges (`ADMIN`, `QUAN_LY`, `THU_NGAN`, `NHAN_VIEN`), account credential creation, account lock/unlock toggles.
- **Branch Management:** Branch card directory with address and phone management.
- **30 Business Queries Explorer:** Interactive workbench executing all 30 original project queries in SQL or Java Stream API mode, side-by-side result comparison, and automated bulk comparison test runner.
- **Reports & Analytics:** Revenue aggregations by branch and payment method, order completion vs cancellation ratios, CSV report exports.

---

## 🏗️ Architecture & Technology Stack

| Layer | Technologies |
|---|---|
| **Backend** | Java 21, Spring Boot 3.2.4, Spring Data JPA, Spring Web, Hibernate, Lombok |
| **Database** | H2 In-Memory (MySQL Mode) by default; MySQL support via environment variables |
| **Data Integrity** | Spring `@Transactional`, entity relationships, foreign keys, constraints |
| **Query Engine** | Dual engine: Spring `JdbcTemplate` (SQL) + `FastFoodService` (Stream API) |
| **Frontend** | React 18, TypeScript, Vite 5, React Router 6, Axios, Lucide Icons |
| **Styling** | Custom modular CSS design system (`index.css`) with responsive layout |

---

## 📁 Project Structure

```
Project_Web/
├── backend/
│   ├── src/main/java/com/fastfood/
│   │   ├── FastFoodApplication.java
│   │   ├── config/             # CORS configuration
│   │   ├── controller/         # REST API endpoints (13 controllers)
│   │   ├── dto/                # Request & response payloads
│   │   ├── entity/             # JPA entity models (11 entities)
│   │   ├── enums/              # Business enumerations (6 enums)
│   │   ├── exception/          # Global centralized exception handling
│   │   ├── repository/         # Spring Data JPA repositories (11 repos)
│   │   └── service/            # Business services & query implementations
│   ├── src/main/resources/
│   │   └── application.yml     # Configuration (H2 / MySQL)
│   └── src/test/java/com/fastfood/
│       ├── All30QueriesTest.java       # Verifies SQL vs Stream API parity across all 30 queries
│       ├── OrderServiceTest.java       # Verifies order creation, discounts & payment
│       └── StreamApiMethodsTest.java   # Verifies FastFoodService stream aggregations
│
├── frontend/
│   ├── src/
│   │   ├── api/                # Typed Axios API client (`ApiClient`)
│   │   ├── context/            # AuthContext & CartContext
│   │   ├── layouts/            # AppLayout (Admin) & PublicLayout (Storefront)
│   │   ├── pages/
│   │   │   ├── admin/          # Dashboard, Food, Orders, Staff, Inventory, Promotions, Queries, Branches, Reports
│   │   │   └── public/         # Home, Menu, Cart, Checkout, OrderConfirm, Login, Register, MyOrders, Profile
│   │   ├── types/              # TypeScript definitions for entities & DTOs
│   │   ├── App.tsx             # React Router routing table
│   │   ├── main.tsx            # Vite root entry
│   │   └── index.css           # Complete custom design system
│   ├── package.json
│   ├── tsconfig.json
│   └── vite.config.ts
│
└── docs/
    └── original/               # Preserved reference docs & SQL from BaiTapNhom
```

---

## 🚀 Quick Start Guide

### Prerequisites
- **Java 17+** (Java 21 recommended)
- **Maven 3.8+**
- **Node.js 18+** & npm

### 1. Run Backend (Port 8080)
Open a terminal in `Project_Web/backend`:
```powershell
mvn spring-boot:run
```
The server will start on `http://localhost:8080`.
On first launch, `DatabaseInitService` automatically creates all 11 tables and seeds the complete original sample data.

To run the automated test suite:
```powershell
mvn test
```

### 2. Run Frontend (Port 5173)
Open a second terminal in `Project_Web/frontend`:
```powershell
npm run dev
```
Open your browser at `http://localhost:5173`.
All `/api/*` requests are proxied automatically to `http://localhost:8080`.

---

## 🔐 Credentials & Demo Accounts

The original dataset pre-seeds 7 staff accounts:

| Username | Role | Branch | Password | Notes |
|---|---|---|---|---|
| `admin01` | System Admin (`ADMIN`) | CN00 (Headquarters) | `admin01` or `admin` or `123456` | Full administrative access |
| `nv01` | Branch Manager (`QUAN_LY`) | CN01 (Quan 1) | `nv01` or `123456` | Branch manager |
| `nv02` | Staff (`NHAN_VIEN`) | CN01 (Quan 1) | `nv02` or `123456` | Counter staff |
| `nv03` | Staff (`NHAN_VIEN`) | CN02 (Quan 3) | `nv03` or `123456` | Counter staff |
| `nv04` | Staff (`NHAN_VIEN`) | CN02 (Quan 3) | `nv04` or `123456` | Account initially `LOCKED` in seed data |
| `nv05` | Staff (`NHAN_VIEN`) | CN03 (Thu Duc) | `nv05` or `123456` | Counter staff |
| `nv08` | Cashier (`THU_NGAN`) | CN01 (Quan 1) | `nv08` or `123456` | Cashier account |

Customers can register directly through `/register` or order as guests.

---

## 📊 30 Business Queries Parity

All 30 original project queries are implemented in both **SQL** (`JdbcTemplate`) and **Java Stream API** (`FastFoodService`), validated by automated integration test `All30QueriesTest`:

| Query ID | Expected Result Count | Description |
|---|---|---|
| Q1 | 1 | Staff list in headquarters |
| Q2 | 4 | Total revenue per branch |
| Q3 | 19 | Inventory count across branches |
| Q4 | 6 | Active promotions |
| Q5 | 4 | Orders handled by branch manager |
| Q6 | 7 | Categories and product counts |
| Q7 | 28 | All order records |
| Q8 | 3 | Top selling items |
| Q9 | 28 | Customer order histories |
| Q10 | 1 | Highest single order |
| Q11–Q30 | Verified | Complex joins, group by, date filtering, having clauses |

Navigate to **Admin Panel > 30 Queries Explorer** (`/admin/queries`) to execute and compare any query live in the browser.

---

## 🛡️ Business Rules Enforced
1. **Server-Side Authoritative Pricing:** The backend recalculates item line amounts, discounts, and final totals.
2. **Promotion Integrity:** Validates active status (`DANG_AP_DUNG`), effective dates, minimum spend (`donToiThieu`), and enforces maximum discount cap (`giamToiDa`).
3. **Inventory Deduction:** Orders deduct stock from the selected branch atomically.
4. **Order Status Machine:** Enforces transition flow (`CHO_XAC_NHAN` → `DA_XAC_NHAN` → `DANG_CHUAN_BI` → `SAN_SANG` → `DA_HOAN_THANH`). Cancelled orders are excluded from revenue analytics.
5. **Historical Price Protection:** Unit prices are frozen into `chi_tiet_don_hang.donGia` upon order placement.
