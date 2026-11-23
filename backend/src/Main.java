// ============================================================
// Main.java - Entry point of the Jansetu backend
//
// This is where Java starts executing your program.
// Every Java application must have exactly one main() method.
//
// At this stage, Main.java only:
//   1. Prints a startup message
//   2. Tests the MySQL database connection
//
// Run this class to verify your setup before building further.
// ============================================================

import util.DBConnection;

import java.sql.Connection;
import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {

        // Step 1: Print a startup banner
        System.out.println("========================================");
        System.out.println("   Welcome to Jansetu Backend v1.0");
        System.out.println("   Indian Government Scheme Recommender");
        System.out.println("========================================");
        System.out.println();

        // Step 2: Test the database connection
        System.out.println("Testing database connection...");

        try {
            // getConnection() talks to MySQL using the URL, username,
            // and password defined in DBConnection.java
            Connection connection = DBConnection.getConnection();

            // If we reach this line, the connection worked!
            System.out.println("Database connected successfully!");
            System.out.println("Jansetu backend started.");
            System.out.println();
            System.out.println("Ready to build features.");

            // Always close the connection when you are done.
            // Not closing causes resource leaks.
            connection.close();

        } catch (SQLException e) {
            // If something goes wrong (wrong password, MySQL not running,
            // wrong DB name), we land here.
            System.out.println("ERROR: Could not connect to the database.");
            System.out.println("Reason: " + e.getMessage());
            System.out.println();
            System.out.println("Checklist:");
            System.out.println("  1. Is MySQL running?");
            System.out.println("  2. Have you run database/schema.sql?");
            System.out.println("  3. Is the password correct in DBConnection.java?");
            System.out.println("  4. Is mysql-connector-j.jar on your classpath?");
        }
    }
}
