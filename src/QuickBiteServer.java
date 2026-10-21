import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.file.*;
import java.util.Arrays;

import part1_singleton.*;
import part2_abstract_factory.*;
import part4_bridge.*;
import part5_observer_proxy.*;
import part6_builder_prototype.*;
import part7_template_method.*;
import part8_strategy.*;
import part9_memento.*;

public class QuickBiteServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        String envPort = System.getenv("PORT");
        if (envPort != null && !envPort.trim().isEmpty()) {
            try {
                port = Integer.parseInt(envPort.trim());
            } catch (NumberFormatException ignored) {}
        }

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new StaticHandler());
        server.createContext("/api/order", new OrderApiHandler());
        server.createContext("/api/stage1", new OrderApiHandler());
        server.createContext("/api/phase2", new Phase2ApiHandler());
        server.createContext("/api/stage2", new Phase2ApiHandler());
        server.setExecutor(null); // default single-threaded executor
        server.start();
        System.out.println("QuickBite server running at http://localhost:" + port + "/");
        System.out.println("Order Gateway API : POST /api/order (or /api/stage1)");
        System.out.println("Fulfillment & Pricing API : POST /api/stage2 (or /api/phase2)");
    }

    // ── GET / → serve public/ files (HTML, CSS, JS, libs) ────────────────
    static class StaticHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange ex) throws IOException {
            if (!"GET".equalsIgnoreCase(ex.getRequestMethod())) {
                ex.sendResponseHeaders(405, -1);
                return;
            }

            String pathStr = ex.getRequestURI().getPath();
            if (pathStr == null || pathStr.equals("/") || pathStr.isEmpty()) {
                pathStr = "/index.html";
            }
            if (pathStr.startsWith("/")) {
                pathStr = pathStr.substring(1);
            }

            Path filePath = Paths.get("public", pathStr).toAbsolutePath();
            if (!Files.exists(filePath)) {
                filePath = Paths.get("public", "index.html").toAbsolutePath();
            }
            if (!Files.exists(filePath)) {
                String msg = "404 Not Found";
                ex.sendResponseHeaders(404, msg.length());
                ex.getResponseBody().write(msg.getBytes());
                ex.getResponseBody().close();
                return;
            }

            String mimeType = "application/octet-stream";
            String lower = filePath.getFileName().toString().toLowerCase();
            if (lower.endsWith(".html")) mimeType = "text/html; charset=UTF-8";
            else if (lower.endsWith(".css")) mimeType = "text/css; charset=UTF-8";
            else if (lower.endsWith(".js")) mimeType = "application/javascript; charset=UTF-8";
            else if (lower.endsWith(".json")) mimeType = "application/json; charset=UTF-8";
            else if (lower.endsWith(".png")) mimeType = "image/png";
            else if (lower.endsWith(".svg")) mimeType = "image/svg+xml";
            else if (lower.endsWith(".ttf")) mimeType = "font/ttf";
            else if (lower.endsWith(".woff2")) mimeType = "font/woff2";

            byte[] content = Files.readAllBytes(filePath);
            ex.getResponseHeaders().set("Content-Type", mimeType);
            ex.sendResponseHeaders(200, content.length);
            ex.getResponseBody().write(content);
            ex.getResponseBody().close();
        }
    }

    // ── STAGE 1: POST /api/order → Order Gateway Pipeline ─────────────────
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

            // Parse JSON fields
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
                // Build the region factory (Abstract Factory)
                RegionFactory regionFactory;
                if ("US".equalsIgnoreCase(region)) {
                    regionFactory = new USRegionFactory();
                } else {
                    regionFactory = new IndiaRegionFactory();
                }

                // Build the notification channel (Bridge)
                NotificationChannel notifChannel;
                switch (channel.toUpperCase()) {
                    case "SMS":  notifChannel = new SMSChannel();  break;
                    case "PUSH": notifChannel = new PushChannel();  break;
                    default:     notifChannel = new EmailChannel(); break;
                }

                // Create order and proxy
                Order order = new Order(customerName, amount, coupon);
                OrderService proxy = new OrderServiceProxy(username, password);
                proxy.placeOrder(order, regionFactory, notifChannel);

                // Determine outcome from captured output
                String log = capture.toString("UTF-8");
                loginFailed = log.contains("Login failed");
                boolean couponFailed  = log.contains("Coupon check FAILED");
                boolean fraudFailed   = log.contains("Fraud check FAILED");

                success = !loginFailed && !couponFailed && !fraudFailed;

                // Calculate tax/total for response
                TaxCalculator taxCalc = regionFactory.createTaxCalculator();
                tax   = taxCalc.calculateTax(amount);
                total = amount + tax;

                // Save order to SQLite
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

    // ── STAGE 2: POST /api/phase2 → Fulfillment & Pricing Pipeline ────────
    static class Phase2ApiHandler implements HttpHandler {
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

            String customerName  = jsonString(body, "customerName");
            if (customerName.isEmpty()) customerName = "Aman Gupta";
            String preset        = jsonString(body, "preset");
            String workflowType  = jsonString(body, "workflow");
            String strategyType  = jsonString(body, "strategy");
            boolean usePrototype = "true".equalsIgnoreCase(jsonString(body, "usePrototype"));
            boolean undoCart     = "true".equalsIgnoreCase(jsonString(body, "undoCart"));

            // Capture System.out
            PrintStream originalOut = System.out;
            ByteArrayOutputStream capture = new ByteArrayOutputStream();
            PrintStream captureStream = new PrintStream(capture, true, "UTF-8");
            System.setOut(captureStream);

            CustomOrder order = null;
            double subtotal = 0.0;
            double payable = 0.0;
            double discount = 0.0;
            String dishName = "";
            String strategyName = "";
            String workflowName = "";

            try {
                // 1. Builder Pattern
                System.out.println("=== [BUILDER PATTERN] Constructing Meal ===");
                MealDirector director = new MealDirector();

                if ("BURGER".equalsIgnoreCase(preset)) {
                    order = director.constructBurgerCombo(customerName);
                } else if ("FITNESS".equalsIgnoreCase(preset)) {
                    order = director.constructFitnessMeal(customerName);
                } else if ("FEAST".equalsIgnoreCase(preset)) {
                    order = director.constructPartyFeast(customerName);
                } else {
                    order = new CustomOrderBuilder()
                            .setCustomerName(customerName)
                            .setBaseDish("Chef's Woodfired Special Pizza", 480.0)
                            .setSize("Large")
                            .addTopping("Smoked Gouda")
                            .addTopping("Black Olives")
                            .setSideDrink("Mint Mojito Cooler")
                            .setSpiceLevel("Medium")
                            .setSpecialInstructions("Extra garlic dip")
                            .build();
                }

                subtotal = order.calculateSubtotal();
                dishName = order.getBaseDish() + " (" + order.getSize() + ")";
                System.out.println("[BUILDER RESULT] " + order);

                // 2. Prototype Pattern (If requested)
                if (usePrototype) {
                    System.out.println("\n=== [PROTOTYPE PATTERN] 1-Click Reorder Cloning ===");
                    OrderRegistry registry = new OrderRegistry();
                    ReorderableMeal cloned = registry.getClone("WEEKLY_LUNCH");
                    System.out.println("[PROTOTYPE] Cloned past ticket: " + cloned);
                    cloned.setCustomerName(customerName);
                    cloned.addAddon("Chef Special Truffle Dip");
                    System.out.println("[PROTOTYPE] Modified cloned ticket independently: " + cloned);
                }

                // 3. Strategy Pattern (Dynamic Discounts)
                System.out.println("\n=== [STRATEGY PATTERN] Calculating Discounts ===");
                PricingContext pricingContext = new PricingContext();
                DiscountStrategy strategy;
                if ("FLAT".equalsIgnoreCase(strategyType)) {
                    strategy = new FlatDiscountStrategy(60.0);
                } else if ("PERCENT".equalsIgnoreCase(strategyType)) {
                    strategy = new PercentageDiscountStrategy(15.0);
                } else if ("LOYALTY".equalsIgnoreCase(strategyType)) {
                    strategy = new LoyaltyTierStrategy("QuickBite Gold VIP", 25.0);
                } else {
                    strategy = new NoDiscountStrategy();
                }
                pricingContext.setStrategy(strategy);
                strategyName = strategy.getStrategyName();
                payable = pricingContext.computePayable(subtotal);
                discount = subtotal - payable;

                // 4. Template Method Pattern (Order Fulfillment)
                System.out.println("\n=== [TEMPLATE METHOD PATTERN] Kitchen Fulfillment ===");
                OrderFulfillmentWorkflow workflow;
                if ("TAKEAWAY".equalsIgnoreCase(workflowType)) {
                    workflow = new TakeawayFulfillmentWorkflow();
                } else if ("DINE_IN".equalsIgnoreCase(workflowType)) {
                    workflow = new DineInFulfillmentWorkflow();
                } else {
                    workflow = new DeliveryFulfillmentWorkflow();
                }
                workflowName = workflow.getWorkflowName();
                workflow.fulfillOrder("QB-" + (int)(Math.random() * 900 + 100), customerName, dishName, payable);

                // 5. Memento Pattern (Snapshot & Undo Cart)
                if (undoCart) {
                    System.out.println("\n=== [MEMENTO PATTERN] Cart Snapshot & Revert ===");
                    CartOriginator cart = new CartOriginator();
                    CartCaretaker caretaker = new CartCaretaker();
                    cart.addItem(dishName, subtotal);
                    caretaker.backup(cart, "Pre-Discount State");
                    cart.applyCoupon("EXPERIMENTAL_OFFER", 150.0);
                    System.out.println("[MEMENTO] Customer triggered Undo: Reverting to previous cart snapshot...");
                    caretaker.undo(cart);
                }

                // Persist order in SQLite database
                Order dbOrder = new Order(customerName, payable, strategyType);
                new Database.OrderRepository().save(dbOrder, "PHASE2_KITCHEN", workflowType, "FULFILLED");

            } finally {
                System.setOut(originalOut);
            }

            String log = capture.toString("UTF-8");

            // Build JSON response
            String json = "{"
                + "\"success\":true,"
                + "\"log\":" + escapeJsonString(log) + ","
                + "\"dish\":" + escapeJsonString(dishName) + ","
                + "\"subtotal\":" + subtotal + ","
                + "\"discount\":" + discount + ","
                + "\"total\":" + payable + ","
                + "\"strategy\":" + escapeJsonString(strategyName) + ","
                + "\"workflow\":" + escapeJsonString(workflowName)
                + "}";

            byte[] resp = json.getBytes("UTF-8");
            ex.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
            ex.sendResponseHeaders(200, resp.length);
            ex.getResponseBody().write(resp);
            ex.getResponseBody().close();
        }
    }

    // ── Tiny JSON helpers ────────────────────────────────────────────────
    private static String jsonString(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        idx = json.indexOf(':', idx) + 1;
        while (idx < json.length() && json.charAt(idx) == ' ') idx++;
        if (idx < json.length() && json.charAt(idx) == '"') {
            int start = idx + 1;
            int end = json.indexOf('"', start);
            return json.substring(start, end);
        }
        int start = idx;
        int end = start;
        while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
        return json.substring(start, end).trim();
    }

    private static double jsonDouble(String json, String key) {
        String val = jsonString(json, key);
        try { return Double.parseDouble(val); }
        catch (NumberFormatException e) { return 0; }
    }

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
