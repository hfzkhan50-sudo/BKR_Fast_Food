/*
 * Decompiled with CFR 0.152.
 */
package api;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Properties;

public class ApiClient {
    private static final String CONFIG_FILE = "config.properties";
    private static String serverUrl = "https://bkr-fastfood-backend.onrender.com/api";
    private static String serverHost = "bkr-fastfood-backend.onrender.com";
    private static int serverPort = 443;
    private static final HttpClient client = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(6L)).build();

    public static synchronized void loadConfig() {
        File file = new File(CONFIG_FILE);
        if (file.exists()) {
            try (FileInputStream fileInputStream = new FileInputStream(file);){
                Properties properties = new Properties();
                properties.load(fileInputStream);
                String string = properties.getProperty("server.url");
                if (string != null && !string.isBlank()) {
                    ApiClient.setServerUrl(string);
                } else {
                    serverHost = properties.getProperty("server.host", "localhost").trim();
                    serverPort = Integer.parseInt(properties.getProperty("server.port", "8080").trim());
                    ApiClient.updateUrlFromHostAndPort();
                }
            }
            catch (Exception exception) {
                System.err.println("Could not load config.properties: " + exception.getMessage());
                ApiClient.updateUrlFromHostAndPort();
            }
        } else {
            ApiClient.updateUrlFromHostAndPort();
        }
    }

    public static synchronized void saveConfig(String string, int n) {
        String string2 = string != null ? string.trim() : "localhost";
        Properties properties = new Properties();
        if (string2.startsWith("http://") || string2.startsWith("https://") || string2.contains("/")) {
            ApiClient.setServerUrl(string2);
            properties.setProperty("server.url", serverUrl);
            properties.setProperty("server.host", serverHost);
            properties.setProperty("server.port", String.valueOf(serverPort));
        } else {
            serverHost = string2;
            serverPort = n > 0 ? n : 8080;
            ApiClient.updateUrlFromHostAndPort();
            properties.setProperty("server.url", serverUrl);
            properties.setProperty("server.host", serverHost);
            properties.setProperty("server.port", String.valueOf(serverPort));
        }
        try (FileOutputStream fileOutputStream = new FileOutputStream(CONFIG_FILE);){
            properties.store(fileOutputStream, "BKR Fast Food Client Configuration");
        }
        catch (Exception exception) {
            System.err.println("Could not save config.properties: " + exception.getMessage());
        }
    }

    private static void setServerUrl(String string) {
        String object = string.trim();
        if (!object.startsWith("http://") && !object.startsWith("https://")) {
            object = "http://" + object;
        }
        if (object.endsWith("/")) {
            object = object.substring(0, object.length() - 1);
        }
        if (!object.endsWith("/api")) {
            object = object + "/api";
        }
        serverUrl = object;
        try {
            URI uRI = URI.create(serverUrl);
            String string2 = serverHost = uRI.getHost() != null ? uRI.getHost() : "localhost";
            serverPort = uRI.getPort() != -1 ? uRI.getPort() : (uRI.getScheme().equalsIgnoreCase("https") ? 443 : 80);
        }
        catch (Exception exception) {
            // empty catch block
        }
    }

    private static void updateUrlFromHostAndPort() {
        String string = serverHost.trim();
        if (string.startsWith("http://") || string.startsWith("https://")) {
            ApiClient.setServerUrl(string);
        } else {
            serverUrl = serverPort == 80 || serverPort == 443 || serverPort <= 0 ? "http://" + string + "/api" : "http://" + string + ":" + serverPort + "/api";
        }
    }

    public static String getBaseUrl() {
        return serverUrl;
    }

    public static String getServerHost() {
        return serverHost;
    }

    public static int getServerPort() {
        return serverPort;
    }

    public static String get(String string) throws Exception {
        Object object = string.startsWith("/") ? string : "/" + string;
        String string2 = serverUrl + (String)object;
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(string2)).timeout(Duration.ofSeconds(12L)).header("Accept", "application/json").GET().build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() >= 400) {
            throw new RuntimeException("Server Error (" + httpResponse.statusCode() + "): " + httpResponse.body());
        }
        return httpResponse.body();
    }

    public static String post(String string, String string2) throws Exception {
        Object object = string.startsWith("/") ? string : "/" + string;
        String string3 = serverUrl + (String)object;
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(string3)).timeout(Duration.ofSeconds(15L)).header("Content-Type", "application/json").header("Accept", "application/json").POST(HttpRequest.BodyPublishers.ofString(string2 != null ? string2 : "")).build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() >= 400) {
            throw new RuntimeException("Server Error (" + httpResponse.statusCode() + "): " + httpResponse.body());
        }
        return httpResponse.body();
    }

    public static String put(String string, String string2) throws Exception {
        Object object = string.startsWith("/") ? string : "/" + string;
        String string3 = serverUrl + (String)object;
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(string3)).timeout(Duration.ofSeconds(12L)).header("Content-Type", "application/json").header("Accept", "application/json").PUT(HttpRequest.BodyPublishers.ofString(string2 != null ? string2 : "")).build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() >= 400) {
            throw new RuntimeException("Server Error (" + httpResponse.statusCode() + "): " + httpResponse.body());
        }
        return httpResponse.body();
    }

    public static String delete(String string) throws Exception {
        Object object = string.startsWith("/") ? string : "/" + string;
        String string2 = serverUrl + (String)object;
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(string2)).timeout(Duration.ofSeconds(12L)).header("Accept", "application/json").DELETE().build();
        HttpResponse<String> httpResponse = client.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (httpResponse.statusCode() >= 400) {
            throw new RuntimeException("Server Error (" + httpResponse.statusCode() + "): " + httpResponse.body());
        }
        return httpResponse.body();
    }

    public static boolean testConnection() {
        try {
            ApiClient.get("/inventory");
            return true;
        }
        catch (Exception exception) {
            return false;
        }
    }

    static {
        ApiClient.loadConfig();
    }
}

