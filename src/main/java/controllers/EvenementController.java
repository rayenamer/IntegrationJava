package controllers;

import entities.Evenement;
import entities.TypeEvent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.EvenementService;
import services.TypeEventService;
import services.AIDescriptionService;
import javafx.application.Platform;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EvenementController {
    private final EvenementService eventService = new EvenementService();
    private final TypeEventService typeService = new TypeEventService();
    private ObservableList<Evenement> events = FXCollections.observableArrayList();
    private ObservableList<TypeEvent> typeEvents = FXCollections.observableArrayList();

    public void show(Stage stage) {
        loadTypeEvents();
        loadEvents();

        // Title
        Label title = new Label("Gestion des Événements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTextFill(Color.web("#1F3A5F"));
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 20, 0));

        // Navigation Buttons
        Button viewTypesButton = new Button("Types d'événements");
        styleButton(viewTypesButton, "#1F3A5F");
        viewTypesButton.setTooltip(new Tooltip("Voir tous les types d'événements"));
        viewTypesButton.setOnAction(e -> new TypeEventController().show(stage));

        Button addButton = new Button("Ajouter un événement");
        styleButton(addButton, "#28A745");
        addButton.setTooltip(new Tooltip("Créer un nouvel événement"));
        addButton.setOnAction(e -> {
            Stage newStage = new Stage();
            newStage.setTitle("Ajouter un événement");
            showEventForm(newStage, null);
        });

        HBox navButtons = new HBox(20, viewTypesButton, addButton);
        navButtons.setAlignment(Pos.CENTER_LEFT);
        navButtons.setPadding(new Insets(15));

        // Table
        TableView<Evenement> table = new TableView<>();
        table.setItems(events);
        table.setStyle("-fx-background-color: white; -fx-background-radius: 10; -fx-border-radius: 10; -fx-border-color: #ced4da; -fx-padding: 15;");
        table.setMinWidth(1000);
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.15));
        shadow.setRadius(15);
        table.setEffect(shadow);

        // Define columns with improved widths
        TableColumn<Evenement, String> nomCol = new TableColumn<>("Nom");
        nomCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        nomCol.setPrefWidth(150);
        styleColumn(nomCol);

        TableColumn<Evenement, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getDate();
            return new SimpleStringProperty(date != null ? date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "");
        });
        dateCol.setPrefWidth(150);
        styleColumn(dateCol);

        TableColumn<Evenement, String> lieuCol = new TableColumn<>("Lieu");
        lieuCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLieu()));
        lieuCol.setPrefWidth(120);
        styleColumn(lieuCol);

        TableColumn<Evenement, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(cellData -> {
            TypeEvent type = cellData.getValue().getTypeEvent();
            return new SimpleStringProperty(type != null ? type.getNom() : "");
        });
        typeCol.setPrefWidth(100);
        styleColumn(typeCol);

        TableColumn<Evenement, String> dispCol = new TableColumn<>("Disponibilité");
        dispCol.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().isDisponibilite() ? "Disponible" : "Complet"));
        dispCol.setPrefWidth(100);
        styleColumn(dispCol);

        TableColumn<Evenement, String> participantsCol = new TableColumn<>("Participants");
        participantsCol.setCellValueFactory(cellData -> {
            List<Integer> participantsIds = cellData.getValue().getParticipantsIds();
            int count = participantsIds != null ? participantsIds.size() : 0;
            return new SimpleStringProperty(String.valueOf(count));
        });
        participantsCol.setPrefWidth(100);
        styleColumn(participantsCol);

        TableColumn<Evenement, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setPrefWidth(220);
        actionsCol.setMinWidth(220);
        styleColumn(actionsCol);
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                styleButton(editButton, "#28A745");
                editButton.setTooltip(new Tooltip("Modifier cet événement"));
                editButton.setMinWidth(90);
                editButton.setPrefWidth(90);
                editButton.setMaxWidth(Double.MAX_VALUE);
                styleButton(deleteButton, "#DC3545");
                deleteButton.setTooltip(new Tooltip("Supprimer cet événement"));
                deleteButton.setMinWidth(90);
                deleteButton.setPrefWidth(90);
                deleteButton.setMaxWidth(Double.MAX_VALUE);

                editButton.setOnAction(e -> {
                    Evenement event = getTableView().getItems().get(getIndex());
                    Stage newStage = new Stage();
                    newStage.setTitle("Modifier un événement");
                    showEventForm(newStage, event);
                });
                deleteButton.setOnAction(e -> {
                    Evenement event = getTableView().getItems().get(getIndex());
                    if (confirm("Confirmation", "Voulez-vous vraiment supprimer " + event.getNom() + " ?")) {
                        eventService.delete(event.getId());
                        loadEvents();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(10, editButton, deleteButton);
                    buttons.setAlignment(Pos.CENTER);
                    buttons.setSpacing(10);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(nomCol, dateCol, lieuCol, typeCol, dispCol, participantsCol, actionsCol);

        // Enhanced row styling
        table.setRowFactory(tv -> {
            TableRow<Evenement> row = new TableRow<>() {
                @Override
                protected void updateItem(Evenement item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        setTooltip(null);
                    } else {
                        setStyle(getIndex() % 2 == 0 ? "-fx-background-color: #f1f3f5;" : "-fx-background-color: white;");
                        String tooltipText = String.format("Nom: %s\nDescription: %s\nDate: %s\nLieu: %s\nImage: %s\nParticipants: %s",
                                item.getNom(), item.getDescription(),
                                item.getDate() != null ? item.getDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : "N/A",
                                item.getLieu(),
                                item.getImageUrl() != null ? item.getImageUrl() : "Aucune",
                                item.getParticipantsIds() != null ? String.valueOf(item.getParticipantsIds().size()) : "0");
                        setTooltip(new Tooltip(tooltipText));
                    }
                }
            };
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e2e6ea;");
                }
            });
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    row.setStyle(row.getIndex() % 2 == 0 ? "-fx-background-color: #f1f3f5;" : "-fx-background-color: white;");
                }
            });
            return row;
        });

        // Main Layout
        VBox mainLayout = new VBox(20, titleBox, navButtons, table);
        mainLayout.setStyle("-fx-background-color: #F8FAFC;");
        mainLayout.setPadding(new Insets(25));
        Scene scene = new Scene(mainLayout, 1000, 700);
        stage.setScene(scene);
        stage.setTitle("Gestion des Événements");
        stage.show();
    }

    private void showEventForm(Stage stage, Evenement event) {
        VBox form = new VBox(20);
        form.setPadding(new Insets(25));
        form.setAlignment(Pos.TOP_CENTER);
        form.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #ced4da; -fx-border-radius: 8; -fx-background-radius: 8;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.15));
        shadow.setRadius(15);
        form.setEffect(shadow);

        // Form Title
        Label formTitle = new Label(event == null ? "Nouvel Événement" : "Modifier l'Événement");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        formTitle.setTextFill(Color.web("#1F3A5F"));

        // Form Fields with Labels
        Label nomLabel = new Label("Nom de l'événement");
        nomLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField nomField = new TextField(event != null ? event.getNom() : "");
        nomField.setPromptText("Entrez le nom de l'événement");
        nomField.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6; -fx-padding: 10;");
        nomField.setPrefWidth(400);

        Label lieuLabel = new Label("Lieu");
        lieuLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        TextField lieuField = new TextField(event != null ? event.getLieu() : "");
        lieuField.setPromptText("Entrez le lieu");
        lieuField.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6; -fx-padding: 10;");
        lieuField.setPrefWidth(400);

        Label typeLabel = new Label("Type d'événement");
        typeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        ComboBox<TypeEvent> typeCombo = new ComboBox<>(typeEvents);
        if (event != null && event.getTypeEvent() != null) {
            typeCombo.setValue(event.getTypeEvent());
        }
        typeCombo.setPromptText("Sélectionnez un type");
        typeCombo.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6;");
        typeCombo.setPrefWidth(400);
        typeCombo.setCellFactory(lv -> new ListCell<TypeEvent>() {
            @Override
            protected void updateItem(TypeEvent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });
        typeCombo.setButtonCell(new ListCell<TypeEvent>() {
            @Override
            protected void updateItem(TypeEvent item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getNom());
            }
        });

        Label descLabel = new Label("Description");
        descLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        Button generateDescButton = new Button("Générer");
        styleButton(generateDescButton, "#1F3A5F");
        generateDescButton.setTooltip(new Tooltip("Générer une description automatique"));
        TextArea descField = new TextArea(event != null ? event.getDescription() : "");
        descField.setPromptText("Entrez la description");
        descField.setPrefRowCount(10);
        descField.setPrefHeight(220);
        descField.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6;");
        descField.setPrefWidth(400);
        HBox descBox = new HBox(15, descField, generateDescButton);
        descBox.setAlignment(Pos.CENTER_LEFT);

        generateDescButton.setOnAction(e -> {
            String nom = nomField.getText().trim();
            TypeEvent selectedType = typeCombo.getValue();
            String type = selectedType != null ? selectedType.getNom() : "";
            String lieu = lieuField.getText().trim();

            if (nom.isEmpty()) {
                showAlert("Erreur", "Veuillez entrer un nom pour l'événement.");
                return;
            }

            generateDescButton.setDisable(true);
            generateDescButton.setText("Génération...");
            new Thread(() -> {
                AIDescriptionService aiService = new AIDescriptionService();
                String generatedDescription = aiService.generateEventDescription(nom, type, lieu);
                Platform.runLater(() -> {
                    descField.setText(generatedDescription);
                    generateDescButton.setDisable(false);
                    generateDescButton.setText("Générer");
                });
            }).start();
        });

        Label dateLabel = new Label("Date");
        dateLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        DatePicker datePicker = new DatePicker(event != null && event.getDate() != null ? event.getDate().toLocalDate() : LocalDate.now());
        datePicker.setPromptText("Sélectionnez une date");
        datePicker.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6;");
        datePicker.setPrefWidth(400);

        Label timeLabel = new Label("Heure");
        timeLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        Spinner<Integer> hourSpinner = new Spinner<>(0, 23, event != null && event.getDate() != null ? event.getDate().getHour() : 0);
        hourSpinner.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6;");
        hourSpinner.setPrefWidth(100);
        hourSpinner.setEditable(true);
        Spinner<Integer> minuteSpinner = new Spinner<>(0, 59, event != null && event.getDate() != null ? event.getDate().getMinute() : 0);
        minuteSpinner.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6;");
        minuteSpinner.setPrefWidth(100);
        minuteSpinner.setEditable(true);
        HBox timeBox = new HBox(15, hourSpinner, new Label(":"), minuteSpinner);
        timeBox.setAlignment(Pos.CENTER_LEFT);

        Label dispLabel = new Label("Disponibilité");
        dispLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        CheckBox dispCheck = new CheckBox("Places disponibles");
        dispCheck.setSelected(event != null ? event.isDisponibilite() : true);
        dispCheck.setStyle("-fx-font-size: 14px;");

        Label imageLabel = new Label("Image (glisser-déposer)");
        imageLabel.setFont(Font.font("Arial", FontWeight.SEMI_BOLD, 14));
        Label imageDropArea = new Label(event != null && event.getImageUrl() != null ? "Image: " + new File(event.getImageUrl()).getName() : "Glissez une image ici");
        imageDropArea.setStyle("-fx-font-size: 14px; -fx-text-fill: #1F3A5F; -fx-background-color: #f1f3f5; -fx-border-color: #ced4da; -fx-border-radius: 6; -fx-padding: 15; -fx-alignment: center;");
        imageDropArea.setPrefSize(400, 120);
        TextField imageField = new TextField(event != null && event.getImageUrl() != null ? event.getImageUrl() : "");
        imageField.setPromptText("Chemin de l'image");
        imageField.setStyle("-fx-font-size: 14px; -fx-border-color: #ced4da; -fx-background-radius: 6; -fx-padding: 10;");
        imageField.setPrefWidth(400);
        imageField.setEditable(false);

        imageDropArea.setOnDragOver(event1 -> {
            if (event1.getDragboard().hasFiles()) {
                event1.acceptTransferModes(TransferMode.COPY);
            }
            event1.consume();
        });

        imageDropArea.setOnDragDropped(event1 -> {
            Dragboard db = event1.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (!files.isEmpty()) {
                    File file = files.get(0);
                    imageField.setText(file.getAbsolutePath());
                    imageDropArea.setText("Image: " + file.getName());
                    success = true;
                }
            }
            event1.setDropCompleted(success);
            event1.consume();
        });

        VBox imageBox = new VBox(10, imageDropArea, imageField);

        // Buttons
        Button saveButton = new Button("Enregistrer");
        styleButton(saveButton, "#28A745");
        saveButton.setTooltip(new Tooltip("Enregistrer l'événement"));
        saveButton.setPrefWidth(150);
        saveButton.setOnAction(e -> {
            StringBuilder errors = new StringBuilder();
            String nom = nomField.getText().trim();
            if (nom.isEmpty()) {
                errors.append("- Le nom est requis\n");
            } else if (nom.length() < 3 || nom.length() > 100) {
                errors.append("- Le nom doit avoir entre 3 et 100 caractères\n");
            }

            String description = descField.getText().trim();
            if (description.isEmpty()) {
                errors.append("- La description est requise\n");
            }

            LocalDateTime eventDateTime = null;
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate == null) {
                errors.append("- La date est requise\n");
            } else {
                try {
                    LocalTime selectedTime = LocalTime.of(hourSpinner.getValue(), minuteSpinner.getValue());
                    eventDateTime = LocalDateTime.of(selectedDate, selectedTime);
                    if (eventDateTime.isBefore(LocalDateTime.now())) {
                        errors.append("- La date doit être dans le futur\n");
                    }
                } catch (Exception ex) {
                    errors.append("- Heure invalide\n");
                }
            }

            String lieu = lieuField.getText().trim();
            if (lieu.isEmpty()) {
                errors.append("- Le lieu est requis\n");
            } else if (lieu.length() > 100) {
                errors.append("- Le lieu ne doit pas dépasser 100 caractères\n");
            }

            TypeEvent selectedType = typeCombo.getValue();
            if (selectedType == null) {
                errors.append("- Le type d'événement est requis\n");
            }

            String imageUrl = imageField.getText().trim();
            if (!imageUrl.isEmpty()) {
                File imageFile = new File(imageUrl);
                if (!imageFile.exists() || !imageFile.isFile()) {
                    errors.append("- L'image sélectionnée est invalide\n");
                }
            }

            if (errors.length() > 0) {
                showAlert("Erreurs de validation", errors.toString());
                return;
            }

            try {
                Evenement newEvent = new Evenement(
                        event != null ? event.getId() : 0,
                        nom,
                        description,
                        eventDateTime,
                        lieu,
                        dispCheck.isSelected(),
                        selectedType,
                        imageUrl.isEmpty() ? null : imageUrl,
                        event != null ? event.getUserId() : null,
                        event != null ? event.getParticipantsIds() : null // Fix: Preserve participantsIds
                );
                eventService.save(newEvent);
                loadEvents();
                stage.close();
            } catch (Exception ex) {
                showAlert("Erreur", "Erreur lors de l'enregistrement : " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Annuler");
        styleButton(cancelButton, "#DC3545");
        cancelButton.setTooltip(new Tooltip("Annuler"));
        cancelButton.setPrefWidth(150);
        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(20, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);

        // Organize form sections
        VBox eventInfoSection = new VBox(10, nomLabel, nomField, lieuLabel, lieuField, typeLabel, typeCombo);
        VBox descSection = new VBox(10, descLabel, descBox);
        VBox dateTimeSection = new VBox(10, dateLabel, datePicker, timeLabel, timeBox);
        VBox statusSection = new VBox(10, dispLabel, dispCheck);
        VBox imageSection = new VBox(10, imageLabel, imageBox);

        form.getChildren().addAll(
                formTitle,
                new Separator(),
                eventInfoSection,
                new Separator(),
                descSection,
                new Separator(),
                dateTimeSection,
                new Separator(),
                statusSection,
                new Separator(),
                imageSection,
                new Separator(),
                buttons
        );

        // Wrap form in ScrollPane
        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        scrollPane.setStyle("-fx-background-color: #F8FAFC; -fx-border-color: #ced4da; -fx-border-radius: 8; -fx-background-radius: 8;");

        Scene scene = new Scene(scrollPane, 500, 750);
        stage.setScene(scene);
        stage.show();
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 12 8 12; -fx-ellipsis-string: '';");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 12 8 12; -fx-ellipsis-string: '';"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-font-weight: bold; -fx-background-radius: 6; -fx-padding: 8 12 8 12; -fx-ellipsis-string: '';"));
    }

    private void styleColumn(TableColumn<Evenement, ?> column) {
        column.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-alignment: CENTER; -fx-background-color: #e9ecef; -fx-text-fill: #1F3A5F; -fx-font-weight: bold; -fx-padding: 10;");
    }

    private String darkenColor(String hexColor) {
        Color color = Color.web(hexColor);
        double factor = 0.85;
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255 * factor),
                (int) (color.getGreen() * 255 * factor),
                (int) (color.getBlue() * 255 * factor));
    }

    private void loadEvents() {
        events.setAll(eventService.findAll());
    }

    private void loadTypeEvents() {
        typeEvents.setAll(typeService.findAll());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }

    private String extractImageUrl(String html) {
        Pattern pattern = Pattern.compile("src=\"([^\"]+\\.(?:png|jpg|jpeg))\"");
        Matcher matcher = pattern.matcher(html);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return null;
    }
}