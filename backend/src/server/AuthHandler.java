
package server;

import auth.AuthService;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpContext;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AuthHandler {

    private static final AuthService authService = new AuthService();
    private static final String COOKIE_NAME = "JANSETU_SESSION";

    private static String getValue(String json, String key) {
        Pattern pattern = Pattern.compile(
                "\"" + Pattern.quote(key) +
                        "\"\\s*:\\s*\"((?:\\\\.|[^\"])*)\"");

        Matcher matcher = pattern.matcher(json);

        if (!matcher.find())
            return null;

        return matcher.group(1)
                .replace("\\\"", "\"")
                .replace("\\\\", "\\")
                .replace("\\n", "\n");
    }

    private static String escape(String value) {
        if (value == null)
            return "";

        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r");
    }

    private static String readBody(HttpExchange exchange)
            throws IOException {

        try (InputStream input = exchange.getRequestBody()) {
            return new String(input.readAllBytes(),
                    StandardCharsets.UTF_8);
        }
    }

    private static String getToken(HttpExchange exchange) {
        String cookieHeader = exchange.getRequestHeaders().getFirst("Cookie");

        if (cookieHeader == null)
            return null;

        for (String cookie : cookieHeader.split(";")) {
            String[] parts = cookie.trim().split("=", 2);

            if (parts.length == 2 &&
                    parts[0].equals(COOKIE_NAME)) {
                return parts[1];
            }
        }

        return null;
    }

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

    private static void setSessionCookie(
            HttpExchange exchange, String token) {

        exchange.getResponseHeaders().add(
                "Set-Cookie",
                COOKIE_NAME + "=" + token +
                        "; HttpOnly; SameSite=Lax; Path=/; Max-Age=86400");
    }

    private static void clearSessionCookie(HttpExchange exchange) {
        exchange.getResponseHeaders().add(
                "Set-Cookie",
                COOKIE_NAME + "=; HttpOnly; SameSite=Lax; Path=/; Max-Age=0");
    }

    public static void registerRoutes(HttpServer server) {

        // REGISTER
        HttpContext registerContext = server.createContext("/api/auth/register", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("POST")) {
                    send(exchange, 405,
                            "{\"error\":\"Method not allowed\"}");
                    return;
                }

                String body = readBody(exchange);

                String name = getValue(body, "fullName");
                String email = getValue(body, "email");
                String password = getValue(body, "password");

                boolean registered = authService.register(name, email, password);

                if (registered) {
                    send(exchange, 201,
                            "{\"message\":\"Account created successfully\"}");
                } else {
                    send(exchange, 400,
                            "{\"error\":\"Registration failed. Check your details or email.\"}");
                }

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Registration server error\"}");
            }
        });

        JansetuServer.configureCors(registerContext);

        // LOGIN
        HttpContext loginContext = server.createContext("/api/auth/login", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("POST")) {
                    send(exchange, 405,
                            "{\"error\":\"Method not allowed\"}");
                    return;
                }

                String body = readBody(exchange);

                String email = getValue(body, "email");
                String password = getValue(body, "password");

                AuthService.LoginResult result = authService.login(email, password);

                if (result == null) {
                    send(exchange, 401,
                            "{\"error\":\"Invalid email or password\"}");
                    return;
                }

                setSessionCookie(exchange, result.getSessionToken());

                send(exchange, 200,
                        "{\"message\":\"Login successful\","
                                + "\"user\":{\"fullName\":\""
                                + escape(result.getFullName())
                                + "\",\"email\":\""
                                + escape(result.getEmail())
                                + "\"}}");

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Login server error\"}");
            }
        });

        JansetuServer.configureCors(loginContext);

        // CHECK CURRENT USER — returns fullName + email from the session
        HttpContext meContext = server.createContext("/api/auth/me", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("GET")) {
                    send(exchange, 405,
                            "{\"error\":\"Method not allowed\"}");
                    return;
                }

                String token = getToken(exchange);

                // Look up the account that owns this session in one DB query.
                // This is safe: identity comes from the server-side session,
                // never from anything the browser sent in the request body.
                String[] account = authService.getAccountByToken(token);

                if (account == null) {
                    send(exchange, 401,
                            "{\"error\":\"Not logged in\"}");
                    return;
                }

                // account[0] = fullName, account[1] = email
                send(exchange, 200,
                        "{\"authenticated\":true"
                                + ",\"fullName\":\"" + escape(account[0]) + "\""
                                + ",\"email\":\""    + escape(account[1]) + "\""
                                + "}");

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Session check failed\"}");
            }
        });

        JansetuServer.configureCors(meContext);

        // LOGOUT
        HttpContext logoutContext = server.createContext("/api/auth/logout", exchange -> {
            try {
                if (!exchange.getRequestMethod().equals("POST")) {
                    send(exchange, 405,
                            "{\"error\":\"Method not allowed\"}");
                    return;
                }

                String token = getToken(exchange);
                authService.logout(token);

                clearSessionCookie(exchange);

                send(exchange, 200,
                        "{\"message\":\"Logged out successfully\"}");

            } catch (Exception e) {
                e.printStackTrace();
                send(exchange, 500,
                        "{\"error\":\"Logout failed\"}");
            }
        });

        JansetuServer.configureCors(logoutContext);
    }
}
