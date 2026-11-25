
package dao;

import util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.Scheme;

public class SavedSchemeDAO {

    // Save a scheme for a registered account
    public boolean saveScheme(int accountId, int schemeId) {

        String sql = """
                INSERT INTO saved_schemes (account_id, scheme_id)
                VALUES (?, ?)
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setInt(2, schemeId);

            return statement.executeUpdate() == 1;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Get all saved schemes for an account
    public List<Scheme> getSavedSchemes(int accountId) {

        List<Scheme> schemes = new ArrayList<>();

        String sql = """
                SELECT s.*
                FROM schemes s
                JOIN saved_schemes ss ON s.scheme_id = ss.scheme_id
                WHERE ss.account_id = ?
                ORDER BY ss.saved_at DESC
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);

            try (ResultSet rs = statement.executeQuery()) {

                while (rs.next()) {
                    Scheme scheme = new Scheme();

                    scheme.setSchemeId(rs.getInt("scheme_id"));
                    scheme.setSchemeName(rs.getString("scheme_name"));
                    scheme.setDescription(rs.getString("description"));
                    scheme.setMinAge(rs.getInt("min_age"));
                    scheme.setMaxAge(rs.getInt("max_age"));
                    scheme.setProfession(rs.getString("profession"));
                    scheme.setState(rs.getString("state"));
                    scheme.setIncomeLimit(rs.getDouble("income_limit"));
                    scheme.setEducationLevel(rs.getString("education_level"));
                    scheme.setCategory(rs.getString("category"));
                    scheme.setBenefits(rs.getString("benefits"));
                    scheme.setApplicationLink(rs.getString("application_link"));
                    scheme.setEligibleGender(rs.getString("eligible_gender"));
                    scheme.setEligibleMaritalStatus(rs.getString("eligible_marital_status"));

                    schemes.add(scheme);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return schemes;
    }

    // Remove a saved scheme
    public boolean removeSavedScheme(int accountId, int schemeId) {

        String sql = """
                DELETE FROM saved_schemes
                WHERE account_id = ? AND scheme_id = ?
                """;

        try (Connection connection = DBConnection.getConnection();
                PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, accountId);
            statement.setInt(2, schemeId);

            return statement.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
