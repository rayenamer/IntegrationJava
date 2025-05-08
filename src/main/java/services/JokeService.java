package services;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class JokeService {

    public String fetchRawJokeResponse() {
        StringBuilder response = new StringBuilder();

        try {
            String apiUrl = "https://v2.jokeapi.dev/joke/Any";
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream())
            );

            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line).append("\n");
            }

            reader.close();
        } catch (Exception e) {
            return "Error fetching joke: " + e.getMessage();
        }

        return response.toString();
    }
}
