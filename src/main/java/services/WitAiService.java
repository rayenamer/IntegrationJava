package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class WitAiService {

    private static final String API_KEY = "AIzaSyDqnGnK9c7tLnezqYCOCoJcMXBAPrLP8fU"; // Replace with your actual Gemini API key
    private static final String GEMINI_API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.0-flash:generateContent?key=" + API_KEY;

    public String sendMessageToGemini(String userMessage) {
        try {
            // Prepare request body
            String jsonBody = "{\"contents\": [{\"parts\": [{\"text\": \"" + userMessage + "\"}]}]}";

            // Create URL object and open connection
            URL url = new URL(GEMINI_API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the request body
            connection.getOutputStream().write(jsonBody.getBytes());

            // Get the response
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Manually parse the response to extract the message
                String responseString = response.toString();
                String start = "\"text\": \"";
                String end = "\"}";
                int startIndex = responseString.indexOf(start) + start.length();
                int endIndex = responseString.indexOf(end, startIndex);

                if (startIndex != -1 && endIndex != -1) {
                    String botMessage = responseString.substring(startIndex, endIndex);
                    return botMessage; // Return the bot's message
                } else {
                    return "Bot response not found.";
                }

            } else {
                return "POST request failed with code: " + responseCode;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Exception occurred: " + e.getMessage();
        }
    }
}
