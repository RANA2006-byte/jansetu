
package server;

import auth.AuthService;
import dao.UserDAO;
import model.User;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class UserProfileHandler {

    private static final AuthService authService = new AuthService();
    private static final UserDAO userDAO = new UserDAO();

    private static final String COOKIE_NAME = "JANSETU_SESSION";

    // Register profile API routes
    public static void registerRoutes(HttpServer server) {

        HttpContext profileContext = server.createContext(
                "/api/profile", exchange -> {

                    String method = exchange.getRequestMethod();

                    if ("OPTIONS".equalsIgnoreCase(method)) {
                        exchange.sendResponseHeaders(204, -1);
                        exchange.close();
                        return;
                    }

                    if ("GET".equalsIgnoreCase(method)) {
                        handleGetProfile(exchange);

                    } else if ("POST".equalsIgnoreCase(method)) {
                        handleSaveProfile(exchange);

                    } else {
                        send(exchange, 405,
                                "{\"error\":\"Method not allowed\"}");
                    }
                });

        // Configure CORS once when the route is registered
        JansetuServer.configureCors(profileContext);
    }

    // Extract session token from browser cookie
    private static String getToken(HttpExchange exchange) {

        String cookieHeader = exchange.getRequestHeaders()
                .getFirst("Cookie");

        if (cookieHeader == null) {
            return null;
        }

        for (String cookie : cookieHeader.split(";")) {

            String[] parts = cookie.trim().split("=", 2);

            if (parts.length == 2 &&
                    COOKIE_NAME.equals(parts[0])) {
                return parts[1];
            }
        }

        return null;
    }

    // Get account ID from authenticated session
    private static Integer getAuthenticatedAccountId(
            HttpExchange exchange) {

        String token = getToken(exchange);

        if (token == null) {
            return null;
        }

        return authService.getAccountIdByToken(token);
    }

    // GET /api/profile
    private static void handleGetProfile(
            HttpExchange exchange) throws IOException {

        Integer accountId = getAuthenticatedAccountId(exchange);

        if (accountId == null) {
            send(exchange, 401,
                    "{\"error\":\"Please log in first\"}");
            return;
        }

        User user = userDAO.getUserByAccountId(accountId);

        if (user == null) {
            send(exchange, 200,
                    "{\"exists\":false,\"message\":\"Profile not found\"}");
            return;
        }

        String json = "{"
                + "\"exists\":true,"
                + "\"userId\":" + user.getUserId() + ","
                + "\"accountId\":" + user.getAccountId() + ","
                + "\"name\":\"" + escapeJson(user.getName()) + "\","
                + "\"age\":" + user.getAge() + ","
                + "\"state\":\"" + escapeJson(user.getState()) + "\","
                + "\"profession\":\"" + escapeJson(user.getProfession()) + "\","
                + "\"income\":" + user.getIncome() + ","
                + "\"educationLevel\":\"" + escapeJson(user.getEducationLevel()) + "\","
                + "\"gender\":\"" + escapeJson(user.getGender()) + "\","
                + "\"maritalStatus\":\"" + escapeJson(user.getMaritalStatus()) + "\""
                + "}";

        send(exchange, 200, json);
    }

    // POST /api/profile
    private static void handleSaveProfile(
            HttpExchange exchange) throws IOException {

        Integer accountId = getAuthenticatedAccountId(exchange);

        if (accountId == null) {
            send(exchange, 401,
                    "{\"error\":\"Please log in first\"}");
            return;
        }

        String body = new String(
                exchange.getRequestBody().readAllBytes(),
                StandardCharsets.UTF_8);

        try {
            String name = getJsonString(body, "name");
            int age = Integer.parseInt(getJsonString(body, "age"));
            String state = getJsonString(body, "state");
            String profession = getJsonString(body, "profession");
            double income = Double.parseDouble(
                    getJsonString(body, "income"));
            String education = getJsonString(body, "educationLevel");
            String gender = getJsonString(body, "gender");
            String maritalStatus = getJsonString(body, "maritalStatus");

            if (name.isBlank()
                    || age < 1 || age > 120
                    || state.isBlank()
                    || profession.isBlank()
                    || !Double.isFinite(income)
                    || income < 0
                    || education.isBlank()
                    || gender.isBlank()
                    || maritalStatus.isBlank()) {

                send(exchange, 400,
                        "{\"error\":\"Invalid profile details\"}");
                return;
            }

            User user = new User();

            user.setAccountId(accountId);
            user.setName(name);
            user.setAge(age);
            user.setState(state);
            user.setProfession(profession);
            user.setIncome(income);
            user.setEducationLevel(education);
            user.setGender(gender);
            user.setMaritalStatus(maritalStatus);

            boolean saved = userDAO.saveOrUpdateUser(accountId, user);

            if (saved) {
                send(exchange, 200,
                        "{\"success\":true,\"message\":\"Profile saved successfully\"}");
            } else {
                send(exchange, 500,
                        "{\"error\":\"Could not save profile\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();

            send(exchange, 400,
                    "{\"error\":\"Invalid profile data\"}");
        }
    }

    // Read a string or numeric value from JSON
    private static String getJsonString(String json, String key) {

        String pattern = "\"" + key + "\"\\s*:\\s*";

        java.util.regex.Matcher matcher = java.util.regex.Pattern.compile(pattern).matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException(
                    "Missing field: " + key);
        }

        int index = matcher.end();

        if (index >= json.length()) {
            throw new IllegalArgumentException(
                    "Invalid field: " + key);
        }

        if (json.charAt(index) == '"') {

            StringBuilder value = new StringBuilder();
            boolean escaped = false;

            for (int i = index + 1; i < json.length(); i++) {

                char c = json.charAt(i);

                if (escaped) {
                    value.append(c);
                    escaped = false;

                } else if (c == '\\') {
                    escaped = true;

                } else if (c == '"') {
                    return value.toString();

                } else {
                    value.append(c);
                }
            }

            throw new IllegalArgumentException(
                    "Invalid JSON string");
        }

        int end = index;

        while (end < json.length()
                && ",}".indexOf(json.charAt(end)) == -1) {
            end++;
        }

        return json.substring(index, end).trim();
    }

    // Escape special characters before creating JSON
    private static String escapeJson(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    // Send JSON response
    private static void send(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type",
                "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (var output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }
}
