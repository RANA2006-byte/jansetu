
package dao;

import model.User;
import util.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {

    // Save user details into MySQL
    public boolean saveUser(User user) {

        String query = "INSERT INTO users " +
                "(name, age, state, profession, income, education_level, gender, marital_status) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

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

            int rows = ps.executeUpdate();

            if (rows > 0) {
                System.out.println("User saved successfully!");
                return true;
            }

            return false;

        } catch (SQLException e) {
            System.out.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    // Get user using ID
    public User getUserById(int userId) {

        String query = "SELECT * FROM users WHERE user_id = ?";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query)) {

            ps.setInt(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new User(
                            rs.getInt("user_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("state"),
                            rs.getString("profession"),
                            rs.getDouble("income"),
                            rs.getString("education_level"),
                            rs.getString("gender"),
                            rs.getString("marital_status"));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving user: " + e.getMessage());
        }

        return null;
    }

    // Get all users
    public List<User> getAllUsers() {

        List<User> users = new ArrayList<>();
        String query = "SELECT * FROM users";

        try (Connection con = DBConnection.getConnection();
                PreparedStatement ps = con.prepareStatement(query);
                ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

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

                users.add(user);
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving users: " + e.getMessage());
        }

        return users;
    }
}
