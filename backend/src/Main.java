
import dao.UserDAO;
import model.User;
import model.Scheme;
import service.RecommendationService;

import java.util.List;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);

        System.out.println("========================================");
        System.out.println("          WELCOME TO JANSETU");
        System.out.println("   Government Scheme Recommendation");
        System.out.println("========================================");

        try {

            System.out.println("\nEnter Your Details");

            String name = readNonEmpty(sc, "Name: ");

            int age = readAge(sc);

            String state = readChoice(
                    sc,
                    "State (e.g. Punjab, Haryana, Madhya Pradesh): ",
                    "Punjab",
                    "Haryana",
                    "Himachal Pradesh",
                    "Madhya Pradesh",
                    "Rajasthan",
                    "Uttar Pradesh",
                    "Delhi",
                    "Maharashtra",
                    "Bihar",
                    "Gujarat",
                    "All");

            String profession = readNonEmpty(sc, "Profession: ");

            double income = readIncome(sc);

            String education = readNonEmpty(sc, "Education Level: ");

            String gender = readChoice(
                    sc, "Gender (Male/Female/Other): ",
                    "Male", "Female", "Other");

            String maritalStatus = readChoice(
                    sc, "Marital Status (Married/Unmarried): ",
                    "Married", "Unmarried");

            // Create applicant object
            User user = new User(
                    0, name, age, state, profession,
                    income, education, gender, maritalStatus);

            System.out.println("\nSaving applicant details...");

            UserDAO userDAO = new UserDAO();

            boolean saved = userDAO.saveUser(user);

            if (!saved) {
                System.out.println("Unable to save applicant details.");
                System.out.println("Please try again later.");
                return;
            }
            System.out.println("\nFinding suitable government schemes...");

            RecommendationService service = new RecommendationService();

            List<Scheme> recommendations = service.getRecommendedSchemes(user);

            System.out.println("\n========================================");
            System.out.println("       JANSETU RECOMMENDATIONS");
            System.out.println("========================================");

            if (recommendations.isEmpty()) {

                System.out.println("Sorry! No matching schemes found.");

            } else {

                for (Scheme scheme : recommendations) {

                    System.out.println("\nScheme: "
                            + scheme.getSchemeName());

                    System.out.println("Category: "
                            + scheme.getCategory());

                    System.out.println("Description: "
                            + scheme.getDescription());

                    System.out.println("Benefits: "
                            + scheme.getBenefits());

                    System.out.println("Application Link: "
                            + scheme.getApplicationLink());

                    System.out.println("--------------------------------");
                }
            }

            System.out.println("\nTotal schemes found: "
                    + recommendations.size());

            System.out.println("\nThank you for using Jansetu!");

        } catch (Exception e) {

            System.out.println("An error occurred: " + e.getMessage());

        } finally {

            sc.close();
        }
    }

    // Validate non-empty text
    private static String readNonEmpty(Scanner sc, String message) {

        while (true) {

            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("Input cannot be empty. Try again.");
        }
    }

    // Validate age
    private static int readAge(Scanner sc) {

        while (true) {

            System.out.print("Age: ");

            try {

                int age = Integer.parseInt(sc.nextLine().trim());

                if (age > 0 && age <= 120) {
                    return age;
                }

                System.out.println("Enter age between 1 and 120.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    // Validate income
    private static double readIncome(Scanner sc) {

        while (true) {

            System.out.print("Annual Income: ");

            try {

                double income = Double.parseDouble(sc.nextLine().trim());

                if (Double.isFinite(income) && income >= 0) {
                    return income;
                }

                System.out.println("Income cannot be negative.");

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid income.");
            }
        }
    }

    // Validate gender and marital status
    private static String readChoice(
            Scanner sc, String message, String... choices) {

        while (true) {

            System.out.print(message);
            String value = sc.nextLine().trim();

            for (String choice : choices) {

                if (choice.equalsIgnoreCase(value)) {
                    return choice;
                }
            }

            System.out.println("Invalid choice. Please try again.");
        }
    }
}
