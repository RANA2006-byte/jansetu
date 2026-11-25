
package server;

import auth.AuthService;
import dao.SavedSchemeDAO;
import model.Scheme;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SavedSchemeHandler {

    private static final AuthService authService = new AuthService();
    private static final SavedSchemeDAO savedSchemeDAO = new SavedSchemeDAO();

    private static final String COOKIE_NAME = "JANSETU_SESSION";

    // Read the session token from the browser cookie
    private static String getToken(HttpExchange exchange) {

        String cookieHeader = exchange.getRequestHeaders()
                .getFirst("Cookie");

        if (cookieHeader == null) {
            return null;
        }

        for (String cookie : cookieHeader.split(";")) {

            String[] parts = cookie.trim().split("=", 2);

            if (parts.length == 2 &&
                    parts[0].equals(COOKIE_NAME)) {
                return parts[1];
            }
        }

        return null;
    }

    // Read JSON request body
    private static String readBody(HttpExchange exchange)
            throws IOException {

        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(),
                    StandardCharsets.UTF_8);
        }
    }

    // Extract schemeId from JSON
    private static Integer getSchemeId(String body) {

        Pattern pattern = Pattern.compile(
                "\"schemeId\"\\s*:\\s*(\\d+)");

        Matcher matcher = pattern.matcher(body);

        if (matcher.find()) {
            return Integer.parseInt(matcher.group(1));
        }

        return null;
    }

    // Escape text for JSON
    private static String escape(String value) {

        if (value == null) {
            return "";
        }

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    // Convert a scheme object into JSON
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

    // Convert a list of schemes into JSON
    private static String schemesToJson(List<Scheme> schemes) {

        StringBuilder json = new StringBuilder("[");

        for (int i = 0; i < schemes.size(); i++) {

            if (i > 0) {
                json.append(",");
            }

            json.append(schemeToJson(schemes.get(i)));
        }

        json.append("]");

        return json.toString();
    }

    // Send JSON response
    private static void send(HttpExchange exchange,
            int status, String response) throws IOException {

        byte[] bytes = response.getBytes(StandardCharsets.UTF_8);

        exchange.getResponseHeaders().set(
                "Content-Type", "application/json; charset=UTF-8");

        exchange.sendResponseHeaders(status, bytes.length);

        try (OutputStream output = exchange.getResponseBody()) {
            output.write(bytes);
        }
    }

    // Register saved scheme API routes
    public static void registerRoutes(HttpServer server) {

        HttpContext context = server.createContext(
                "/api/saved-schemes", exchange -> {

                    try {

                        String method = exchange.getRequestMethod();

                        // Authenticate the user using their session cookie
                        String token = getToken(exchange);

                        Integer accountId = authService.getAccountIdByToken(token);

                        if (accountId == null) {
                            send(exchange, 401,
                                    "{\"error\":\"Please log in first\"}");
                            return;
                        }

                        // SAVE SCHEME
                        if ("POST".equals(method)) {

                            String body = readBody(exchange);

                            Integer schemeId = getSchemeId(body);

                            if (schemeId == null || schemeId <= 0) {
                                send(exchange, 400,
                                        "{\"error\":\"Invalid scheme ID\"}");
                                return;
                            }

                            boolean saved = savedSchemeDAO.saveScheme(
                                    accountId, schemeId);

                            if (saved) {
                                send(exchange, 201,
                                        "{\"message\":\"Scheme saved successfully\"}");
                            } else {
                                send(exchange, 409,
                                        "{\"error\":\"Could not save scheme. It may already be saved.\"}");
                            }

                            return;
                        }

                        // GET ALL SAVED SCHEMES
                        if ("GET".equals(method)) {

                            List<Scheme> schemes = savedSchemeDAO.getSavedSchemes(accountId);

                            send(exchange, 200, schemesToJson(schemes));

                            return;
                        }

                        // DELETE A SAVED SCHEME
                        if ("DELETE".equals(method)) {

                            String path = exchange.getRequestURI().getPath();

                            String basePath = "/api/saved-schemes/";

                            if (!path.startsWith(basePath)) {
                                send(exchange, 400,
                                        "{\"error\":\"Scheme ID is required\"}");
                                return;
                            }

                            String idText = path.substring(basePath.length());

                            int schemeId;

                            try {
                                schemeId = Integer.parseInt(idText);

                                if (schemeId <= 0) {
                                    throw new NumberFormatException();
                                }

                            } catch (NumberFormatException e) {
                                send(exchange, 400,
                                        "{\"error\":\"Invalid scheme ID\"}");
                                return;
                            }

                            boolean removed = savedSchemeDAO.removeSavedScheme(
                                    accountId, schemeId);

                            if (removed) {
                                send(exchange, 200,
                                        "{\"message\":\"Scheme removed successfully\"}");
                            } else {
                                send(exchange, 404,
                                        "{\"error\":\"Saved scheme not found\"}");
                            }

                            return;
                        }

                        send(exchange, 405,
                                "{\"error\":\"Method not allowed\"}");

                    } catch (Exception e) {

                        e.printStackTrace();

                        try {
                            send(exchange, 500,
                                    "{\"error\":\"Saved schemes server error\"}");
                        } catch (IOException ignored) {
                        }
                    }
                });

        JansetuServer.configureCors(context);
    }
}
