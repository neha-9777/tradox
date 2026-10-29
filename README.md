# 📈 TradoX — Smart Portfolio Management & Analytics Platform

TradoX is a full-stack **stock portfolio management and analytics platform** designed to help investors manage their portfolios, track transactions, monitor holdings, and analyze portfolio performance from a centralized dashboard.

The project follows a modern **Spring Boot + React** architecture with a dedicated backend and frontend, secure authentication, portfolio management, transaction tracking, broker integration support, and analytics capabilities.

---

## 🚀 Features

### 🔐 Authentication & Security

* User authentication and authorization
* JWT-based security
* Protected REST APIs
* Secure password handling
* Role-based access control
* Centralized security configuration

### 💼 Portfolio Management

* Create and manage investment portfolios
* Track portfolio holdings
* Monitor portfolio value
* Manage portfolio-related information
* Portfolio-specific transaction management

### 📊 Transaction Management

* Add investment transactions
* Track buy/sell transactions
* Maintain transaction history
* Associate transactions with portfolios
* Calculate portfolio performance based on transactions

### 📈 Portfolio Analytics

TradoX is designed to provide useful portfolio insights such as:

* Total invested amount
* Current portfolio value
* Profit & Loss
* Portfolio performance
* Individual stock performance
* Transaction-based portfolio analysis

### 🔗 Broker Integration

The backend contains a dedicated `broker` module for broker-related functionality.

The architecture is designed to support integration with external stock brokers and can be extended to retrieve portfolio and market-related information from supported broker APIs.

---

# 🏗️ Project Architecture

TradoX follows a **full-stack client-server architecture**.

```text
                    ┌──────────────────────┐
                    │      TradoX UI       │
                    │   React + Vite       │
                    └──────────┬───────────┘
                               │
                               │ REST API
                               ▼
                    ┌──────────────────────┐
                    │    TradoX Backend    │
                    │   Spring Boot        │
                    └──────────┬───────────┘
                               │
             ┌─────────────────┼─────────────────┐
             │                 │                 │
             ▼                 ▼                 ▼
        Controllers        Services         Security
             │                 │
             └────────┬────────┘
                      ▼
                 Repositories
                      │
                      ▼
                   Database

                      │
                      ▼
              Broker Integrations
```

---

# 📁 Project Structure

```text
TradoX/
│
├── backend/
│   │
│   ├── .mvn/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   │   └── com/
│   │   │   │       └── tradox/
│   │   │   │           │
│   │   │   │           ├── broker/
│   │   │   │           ├── config/
│   │   │   │           ├── controller/
│   │   │   │           ├── dto/
│   │   │   │           ├── entity/
│   │   │   │           ├── exception/
│   │   │   │           ├── repository/
│   │   │   │           ├── security/
│   │   │   │           ├── service/
│   │   │   │           │
│   │   │   │           └── TradoxApplication.java
│   │   │   │
│   │   │   └── resources/
│   │   │
│   │   └── test/
│   │
│   ├── .env
│   ├── .env.example
│   ├── .gitignore
│   ├── mvnw.cmd
│   └── pom.xml
│
├── frontend/
│   │
│   ├── public/
│   ├── src/
│   │   ├── api/
│   │   ├── assets/
│   │   ├── components/
│   │   ├── context/
│   │   ├── hooks/
│   │   ├── pages/
│   │   ├── routes/
│   │   ├── utils/
│   │   │
│   │   ├── App.jsx
│   │   ├── index.css
│   │   └── main.jsx
│   │
│   ├── .gitignore
│   ├── .eslintrc.json
│   ├── index.html
│   ├── package.json
│   ├── package-lock.json
│   ├── postcss.config.js
│   ├── README.md
│   ├── tailwind.config.js
│   └── vite.config.js
│
└── docs/
```

---

# 🛠️ Technology Stack

## Backend

| Technology      | Purpose                        |
| --------------- | ------------------------------ |
| Java            | Backend programming language   |
| Spring Boot     | Backend framework              |
| Spring Security | Authentication & authorization |
| JWT             | Stateless authentication       |
| Spring Data JPA | Database interaction           |
| Hibernate       | ORM                            |
| Maven           | Dependency management & build  |
| REST API        | Frontend-backend communication |
| MySQL           | Relational database            |

## Frontend

| Technology       | Purpose                      |
| ---------------- | ---------------------------- |
| React            | User interface               |
| Vite             | Frontend build tool          |
| JavaScript / JSX | Frontend development         |
| Tailwind CSS     | UI styling                   |
| React Context    | Application state management |
| React Router     | Client-side routing          |

---

# 📂 Backend Architecture

The backend is organized using a layered architecture.

```text
com.tradox
│
├── broker
│   └── Broker integration functionality
│
├── config
│   └── Application configuration
│
├── controller
│   └── REST API endpoints
│
├── dto
│   └── Data Transfer Objects
│
├── entity
│   └── Database entities
│
├── exception
│   └── Exception handling
│
├── repository
│   └── Database access layer
│
├── security
│   └── Authentication and authorization
│
├── service
│   └── Business logic
│
└── TradoxApplication.java
    └── Spring Boot entry point
```

### Request Flow

```text
Client
   │
   ▼
Controller
   │
   ▼
Service
   │
   ▼
Repository
   │
   ▼
Database
```

This separation keeps the application maintainable and makes individual layers easier to test and extend.

---

# 🎨 Frontend Architecture

The React application follows a modular structure.

```text
src/
│
├── api/
│   └── API communication
│
├── assets/
│   └── Static assets
│
├── components/
│   └── Reusable UI components
│
├── context/
│   └── Global application state
│
├── hooks/
│   └── Custom React hooks
│
├── pages/
│   └── Application pages
│
├── routes/
│   └── Application routing
│
├── utils/
│   └── Utility functions
│
├── App.jsx
├── main.jsx
└── index.css
```

---

# ⚙️ Prerequisites

Before running TradoX, make sure the following are installed:

* **Java 21+**
* **Maven**
* **Node.js**
* **npm**
* **MySQL**
* **Git**

Verify the installations:

```bash
java -version
mvn -version
node -v
npm -v
mysql --version
```

---

# 🔧 Backend Setup

### 1. Clone the repository

```bash
git clone <YOUR_GITHUB_REPOSITORY_URL>
cd TradoX
```

### 2. Navigate to backend

```bash
cd backend
```

### 3. Configure environment variables

Create a `.env` file inside the `backend` directory.

Example:

```env
DB_URL=jdbc:mysql://localhost:3306/tradox?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC

DB_USERNAME=your_database_username

DB_PASSWORD=your_database_password

JWT_SECRET=your_jwt_secret

ENCRYPTION_SECRET=your_encryption_secret
```

> **Important:** Never commit your `.env` file to GitHub.

The repository contains `.env.example` so developers can understand which environment variables are required.

### 4. Start the backend

Using Maven:

```bash
mvn spring-boot:run
```

Or on Windows:

```bash
.\mvnw.cmd spring-boot:run
```

The backend will start on the configured Spring Boot port.

---

# 💻 Frontend Setup

Open another terminal.

### 1. Navigate to frontend

```bash
cd frontend
```

### 2. Install dependencies

```bash
npm install
```

### 3. Start the development server

```bash
npm run dev
```

Vite will provide a local development URL, typically:

```text
http://localhost:5173
```

---

# 🔌 API Communication

The frontend communicates with the Spring Boot backend through REST APIs.

Typical flow:

```text
React Frontend
      │
      │ HTTP Request
      ▼
Spring Boot REST API
      │
      ▼
Service Layer
      │
      ▼
Repository
      │
      ▼
MySQL
```

Authenticated requests use the user's JWT token to access protected resources.

---

# 🔐 Authentication Flow

TradoX uses JWT-based authentication.

```text
User
 │
 │ Login
 ▼
Authentication API
 │
 │ Validate credentials
 ▼
Spring Security
 │
 │ Generate JWT
 ▼
Frontend
 │
 │ Store/use token
 ▼
Protected APIs
```

For protected requests:

```text
Frontend
   │
   │ Authorization: Bearer <JWT>
   ▼
Spring Security
   │
   ├── Valid Token ──► Controller
   │
   └── Invalid Token ► 401 Unauthorized
```

---

# 📊 Portfolio Management Flow

```text
Create Portfolio
       │
       ▼
Add Transactions
       │
       ▼
Calculate Holdings
       │
       ▼
Calculate Investment
       │
       ▼
Calculate Current Value
       │
       ▼
Calculate Profit / Loss
       │
       ▼
Display Analytics
```

This allows users to view their portfolio from both a transaction and performance perspective.

---

# 🔗 Broker Integration

The `broker` package provides a dedicated area for broker-related functionality.

The architecture can be extended to support multiple brokers without tightly coupling broker-specific logic to the core portfolio system.

A future broker integration can follow a structure such as:

```text
TradoX
  │
  ├── Broker A
  │
  ├── Broker B
  │
  └── Broker C
```

This makes the system suitable for a multi-broker portfolio management platform.

---

# 📈 Analytics

The analytics layer can provide users with important portfolio metrics including:

* Total investment
* Current portfolio value
* Realized profit/loss
* Unrealized profit/loss
* Overall return
* Individual asset performance
* Transaction history
* Portfolio-level performance

> Financial calculations should be treated as informational and should not be considered investment advice.

---

# 🏃 Running the Complete Application

You need two terminals.

### Terminal 1 — Backend

```bash
cd backend
mvn spring-boot:run
```

### Terminal 2 — Frontend

```bash
cd frontend
npm install
npm run dev
```

Then open the frontend URL provided by Vite.

---

# 🔒 Environment & Security

The following files should **not** be committed with sensitive credentials:

```text
.env
```

The project includes:

```text
.env.example
```

Use `.env.example` as a template for configuring a local development environment.

Never expose:

* Database passwords
* JWT secrets
* Encryption keys
* Broker API keys
* Broker access tokens
* Other private credentials

---

# 📝 Documentation

Project documentation is maintained inside:

```text
docs/
```

The documentation can contain architecture, requirements, design decisions, development phases, API documentation, and other project-related information.

---

# 🗺️ Future Enhancements

Potential future improvements include:

* 🔗 More broker integrations
* 📡 Real-time market data
* 📊 Advanced portfolio analytics
* 📈 Interactive performance charts
* 🤖 AI-powered portfolio assistant
* 🔔 Price and portfolio alerts
* 📱 Mobile-responsive improvements
* 📑 Detailed portfolio reports
* 📤 Export portfolio data
* 🧠 Personalized portfolio insights
* ⚡ Real-time portfolio updates
* ☁️ Cloud deployment
* 🐳 Docker containerization
* 🧪 Expanded automated testing

---

# ⚠️ Disclaimer

TradoX is a software project created for portfolio management, analytics, and educational purposes.

The information and calculations provided by the application are intended for informational purposes only and should not be considered financial, investment, or trading advice.

Users should perform their own research and consult qualified financial professionals before making investment decisions.

---
<div align="center">

**⭐ Star this repo if you found it helpful!**

Made By Neha Pathan ❤️ 

</div>

