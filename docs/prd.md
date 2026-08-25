# TradoX — Product Requirements Document

## 1. Product Overview
TradoX is a smart multi-broker stock portfolio management and analytics platform. It helps investors and traders manage investments, automatically track portfolio performance, calculate profit and loss, and gain data-driven insights from a unified dashboard.

## 2. Problem Statement
Investors and traders often rely on spreadsheets or manual calculations to manage portfolios. Tracking holdings, transactions, daily/weekly/monthly P&L, returns, and portfolio trends is time-consuming and error-prone. Data may also be scattered across different broker platforms, making unified analysis difficult.

TradoX addresses this problem by centralizing portfolio data and automating calculations and analytics. Its connector architecture allows supported broker accounts to be connected so portfolio information can be synchronized into one platform.

## 3. Goals
- Centralize portfolio information.
- Reduce manual portfolio calculations.
- Track daily, weekly, monthly, and overall P&L.
- Provide portfolio and stock-level performance analytics.
- Support multiple broker connectors through a common abstraction.
- Provide secure authentication and authorization.
- Provide useful, data-driven portfolio insights.
- Build an architecture that can be extended to additional brokers and asset classes.

## 4. Target Users
- Individual investors.
- Active stock traders.
- Students learning portfolio analytics.
- Users managing accounts across multiple brokers.

## 5. MVP Scope

### Authentication
- User registration and login.
- JWT-based authentication.
- Secure password storage.
- Role-based authorization where required.

### Portfolio Management
- Create and manage portfolios.
- Add holdings and transactions.
- View current holdings.
- View transaction history.
- Track invested amount and current value.

### P&L
- Realized P&L.
- Unrealized P&L.
- Daily P&L.
- Weekly P&L.
- Monthly P&L.
- Overall return and return percentage.

### Analytics
- Portfolio allocation.
- Stock-wise performance.
- Best and worst performers.
- Historical portfolio performance.
- Basic diversification and concentration indicators.

### Broker Connector
- Connect supported brokers using their official authentication/API mechanisms.
- Fetch permitted holdings, positions, orders, and/or transactions.
- Normalize broker-specific data into TradoX's internal model.
- Synchronize data safely.
- Allow disconnection/revocation.

### Dashboard
- Portfolio summary.
- Current value.
- Total investment.
- P&L.
- Performance charts.
- Holdings overview.
- Broker connection status.

## 6. Future Scope
- Additional broker connectors.
- Order placement, subject to broker API capabilities and applicable requirements.
- Alerts and notifications.
- Advanced risk metrics.
- Mutual funds, ETFs, bonds, and other supported assets.
- Tax reports.
- AI-assisted portfolio summaries and insights.
- Mobile application.

## 7. Non-Goals for MVP
- Guaranteed investment recommendations.
- Automated trading.
- High-frequency trading.
- Custody of user funds.
- Storing broker passwords.
- Promising real-time market data unless supported by a licensed/authorized data source.

## 8. Functional Requirements
1. Users shall be able to register and log in.
2. Authenticated users shall be able to view their portfolio.
3. Users shall be able to add, edit, and review transactions where manual entry is enabled.
4. The system shall calculate portfolio metrics from transaction and market data.
5. The system shall display P&L for supported time periods.
6. The system shall synchronize data from supported broker connectors.
7. Users shall be able to disconnect a broker.
8. The system shall display analytics through dashboards and charts.
9. The system shall protect user-specific portfolio data from unauthorized access.

## 9. Non-Functional Requirements
- Security: authentication, authorization, encrypted transport, secure credential/token handling.
- Performance: common dashboard requests should respond quickly under expected MVP load.
- Scalability: broker connectors and analytics should be independently extensible.
- Reliability: synchronization failures should not corrupt existing portfolio data.
- Maintainability: modular backend and clear API contracts.
- Usability: simple dashboard and understandable financial metrics.
- Auditability: important synchronization and transaction events should be traceable.

## 10. Success Criteria
TradoX is successful when a user can securely authenticate, connect a supported broker, synchronize permitted portfolio data, view holdings and transactions, and understand portfolio value and P&L without manually maintaining spreadsheets.
