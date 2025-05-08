package controllers;

import entities.Evenement;
import entities.TypeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.EvenementService;
import services.TypeEventService;
import services.WeatherService;
import utils.Config;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import entities.User;


public class EventListController {
    private final EvenementService eventService = new EvenementService();
    private final TypeEventService typeEventService = new TypeEventService();
    private final WeatherService weatherService = new WeatherService();
    private ObservableList<Evenement> events = FXCollections.observableArrayList();
    private ObservableList<Evenement> filteredEvents = FXCollections.observableArrayList();

    private User currentUser;

    currentUser = Session.getCurrentUser();
    int loggedInUserId = currentUser.getId();


    private String currentLanguage = "FR"; // DeepL language codes
    private VBox mainLayout;
    private final Config config = Config.getInstance();
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Map<String, String> translationCache = new HashMap<>(); // Cache translations
    private GridPane eventGrid; // Store grid for refresh

    public void show(Stage stage) {
        loadEvents();
        mainLayout = createMainLayout(stage);
        Scene scene = new Scene(mainLayout, 900, 600);
        stage.setScene(scene);
        stage.setTitle(translate("Events"));
        stage.show();
    }

    private void loadEvents() {
        events.setAll(eventService.findAll());
        filteredEvents.setAll(events); // Initialize filteredEvents
    }

    private String translate(String text) {
        return translate(text, "EN", currentLanguage); // Default source language: English
    }

    private String translate(String text, String sourceLang, String targetLang) {
        if (text == null || text.isEmpty()) return text;
        String cacheKey = text + ":" + sourceLang + ":" + targetLang;
        if (translationCache.containsKey(cacheKey)) {
            return translationCache.get(cacheKey);
        }

        try {
            String url = "https://api-free.deepl.com/v2/translate";
            String requestBody = "auth_key=" + config.getDeepLApiKey() +
                    "&text=" + URLEncoder.encode(text, StandardCharsets.UTF_8) +
                    "&source_lang=" + sourceLang +
                    "&target_lang=" + targetLang;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 200) {
                String body = response.body();
                String marker = "\"text\":\"";
                int start = body.indexOf(marker) + marker.length();
                if (start == -1 + marker.length()) {
                    System.err.println("Invalid DeepL response: " + body);
                    return text;
                }
                int end = body.indexOf("\"", start);
                if (end == -1) {
                    System.err.println("Invalid DeepL response: " + body);
                    return text;
                }
                String translatedText = body.substring(start, end);
                translationCache.put(cacheKey, translatedText);
                return translatedText;
            } else {
                System.err.println("DeepL API error: " + response.statusCode() + " - " + response.body());
                return text;
            }
        } catch (Exception e) {
            System.err.println("Translation error for '" + text + "': " + e.getMessage());
            return text;
        }
    }

    private VBox createMainLayout(Stage stage) {
        // Language Selector
        ComboBox<String> languageSelector = new ComboBox<>();
        Map<String, String> languageMap = new HashMap<>();
        languageMap.put("Français", "FR");
        languageMap.put("English", "EN");
        languageMap.put("Español", "ES");
        languageSelector.getItems().addAll(languageMap.keySet());
        languageSelector.setValue(currentLanguage.equals("FR") ? "Français" :
                currentLanguage.equals("EN") ? "English" : "Español");
        languageSelector.setStyle("-fx-background-radius: 5;");
        languageSelector.setOnAction(e -> {
            currentLanguage = languageMap.get(languageSelector.getValue());
            mainLayout.getChildren().setAll(createMainLayout(stage));
            stage.setTitle(translate("Events"));
        });

        HBox languageBox = new HBox(10, new Label(translate("Language")), languageSelector);
        languageBox.setAlignment(Pos.CENTER_LEFT);
        languageBox.setPadding(new Insets(10));

        // Title
        Label title = new Label(translate("Events"));
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1F3A5F"));
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 20, 0));

        // Search and Filter Controls
        TextField searchField = new TextField();
        searchField.setPromptText(translate("Search by description..."));
        searchField.setPrefWidth(300);
        searchField.setStyle("-fx-background-radius: 5; -fx-padding: 8;");

        ComboBox<TypeEvent> typeFilter = new ComboBox<>();
        typeFilter.getItems().add(new TypeEvent(0, translate("All")));
        typeFilter.getItems().addAll(typeEventService.findAll());
        typeFilter.setValue(typeFilter.getItems().get(0));
        typeFilter.setStyle("-fx-background-radius: 5;");
        typeFilter.setCellFactory(lv -> new ListCell<TypeEvent>() {
            @Override
            protected void updateItem(TypeEvent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        typeFilter.setButtonCell(new ListCell<TypeEvent>() {
            @Override
            protected void updateItem(TypeEvent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        ComboBox<String> availabilityFilter = new ComboBox<>();
        availabilityFilter.getItems().addAll(
                translate("All"),
                translate("Available"),
                translate("Not Available")
        );
        availabilityFilter.setValue(translate("All"));
        availabilityFilter.setStyle("-fx-background-radius: 5;");

        Button clearFiltersButton = new Button(translate("Clear Filters"));
        styleButton(clearFiltersButton, "#6C757D");

        HBox searchFilteredBox = new HBox(10,
                new Label(translate("Search:")),
                searchField,
                new Label(translate("Type:")),
                typeFilter,
                new Label(translate("Availability:")),
                availabilityFilter,
                clearFiltersButton);
        searchFilteredBox.setAlignment(Pos.CENTER_LEFT);
        searchFilteredBox.setPadding(new Insets(10));

        // Event Grid
        eventGrid = new GridPane();
        eventGrid.setHgap(20);
        eventGrid.setVgap(20);
        eventGrid.setPadding(new Insets(20));
        ScrollPane scrollPane = new ScrollPane(eventGrid);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background-color: transparent;");

        // Populate grid and set up listeners
        applyFilters(searchField, typeFilter, availabilityFilter);
        searchField.textProperty().addListener((obs, old, newVal) -> applyFilters(searchField, typeFilter, availabilityFilter));
        typeFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> applyFilters(searchField, typeFilter, availabilityFilter));
        availabilityFilter.getSelectionModel().selectedItemProperty().addListener((obs, old, newVal) -> applyFilters(searchField, typeFilter, availabilityFilter));
        clearFiltersButton.setOnAction(e -> {
            searchField.clear();
            typeFilter.setValue(typeFilter.getItems().get(0));
            availabilityFilter.setValue(translate("All"));
            applyFilters(searchField, typeFilter, availabilityFilter);
        });
        events.addListener((javafx.beans.Observable obs) -> applyFilters(searchField, typeFilter, availabilityFilter));

        // Main Layout
        mainLayout = new VBox(15, languageBox, titleBox, searchFilteredBox, scrollPane);
        mainLayout.setStyle("-fx-background-color: #E9F9EF;");
        mainLayout.setPadding(new Insets(20));
        return mainLayout;
    }

    private void applyFilters(TextField searchField, ComboBox<TypeEvent> typeFilter, ComboBox<String> availabilityFilter) {
        String searchText = searchField.getText() != null ? searchField.getText().toLowerCase().trim() : "";
        TypeEvent selectedType = typeFilter.getValue();
        String selectedAvailability = availabilityFilter.getValue();

        filteredEvents.setAll(events.stream()
                .filter(event -> event.getDescription() == null || searchText.isEmpty() || event.getDescription().toLowerCase().contains(searchText))
                .filter(event -> selectedType == null || selectedType.getId() == 0 || (event.getTypeEvent() != null && event.getTypeEvent().getId() == selectedType.getId()))
                .filter(event -> selectedAvailability == null || selectedAvailability.equals(translate("All")) ||
                        (selectedAvailability.equals(translate("Available")) && event.isDisponibilite()) ||
                        (selectedAvailability.equals(translate("Not Available")) && !event.isDisponibilite()))
                .collect(Collectors.toList()));

        populateEventGrid();
    }

    private void populateEventGrid() {
        eventGrid.getChildren().clear();
        int row = 0;
        int col = 0;
        for (Evenement event : filteredEvents) {
            VBox card = new VBox(10);
            card.setPadding(new Insets(15));
            card.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dee2e6;");
            card.setEffect(new DropShadow(10, Color.web("#000000", 0.2)));

            // Use event.getNom() for the title
            Label descriptionLabel = new Label(event.getNom() != null ? event.getNom() : "N/A");
            descriptionLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));
            descriptionLabel.setTextFill(Color.web("#1F3A5F"));
            descriptionLabel.setAlignment(Pos.CENTER);

            String weatherInfo = translate("Weather") + ": Loading...";
            if (event.getLieu() != null && event.getDate() != null) {
                weatherInfo = translate("Weather") + ": " + weatherService.getWeatherPrediction(event.getLieu(), event.getDate());
            }

            Label detailsLabel = new Label(String.format(
                    "%s: %s\n%s: %s\n%s: %s\n%s",
                    translate("Date"),
                    event.getDate() != null ? event.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")) : "N/A",
                    translate("Location"),
                    event.getLieu() != null ? event.getLieu() : "N/A",
                    translate("Type"),
                    event.getTypeEvent() != null ? event.getTypeEvent().getNom() : "N/A",
                    weatherInfo
            ));
            detailsLabel.setFont(Font.font("Arial", 14));
            detailsLabel.setTextFill(Color.web("#6C757D"));
            detailsLabel.setAlignment(Pos.CENTER);
            detailsLabel.setWrapText(true);

            if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
                try {
                    ImageView imageView = new ImageView(new Image(event.getImageUrl()));
                    imageView.setFitWidth(200);
                    imageView.setPreserveRatio(true);
                    card.getChildren().add(1, imageView);
                } catch (Exception ignored) {
                }
            }

            Label availabilityLabel = new Label(event.isDisponibilite() ? translate("Available") : translate("Not Available"));
            availabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
            availabilityLabel.setTextFill(event.isDisponibilite() ? Color.web("#28A745") : Color.web("#DC3545"));
            availabilityLabel.setStyle("-fx-background-color: " + (event.isDisponibilite() ? "#D4EDDA" : "#F8D7DA") + "; -fx-padding: 5; -fx-background-radius: 5;");
            HBox availabilityBox = new HBox(availabilityLabel);
            availabilityBox.setAlignment(Pos.CENTER);

            // Create buttons container
            VBox buttonBox = new VBox(10);
            buttonBox.setAlignment(Pos.CENTER);

            // Add Participate/Cancel Participation button based on participation and availability
            if (event.isParticipant(loggedInUserId)) {
                // Show "Cancel Participation" if the user is a participant, regardless of availability
                Button cancelButton = new Button(translate("Cancel Participation"));
                styleButton(cancelButton, "#DC3545");
                cancelButton.setPrefWidth(200);
                cancelButton.setOnAction(e -> {
                    try {
                        event.removeParticipant(loggedInUserId);
                        eventService.save(event);
                        loadEvents();
                        applyFilters(
                                (TextField) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(1),
                                (ComboBox<TypeEvent>) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(3),
                                (ComboBox<String>) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(5));
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(translate("Error"));
                        alert.setContentText(translate("Error updating participation: ") + ex.getMessage());
                        alert.showAndWait();
                    }
                });
                buttonBox.getChildren().add(cancelButton);
            } else if (event.isDisponibilite()) {
                // Show "Participate" only if the event is available and the user is not a participant
                Button participateButton = new Button(translate("Participate"));
                styleButton(participateButton, "#007BFF");
                participateButton.setPrefWidth(200);
                participateButton.setOnAction(e -> {
                    try {
                        event.addParticipant(loggedInUserId);
                        eventService.save(event);
                        loadEvents();
                        applyFilters(
                                (TextField) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(1),
                                (ComboBox<TypeEvent>) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(3),
                                (ComboBox<String>) ((HBox) mainLayout.getChildren().get(2)).getChildren().get(5));
                    } catch (Exception ex) {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle(translate("Error"));
                        alert.setContentText(translate("Error updating participation: ") + ex.getMessage());
                        alert.showAndWait();
                    }
                });
                buttonBox.getChildren().add(participateButton);
            }

            // Always add Details button
            Button detailsButton = new Button(translate("View Details"));
            styleButton(detailsButton, "#17A2B8");
            detailsButton.setPrefWidth(200);
            detailsButton.setOnAction(e -> {
                Stage newStage = new Stage();
                newStage.setTitle(translate("Event Details") + " - " + (event.getNom() != null ? event.getNom() : "N/A"));
                new EventDetailsController(loggedInUserId).show(newStage, event);
            });
            buttonBox.getChildren().add(detailsButton);

            card.getChildren().addAll(descriptionLabel, detailsLabel, availabilityBox, buttonBox);

            eventGrid.add(card, col, row);
            col++;
            if (col == 3) {
                col = 0;
                row++;
            }
        }
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;"));
    }

    private String darkenColor(String hexColor) {
        Color color = Color.web(hexColor);
        double factor = 0.8;
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255 * factor),
                (int) (color.getGreen() * 255 * factor),
                (int) (color.getBlue() * 255 * factor));
    }
}