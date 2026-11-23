
package util;

import dao.UserDAO;
import model.User;
import model.Scheme;
import service.RecommendationService;

import java.util.List;

public class RecommendationTest {

    public static void main(String[] args) {

        UserDAO userDAO = new UserDAO();

        // Retrieve Rahul from MySQL
        User user = userDAO.getUserById(1);

        if (user == null) {
            System.out.println("User not found!");
            return;
        }

        System.out.println("===== APPLICANT DETAILS =====");
        System.out.println(user);

        // Generate recommendations
        RecommendationService service = new RecommendationService();

        List<Scheme> recommendations = service.getRecommendedSchemes(user);

        System.out.println("\n===== RECOMMENDED SCHEMES =====");

        for (Scheme scheme : recommendations) {
            System.out.println(scheme);
        }

        System.out.println("\nTotal recommended schemes: "
                + recommendations.size());
    }
}
