package util; // tells Java this file belongs to the "util" package (folder)

// ============================================================
// DBConnection.java - Utility class to connect Java to MySQL
//
// JDBC = Java Database Connectivity
// It is a standard Java API (built-in) that lets Java talk
// to any relational database (MySQL, PostgreSQL, Oracle, etc.)
//
// How it works in 3 steps:
//   1. Load the MySQL JDBC Driver (the "translator")
//   2. Call DriverManager.getConnection() with the DB URL
//   3. Use the returned Connection object for queries
// ============================================================

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {

    // ============================================================
    // >>> CONFIGURE YOUR DATABASE DETAILS HERE <<<
    //
    // DB_URL : jdbc:mysql://HOST:PORT/DATABASE_NAME
    // localhost = your own machine
    // 3306 = default MySQL port
    // jansetu = the database we create in schema.sql
    //
    // DB_USER : your MySQL username (usually "root" for local dev)
    //
    // DB_PASS : your MySQL password
    // IMPORTANT: Do NOT commit a real password to GitHub.
    // For a college project, keep this file in .gitignore,
    // or replace the password before pushing to GitHub.
    // ============================================================

    private static final String DB_URL = "jdbc:mysql://localhost:3306/jansetu";
    private static final String DB_USER = "root";
    private static final String DB_PASS = System.getenv("JANSETU_DB_PASS"); // <-- CHANGE THIS (do not commit real
                                                                            // password)

    // --------------------------------------------------------
    // getConnection() — call this method anywhere you need a
    // database connection. It returns a Connection object.
    //
    // We declare 'throws SQLException' so the caller handles
    // any connection errors (keeps this method simple).
    // --------------------------------------------------------
    public static Connection getConnection() throws SQLException {

        // DriverManager looks at the URL prefix "jdbc:mysql://"
        // and finds the right driver automatically (since MySQL
        // Connector/J 5.1+ and Java 6+, explicit Class.forName
        // is no longer required).
        Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);

        return connection;
    }

    // --------------------------------------------------------
    // main() — quick self-test; run this class directly to
    // verify the DB connection before running the full app.
    // --------------------------------------------------------
    public static void main(String[] args) {
        try {
            Connection conn = getConnection();
            if (conn != null) {
                System.out.println("DBConnection test: Connected to MySQL successfully!");
                conn.close(); // Always close the connection when done
            }
        } catch (SQLException e) {
            System.out.println("DBConnection test: Connection FAILED.");
            System.out.println("Error: " + e.getMessage());
        }
    }
}
