
package util;

import dao.SchemeDAO;
import model.Scheme;
import java.util.List;

public class SchemeDAOTest {

    public static void main(String[] args) {

        SchemeDAO schemeDAO = new SchemeDAO();

        // TEST 1: Retrieve all schemes
        System.out.println("\n===== ALL SCHEMES =====");

        List<Scheme> schemes = schemeDAO.getAllSchemes();

        for (Scheme scheme : schemes) {
            System.out.println(scheme);
        }

        System.out.println("Total schemes: " + schemes.size());

        // TEST 2: Retrieve scheme by ID
        System.out.println("\n===== SCHEME BY ID =====");

        Scheme scheme = schemeDAO.getSchemeById(1);

        if (scheme != null) {
            System.out.println(scheme);
        } else {
            System.out.println("Scheme not found.");
        }

        // TEST 3: Retrieve schemes by category
        System.out.println("\n===== AGRICULTURE SCHEMES =====");

        List<Scheme> agricultureSchemes = schemeDAO.getSchemesByCategory("Agriculture");

        for (Scheme s : agricultureSchemes) {
            System.out.println(s);
        }

        System.out.println(
                "Agriculture schemes found: " +
                        agricultureSchemes.size());
    }
}
