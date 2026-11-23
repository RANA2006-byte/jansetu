
package auth;

import util.DBConnection;

import java.sql.*;
import java.security.SecureRandom;
import java.util.Base64;

public class AuthDAO {

    // Register a new account
    public boolean register(
            String fullName,
            String email,
            String passwordHash,
            String passwordSalt) {

        String sql = """
                INSERT INTO accounts
                (full_name, email, password_hash, password_salt)
                VALUES (?, ?, ?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, fullName);
            statement.setString(2, email);
            statement.setString(3, passwordHash);
            statement.setString(4, passwordSalt);

            return statement.executeUpdate() == 1;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.out.println("Email already registered.");
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Find an account using email
    public ResultSet getAccountByEmail(
            Connection connection, String email) throws SQLException {

        String sql = """
                SELECT account_id, full_name, email,
                       password_hash, password_salt
                FROM accounts
                WHERE email = ?
                """;

        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, email);

        return statement.executeQuery();
    }

    // Create a secure random session token
    public String createSession(int accountId) {

        byte[] tokenBytes = new byte[32];
        new SecureRandom().nextBytes(tokenBytes);

        String token = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(tokenBytes);

        String tokenHash = hashToken(token);

        String sql = """
                INSERT INTO user_sessions
                (account_id, token_hash, expires_at)
                VALUES (?, ?, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 1 DAY))
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setString(2, tokenHash);

            statement.executeUpdate();

            return token;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hash session token before storing it
    private String hashToken(String token) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    token.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            StringBuilder result = new StringBuilder();

            for (byte b : hash) {
                result.append(String.format("%02x", b));
            }

            return result.toString();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    // Look up the account linked to a valid session token.
    // Returns a String array [fullName, email] or null if not found.
    public String[] getAccountByToken(String token) {

        if (token == null || token.isBlank()) {
            return null;
        }

        String sql = """
                SELECT a.full_name, a.email
                FROM user_sessions s
                JOIN accounts a ON a.account_id = s.account_id
                WHERE s.token_hash = ?
                  AND s.expires_at > UTC_TIMESTAMP()
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashToken(token));

            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return new String[]{ rs.getString("full_name"), rs.getString("email") };
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    // Validate an active session
    public boolean isSessionValid(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        String sql = """
                SELECT session_id
                FROM user_sessions
                WHERE token_hash = ?
                  AND expires_at > UTC_TIMESTAMP()
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashToken(token));

            try (ResultSet rs = statement.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Invalidate session during logout
    public boolean deleteSession(String token) {

        if (token == null || token.isBlank()) {
            return false;
        }

        String sql = "DELETE FROM user_sessions WHERE token_hash = ?";

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, hashToken(token));

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
