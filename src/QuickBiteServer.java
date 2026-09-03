import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;

import part1_singleton.*;
import part2_abstract_factory.*;
import part4_bridge.*;
import part5_observer_proxy.*;

public class QuickBiteServer {

    private static final int PORT = 8080;

    public static void main(String[] args) throws Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/api/order", new OrderApiHandler());
        server.setExecutor(null); // default single-threaded executor
        server.start();
        System.out.println("QuickBite server running at http://localhost:" + PORT + "/");
    }

    // ── GET / → serve design.html ───────────────────────────────────────
    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(405, -1);
                return;
            }

            // Resolve index.html (or legacy design.html) relative to the working directory
            Path htmlPath = Paths.get("public", "index.html").toAbsolutePath();
            if (!Files.exists(htmlPath)) {
                htmlPath = Paths.get("design.html").toAbsolutePath();
            }
            if (!Files.exists(htmlPath)) {
                String msg = "HTML interface not found (looked for public/index.html or design.html)";
                ex.sendResponseHeaders(404, msg.length());
                ex.getResponseBody().write(msg.getBytes());
                ex.getResponseBody().close();
                return;
            }

            byte[] content = Files.readAllBytes(htmlPath);
            ex.getResponseHeaders().set("Content-Type", "text/html; charset=UTF-8");
            ex.sendResponseHeaders(200, content.length);
            ex.getResponseBody().write(content);
            ex.getResponseBody().close();
        }
    }

    // ── POST /api/order → run pipeline, return JSON ─────────────────────
    static class OrderApiHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {

            // CORS preflight
            ex.getResponseHeaders().set("Access-Control-Allow-Origin", "*");
            ex.getResponseHeaders().set("Access-Control-Allow-Methods", "POST, OPTIONS");
            ex.getResponseHeaders().set("Access-Control-Allow-Headers", "Content-Type");

            if ("OPTIONS".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(204, -1);
                return;
            }

            if (!"POST".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(405, -1);
                return;
            }

            // Read request body
            String body;
            try (BufferedReader br = new BufferedReader(new InputStreamReader(ex.getRequestBody()))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) sb.append(line);
                body = sb.toString();
            }

            // Parse JSON fields (simple manual parsing - no library needed)
            String customerName = jsonString(body, "customerName");
            double amount       = jsonDouble(body, "amount");
            String coupon       = jsonString(body, "coupon");
            String region       = jsonString(body, "region");
            String channel      = jsonString(body, "channel");
            String username     = jsonString(body, "username");
            String password     = jsonString(body, "password");

            // Capture System.out
            PrintStream originalOut = System.out;
            ByteArrayOutputStream capture = new ByteArrayOutputStream();
            PrintStream captureStream = new PrintStream(capture, true, "UTF-8");
            System.setOut(captureStream);

            boolean success = false;
            boolean loginFailed = false;
            double tax = 0;
            double total = 0;

            try {
                // Build the region factory
                RegionFactory regionFactory;
                if ("US".equalsIgnoreCase(region)) {
                    regionFactory = new USRegionFactory();
                } else {
                    regionFactory = new IndiaRegionFactory();
                }

                // Build the notification channel
                NotificationChannel notifChannel;
                switch (channel.toUpperCase()) {
                    case "SMS":  notifChannel = new SMSChannel();  break;
                    case "PUSH": notifChannel = new PushChannel();  break;
                    default:     notifChannel = new EmailChannel(); break;
                }

                // Create order and proxy - exactly the same as QuickBiteDemo
                Order order = new Order(customerName, amount, coupon);
                OrderService proxy = new OrderServiceProxy(username, password);
                proxy.placeOrder(order, regionFactory, notifChannel);

                // Determine outcome from captured output
                String log = capture.toString("UTF-8");
                loginFailed = log.contains("Login failed");
                boolean couponFailed  = log.contains("Coupon check FAILED");
                boolean fraudFailed   = log.contains("Fraud check FAILED");

                success = !loginFailed && !couponFailed && !fraudFailed;

                // Calculate tax/total for the response
                TaxCalculator taxCalc = regionFactory.createTaxCalculator();
                tax   = taxCalc.calculateTax(amount);
                total = amount + tax;

                // Save the order to SQLite
                new Database.OrderRepository().save(order, region, channel, success ? "PROCESSED" : "FAILED");

            } finally {
                System.setOut(originalOut);
            }

            String log = capture.toString("UTF-8");

            // Build JSON response
            String json = "{"
                + "\"success\":" + success + ","
                + "\"loginFailed\":" + loginFailed + ","
                + "\"log\":" + escapeJsonString(log) + ","
                + "\"amount\":" + amount + ","
                + "\"tax\":" + tax + ","
                + "\"total\":" + total + ","
                + "\"region\":" + escapeJsonString(region) + ","
                + "\"channel\":" + escapeJsonString(channel)
                + "}";

            byte[] resp = json.getBytes("UTF-8");
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.sendResponseHeaders(200, resp.length);
            ex.getResponseBody().write(resp);
            ex.getResponseBody().close();
        }
    }

    // ── Tiny JSON helpers (no library needed) ───────────────────────────

    /** Extract a string value for a given key from a flat JSON object. */
    private static String jsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        idx = json.indexOf(':', idx) + 1;
        // Skip whitespace
        while (idx < json.length() && json.charAt(idx) == ' ') idx++;
        if (idx < json.length() && json.charAt(idx) == '"') {
            int start = idx + 1;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }
        // Unquoted value (number/boolean) - read until comma or brace
        int start = idx;
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    /** Extract a double value for a given key from a flat JSON object. */
    private static double jsonDouble(String json, String key) {
        String val = jsonString(json, key);
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { return 0; }
    }

    /** Escape a Java string for use as a JSON string value (with quotes). */
    private static String escapeJsonString(String s) {
        if (s == null) return "null";
        StringBuilder sb = new StringBuilder("\"");
        for (char c : s.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n");  break;
                case '\r': sb.append("\\r");  break;
                case '\t': sb.append("\\t");  break;
                default:   sb.append(c);
            }
        }
        sb.append('"');
        return sb.toString();
    }
}
