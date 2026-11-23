
package dao;

import model.Scheme;
import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.ArrayList;
import java.util.List;

public class SchemeDAO {

    // Convert database row into Scheme object
    private Scheme mapScheme(ResultSet rs) throws SQLException {

        Scheme scheme = new Scheme(
                rs.getInt("scheme_id"),
                rs.getString("scheme_name"),
                rs.getString("description"),
                rs.getInt("min_age"),
                rs.getInt("max_age"),
                rs.getString("profession"),
                rs.getString("state"),
                rs.getDouble("income_limit"),
                rs.getString("education_level"),
                rs.getString("category"),
                rs.getString("benefits"),
                rs.getString("application_link"));

        scheme.setEligibleGender(rs.getString("eligible_gender"));
        scheme.setEligibleMaritalStatus(
                rs.getString("eligible_marital_status"));

        return scheme;
    }

    // 1. Retrieve all government schemes
    public List<Scheme> getAllSchemes() {

        List<Scheme> schemes = new ArrayList<>();
        String sql = "SELECT * FROM schemes";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql);
                ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                schemes.add(mapScheme(rs));
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving schemes: " + e.getMessage());
        }

        return schemes;
    }

    // 2. Retrieve one scheme using its ID
    public Scheme getSchemeById(int schemeId) {

        String sql = "SELECT * FROM schemes WHERE scheme_id = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, schemeId);

            try (ResultSet rs = pstmt.executeQuery()) {

                if (rs.next()) {
                    return mapScheme(rs);
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving scheme: " + e.getMessage());
        }

        return null;
    }

    // 3. Retrieve schemes by category
    public List<Scheme> getSchemesByCategory(String category) {

        List<Scheme> schemes = new ArrayList<>();
        String sql = "SELECT * FROM schemes WHERE category = ?";

        try (Connection conn = DBConnection.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, category);

            try (ResultSet rs = pstmt.executeQuery()) {

                while (rs.next()) {
                    schemes.add(mapScheme(rs));
                }
            }

        } catch (SQLException e) {
            System.out.println("Error retrieving schemes by category: "
                    + e.getMessage());
        }

        return schemes;
    }
}
