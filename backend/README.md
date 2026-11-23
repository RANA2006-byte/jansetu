# Jansetu — Backend README

## About
Jansetu is a college Java project: an **Indian Government Scheme Recommendation System**.  
It recommends relevant government schemes to citizens based on their profile (age, state, income, profession, etc.).

---

## Tech Stack
| Layer | Technology |
|---|---|
| Language | Java (plain Java, no Spring Boot) |
| Database | MySQL |
| DB Connectivity | JDBC (`java.sql.*`) |
| Frontend (later) | HTML / CSS / JavaScript |

---

## Folder Structure

```
backend/
├── src/
│   ├── model/
│   │   ├── User.java           ← Represents a user/applicant
│   │   └── Scheme.java         ← Represents a government scheme
│   ├── dao/
│   │   ├── UserDAO.java        ← All SQL for the users table
│   │   └── SchemeDAO.java      ← All SQL for the schemes table
│   ├── service/
│   │   └── RecommendationService.java  ← Matching logic
│   ├── util/
│   │   └── DBConnection.java   ← JDBC connection helper
│   └── Main.java               ← Entry point; run this to start
database/
└── schema.sql                  ← Run this in MySQL first
```

---

## Step 1 — Set Up MySQL

1. Make sure MySQL is installed and running.
2. Open a terminal and run:

```bash
mysql -u root -p < database/schema.sql
```

Or inside the MySQL prompt:
```sql
source C:/path/to/jansetu/database/schema.sql
```

This will:
- Create the `jansetu` database
- Create the `users` and `schemes` tables
- Insert 8 sample government schemes

---

## Step 2 — Download MySQL JDBC Driver

Java needs a "connector" JAR to talk to MySQL.

1. Download **MySQL Connector/J** from:  
   https://dev.mysql.com/downloads/connector/j/
2. Choose **Platform Independent → ZIP**
3. Extract it and find `mysql-connector-j-X.X.X.jar`
4. Place the JAR in `backend/lib/` (create this folder)

---

## Step 3 — Configure Database Password

Open `backend/src/util/DBConnection.java` and edit these lines:

```java
private static final String DB_URL  = "jdbc:mysql://localhost:3306/jansetu";
private static final String DB_USER = "root";
private static final String DB_PASS = "your_password_here"; // ← PUT YOUR PASSWORD HERE
```

> ⚠️ Do NOT commit your real password to GitHub. Add `DBConnection.java` to `.gitignore`, or replace the password with a placeholder before pushing.

---

## Step 4 — Compile and Run

### Option A — Command Line (javac + java)

```bash
# From the backend/ directory
# Compile all Java files
javac -cp "lib/mysql-connector-j-X.X.X.jar" -d out src/util/DBConnection.java src/model/User.java src/model/Scheme.java src/dao/UserDAO.java src/dao/SchemeDAO.java src/service/RecommendationService.java src/Main.java

# Run Main.java
java -cp "out;lib/mysql-connector-j-X.X.X.jar" Main
```

> On Linux/Mac use `:` instead of `;` in the classpath.

### Option B — IntelliJ IDEA (Recommended for college)

1. Open IntelliJ → **File → Open** → select the `jansetu` folder
2. Right-click `backend/lib/` → **Add as Library**
3. Right-click `Main.java` → **Run 'Main.main()'**

---

## Expected Output (when everything is set up correctly)

```
========================================
   Welcome to Jansetu Backend v1.0
   Indian Government Scheme Recommender
========================================

Testing database connection...
Database connected successfully!
Jansetu backend started.

Ready to build features.
```

---

## What Each Class Does

| Class | Role |
|---|---|
| `User.java` | Plain data holder for user details (fields + getters/setters) |
| `Scheme.java` | Plain data holder for scheme details (fields + getters/setters) |
| `DBConnection.java` | Returns a `Connection` object using `DriverManager.getConnection()` |
| `UserDAO.java` | Will contain all SQL INSERT/SELECT for the `users` table |
| `SchemeDAO.java` | Will contain all SQL SELECT/FILTER for the `schemes` table |
| `RecommendationService.java` | Will compare user profile against schemes and return matches |
| `Main.java` | Program entry point; currently just tests the DB connection |

---

## Current Status
- [x] Project structure created
- [x] Model classes (User, Scheme)
- [x] Database schema + sample data
- [x] JDBC connection utility
- [x] DAO stubs (foundation ready)
- [ ] DAO SQL implementation *(next stage)*
- [ ] Recommendation logic *(next stage)*
- [ ] Frontend (HTML/CSS/JS) *(later stage)*
