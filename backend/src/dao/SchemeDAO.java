package dao; // tells Java this file belongs to the "dao" package (folder)

// ============================================================
// SchemeDAO.java - Data Access Object for the 'schemes' table
//
// All SQL queries that touch the 'schemes' table go here.
// No other class should write scheme-related SQL directly.
//
// NOTE: This is a foundation stub only.
//       SQL logic will be added in the next stage.
// ============================================================

import model.Scheme;

import java.util.List;

public class SchemeDAO {

    // --------------------------------------------------------
    // getAllSchemes() — will return every scheme from the DB
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public List<Scheme> getAllSchemes() {
        // TODO: Write SELECT * FROM schemes here
        System.out.println("SchemeDAO.getAllSchemes() - Not implemented yet.");
        return null;
    }

    // --------------------------------------------------------
    // getSchemeById() — will fetch one scheme by its ID
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public Scheme getSchemeById(int schemeId) {
        // TODO: Write SELECT * FROM schemes WHERE scheme_id = ? here
        System.out.println("SchemeDAO.getSchemeById() - Not implemented yet.");
        return null;
    }

    // --------------------------------------------------------
    // getSchemesByCategory() — will filter schemes by category
    // e.g. "Agriculture", "Education", "Health"
    // (Implementation coming in next stage)
    // --------------------------------------------------------
    public List<Scheme> getSchemesByCategory(String category) {
        // TODO: Write SELECT * FROM schemes WHERE category = ? here
        System.out.println("SchemeDAO.getSchemesByCategory() - Not implemented yet.");
        return null;
    }
}
