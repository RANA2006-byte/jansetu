
package service;

import model.Scheme;
import model.User;
import dao.SchemeDAO;

import java.util.ArrayList;
import java.util.List;

public class RecommendationService {

    private SchemeDAO schemeDAO;

    public RecommendationService() {
        schemeDAO = new SchemeDAO();
    }

    public List<Scheme> getRecommendedSchemes(User user) {

        List<Scheme> recommendedSchemes = new ArrayList<>();

        if (user == null) {
            System.out.println("Invalid applicant details.");
            return recommendedSchemes;
        }

        List<Scheme> allSchemes = schemeDAO.getAllSchemes();

        for (Scheme scheme : allSchemes) {

            if (scheme == null) {
                continue;
            }

            // Check age
            boolean ageEligible = user.getAge() >= scheme.getMinAge() &&
                    user.getAge() <= scheme.getMaxAge();

            // Check profession
            boolean professionEligible = matches(scheme.getProfession(), "Any") ||
                    matches(scheme.getProfession(), user.getProfession());

            // Check state
            boolean stateEligible = matches(scheme.getState(), "All") ||
                    matches(scheme.getState(), user.getState());

            // Check income
            boolean incomeEligible = scheme.getIncomeLimit() <= 0 ||
                    user.getIncome() <= scheme.getIncomeLimit();

            // Check education
            int userEducation = getEducationRank(user.getEducationLevel());
            int requiredEducation = getEducationRank(scheme.getEducationLevel());

            boolean educationEligible = matches(scheme.getEducationLevel(), "Any") ||
                    (userEducation > 0 &&
                            requiredEducation > 0 &&
                            userEducation >= requiredEducation);

            // Check gender
            boolean genderEligible = matches(scheme.getEligibleGender(), "Any") ||
                    matches(scheme.getEligibleGender(), user.getGender());

            // Check marital status
            boolean maritalStatusEligible = matches(scheme.getEligibleMaritalStatus(), "Any") ||
                    matches(scheme.getEligibleMaritalStatus(),
                            user.getMaritalStatus());

            // Recommend only when every condition matches
            if (ageEligible &&
                    professionEligible &&
                    stateEligible &&
                    incomeEligible &&
                    educationEligible &&
                    genderEligible &&
                    maritalStatusEligible) {

                recommendedSchemes.add(scheme);
            }
        }

        return recommendedSchemes;
    }

    // Safely compare two strings
    private boolean matches(String value1, String value2) {

        if (value1 == null || value2 == null) {
            return false;
        }

        return value1.trim().equalsIgnoreCase(value2.trim());
    }

    // Education hierarchy
    private int getEducationRank(String education) {

        if (education == null) {
            return 0;
        }

        switch (education.trim().toLowerCase()) {

            case "below 10th":
                return 1;

            case "10th pass":
                return 2;

            case "12th pass":
                return 3;

            case "diploma":
                return 4;

            case "graduate":
                return 5;

            case "postgraduate":
                return 6;

            case "doctorate":
                return 7;

            default:
                return 0;
        }
    }
}
