package services;

import utils.Config;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class AIDescriptionService {
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-pro:generateContent";
    private static final Config config = Config.getInstance();

    public String generateEventDescription(String eventName, String eventType, String location) {
        try {
            // Create the improved prompt
            String prompt = String.format(
                    "Crée une description captivante et professionnelle de 40 à 50 mots pour un événement nommé '%s', de type '%s', se déroulant à '%s'. " +
                            "La description doit être en français, avec un ton chaleureux et engageant, incitant à la participation. " +
                            "Intègre de manière créative le type d'événement '%s' pour refléter son essence (ex. : innovation pour un hackathon, culture pour un festival) " +
                            "et utilise la ville '%s' pour ancrer l'ambiance et le contexte local. " +
                            "Cible un public varié (ex. : professionnels, étudiants, passionnés) et mets en avant l'objectif de l'événement, " +
                            "son unicité et ses opportunités (ex. : réseautage, apprentissage, inspiration). " +
                            "Évite les placeholders comme [date] ou [lien]. Assure-toi que le texte est prêt à être utilisé directement dans une application.",
                    eventName, eventType, location != null && !location.isEmpty() ? location : "un lieu vibrant", eventType, location != null && !location.isEmpty() ? location : "un lieu vibrant"
            );

            // Create the request body
            String requestBody = String.format(
                    "{\"contents\":[{\"parts\":[{\"text\":\"%s\"}]}]}",
                    prompt.replace("\"", "\\\"")
            );

            // Set up the HTTP connection
            URL url = new URL(API_URL + "?key=" + config.getGeminiApiKey());
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Send the request
            connection.getOutputStream().write(requestBody.getBytes(StandardCharsets.UTF_8));

            // Check the response code
            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
                StringBuilder errorResponse = new StringBuilder();
                String line;
                while ((line = errorReader.readLine()) != null) {
                    errorResponse.append(line);
                }
                errorReader.close();
                System.out.println("Error Response: " + errorResponse.toString());
                return "Le service est temporairement indisponible. Veuillez réessayer plus tard.";
            }

            // Read the response
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String responseText = response.toString();
            System.out.println("Full Response: " + responseText);

            // Extract the text using string manipulation
            String textMarker = "\"text\": \"";
            int startIndex = responseText.indexOf(textMarker);
            if (startIndex == -1) {
                System.out.println("No 'text' field found in response");
                return "Impossible de générer la description.";
            }

            startIndex += textMarker.length();
            int endIndex = responseText.indexOf("\"", startIndex);
            while (endIndex > startIndex && responseText.charAt(endIndex - 1) == '\\') {
                // Skip escaped quotes
                endIndex = responseText.indexOf("\"", endIndex + 1);
            }

            if (endIndex == -1) {
                System.out.println("Could not find closing quote for 'text' field");
                return "Impossible de générer la description.";
            }

            String generatedText = responseText.substring(startIndex, endIndex);
            // Unescape the text (handle \n, \", etc.)
            generatedText = generatedText.replace("\\n", "\n").replace("\\\"", "\"").replace("\\\\", "\\");
            return generatedText.trim();

        } catch (Exception e) {
            System.out.println("General Error: " + e.getMessage());
            e.printStackTrace();
            return "Le service est temporairement indisponible. Veuillez réessayer plus tard.";
        }
    }
}