# TradoX — Development Phases

## Phase 0 — Planning
- Finalize product scope.
- Confirm MVP.
- Finalize technology stack.
- Define requirements.
- Create architecture and design documents.

## Phase 1 — Project Setup
- Create Git repository.
- Create Spring Boot backend.
- Create React/Vite frontend.
- Configure MySQL.
- Configure environment variables.
- Establish development conventions.

## Phase 2 — Authentication & Users
- User entity.
- Registration.
- Password hashing.
- Login.
- JWT access token.
- Refresh-token strategy if required.
- Protected APIs.
- User profile.

## Phase 3 — Portfolio Core
- Portfolio entity.
- Asset entity.
- Holding entity.
- Transaction entity.
- CRUD APIs.
- Portfolio ownership and authorization.

## Phase 4 — P&L Engine
- Position calculation.
- Average cost methodology.
- Realized P&L.
- Unrealized P&L.
- Daily P&L.
- Weekly P&L.
- Monthly P&L.
- Return percentage.
- Unit tests for calculations.

## Phase 5 — Market Data
- Select suitable market-data provider.
- Implement market-data service.
- Current price retrieval.
- Historical price retrieval where available.
- Cache/rate-limit strategy.
- Timestamp and data-source tracking.

## Phase 6 — Analytics Dashboard
- Portfolio summary.
- Holdings table.
- Performance charts.
- Allocation charts.
- Best/worst performers.
- Concentration indicators.
- Historical performance.

## Phase 7 — Broker Connector Framework
- Define BrokerConnector interface.
- Define normalized broker models.
- Create broker connection entity.
- Implement connection lifecycle.
- Implement sync framework.
- Add sync logs and failure handling.

## Phase 8 — First Broker Integration
- Select one broker with suitable official API access.
- Implement official authentication flow.
- Fetch permitted holdings.
- Fetch positions.
- Fetch transactions/orders where supported.
- Normalize data.
- Reconcile with TradoX.
- Test synchronization.

## Phase 9 — Multi-Broker Expansion
- Add second broker.
- Add third broker.
- Reuse connector abstraction.
- Compare broker capability differences.
- Improve synchronization engine.

## Phase 10 — Intelligent Insights
- Portfolio summaries.
- Concentration warnings.
- Performance observations.
- Trend detection.
- Risk indicators.
- Optional AI-generated explanations.

## Phase 11 — Security & Reliability
- Security audit.
- API authorization testing.
- Secret-management review.
- Rate-limit handling.
- Error recovery.
- Audit logs.
- Backup/recovery strategy.

## Phase 12 — Testing
- Unit tests.
- Integration tests.
- Frontend tests.
- End-to-end tests.
- Performance tests.
- Security tests.

## Phase 13 — Deployment
- Production database.
- Backend deployment.
- Frontend deployment.
- HTTPS.
- Environment configuration.
- Monitoring and logs.

## Phase 14 — Future Features
- Order execution where officially supported and appropriate.
- Alerts.
- Tax reports.
- Additional asset classes.
- Mobile application.
- Advanced portfolio optimization.

## Recommended MVP Boundary

The first release should contain:

```text
Authentication
     +
Portfolio Management
     +
Transactions
     +
P&L
     +
Market Data
     +
Dashboard
     +
One Broker Connector (read-only)
```

Do not attempt all brokers, AI, automated trading, and advanced risk analytics in the first development cycle.
