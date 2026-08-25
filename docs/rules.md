# TradoX — Development Rules

## 1. General Rules
1. Keep the codebase modular and maintainable.
2. Do not mix business logic with controllers.
3. Follow clear naming conventions.
4. Prefer small, testable services.
5. Avoid duplicated business logic.
6. Document important architectural decisions.

## 2. Backend Rules
- Use Java and Spring Boot.
- Use layered architecture:
  - Controller
  - Service
  - Repository
  - Entity/Domain
  - DTO
- Controllers should handle HTTP concerns only.
- Services should contain business logic.
- Repositories should handle persistence.
- Do not expose JPA entities directly when DTOs are more appropriate.
- Validate request payloads.
- Use centralized exception handling.
- Use transactions where atomicity is required.

## 3. Security Rules
- Use Spring Security.
- Use JWT for API authentication.
- Hash passwords; never store plaintext passwords.
- Never log passwords, access tokens, refresh tokens, API secrets, or broker secrets.
- Never ask users to provide broker passwords to TradoX.
- Use official broker authentication mechanisms.
- Apply authorization checks to every user-owned resource.
- Use HTTPS in production.
- Keep secrets in environment variables or a secure secret manager.

## 4. Broker Connector Rules
1. Each broker must have an isolated adapter/connector.
2. Never put broker-specific logic inside generic portfolio services.
3. Use a common connector contract.
4. Normalize external data into internal domain models.
5. Do not assume all brokers expose the same fields or capabilities.
6. Detect and handle rate limits.
7. Handle token expiration and authentication failures.
8. Make synchronization safe to retry.
9. Respect each broker's API terms, permissions, rate limits, and supported functionality.
10. Start with read-only synchronization for MVP.

## 5. Financial Calculation Rules
- Store transaction prices and quantities accurately.
- Use appropriate decimal types for monetary values; do not use floating-point types for financial amounts.
- Define the exact methodology for average cost and realized P&L before implementation.
- Clearly distinguish realized and unrealized P&L.
- Clearly label market-data timestamps.
- Never imply that analytics are guaranteed investment advice.

## 6. Database Rules
- Use foreign keys for relationships.
- Add indexes for frequently queried fields.
- Use unique constraints for appropriate identifiers.
- Keep audit/synchronization information where needed.
- Avoid storing redundant calculated values unless there is a clear performance reason.

## 7. Frontend Rules
- Keep API calls in a dedicated API/service layer.
- Use reusable components.
- Protect authenticated routes.
- Do not store highly sensitive secrets in browser local storage.
- Handle loading, empty, error, and synchronization states.
- Display financial values with clear units and timestamps.

## 8. API Rules
- Use REST conventions.
- Use meaningful HTTP status codes.
- Validate all inputs.
- Never trust client-provided ownership identifiers without server-side authorization.
- Return consistent error structures.
- Version APIs if breaking changes become necessary.

## 9. Git Rules
Recommended branches:

```text
main
develop
feature/*
bugfix/*
```

Commit examples:

```text
feat: add JWT authentication
feat: add portfolio holdings API
fix: correct realized P&L calculation
refactor: isolate broker connector interface
docs: update architecture
```

## 10. Testing Rules
- Unit test financial calculations.
- Unit test authentication/security logic.
- Test broker data mapping.
- Test synchronization idempotency.
- Test authorization boundaries.
- Add integration tests for critical APIs.
- Never use real broker credentials in automated tests.

## 11. Definition of Done
A feature is complete only when:
- Code is implemented.
- Validation and error handling exist.
- Relevant tests pass.
- Security implications are reviewed.
- API/documentation is updated where required.
- The feature does not break existing functionality.
