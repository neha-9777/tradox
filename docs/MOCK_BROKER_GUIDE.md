# TradoX Mock Broker Synchronization Guide

This guide explains how to use, configure, and trigger the `MockBrokerConnector` in TradoX for development and testing.

---

## 1. Overview

`MockBrokerConnector` simulates a real broker API (such as Zerodha Kite, Groww, or Angel One) by reading normalized portfolio holdings, active positions, user profiles, and transaction histories from a mock JSON file (`mock-broker-data.json`).

### Key Features
- **JSON-Driven Broker Simulation**: Loads profile, holdings, positions, and transaction streams dynamically from `mock-broker-data.json`.
- **Architectural Abstraction**: Fulfills the standard `BrokerConnector` interface without altering core system architecture.
- **Idempotent Synchronization & Deduplication**: Employs unique `externalTxId` identifiers on transactions to prevent duplicate trade ingestion during repeated sync triggers.
- **Position & P&L Recalculation**: Automatically updates holding quantities, average buy prices, total invested capital, realized gains/losses on sells, and unrealized portfolio P&L.
- **Development-Only Auto-Sync Endpoint**: `POST /api/brokers/dev/sync` auto-connects the mock broker and executes data synchronization instantly for the authenticated user.

---

## 2. Test Data Scenarios (`mock-broker-data.json`)

The test data in `src/main/resources/mock-broker-data.json` includes comprehensive financial scenarios:

| Symbol | Asset Type | Scenario | Details |
|---|---|---|---|
| **RELIANCE** | Equity | **Multiple Buys** | Buy 10 shares @ ₹2400.00, then Buy 10 shares @ ₹2500.00. Recalculates average price to ₹2450.00. |
| **TCS** | Equity | **Partial Sell & Realized Profit** | Buy 20 shares @ ₹3500.00, then Sell 5 shares @ ₹3900.00. Realizes profit on 5 shares, holding 15 shares. |
| **TATASTEEL** | Equity | **Realized & Unrealized Loss** | Buy 100 shares @ ₹150.00, then Sell 20 shares @ ₹120.00. Realizes loss on 20 shares, market price drops to ₹110.00. |
| **HDFCBANK** | Equity | **Holding with Gain** | Buy 25 shares @ ₹1550.00. Current price ₹1680.00. |
| **INFOSYS** | Equity | **Active Position** | Open position of 10 shares @ ₹1400.00. Current price ₹1450.00. |

---

## 3. How Duplicate Transaction Prevention Works

1. Each broker transaction contains a unique `externalTxId` (e.g. `TX-MB-101`, `TX-MB-104`).
2. When `syncBrokerData` runs, it queries `transactionService.existsByPortfolioAndExternalTxId(portfolioId, externalTxId)`.
3. If the transaction already exists in the target portfolio, it is safely skipped.
4. On subsequent sync triggers, `transactionsSynced` will report `0`, maintaining exact portfolio state idempotency.

---

## 4. Triggering Mock Synchronization

### Option A: Via Development Endpoint (Recommended for Dev & Testing)

The development endpoint auto-creates a mock broker connection if one doesn't exist and triggers synchronization immediately:

```bash
# 1. Obtain JWT Bearer Token via Login
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@tradox.com",
    "password": "Password123!"
  }'

# 2. Trigger Mock Broker Dev Synchronization
curl -X POST http://localhost:8080/api/brokers/dev/sync \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

**Sample Response**:
```json
{
  "id": 1,
  "brokerConnectionId": 1,
  "status": "SUCCESS",
  "holdingsSynced": 5,
  "transactionsSynced": 7,
  "errorMessage": null,
  "syncStartedAt": "2026-08-22T20:50:00",
  "syncCompletedAt": "2026-08-22T20:50:01"
}
```

---

### Option B: Via Standard Broker Connect API

```bash
# 1. Connect Mock Broker Account
curl -X POST http://localhost:8080/api/brokers/connect \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "brokerId": "MOCK_BROKER",
    "accountName": "My Mock Account",
    "apiKey": "mock-api-key"
  }'

# 2. Trigger Sync for Connection (e.g. connection ID 1)
curl -X POST http://localhost:8080/api/brokers/1/sync \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>"
```

---

## 5. Running Automated Tests

To run unit and integration tests for the mock broker synchronization workflow:

```powershell
# From the backend directory:
java "-Dmaven.multiModuleProjectDirectory=." -classpath .mvn/wrapper/maven-wrapper.jar org.apache.maven.wrapper.MavenWrapperMain test
```

### Included Tests:
- **`MockBrokerConnectorTest`**: Verifies JSON parsing, profile mapping, and financial scenario data (profit, loss, multiple buys, partial sells).
- **`BrokerSyncServiceTest`**: Tests unit synchronization flow, connector routing, and duplicate skipping.
- **`MockBrokerDevSyncIntegrationTest`**: Full integration test verifying end-to-end endpoint execution, deduplication idempotency, and database P&L recalculation.
