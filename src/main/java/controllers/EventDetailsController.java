package controllers;

import entities.Evenement;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.EvenementService;

import java.io.File;
import java.time.format.DateTimeFormatter;

public class EventDetailsController {
    private final int loggedInUserId;
    private final EvenementService eventService = new EvenementService();

    public EventDetailsController(int loggedInUserId) {
        this.loggedInUserId = loggedInUserId;
    }

    public void show(Stage stage, Evenement event) {
        // Main Container
        HBox mainLayout = new HBox(25);
        mainLayout.setPadding(new Insets(25));
        mainLayout.setStyle("-fx-background-color: #F8FAFC;");

        // Left Section (Event Details)
        VBox detailsSection = new VBox(20);
        detailsSection.setPrefWidth(650);

        // Card for Details
        VBox detailsCard = new VBox(20);
        detailsCard.setPadding(new Insets(25));
        detailsCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ced4da; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 0);");

        // Title and Type Badge
        Label titleLabel = new Label(event.getNom());
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        titleLabel.setTextFill(Color.web("#1F3A5F"));
        titleLabel.setWrapText(true);

        Label typeLabel = new Label(event.getTypeEvent().getNom());
        typeLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        typeLabel.setTextFill(Color.WHITE);
        typeLabel.setStyle("-fx-background-color: #007BFF; -fx-padding: 6 12 6 12; -fx-background-radius: 6;");

        HBox titleBox = new HBox(15, titleLabel, typeLabel);
        titleBox.setAlignment(Pos.CENTER_LEFT);

        // Image Section
        ImageView imageView = new ImageView();
        if (event.getImageUrl() != null && !event.getImageUrl().isEmpty()) {
            try {
                File imageFile = new File(event.getImageUrl());
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                    imageView.setFitWidth(600);
                    imageView.setFitHeight(300);
                    imageView.setPreserveRatio(true);
                    imageView.setStyle("-fx-background-radius: 8;");
                } else {
                    imageView.setImage(new Image("file:resources/images/placeholder.png")); // Fallback placeholder
                    imageView.setFitWidth(600);
                    imageView.setFitHeight(300);
                }
            } catch (Exception e) {
                imageView.setImage(new Image("file:resources/images/placeholder.png")); // Fallback placeholder
                imageView.setFitWidth(600);
                imageView.setFitHeight(300);
            }
        } else {
            imageView.setImage(new Image("file:resources/images/placeholder.png")); // Fallback placeholder
            imageView.setFitWidth(600);
            imageView.setFitHeight(300);
        }

        // Description Section
        Label descHeader = new Label("Description");
        descHeader.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
        descHeader.setTextFill(Color.web("#1F3A5F"));

        Label descLabel = new Label(event.getDescription());
        descLabel.setFont(Font.font("Arial", 14));
        descLabel.setTextFill(Color.web("#495057"));
        descLabel.setWrapText(true);
        descLabel.setMaxWidth(600);

        VBox descSection = new VBox(10, descHeader, descLabel);

        // Details Section
        Label detailsHeader = new Label("Détails");
        detailsHeader.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
        detailsHeader.setTextFill(Color.web("#1F3A5F"));

        Label dateLabel = new Label("Date: " + event.getDate().format(DateTimeFormatter.ofPattern("dd MMMM yyyy à HH:mm")));
        dateLabel.setFont(Font.font("Arial", 14));
        dateLabel.setTextFill(Color.web("#495057"));

        Label lieuLabel = new Label("Lieu: " + event.getLieu());
        lieuLabel.setFont(Font.font("Arial", 14));
        lieuLabel.setTextFill(Color.web("#495057"));

        Label availabilityLabel = new Label(event.isDisponibilite() ? "Places disponibles" : "Complet");
        availabilityLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));
        availabilityLabel.setTextFill(event.isDisponibilite() ? Color.web("#28A745") : Color.web("#DC3545"));
        availabilityLabel.setStyle("-fx-background-color: " + (event.isDisponibilite() ? "#D4EDDA" : "#F8D7DA") + "; -fx-padding: 6 12 6 12; -fx-background-radius: 6;");

        VBox detailsList = new VBox(15, dateLabel, lieuLabel, availabilityLabel);

        VBox detailsSectionInner = new VBox(10, detailsHeader, detailsList);

        detailsCard.getChildren().addAll(titleBox, imageView, descSection, detailsSectionInner);
        detailsSection.getChildren().add(detailsCard);

        // Right Section (Participate)
        VBox participateSection = new VBox(20);
        participateSection.setPrefWidth(300);

        // Card for Participation
        VBox participateCard = new VBox(20);
        participateCard.setPadding(new Insets(25));
        participateCard.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ced4da; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.15), 15, 0, 0, 0);");

        Label participateHeader = new Label("Participer");
        participateHeader.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 20));
        participateHeader.setTextFill(Color.web("#1F3A5F"));

        Button participateButton = new Button(event.isParticipant(loggedInUserId) ? "Annuler ma participation" : "Je participe");
        participateButton.setPrefWidth(250);
        styleButton(participateButton, event.isParticipant(loggedInUserId) ? "#DC3545" : "#007BFF");
        participateButton.setOnAction(e -> {
            if (event.isParticipant(loggedInUserId)) {
                event.removeParticipant(loggedInUserId);
                participateButton.setText("Je participe");
                styleButton(participateButton, "#007BFF");
            } else {
                event.addParticipant(loggedInUserId);
                participateButton.setText("Annuler ma participation");
                styleButton(participateButton, "#DC3545");
            }
            eventService.save(event);
        });

        participateCard.getChildren().addAll(participateHeader, participateButton);
        participateSection.getChildren().add(participateCard);

        mainLayout.getChildren().addAll(detailsSection, participateSection);

        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setScene(scene);
        stage.setTitle(event.getNom());
        stage.show();
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20 10 20;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20 10 20;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 10 20 10 20;"));
    }

    private String darkenColor(String hexColor) {
        Color color = Color.web(hexColor);
        double factor = 0.85;
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255 * factor),
                (int) (color.getGreen() * 255 * factor),
                (int) (color.getBlue() * 255 * factor));
    }
}