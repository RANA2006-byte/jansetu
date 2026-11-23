
package auth;

import util.DBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class AuthService {

    private final AuthDAO authDAO = new AuthDAO();

    // Result returned after successful login
    public static class LoginResult {
        private final int accountId;
        private final String fullName;
        private final String email;
        private final String sessionToken;

        public LoginResult(int accountId, String fullName,
                String email, String sessionToken) {
            this.accountId = accountId;
            this.fullName = fullName;
            this.email = email;
            this.sessionToken = sessionToken;
        }

        public int getAccountId() {
            return accountId;
        }

        public String getFullName() {
            return fullName;
        }

        public String getEmail() {
            return email;
        }

        public String getSessionToken() {
            return sessionToken;
        }
    }

    // Register a new user
    public boolean register(String fullName, String email,
            String password) {

        if (fullName == null || fullName.trim().isEmpty()) {
            return false;
        }

        if (email == null ||
                !email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            return false;
        }

        if (password == null || password.length() < 8) {
            return false;
        }

        fullName = fullName.trim();
        email = email.trim().toLowerCase();

        String salt = PasswordUtil.generateSalt();
        String passwordHash = PasswordUtil.hashPassword(password, salt);

        return authDAO.register(
                fullName, email, passwordHash, salt);
    }

    // Login an existing user
    public LoginResult login(String email, String password) {

        if (email == null || password == null ||
                email.trim().isEmpty() || password.isEmpty()) {
            return null;
        }

        email = email.trim().toLowerCase();

        try (Connection connection = DBConnection.getConnection();
                ResultSet rs = authDAO.getAccountByEmail(
                        connection, email)) {

            if (!rs.next()) {
                return null;
            }

            int accountId = rs.getInt("account_id");
            String fullName = rs.getString("full_name");
            String storedEmail = rs.getString("email");
            String storedHash = rs.getString("password_hash");
            String storedSalt = rs.getString("password_salt");

            boolean passwordCorrect = PasswordUtil.verifyPassword(
                    password, storedSalt, storedHash);

            if (!passwordCorrect) {
                return null;
            }

            // Close the query statement before creating the session
            Statement statement = rs.getStatement();
            if (statement != null) {
                statement.close();
            }

            String token = authDAO.createSession(accountId);

            if (token == null) {
                return null;
            }

            return new LoginResult(
                    accountId, fullName, storedEmail, token);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Validate an existing session
    public boolean validateSession(String token) {
        return authDAO.isSessionValid(token);
    }

    // Return [fullName, email] for the account that owns this session token.
    // Returns null if the token is invalid or expired.
    public String[] getAccountByToken(String token) {
        return authDAO.getAccountByToken(token);
    }

    // Logout
    public boolean logout(String token) {
        return authDAO.deleteSession(token);
    }
}
