
package dao;

import model.User;
import util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // 1. Save applicant details into MySQL
    // Used by the eligibility recommendation system
    public boolean saveUser(User user) {

        if (user == null) {
            return false;
        }

        String query = """
                INSERT INTO users
                (name, age, state, profession, income,
                 education_level, gender, marital_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setString(1, user.getName());
            ps.setInt(2, user.getAge());
            ps.setString(3, user.getState());
            ps.setString(4, user.getProfession());
            ps.setDouble(5, user.getIncome());
            ps.setString(6, user.getEducationLevel());
            ps.setString(7, user.getGender());
            ps.setString(8, user.getMaritalStatus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.out.println("Error saving applicant: " + e.getMessage());
            return false;
        }
    }

    // 2. Retrieve user using user ID
    public User getUserById(int userId) {

        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }

    // 3. Retrieve profile using registered account ID
    public User getUserByAccountId(int accountId) {

        String query = """
                SELECT *
                FROM users
                WHERE account_id = ?
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, accountId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return mapUser(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println(
                    "Error retrieving account profile: " + e.getMessage());
        }

        return null;
    }

    // 4. Create or update profile for a registered account
    public boolean saveOrUpdateUser(int accountId, User user) {

        if (accountId <= 0 || user == null) {
            return false;
        }

        String query = """
                INSERT INTO users
                (account_id, name, age, state, profession, income,
                 education_level, gender, marital_status)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)

                ON DUPLICATE KEY UPDATE
                    name = VALUES(name),
                    age = VALUES(age),
                    state = VALUES(state),
                    profession = VALUES(profession),
                    income = VALUES(income),
                    education_level = VALUES(education_level),
                    gender = VALUES(gender),
                    marital_status = VALUES(marital_status)
                """;

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, accountId);
            ps.setString(2, user.getName());
            ps.setInt(3, user.getAge());
            ps.setString(4, user.getState());
            ps.setString(5, user.getProfession());
            ps.setDouble(6, user.getIncome());
            ps.setString(7, user.getEducationLevel());
            ps.setString(8, user.getGender());
            ps.setString(9, user.getMaritalStatus());

            int rows = ps.executeUpdate();

            if (rows > 0) {
                user.setAccountId(accountId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.out.println(
                    "Error saving account profile: " + e.getMessage());
            return false;
        }
    }

    // 5. Retrieve all applicants
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();

        String query = "SELECT * FROM users";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                users.add(mapUser(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }

    // 6. Convert database record into User object
    private User mapUser(ResultSet rs) throws SQLException {

        User user = new User(
                rs.getInt("user_id"),
                rs.getString("name"),
                rs.getInt("age"),
                rs.getString("state"),
                rs.getString("profession"),
                rs.getDouble("income"),
                rs.getString("education_level"),
                rs.getString("gender"),
                rs.getString("marital_status"));

        int accountId = rs.getInt("account_id");

        user.setAccountId(
                rs.wasNull() ? 0 : accountId);

        return user;
    }
}
