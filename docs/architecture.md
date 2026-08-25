# TradoX — System Architecture

## 1. Architecture Style
TradoX will use a modular client-server architecture.

Recommended MVP stack:
- Frontend: React + Vite
- Backend: Java + Spring Boot
- Security: Spring Security + JWT
- Persistence: Spring Data JPA / Hibernate
- Database: MySQL
- API: REST
- Market/broker integration: External APIs through connector adapters
- Deployment: Containerized or cloud-hosted services

## 2. High-Level Architecture

```text
┌───────────────────────────────┐
│          React Frontend       │
│ Dashboard | Portfolio | Auth  │
└───────────────┬───────────────┘
                │ HTTPS / REST
┌───────────────▼───────────────┐
│       Spring Boot Backend     │
│                               │
│ Auth & User Module            │
│ Portfolio Module              │
│ Transaction Module            │
│ P&L & Analytics Module        │
│ Broker Connector Module       │
│ Market Data Module            │
└───────┬─────────────┬─────────┘
        │             │
        │             ├───────────────┐
        ▼             ▼               ▼
┌────────────┐  ┌──────────────┐  ┌──────────────┐
│   MySQL    │  │ Broker APIs   │  │ Market Data  │
│ Database   │  │              │  │ APIs         │
└────────────┘  └──────────────┘  └──────────────┘
```

## 3. Backend Modules

### Auth Module
Responsible for:
- Registration.
- Login.
- JWT generation and validation.
- Password hashing.
- Authorization.

### User Module
Responsible for:
- User profile.
- User preferences.
- Connected broker metadata.

### Portfolio Module
Responsible for:
- Portfolio creation.
- Holdings.
- Portfolio valuation.
- Portfolio summaries.

### Transaction Module
Responsible for:
- BUY/SELL transactions.
- Transaction history.
- Transaction validation.
- Position calculations.

### Analytics Module
Responsible for:
- P&L.
- Returns.
- Allocation.
- Performance history.
- Risk/concentration indicators.

### Broker Connector Module
Expose a common interface and isolate broker-specific implementations.

```text
BrokerConnector
    ├── BrokerAConnector
    ├── BrokerBConnector
    └── BrokerCConnector
```

Suggested interface operations:

```text
authenticate()
getProfile()
getHoldings()
getPositions()
getOrders()
getTransactions()
disconnect()
```

Actual methods should be implemented according to each broker's official API.

## 4. Connector Flow

```text
User
  │
  ▼
Select Broker
  │
  ▼
Official Broker Authentication
  │
  ▼
Authorization Result
  │
  ▼
TradoX Connector
  │
  ▼
Fetch Broker Data
  │
  ▼
Normalize Data
  │
  ▼
Validate
  │
  ▼
Persist / Reconcile
  │
  ▼
Analytics
  │
  ▼
Dashboard
```

## 5. Database Concept

Core entities:

```text
User
 ├── Portfolio
 │     ├── Holding
 │     └── Transaction
 │
 └── BrokerConnection
```

Additional entities can include:
- Asset
- MarketPrice
- PortfolioSnapshot
- SyncJob
- SyncLog
- RefreshToken
- AuditLog

## 6. Security Architecture
- Passwords must be hashed using a strong password hashing algorithm.
- JWT access tokens should be short-lived.
- Refresh tokens should be handled securely.
- HTTPS must be used in deployed environments.
- Broker credentials/passwords must never be stored by TradoX.
- Store only the minimum broker token/connection information required by the official integration.
- Apply least-privilege permissions.
- Validate ownership of every portfolio resource.
- Log security-sensitive events without exposing secrets.

## 7. Data Synchronization
Synchronization should be idempotent where possible.

A synchronization job should:
1. Authenticate using the stored authorized connection.
2. Fetch supported broker data.
3. Map broker-specific fields to internal models.
4. Validate records.
5. Deduplicate using stable broker identifiers where available.
6. Reconcile changes.
7. Update portfolio calculations.
8. Record synchronization status.

## 8. API Layer

Example API groups:

```text
/api/auth/*
/api/users/*
/api/portfolios/*
/api/holdings/*
/api/transactions/*
/api/analytics/*
/api/brokers/*
/api/market/*
```

Example endpoints:

```text
POST /api/auth/register
POST /api/auth/login

GET  /api/portfolios
POST /api/portfolios

GET  /api/portfolios/{id}/holdings
GET  /api/portfolios/{id}/transactions
GET  /api/portfolios/{id}/performance

GET  /api/brokers
POST /api/brokers/{broker}/connect
POST /api/brokers/{broker}/sync
DELETE /api/brokers/{broker}/connection
```

Endpoint details will be finalized during API design.

## 9. Architecture Principles
- Separation of concerns.
- Broker-specific code must remain isolated.
- Domain models should not depend directly on broker SDK models.
- Business calculations should be independent of controllers.
- External API failures must be handled gracefully.
- Sensitive information must never be exposed in logs or API responses.
