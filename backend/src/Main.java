
import auth.AuthService;
import dao.SavedSchemeDAO;
import dao.SchemeDAO;
import dao.UserDAO;
import model.Scheme;
import model.User;
import service.RecommendationService;

import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.LinkedHashSet;

public class Main {

    private static final Scanner sc = new Scanner(System.in);

    private static final AuthService authService = new AuthService();
    private static final UserDAO userDAO = new UserDAO();
    private static final SchemeDAO schemeDAO = new SchemeDAO();
    private static final SavedSchemeDAO savedSchemeDAO = new SavedSchemeDAO();
    private static final RecommendationService recommendationService = new RecommendationService();

    private static AuthService.LoginResult currentUser = null;

    public static void main(String[] args) {

        System.out.println("\n========================================");
        System.out.println("          WELCOME TO JANSETU");
        System.out.println("  Government Scheme Discovery System");
        System.out.println("========================================");

        boolean running = true;

        while (running) {

            if (currentUser == null) {
                running = showGuestMenu();
            } else {
                showUserMenu();
            }
        }

        System.out.println("\nThank you for using JANSETU!");
        sc.close();
    }

    // ==========================================
    // GUEST MENU
    // ==========================================

    private static boolean showGuestMenu() {

        System.out.println("\n--------------- MAIN MENU ---------------");
        System.out.println("1. Register New Account");
        System.out.println("2. Login");
        System.out.println("3. Explore All Government Schemes");
        System.out.println("4. Search Schemes by Category");
        System.out.println("0. Exit");
        System.out.println("-----------------------------------------");

        int choice = readInt("Enter your choice: ", 0, 4);

        switch (choice) {

            case 1:
                register();
                break;

            case 2:
                login();
                break;

            case 3:
                exploreAllSchemes();
                break;

            case 4:
                searchByCategory();
                break;

            case 0:
                return false;

            default:
                System.out.println("Invalid choice.");
        }

        return true;
    }

    // ==========================================
    // REGISTER
    // ==========================================

    private static void register() {

        System.out.println("\n========== CREATE ACCOUNT ==========");

        String name = readNonEmpty("Enter your full name: ");
        String email = readNonEmpty("Enter your email: ");
        String password = readNonEmpty("Create password (minimum 8 characters): ");

        boolean registered = authService.register(name, email, password);

        if (registered) {
            System.out.println("\nAccount registered successfully!");
            System.out.println("You can now login using your email and password.");
        } else {
            System.out.println("\nRegistration failed.");
            System.out.println("Please check your details or try another email.");
        }
    }

    // ==========================================
    // LOGIN
    // ==========================================

    private static void login() {

        System.out.println("\n============== LOGIN ==============");

        String email = readNonEmpty("Enter email: ");
        String password = readNonEmpty("Enter password: ");

        currentUser = authService.login(email, password);

        if (currentUser != null) {
            System.out.println("\nLogin successful!");
            System.out.println("Welcome, " + currentUser.getFullName() + "!");
        } else {
            System.out.println("\nInvalid email or password.");
        }
    }

    // ==========================================
    // LOGGED-IN USER MENU
    // ==========================================

    private static void showUserMenu() {

        System.out.println("\n========================================");
        System.out.println("             JANSETU DASHBOARD");
        System.out.println("       Welcome, " + currentUser.getFullName());
        System.out.println("========================================");

        System.out.println("1. Explore All Government Schemes");
        System.out.println("2. Search Schemes by Category");
        System.out.println("3. Check Eligibility & Recommendations");
        System.out.println("4. View Applicant Profile");
        System.out.println("5. Update Applicant Profile");
        System.out.println("6. View Scheme Details");
        System.out.println("7. Save a Scheme");
        System.out.println("8. View Saved Schemes");
        System.out.println("9. Remove Saved Scheme");
        System.out.println("10. Logout");
        System.out.println("0. Exit");

        System.out.println("----------------------------------------");

        int choice = readInt("Enter your choice: ", 0, 10);

        switch (choice) {

            case 1:
                exploreAllSchemes();
                break;

            case 2:
                searchByCategory();
                break;

            case 3:
                showRecommendations();
                break;

            case 4:
                viewProfile();
                break;

            case 5:
                updateProfile();
                break;

            case 6:
                viewSchemeDetails();
                break;

            case 7:
                saveScheme();
                break;

            case 8:
                viewSavedSchemes();
                break;

            case 9:
                removeSavedScheme();
                break;

            case 10:
                logout();
                break;

            case 0:
                logout();
                System.out.println("Exiting JANSETU.");
                System.exit(0);
                break;

            default:
                System.out.println("Invalid choice.");
        }
    }

    // ==========================================
    // EXPLORE ALL SCHEMES
    // ==========================================

    private static void exploreAllSchemes() {

        System.out.println("\n========== ALL GOVERNMENT SCHEMES ==========");

        List<Scheme> schemes = schemeDAO.getAllSchemes();

        if (schemes == null || schemes.isEmpty()) {
            System.out.println("No schemes found in the database.");
            return;
        }

        displaySchemes(schemes);
    }

    // ==========================================
    // SEARCH BY CATEGORY
    // ==========================================

    private static void searchByCategory() {

        System.out.println("\n========== SEARCH BY CATEGORY ==========");

        List<Scheme> allSchemes = schemeDAO.getAllSchemes();

        if (allSchemes == null || allSchemes.isEmpty()) {
            System.out.println("No schemes available.");
            return;
        }

        Set<String> categories = new LinkedHashSet<>();

        for (Scheme scheme : allSchemes) {
            if (scheme.getCategory() != null) {
                categories.add(scheme.getCategory());
            }
        }

        System.out.println("\nAvailable Categories:");

        for (String category : categories) {
            System.out.println("- " + category);
        }

        String category = readNonEmpty("\nEnter category: ");

        List<Scheme> results = schemeDAO.getSchemesByCategory(category);

        if (results == null || results.isEmpty()) {
            System.out.println("No schemes found in this category.");
            return;
        }

        displaySchemes(results);
    }

    // ==========================================
    // DISPLAY SCHEMES
    // ==========================================

    private static void displaySchemes(List<Scheme> schemes) {

        System.out.println("\nTotal schemes found: " + schemes.size());

        for (Scheme scheme : schemes) {

            System.out.println("\n----------------------------------------");
            System.out.println("Scheme ID   : " + scheme.getSchemeId());
            System.out.println("Name        : " + scheme.getSchemeName());
            System.out.println("Category    : " + scheme.getCategory());
            System.out.println("Description : " + scheme.getDescription());
            System.out.println("Benefits    : " + scheme.getBenefits());
            System.out.println("----------------------------------------");
        }
    }

    // ==========================================
    // SCHEME DETAILS
    // ==========================================

    private static void viewSchemeDetails() {

        System.out.println("\n========== SCHEME DETAILS ==========");

        int schemeId = readInt("Enter Scheme ID: ", 1, Integer.MAX_VALUE);

        Scheme scheme = schemeDAO.getSchemeById(schemeId);

        if (scheme == null) {
            System.out.println("Scheme not found.");
            return;
        }

        System.out.println("\n========== " + scheme.getSchemeName() + " ==========");
        System.out.println("Scheme ID          : " + scheme.getSchemeId());
        System.out.println("Description        : " + scheme.getDescription());
        System.out.println("Category           : " + scheme.getCategory());
        System.out.println("Benefits           : " + scheme.getBenefits());
        System.out.println("Minimum Age        : " + scheme.getMinAge());
        System.out.println("Maximum Age        : " + scheme.getMaxAge());
        System.out.println("Profession         : " + scheme.getProfession());
        System.out.println("State              : " + scheme.getState());
        System.out.println("Income Limit       : " + scheme.getIncomeLimit());
        System.out.println("Education          : " + scheme.getEducationLevel());
        System.out.println("Eligible Gender    : " + scheme.getEligibleGender());
        System.out.println("Marital Status     : " + scheme.getEligibleMaritalStatus());
        System.out.println("Application Link   : " + scheme.getApplicationLink());
        System.out.println("========================================");
    }

    // ==========================================
    // GET OR CREATE APPLICANT PROFILE
    // ==========================================

    private static User getOrCreateProfile() {

        int accountId = currentUser.getAccountId();

        User user = userDAO.getUserByAccountId(accountId);

        if (user == null) {

            System.out.println("\nNo applicant profile found.");
            System.out.println("Please enter your applicant details.");

            user = collectApplicantDetails();

            boolean saved = userDAO.saveOrUpdateUser(accountId, user);

            if (saved) {
                System.out.println("Applicant profile created successfully!");
                user.setAccountId(accountId);
            } else {
                System.out.println("Could not save applicant profile.");
                return null;
            }
        }

        return user;
    }

    // ==========================================
    // COLLECT APPLICANT DETAILS
    // ==========================================

    private static User collectApplicantDetails() {

        String name = readNonEmpty("Full name: ");
        int age = readInt("Age: ", 1, 120);
        String state = readNonEmpty("State: ");
        String profession = readNonEmpty("Profession: ");
        double income = readDouble("Annual income (in rupees): ", 0);
        String education = readNonEmpty("Education level: ");
        String gender = readNonEmpty("Gender: ");
        String maritalStatus = readNonEmpty("Marital status: ");

        return new User(
                0,
                name,
                age,
                state,
                profession,
                income,
                education,
                gender,
                maritalStatus);
    }

    // ==========================================
    // RECOMMENDATIONS
    // ==========================================

    private static void showRecommendations() {

        System.out.println("\n====== ELIGIBLE SCHEME RECOMMENDATIONS ======");

        User user = getOrCreateProfile();

        if (user == null) {
            return;
        }

        List<Scheme> recommendations = recommendationService.getRecommendedSchemes(user);

        if (recommendations == null || recommendations.isEmpty()) {
            System.out.println("No matching schemes found for your profile.");
            return;
        }

        System.out.println("\nWe found " + recommendations.size()
                + " eligible scheme(s) for you!");

        displaySchemes(recommendations);
    }

    // ==========================================
    // VIEW PROFILE
    // ==========================================

    private static void viewProfile() {

        System.out.println("\n========== APPLICANT PROFILE ==========");

        User user = getOrCreateProfile();

        if (user == null) {
            return;
        }

        System.out.println("User ID         : " + user.getUserId());
        System.out.println("Account ID      : " + user.getAccountId());
        System.out.println("Name            : " + user.getName());
        System.out.println("Age             : " + user.getAge());
        System.out.println("State           : " + user.getState());
        System.out.println("Profession      : " + user.getProfession());
        System.out.println("Annual Income   : " + user.getIncome());
        System.out.println("Education       : " + user.getEducationLevel());
        System.out.println("Gender          : " + user.getGender());
        System.out.println("Marital Status  : " + user.getMaritalStatus());

        System.out.println("=======================================");
    }

    // ==========================================
    // UPDATE PROFILE
    // ==========================================

    private static void updateProfile() {

        System.out.println("\n========== UPDATE APPLICANT PROFILE ==========");

        User user = getOrCreateProfile();

        if (user == null) {
            return;
        }

        System.out.println("Enter your updated details:");

        User updatedUser = collectApplicantDetails();

        boolean updated = userDAO.saveOrUpdateUser(
                currentUser.getAccountId(),
                updatedUser);

        if (updated) {
            System.out.println("\nProfile updated successfully!");
        } else {
            System.out.println("\nProfile update failed.");
        }
    }

    // ==========================================
    // SAVE SCHEME
    // ==========================================

    private static void saveScheme() {

        System.out.println("\n========== SAVE A SCHEME ==========");

        int schemeId = readInt("Enter Scheme ID to save: ", 1, Integer.MAX_VALUE);

        Scheme scheme = schemeDAO.getSchemeById(schemeId);

        if (scheme == null) {
            System.out.println("Scheme not found.");
            return;
        }

        boolean saved = savedSchemeDAO.saveScheme(
                currentUser.getAccountId(),
                schemeId);

        if (saved) {
            System.out.println("Scheme saved successfully!");
        } else {
            System.out.println("Could not save scheme. It may already be saved.");
        }
    }

    // ==========================================
    // VIEW SAVED SCHEMES
    // ==========================================

    private static void viewSavedSchemes() {

        System.out.println("\n========== YOUR SAVED SCHEMES ==========");

        List<Scheme> savedSchemes = savedSchemeDAO.getSavedSchemes(currentUser.getAccountId());

        if (savedSchemes == null || savedSchemes.isEmpty()) {
            System.out.println("You have not saved any schemes yet.");
            return;
        }

        displaySchemes(savedSchemes);
    }

    // ==========================================
    // REMOVE SAVED SCHEME
    // ==========================================

    private static void removeSavedScheme() {

        System.out.println("\n========== REMOVE SAVED SCHEME ==========");

        int schemeId = readInt("Enter Scheme ID to remove: ", 1, Integer.MAX_VALUE);

        boolean removed = savedSchemeDAO.removeSavedScheme(
                currentUser.getAccountId(),
                schemeId);

        if (removed) {
            System.out.println("Scheme removed from your saved list.");
        } else {
            System.out.println("Scheme was not found in your saved list.");
        }
    }

    // ==========================================
    // LOGOUT
    // ==========================================

    private static void logout() {

        if (currentUser != null) {

            boolean loggedOut = authService.logout(currentUser.getSessionToken());

            if (loggedOut) {
                System.out.println("\nLogged out successfully.");
            } else {
                System.out.println("\nSession ended.");
            }

            currentUser = null;
        }
    }

    // ==========================================
    // INPUT HELPER METHODS
    // ==========================================

    private static String readNonEmpty(String message) {

        while (true) {

            System.out.print(message);
            String value = sc.nextLine().trim();

            if (!value.isEmpty()) {
                return value;
            }

            System.out.println("This field cannot be empty. Try again.");
        }
    }

    private static int readInt(String message, int min, int max) {

        while (true) {

            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.println("Enter a number between "
                        + min + " and " + max + ".");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid whole number.");
            }
        }
    }

    private static double readDouble(String message, double min) {

        while (true) {

            System.out.print(message);
            String input = sc.nextLine().trim();

            try {
                double value = Double.parseDouble(input);

                if (Double.isFinite(value) && value >= min) {
                    return value;
                }

                System.out.println("Enter a valid amount greater than or equal to "
                        + min + ".");

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }
    }
}
