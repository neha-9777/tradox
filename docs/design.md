# TradoX — UI/UX Design Specification

## 1. Design Goal
TradoX should provide a clean financial dashboard that lets users understand portfolio status quickly without requiring advanced financial knowledge.

Primary principles:
- Simple.
- Data-focused.
- Responsive.
- Consistent.
- Accessible.
- Clear financial terminology.
- Minimal visual clutter.

## 2. Main Screens

### Authentication
- Login.
- Register.
- Forgot password (future).
- Session/error states.

### Dashboard
Display:
- Total invested.
- Current portfolio value.
- Total P&L.
- Return percentage.
- Today's P&L.
- Connected broker status.
- Portfolio performance chart.
- Allocation chart.
- Top gainers/losers.

Suggested layout:

```text
┌──────────────────────────────────────────────────────────┐
│ TradoX                         Dashboard  Portfolio  User │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ Total Investment   Current Value   Total P&L   Return %  │
│ ₹2,50,000          ₹2,78,450       ₹28,450     11.38%    │
│                                                          │
├──────────────────────────────────────────────────────────┤
│                                                          │
│ Portfolio Performance                                   │
│                                                          │
│             ╱───────────                                 │
│        ╭───╯                                             │
│   ╭────╯                                                 │
│───╯                                                      │
│                                                          │
├──────────────────────────┬───────────────────────────────┤
│ Portfolio Allocation      │ Top Performers               │
│                          │                               │
│       Chart              │ Stock A   +18%                │
│                          │ Stock B   +12%                │
│                          │ Stock C    +9%                │
└──────────────────────────┴───────────────────────────────┘
```

## 3. Portfolio Page

Show:
- Stock symbol.
- Company name.
- Quantity.
- Average buy price.
- Current price.
- Invested value.
- Current value.
- Unrealized P&L.
- Return %.
- Data timestamp.

Actions:
- View stock.
- View transactions.
- Refresh/synchronize.

## 4. Transaction Page

Fields:
- Asset.
- Transaction type.
- Quantity.
- Price.
- Transaction date/time.
- Broker/source.
- Fees if supported.

Display:
- Transaction history.
- Filters.
- Search.
- Sort.

## 5. Broker Connections Page

Primary CTA:

**Connect Broker**

Flow:

```text
Select Broker
      ↓
View required permissions
      ↓
Continue to official authentication
      ↓
Authorization
      ↓
Return to TradoX
      ↓
Connection verified
      ↓
Initial synchronization
      ↓
Connected
```

Display:
- Broker name.
- Connection status.
- Last synchronized time.
- Sync button.
- Disconnect button.

Never display broker secrets or tokens.

## 6. Analytics Page

Sections:
- Portfolio performance.
- Asset allocation.
- Stock performance.
- P&L breakdown.
- Concentration.
- Historical trends.

Filters:
- 1D.
- 1W.
- 1M.
- 3M.
- 6M.
- 1Y.
- Custom range where supported.

## 7. Insights Page

Use concise insight cards such as:

```text
Portfolio Insight
Your largest holding represents 32% of your portfolio.

Performance Insight
Your portfolio gained 4.2% during the selected period.

Risk Insight
A small number of positions contribute most of the portfolio value.
```

Insights must be clearly presented as analytical information, not guaranteed financial advice.

## 8. Navigation

Desktop:

```text
TradoX
├── Dashboard
├── Portfolio
├── Transactions
├── Analytics
├── Insights
├── Broker Connections
└── Settings
```

Mobile navigation should prioritize:
- Dashboard.
- Portfolio.
- Transactions.
- Broker.
- More.

## 9. Visual Language
- Use a professional fintech visual style.
- Use consistent spacing and typography.
- Use cards for summary metrics.
- Use charts for trends and allocation.
- Avoid excessive gradients or decorative elements.
- Ensure sufficient contrast.
- Financial gain/loss indicators should be understandable through both color and text/icon, not color alone.

## 10. Responsive Design
Support:
- Desktop.
- Tablet.
- Mobile.

Tables should become horizontally scrollable or transform into compact cards on smaller screens.

## 11. UX States
Every data-driven screen should support:
- Loading.
- Empty.
- Error.
- Success.
- Synchronizing.
- Last updated timestamp.

Example:

```text
Syncing portfolio...
Last synchronized: 2 minutes ago
```

## 12. Accessibility
- Keyboard navigable controls.
- Clear focus states.
- Accessible labels.
- Sufficient text contrast.
- Charts should have textual summaries where practical.
- Do not rely only on color to communicate meaning.

## 13. Design System
Create reusable components:
- Button.
- Input.
- Select.
- Modal.
- Card.
- Table.
- Badge.
- Tabs.
- Dropdown.
- Toast.
- Chart container.
- Loading skeleton.
- Empty state.
- Error state.

## 14. Design Priority
The most important information should be visible immediately:

1. Portfolio value.
2. Total P&L.
3. Return.
4. Today's performance.
5. Holdings.
6. Portfolio trend.
7. Broker synchronization status.
8. Detailed analytics.
