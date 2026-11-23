
package server;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.Filter;

import dao.UserDAO;
import dao.SchemeDAO;
import model.User;
import model.Scheme;
import service.RecommendationService;

import java.net.InetSocketAddress;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class JansetuServer {

    // Only allow the local frontend origins used during development
    private static final Set<String> ALLOWED_ORIGINS = Set.of(
            "http://localhost:5500",
            "http://127.0.0.1:5500",
            "http://localhost:5501",
            "http://127.0.0.1:5501");

    private static String getValue(String json, String key) {
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(key) +
                        "\"\\s*:\\s*(\"((?:\\\\.|[^\"])*)\"|(-?\\d+(?:\\.\\d+)?))");

        Matcher matcher = pattern.matcher(json);

        if (!matcher.find()) {
            throw new IllegalArgumentException("Missing field: " + key);
        }

        if (matcher.group(2) != null) {
            return matcher.group(2);
        }

        return matcher.group(3);
    }

    private static String escape(String value) {
        if (value == null)
            return "";

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // Configure CORS for browser requests, including session cookies
    public static void configureCors(HttpContext context) {

        context.getFilters().add(new Filter() {

            @Override
            public String description() {
                return "Jansetu CORS filter";
            }

            @Override
            public void doFilter(
                    HttpExchange exchange,
                    Chain chain) throws IOException {

                String origin = exchange.getRequestHeaders()
                        .getFirst("Origin");

                if (origin != null && ALLOWED_ORIGINS.contains(origin)) {

                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Origin", origin);

                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Credentials", "true");

                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Methods",
                            "GET, POST, OPTIONS");

                    exchange.getResponseHeaders().set(
                            "Access-Control-Allow-Headers",
                            "Content-Type");

                    exchange.getResponseHeaders().set(
                            "Vary", "Origin");
                }

                if ("OPTIONS".equals(exchange.getRequestMethod())) {
                    exchange.sendResponseHeaders(204, -1);
                    exchange.close();
                    return;
                }

                chain.doFilter(exchange);
            }
        });
    }

    private static void sendResponse(
            HttpExchange exchange,
            int status,
            String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    private static String schemeToJson(Scheme scheme) {
        return "{"
                + "\"schemeId\":" + scheme.getSchemeId() + ","
                + "\"schemeName\":\"" + escape(scheme.getSchemeName()) + "\","
                + "\"description\":\"" + escape(scheme.getDescription()) + "\","
                + "\"category\":\"" + escape(scheme.getCategory()) + "\","
                + "\"benefits\":\"" + escape(scheme.getBenefits()) + "\","
                + "\"applicationLink\":\"" + escape(scheme.getApplicationLink()) + "\""
                + "}";
    }

    private static String schemesToJson(List<Scheme> schemes) {
        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < schemes.size(); i++) {
            if (i > 0)
                json.append(",");
            json.append(schemeToJson(schemes.get(i)));
        }

        json.append("]");
        return json.toString();
    }

    public static void main(String[] args) {

        try {
            HttpServer server = HttpServer.create(
                    new InetSocketAddress(8080), 0);

            // GET: Return all schemes
            HttpContext schemesContext = server.createContext("/api/schemes", exchange -> {

                try {
                    if (!exchange.getRequestMethod().equals("GET")) {
                        sendResponse(exchange, 405,
                                "{\"error\":\"Method not allowed\"}");
                        return;
                    }

                    SchemeDAO schemeDAO = new SchemeDAO();
                    List<Scheme> schemes = schemeDAO.getAllSchemes();

                    sendResponse(exchange, 200,
                            schemesToJson(schemes));

                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500,
                            "{\"error\":\"Could not retrieve schemes\"}");
                }
            });

            configureCors(schemesContext);

            // POST: Save applicant and return recommendations
            HttpContext recommendationsContext = server.createContext("/api/recommendations", exchange -> {

                try {
                    if (!exchange.getRequestMethod().equals("POST")) {
                        sendResponse(exchange, 405,
                                "{\"error\":\"Method not allowed\"}");
                        return;
                    }

                    String body;

                    try (InputStream input = exchange.getRequestBody()) {
                        body = new String(
                                input.readAllBytes(),
                                StandardCharsets.UTF_8);
                    }

                    User user = new User(
                            0,
                            getValue(body, "name"),
                            Integer.parseInt(getValue(body, "age")),
                            getValue(body, "state"),
                            getValue(body, "profession"),
                            Double.parseDouble(getValue(body, "income")),
                            getValue(body, "educationLevel"),
                            getValue(body, "gender"),
                            getValue(body, "maritalStatus"));

                    if (user.getAge() < 1 || user.getAge() > 120
                            || !Double.isFinite(user.getIncome())
                            || user.getIncome() < 0) {

                        sendResponse(exchange, 400,
                                "{\"error\":\"Invalid age or income\"}");
                        return;
                    }

                    UserDAO userDAO = new UserDAO();

                    if (!userDAO.saveUser(user)) {
                        sendResponse(exchange, 500,
                                "{\"error\":\"Could not save applicant\"}");
                        return;
                    }

                    RecommendationService service = new RecommendationService();

                    List<Scheme> recommendations = service.getRecommendedSchemes(user);

                    String response = "{\"message\":\"Applicant saved successfully\","
                            + "\"recommendations\":"
                            + schemesToJson(recommendations) + "}";

                    sendResponse(exchange, 200, response);

                } catch (IllegalArgumentException e) {
                    sendResponse(exchange, 400,
                            "{\"error\":\"" + escape(e.getMessage()) + "\"}");

                } catch (Exception e) {
                    e.printStackTrace();
                    sendResponse(exchange, 500,
                            "{\"error\":\"Server error\"}");
                }
            });

            configureCors(recommendationsContext);

            // Register authentication endpoints
            AuthHandler.registerRoutes(server);

            server.setExecutor(null);
            server.start();

            System.out.println("==================================");
            System.out.println("Jansetu API Server Started!");
            System.out.println("Schemes: http://localhost:8080/api/schemes");
            System.out.println("Recommendations: http://localhost:8080/api/recommendations");
            System.out.println("Authentication: /api/auth/register");
            System.out.println("Authentication: /api/auth/login");
            System.out.println("Authentication: /api/auth/logout");
            System.out.println("Authentication: /api/auth/me");
            System.out.println("==================================");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
