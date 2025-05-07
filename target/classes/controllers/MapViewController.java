package controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.web.WebView;
import java.net.URL;
import java.util.ResourceBundle;

public class MapViewController implements Initializable {
    @FXML
    private WebView webView;
    
    private String location;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize empty map
        loadMap();
    }

    public void setLocation(String location) {
        this.location = location;
        loadMap();
    }

    private void loadMap() {
        if (webView != null) {
            String mapContent = String.format("""
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <title>Location Map</title>
                    <link rel="stylesheet" href="https://unpkg.com/leaflet@1.7.1/dist/leaflet.css"/>
                    <script src="https://unpkg.com/leaflet@1.7.1/dist/leaflet.js"></script>
                    <style>
                        #map { height: 100%%; width: 100%%; }
                        html, body { height: 100%%; margin: 0; padding: 0; }
                    </style>
                </head>
                <body>
                    <div id="map"></div>
                    <script>
                        var map = L.map('map').setView([36.8065, 10.1815], 13); // Default center on Tunisia
                        
                        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
                            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors'
                        }).addTo(map);
                        
                        // Function to search location and add marker
                        function searchLocation(address) {
                            fetch('https://nominatim.openstreetmap.org/search?format=json&q=' + encodeURIComponent(address))
                                .then(response => response.json())
                                .then(data => {
                                    if (data.length > 0) {
                                        var lat = parseFloat(data[0].lat);
                                        var lon = parseFloat(data[0].lon);
                                        map.setView([lat, lon], 15);
                                        L.marker([lat, lon]).addTo(map)
                                            .bindPopup(address)
                                            .openPopup();
                                    }
                                });
                        }
                        
                        // Search for the location
                        searchLocation('%s');
                    </script>
                </body>
                </html>
            """, location != null ? location : "Tunisia");

            webView.getEngine().loadContent(mapContent);
        }
    }
}
