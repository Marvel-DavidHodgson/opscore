# OpsCore - White-Label Operations Management Platform

A production-quality white-label operations management platform designed for Japanese enterprise industries including ERP, Expense Management, Manufacturing, Logistics, and SAP-adjacent systems.

## 🏗️ Architecture Overview

### Tech Stack

**Backend:**
- Java 17
- Spring Boot 3.2.2
- Spring Security 6 (JWT authentication)
- Spring Data JPA + Hibernate
- PostgreSQL
- Flyway migrations
- Lombok & MapStruct
- SpringDoc OpenAPI 3 (Swagger)
- Maven

**Frontend:**
- Vite + React 18 + TypeScript
- Tailwind CSS
- shadcn/ui components
- TanStack Query (React Query v5)
- Zustand (state management)
- React Router v6
- Axios

## 🚀 Quick Start

### Prerequisites

- Java 17+
- Node.js 18+
- PostgreSQL 14+
- Maven 3.8+

### Database Setup

```bash
# Create PostgreSQL database
createdb opscore_db

# Or using psql
psql -U postgres
CREATE DATABASE opscore_db;
```

### Backend Setup

```bash
cd backend

# Install dependencies and run
mvn clean install
mvn spring-boot:run

# Backend will start on http://localhost:8080
# Swagger UI: http://localhost:8080/swagger-ui.html
```

### Frontend Setup

```bash
cd frontend

# Install dependencies
npm install

# Start development server
npm run dev

# Frontend will start on http://localhost:5173
```

## 📁 Project Structure

```
opscore/
├── backend/
│   ├── pom.xml
│   └── src/main/
│       ├── java/com/opscore/
│       │   ├── OpsCoreApplication.java
│       │   ├── config/          # Security, JWT, OpenAPI configs
│       │   ├── auth/            # Auth controllers, services, JWT
│       │   ├── tenant/          # Multi-tenant management
│       │   ├── user/            # User management
│       │   ├── item/            # Core white-label entity
│       │   ├── workflow/        # Approval workflow
│       │   ├── audit/           # AOP-based audit logging
│       │   └── report/          # JPQL-based reporting
│       └── resources/
│           ├── application.yml
│           └── db/migration/    # Flyway SQL scripts
└── frontend/
    ├── package.json
    ├── vite.config.ts
    ├── tailwind.config.js
    └── src/
        ├── api/                 # API client functions
        ├── components/          # React components
        ├── pages/               # Route pages
        ├── stores/              # Zustand stores
        ├── types/               # TypeScript types
        └── lib/                 # Utility functions
```

## 🔐 Authentication & Security

### Login Flow

1. POST `/api/auth/login` with email/password
2. Returns `accessToken` (15min) + `refreshToken` (7 days)
3. Access token auto-refreshes on 401 response
4. JWT payload includes: userId, tenantId, role, email

### Default Credentials

```
Email: admin@demo.com
Password: Admin123!
Tenant ID: 00000000-0000-0000-0000-000000000001
```

### Role Hierarchy

```
ADMIN > MANAGER > OPERATOR > VIEWER
```

## 🎯 Core Features

### 1. Multi-Tenant Architecture

Each tenant can configure:
- Company name & branding (logo, colors)
- Active modules
- Industry-specific label overrides
- Per-tenant data isolation

### 2. White-Label Entity (Items)

The `Item` entity adapts to different industries:
- **ERP System**: "業務項目" (Business Items)
- **Expense Management**: "経費申請" (Expense Claims)
- **Manufacturing**: "製造オーダー" (Production Orders)
- **Logistics**: "配送依頼" (Delivery Requests)

Configured via `labelOverrides` in tenant config.

### 3. Approval Workflow

State machine for item status transitions:

```
DRAFT → PENDING → APPROVED → CLOSED
           ↓
        REJECTED → DRAFT
```

Actions:
- `POST /api/items/{id}/submit` (OPERATOR+)
- `POST /api/items/{id}/approve` (MANAGER+)
- `POST /api/items/{id}/reject` (MANAGER+)
- `POST /api/items/{id}/close` (MANAGER+)

### 4. Automatic Audit Logging

Spring AOP aspect logs all service method calls:
- Entity type & ID
- Actor user & tenant
- Old/new values
- IP address & user agent
- Timestamp

### 5. Reporting & Analytics

JPQL-based aggregations:
- KPI summary (items by status/assignee)
- Category breakdowns
- CSV export with filters

## 📡 API Endpoints

### Authentication
- `POST /api/auth/login`
- `POST /api/auth/refresh`
- `POST /api/auth/logout`

### Tenants (ADMIN)
- `GET /api/tenants/me`
- `PUT /api/tenants/me`

### Users (ADMIN/MANAGER)
- `GET /api/users`
- `POST /api/users`
- `PUT /api/users/{id}`
- `DELETE /api/users/{id}`

### Items
- `GET /api/items` (paginated, filterable)
- `POST /api/items` (OPERATOR+)
- `GET /api/items/{id}`
- `PUT /api/items/{id}` (OPERATOR+)
- `DELETE /api/items/{id}` (MANAGER+)

### Workflow
- `POST /api/items/{id}/submit`
- `POST /api/items/{id}/approve`
- `POST /api/items/{id}/reject`
- `POST /api/items/{id}/close`
- `GET /api/items/{id}/history`

### Reports (MANAGER+)
- `GET /api/reports/kpi`
- `GET /api/reports/items/export`
- `GET /api/reports/categories`

### Audit (ADMIN)
- `GET /api/audit`
- `GET /api/audit/entity/{type}/{id}`

## 🔧 Configuration

### Backend (`application.yml`)

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/opscore_db
    username: postgres
    password: postgres
  jpa:
    hibernate:
      ddl-auto: validate
  flyway:
    enabled: true

jwt:
  secret: YOUR_SECRET_KEY
  accessTokenExpiry: 900000      # 15 minutes
  refreshTokenExpiry: 604800000  # 7 days
```

### Frontend (`vite.config.ts`)

```typescript
server: {
  proxy: {
    '/api': 'http://localhost:8080'
  }
}
```

## 🗄️ Database Schema

### Core Tables
- `tenants` - Tenant configuration & branding
- `users` - User accounts (FK to tenants)
- `refresh_tokens` - JWT refresh tokens
- `items` - White-label core entity
- `approval_events` - Workflow state transitions
- `audit_logs` - AOP-generated audit trail

### Migrations

Flyway scripts in `src/main/resources/db/migration/`:
- `V1__create_tenants.sql`
- `V2__create_users.sql`
- `V3__create_items.sql`
- `V4__create_workflow.sql`
- `V5__create_audit_log.sql`

## 🧪 Testing

### Backend

```bash
mvn test
```

### Frontend

```bash
npm run test
```

## 📦 Production Build

### Backend

```bash
mvn clean package
java -jar target/opscore-backend-1.0.0.jar
```

### Frontend

```bash
npm run build
# Output in dist/ folder - serve with nginx/apache
```

## 🌐 Internationalization

The platform supports label overrides per tenant for Japanese enterprise terminology:

```json
{
  "labelOverrides": {
    "item": "経費申請",
    "items": "経費一覧",
    "submit": "申請",
    "approve": "承認",
    "reject": "却下"
  }
}
```

## 🔒 Security Considerations

1. **CORS**: Configured for `localhost:5173` and `localhost:3000` (update for production)
2. **JWT Secret**: Change the default secret in production
3. **Password Hashing**: BCrypt with strength 10
4. **SQL Injection**: Protected via JPA/Hibernate parameterized queries
5. **XSS**: React's built-in escaping
6. **CSRF**: Disabled (stateless JWT)

## 📊 Performance

- **Database Indexing**: All foreign keys, status fields, and timestamps indexed
- **Pagination**: Default 20 items per page
- **Query Caching**: React Query with 5-minute stale time
- **Connection Pooling**: HikariCP (Spring Boot default)

## 🛠️ Development

### Adding a New Entity

1. Create entity class in `com.opscore.{domain}/`
2. Create repository interface
3. Create service class with business logic
4. Create DTOs and MapStruct mapper
5. Create controller with REST endpoints
6. Create Flyway migration SQL
7. Create frontend API client functions
8. Create React Query hooks

### Adding a New Industry Type

1. Add enum value to `IndustryType`
2. Create label overrides mapping
3. Update tenant creation logic
4. Update frontend type definitions

## 📝 License

Proprietary - OpsCore Enterprise License

## 👥 Support

For enterprise support, contact: support@opscore.com

---

**Built with ❤️ for Japanese Enterprise Operations**
