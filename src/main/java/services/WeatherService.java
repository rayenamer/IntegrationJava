package services;

import utils.Config;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WeatherService {
    private static final Config config = Config.getInstance();
    private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/weather";

    public String getWeatherPrediction(String location, LocalDateTime date) {
        try {
            // Format the location for the API call
            String formattedLocation = location.replace(" ", "%20");
            
            // Build the API URL
            String urlString = String.format("%s?q=%s&appid=%s&units=metric", 
                BASE_URL, formattedLocation, config.getWeatherApiKey());
            
            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();
                
                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();
                
                // Parse the JSON response using regex
                String jsonResponse = response.toString();
                
                // Extract temperature
                Pattern tempPattern = Pattern.compile("\"temp\":([0-9.]+)");
                Matcher tempMatcher = tempPattern.matcher(jsonResponse);
                double temperature = 0.0;
                if (tempMatcher.find()) {
                    temperature = Double.parseDouble(tempMatcher.group(1));
                }
                
                // Extract weather description
                Pattern descPattern = Pattern.compile("\"description\":\"([^\"]+)\"");
                Matcher descMatcher = descPattern.matcher(jsonResponse);
                String description = "Unknown";
                if (descMatcher.find()) {
                    description = descMatcher.group(1);
                }
                
                return String.format("%.1f°C, %s", temperature, description);
            } else {
                return "Weather data unavailable";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "Error fetching weather data";
        }
    }
} 