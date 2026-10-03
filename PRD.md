# Product Requirements Document (PRD)
## Travel & Tourism Management System REST API

---

### 1. Project Overview

The **Travel & Tourism Management System (TMS) REST API** is an enterprise-grade, backend-only travel and reservation platform built on **Java 21, Spring Boot 3.3.4, Spring Data JPA, Hibernate, MySQL, Lombok, Jakarta Bean Validation, and OpenAPI 3.0 (Swagger UI)**.

The system provides a unified, headless service architecture that coordinates user accounts, destination catalogs, travel package itineraries, multi-criteria search and filtering algorithms, transactional seat allocations, reservation lifecycle tracking, and financial payment settlement. All user interactions are mediated strictly via RESTful HTTP interfaces producing and consuming `application/json`, with interactive exploration and developer testing enabled via Springdoc OpenAPI (Swagger UI) at `http://localhost:8081/swagger-ui/index.html`.

The primary goal of this platform is to establish transactional consistency, eliminate overbooking and seat-overselling anomalies, enforce inventory re-allocation invariants upon cancellations, provide rich multi-parameter search/filter capabilities, and offer an extensible API foundation for web booking portals, mobile travel apps, and third-party tourism aggregators.

---

### 2. Feature List

1. **User Identity & Account Management**: Provides registration, profile retrieval, updates, and role-based segregation (`USER`, `ADMIN`) with uniqueness enforcement on user email addresses.
2. **Destination Catalog & Regional Discovery**: Manages world destinations, regional metadata, sightseeing highlights, best seasons to visit, high-resolution media references, and keyword-based search across countries and states.
3. **Travel Package & Itinerary Administration**: Defines comprehensive holiday itineraries, day durations, per-traveler pricing models, seat quotas, start/end dates, inclusions, and exclusions with automatic `ACTIVE` / `SOLD_OUT` status transitions.
4. **Multi-Criteria Search & Filter Engine**: Filters packages dynamically across destination identifiers, minimum and maximum price brackets, trip duration ranges, booking status, and free-text keyword search against package titles, destination names, and countries.
5. **Real-Time Seat Inventory & Overbooking Prevention**: Intercepts reservation attempts with atomic checks ensuring that requested traveler seats never exceed live remaining quotas, automatically marking depleted packages as `SOLD_OUT`.
6. **Booking Lifecycle & Transactional Seat Allocation**: Manages end-to-end reservations (`CONFIRMED`, `PENDING`, `CANCELLED`, `COMPLETED`), computes gross pricing deterministically (`price * numberOfTravelers`), and locks seat capacity within `@Transactional` boundaries.
7. **Automated Seat Restoration & Refund Processing**: Upon booking cancellation, atomically restores reserved seats back to the package pool, transitions `SOLD_OUT` packages back to `ACTIVE`, and updates settled payments to `REFUNDED`.
8. **User & Package Reservation Querying**: Allows travelers to inspect their personal trip booking history, and administrators to review all passenger rosters associated with specific travel packages.
9. **Global Centralized Error Envelope**: Normalizes all domain and validation failures into an RFC 7807-compliant error structure (`timestamp`, `status`, `error`, `message`, `path`, `validationErrors`).
10. **Actuator Health & Operational Telemetry**: Exposes production-ready health and uptime metrics (`/actuator/health`, `/actuator/info`) for container orchestrators and load balancers.

---

### 3. Functional Requirements

#### 3.1 User Management & Identity
* **User Registration**:
  * Accepts `name`, `email`, `phone`, `password`, and optional `role` (`USER` or `ADMIN`, defaulting to `USER`).
  * Rejects registration attempts if the normalized email address already exists in the system (`DuplicateResourceException`, HTTP 409 Conflict).
  * Validates required constraints (`@NotBlank`, `@Email`, password length between 6 and 50 characters).
* **Profile Operations**:
  * `GET /api/users/{id}`: Retrieves profile details while hiding confidential credentials from output DTOs.
  * `GET /api/users`: Returns paginated user records with customizable sorting (`sortBy`, `sortDir`).
  * `PUT /api/users/{id}`: Allows users to update contact numbers, names, or credentials; prevents collision if modifying email to an already registered address.
  * `DELETE /api/users/{id}`: Removes user records and cascades cleanly according to foreign key constraints.

#### 3.2 Destination Registry & Search
* **Destination Invariants**:
  * Accepts `name`, `country`, `state`, `description`, `bestTimeToVisit`, and `imageUrl`.
  * Destination name must be unique system-wide (`DuplicateResourceException`, HTTP 409 Conflict).
* **Regional Discovery**:
  * `GET /api/destinations/search?query={keyword}`: Performs case-insensitive matching across destination `name`, `country`, and `state`.
  * Supports pagination and sorting across all queries.

#### 3.3 Travel Package Management & Lifecycle
* **Package Specifications**:
  * Associated with an existing destination via foreign key relationship (`@ManyToOne`). Rejects invalid destination IDs with `ResourceNotFoundException` (HTTP 404).
  * Validates that `endDate` is on or after `startDate`; rejects invalid chronologies with `BadRequestException` (HTTP 400).
  * Enforces positive pricing (`BigDecimal`) and non-negative available seat quotas (`availableSeats >= 0`).
* **Package Lifecycle States**:
  * `ACTIVE`: Package is open for traveler bookings and has `availableSeats > 0`.
  * `INACTIVE`: Package is temporarily paused or unlisted by administrators. Booking attempts must be rejected with `InvalidBookingException` (HTTP 400).
  * `SOLD_OUT`: Package has exhausted its available seat pool (`availableSeats == 0`). Booking attempts must be rejected with `InvalidBookingException` (HTTP 400).

#### 3.4 Booking Engine & Inventory Consistency
* **Booking Creation Invariants**:
  * **User Verification**: Rejects booking if `userId` is invalid (`ResourceNotFoundException`, HTTP 404).
  * **Package Verification**: Rejects booking if `travelPackageId` is invalid (`ResourceNotFoundException`, HTTP 404).
  * **Status Check**: Rejects booking if package status is `INACTIVE` or `SOLD_OUT` (`InvalidBookingException`, HTTP 400).
  * **Departure Check**: Rejects booking if package `startDate` is in the past (`InvalidBookingException`, HTTP 400).
  * **Seat Check**: Rejects booking if `numberOfTravelers > availableSeats` (`InsufficientSeatsException`, HTTP 400).
* **Atomic Seat Deduction**:
  * Within an isolated `@Transactional` boundary, decrements `availableSeats` by `numberOfTravelers`.
  * If `availableSeats` reaches `0`, the system automatically transitions package status to `SOLD_OUT`.
  * Auto-computes `totalAmount = package.price * numberOfTravelers` using `BigDecimal` arithmetic.
  * Sets initial state: `bookingStatus = CONFIRMED`, `paymentStatus = PENDING`.
* **Booking Cancellation & Inventory Restoration**:
  * Rejects cancellation if booking is already `CANCELLED` (`InvalidBookingException`, HTTP 400).
  * Restores `numberOfTravelers` back to `travelPackage.availableSeats`.
  * If package status was `SOLD_OUT` and now has positive seats, resets status to `ACTIVE`.
  * Marks `bookingStatus = CANCELLED`.
  * If `paymentStatus == PAID`, transitions payment status to `REFUNDED`.

#### 3.5 Search & Multi-Parametric Filtering
* Flexible querying via `GET /api/packages/filter`:
  * `destinationId`: Filters packages located at a specific destination.
  * `minPrice` & `maxPrice`: Filters within specific budgetary limits.
  * `minDuration` & `maxDuration`: Filters by vacation length in days.
  * `status`: Filters by package status (`ACTIVE`, `INACTIVE`, `SOLD_OUT`).
  * `keyword`: Free-text search on package name, destination name, or country.

---

### 4. Data Structures

The backend data domain is modeled using Jakarta Persistence (`jakarta.persistence`) mapped to relational database tables. Below are the corresponding structural TypeScript definitions for client-side API consumers:

```typescript
// Core Identity & Roles
export enum UserRole {
  USER = "USER",
  ADMIN = "ADMIN"
}

export interface User {
  id: number;
  name: string;
  email: string;
  phone: string;
  role: UserRole;
  createdAt: string; // ISO-8601
  updatedAt: string;
}

// Destination Catalog
export interface Destination {
  id: number;
  name: string;
  country: string;
  state: string;
  description?: string;
  bestTimeToVisit?: string;
  imageUrl?: string;
  createdAt: string;
}

// Travel Packages & Itineraries
export enum PackageStatus {
  ACTIVE = "ACTIVE",
  INACTIVE = "INACTIVE",
  SOLD_OUT = "SOLD_OUT"
}

export interface TravelPackage {
  id: number;
  packageName: string;
  destinationId: number;
  destinationName?: string;
  destinationCountry?: string;
  description?: string;
  duration: number; // in days
  price: number;    // per traveler in USD
  availableSeats: number;
  startDate: string; // YYYY-MM-DD
  endDate: string;   // YYYY-MM-DD
  inclusions?: string;
  exclusions?: string;
  status: PackageStatus;
  createdAt: string;
  updatedAt: string;
}

// Bookings & Transactions
export enum BookingStatus {
  CONFIRMED = "CONFIRMED",
  PENDING = "PENDING",
  CANCELLED = "CANCELLED",
  COMPLETED = "COMPLETED"
}

export enum PaymentStatus {
  PENDING = "PENDING",
  PAID = "PAID",
  FAILED = "FAILED",
  REFUNDED = "REFUNDED"
}

export interface Booking {
  id: number;
  userId: number;
  userName?: string;
  userEmail?: string;
  travelPackageId: number;
  packageName?: string;
  destinationName?: string;
  bookingDate: string; // ISO-8601
  numberOfTravelers: number;
  packagePrice: number;
  totalAmount: number;
  bookingStatus: BookingStatus;
  paymentStatus: PaymentStatus;
  createdAt: string;
  updatedAt: string;
}
```

---

### 5. API Interfaces

The system operates on default server port `8081` with base path `/`.

#### 5.1 User Management API (`/api/users/**`)
| Endpoint | Method | Status | Request Body | Description |
| :--- | :---: | :---: | :--- | :--- |
| `/api/users` | `POST` | 201 | `UserRequestDTO` | Register a new user |
| `/api/users/{id}` | `GET` | 200 | *None* | Get user details by primary ID |
| `/api/users` | `GET` | 200 | *None* | Get paginated list of users |
| `/api/users/{id}` | `PUT` | 200 | `UserRequestDTO` | Update user details |
| `/api/users/{id}` | `DELETE` | 204 | *None* | Delete user account |

#### 5.2 Destination Management API (`/api/destinations/**`)
| Endpoint | Method | Status | Request Body | Description |
| :--- | :---: | :---: | :--- | :--- |
| `/api/destinations` | `POST` | 201 | `DestinationRequestDTO` | Add new destination |
| `/api/destinations/{id}` | `GET` | 200 | *None* | Retrieve destination by ID |
| `/api/destinations` | `GET` | 200 | *None* | List all destinations (paginated) |
| `/api/destinations/{id}` | `PUT` | 200 | `DestinationRequestDTO` | Update destination metadata |
| `/api/destinations/{id}` | `DELETE` | 204 | *None* | Delete destination |
| `/api/destinations/search` | `GET` | 200 | *None* | Search destinations by keyword |

#### 5.3 Travel Package API (`/api/packages/**`)
| Endpoint | Method | Status | Request Body | Description |
| :--- | :---: | :---: | :--- | :--- |
| `/api/packages` | `POST` | 201 | `TravelPackageRequestDTO` | Create travel package |
| `/api/packages/{id}` | `GET` | 200 | *None* | Get package details by ID |
| `/api/packages` | `GET` | 200 | *None* | List all packages (paginated) |
| `/api/packages/{id}` | `PUT` | 200 | `TravelPackageRequestDTO` | Update package details/seats |
| `/api/packages/{id}` | `DELETE` | 204 | *None* | Delete travel package |
| `/api/packages/filter` | `GET` | 200 | *None* | Multi-criteria search and filter |
| `/api/packages/destination/{id}` | `GET` | 200 | *None* | List packages for a destination |
| `/api/packages/available` | `GET` | 200 | *None* | List packages with available seats |

#### 5.4 Booking Management API (`/api/bookings/**`)
| Endpoint | Method | Status | Request Body | Description |
| :--- | :---: | :---: | :--- | :--- |
| `/api/bookings` | `POST` | 201 | `BookingRequestDTO` | Reserve seats & deduct stock |
| `/api/bookings/{id}` | `GET` | 200 | *None* | Retrieve booking details by ID |
| `/api/bookings` | `GET` | 200 | *None* | List all bookings (paginated) |
| `/api/bookings/user/{userId}` | `GET` | 200 | *None* | List all bookings for a user |
| `/api/bookings/package/{pkgId}` | `GET` | 200 | *None* | List all bookings for a package |
| `/api/bookings/{id}` | `PUT` | 200 | `BookingStatusUpdateDTO` | Update booking status/payment/seats |
| `/api/bookings/{id}` | `DELETE` | 200 | *None* | Cancel booking & restore seats |
| `/api/bookings/{id}/cancel` | `PUT` | 200 | *None* | Cancel booking (PUT alternative) |

#### 5.5 Usage Examples

##### Booking Creation Request:
```http
POST /api/bookings HTTP/1.1
Host: localhost:8081
Content-Type: application/json

{
  "userId": 2,
  "travelPackageId": 1,
  "numberOfTravelers": 2
}
```

##### Successful Response (201 Created):
```json
{
  "success": true,
  "message": "Booking created successfully",
  "data": {
    "id": 1,
    "userId": 2,
    "userName": "Alice Johnson",
    "userEmail": "alice@travel.com",
    "travelPackageId": 1,
    "packageName": "Exotic Bali 7-Day Adventure",
    "destinationName": "Bali",
    "bookingDate": "2026-10-03T15:20:00",
    "numberOfTravelers": 2,
    "packagePrice": 899.99,
    "totalAmount": 1799.98,
    "bookingStatus": "CONFIRMED",
    "paymentStatus": "PENDING",
    "createdAt": "2026-10-03T15:20:00",
    "updatedAt": "2026-10-03T15:20:00"
  },
  "timestamp": "2026-10-03T15:20:00"
}
```

##### Insufficient Seats Error (400 Bad Request):
```json
{
  "timestamp": "2026-10-03T15:21:00",
  "status": 400,
  "error": "Insufficient Seats",
  "message": "Requested 50 seats, but only 18 seats are available.",
  "path": "/api/bookings"
}
```

---

### 6. User Interface (UI) Outline

> **Backend-Only Architecture Note:** This platform operates strictly as a Headless RESTful API. The primary operational and developer user interface is the interactive OpenAPI / Swagger UI console.

#### 6.1 Swagger UI Console (`http://localhost:8081/swagger-ui/index.html`)
The interactive portal organizes endpoints into 4 primary modules:
1. **User Management**: Registration, profile queries, updates, and user listing.
2. **Destination Management**: Destination registry, geographic exploration, and search.
3. **Travel Package Management**: Package CRUD, multi-criteria filtering, and live availability.
4. **Booking Management**: Booking creation, seat locks, customer history, and cancellation.

#### 6.2 UI Consumer Interface Guidelines (For Frontend Developers)
Developers integrating web or mobile apps should structure client experiences as follows:
* **Traveler Landing Page**: Hero banner pulling featured destinations from `GET /api/destinations`, with instant keyword search against `GET /api/destinations/search`.
* **Package Exploration Grid**: Multi-filter sidebar bound to `GET /api/packages/filter` with sliders for price range (`minPrice`, `maxPrice`) and duration (`minDuration`, `maxDuration`).
* **Package Details & Checkout Screen**: Real-time display of remaining seats (`availableSeats`). Reservation submission bound to `POST /api/bookings`.
* **User Dashboard / Trip History**: Passenger itinerary card listing bound to `GET /api/bookings/user/{userId}` with action button triggering `DELETE /api/bookings/{id}` for cancellation.
* **Admin Management Console**: Data grid for managing package catalogs and viewing passenger rosters per package via `GET /api/bookings/package/{packageId}`.

---

### 7. Non-Functional Requirements

#### 7.1 Performance & Concurrency
* **High-Throughput Connection Pool**: Configured with HikariCP (pool size: 10, minimum idle: 5, connection timeout: 20000ms).
* **Isolated Transactions**: Seat inventory decrements and restorations execute under `@Transactional` boundaries with pessimistic/atomic updates to prevent race conditions during concurrent bookings.

#### 7.2 Data Integrity & Validation
* **Strict Monetary Arithmetic**: All monetary variables (`price`, `totalAmount`) utilize `BigDecimal` with explicit two-decimal precision to prevent floating-point rounding errors.
* **Declarative Bean Validation**: Input payloads are validated before service dispatch using Jakarta Bean Validation (`@NotBlank`, `@NotNull`, `@Min`, `@Max`, `@Positive`, `@Email`).

#### 7.3 Multi-Profile Persistence Architecture
* **`mysql` Profile (Production / Dev)**: Connects to MySQL 8.x (`travel_tourism_db`) with automatic database creation (`createDatabaseIfNotExist=true`) and schema update migration (`hibernate.ddl-auto: update`).
* **`test` Profile (CI / Unit Testing)**: Utilizes an in-memory H2 database in MySQL emulation mode (`MODE=MySQL`) allowing automated test suites to run deterministically with zero external database dependencies.

#### 7.4 Maintainability & Clean Code
* **Strict Layered Separation**: Controllers remain thin and focus exclusively on HTTP dispatch; business invariants reside exclusively in the Service layer.
* **DTO Encapsulation**: JPA entities are never exposed to HTTP clients; entity-to-DTO translation is handled via dedicated mapper components.

---

### 8. Assumptions and Uncertainties

#### 8.1 Assumptions
* **Currency**: All financial values (`price`, `totalAmount`) are assumed to be denominated in standard currency units (e.g. USD) represented in `BigDecimal`.
* **Timezone**: Dates and timestamps use server clock time (`LocalDate.now()`, `LocalDateTime.now()`) standardized to UTC across database connections.
* **Automatic Inventory Reconciliation**: When all seats are reserved (`availableSeats == 0`), the package is immediately set to `SOLD_OUT`. When seats are cancelled and restored, the package is immediately set back to `ACTIVE`.

#### 8.2 Uncertainties & Future Integrations
* **Payment Gateway Webhooks**: The current version models payment states (`PENDING`, `PAID`, `FAILED`, `REFUNDED`), but real-time third-party payment gateway webhooks (e.g. Stripe, PayPal, Razorpay) are planned for future phases.
* **Notification Dispatchers**: Booking confirmation and cancellation logic is tracked in the database; automated email/SMS dispatchers (e.g. SendGrid, Twilio) can be connected as event listeners.
