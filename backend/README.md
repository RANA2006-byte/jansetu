# JANSETU 🇮🇳
### Government Scheme Discovery & Recommendation System

Jansetu is a Java-based Government Scheme Discovery and Recommendation System designed to help citizens discover relevant Indian government schemes based on their personal and eligibility details.

The application uses Core Java, JDBC and MySQL to provide account management, scheme exploration, eligibility-based recommendations and saved scheme management through an interactive terminal interface.

---

## 🚀 Features

- **User Registration:** Create a new account securely.
- **User Authentication:** Login and logout functionality.
- **Explore Government Schemes:** Browse all available schemes.
- **Category-Based Search:** Find schemes by category.
- **Smart Recommendations:** Identify eligible schemes based on applicant details.
- **Applicant Profile Management:** View and update personal information.
- **Scheme Details:** View eligibility criteria, benefits and application links.
- **Saved Schemes:** Save, view and remove schemes from your personal list.

## 🛠️ Technology Stack

| Component | Technology |
|---|---|
| Programming Language | Core Java |
| Database | MySQL |
| Database Connectivity | JDBC |
| Architecture | DAO, Service and Model layers |
| Authentication | Java-based authentication |
| Build and Execution | javac, java, Windows Batch |
| Frontend | HTML, CSS, JavaScript (existing project files) |

## 📁 Project Structure

```text
jansetu/
│
├── backend/
│   ├── src/
│   │   ├── auth/
│   │   │   ├── AuthDAO.java
│   │   │   ├── AuthService.java
│   │   │   └── PasswordUtil.java
│   │   │
│   │   ├── dao/
│   │   │   ├── UserDAO.java
│   │   │   ├── SchemeDAO.java
│   │   │   └── SavedSchemeDAO.java
│   │   │
│   │   ├── model/
│   │   │   ├── User.java
│   │   │   └── Scheme.java
│   │   │
│   │   ├── service/
│   │   │   └── RecommendationService.java
│   │   │
│   │   ├── util/
│   │   │   └── DBConnection.java
│   │   │
│   │   ├── server/
│   │   │   ├── JansetuServer.java
│   │   │   ├── AuthHandler.java
│   │   │   ├── SavedSchemeHandler.java
│   │   │   └── UserProfileHandler.java
│   │   │
│   │   └── Main.java
│   │
│   ├── lib/
│   ├── run.bat
│   └── out/
│
├── frontend/
│   ├── index.html
│   └── app.js
│
├── database/
│   └── schema.sql
│
└── README.md
```

## ⚙️ Installation and Setup

### 1. Prerequisites

Install the following:

- JDK 17 or later
- MySQL Server
- MySQL Workbench (recommended)
- Git

### 2. Clone the Repository

```bash
git clone https://github.com/RANA2006-byte/jansetu.git
cd jansetu
```

### 3. Set Up the Database

Make sure MySQL Server is running.

Execute the project's database schema file:

```bash
mysql -u root -p < database/schema.sql
```

Alternatively, open MySQL Workbench and execute `database/schema.sql`.

This initializes the database and its required tables. The application uses tables for accounts, sessions, applicant profiles, schemes and saved schemes.

### 4. Download MySQL Connector/J

Java requires MySQL Connector/J to communicate with MySQL.

1. Download MySQL Connector/J from:
   https://dev.mysql.com/downloads/connector/j/
2. Extract the downloaded archive.
3. Place the Connector/J JAR inside `backend/lib/`.
4. Make sure its filename matches the JAR filename configured in `backend/run.bat`.

The current run script expects:

```text
mysql-connector-j-26.10.0.jar
```

If you download a different version, update the `JAR` variable in `run.bat` accordingly.

### 5. Configure Database Credentials

The database connection is configured in:

`backend/src/util/DBConnection.java`

The application reads the MySQL password through an environment variable:

```java
private static final String DB_URL =
        "jdbc:mysql://localhost:3306/jansetu";

private static final String DB_USER = "root";

private static final String DB_PASS =
        System.getenv("JANSETU_DB_PASS");
```

Set the password in PowerShell:

```powershell
$env:JANSETU_DB_PASS="your_mysql_password"
```

Replace the placeholder with your local MySQL password.

**Security:** Never commit actual database passwords or credentials to GitHub.

## ▶️ Running the Application

Open PowerShell in the backend directory:

```powershell
cd backend
```

Run the terminal application:

```powershell
.\run.bat main
```

The batch file automatically compiles the Java source files and launches the application.

## 💻 Application Menu

The application provides two menus.

### Guest Menu

```text
========================================
          WELCOME TO JANSETU
  Government Scheme Discovery System
========================================

1. Register New Account
2. Login
3. Explore All Government Schemes
4. Search Schemes by Category
0. Exit
```

### Logged-In User Dashboard

```text
========================================
             JANSETU DASHBOARD
========================================

1. Explore All Government Schemes
2. Search Schemes by Category
3. Check Eligibility & Recommendations
4. View Applicant Profile
5. Update Applicant Profile
6. View Scheme Details
7. Save a Scheme
8. View Saved Schemes
9. Remove Saved Scheme
10. Logout
0. Exit
```

## 🧠 Recommendation System

Jansetu uses an eligibility-based recommendation algorithm.

It compares applicant information with the eligibility criteria of available government schemes.

The recommendation logic considers:

1. Age
2. Profession
3. State
4. Annual Income
5. Education Level
6. Gender
7. Marital Status

Only schemes satisfying all the implemented eligibility conditions are returned as recommendations.

## 🏗️ Architecture

The application follows a layered architecture:

| Layer | Responsibility |
|---|---|
| Model | Represents applicants and government schemes |
| DAO | Handles database operations using JDBC |
| Service | Contains business logic and recommendation logic |
| Auth | Handles registration, login and authentication |
| Util | Provides database connectivity |
| Main | Controls the terminal interface and application flow |

## 🔐 Database and Security

- Uses JDBC prepared statements for database operations.
- Uses password hashing and salt through the authentication utilities.
- Uses account-based sessions.
- Stores saved schemes against individual accounts.
- Keeps database credentials outside the source code.

## 📌 Current Project Status

- [x] Java model classes
- [x] MySQL database integration
- [x] User registration and login
- [x] Authentication and session management
- [x] Government scheme exploration
- [x] Category-based scheme search
- [x] Eligibility-based recommendations
- [x] Applicant profile management
- [x] Scheme details
- [x] Save and view schemes
- [x] Remove saved schemes
- [x] Terminal-based application interface
- [x] GitHub repository setup

## 👨‍💻 Author

**Ashutosh Rana**

GitHub: https://github.com/RANA2006-byte

---

*JANSETU — Connecting Citizens with Government Opportunities.* 🇮🇳