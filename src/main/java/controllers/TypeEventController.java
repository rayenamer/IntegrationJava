package controllers;

import entities.TypeEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import services.TypeEventService;

public class TypeEventController {
    private final TypeEventService typeService = new TypeEventService();
    private ObservableList<TypeEvent> typeEvents = FXCollections.observableArrayList();

    public void show(Stage stage) {
        loadTypeEvents();

        // Title
        Label title = new Label("Gestion des Types d'Événements");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 28));
        title.setTextFill(Color.web("#1F3A5F"));
        VBox titleBox = new VBox(title);
        titleBox.setAlignment(Pos.CENTER);
        titleBox.setPadding(new Insets(20, 0, 20, 0));

        // Navigation Buttons
        Button viewEventsButton = new Button("Consulter les EVENEMENTS");
        styleButton(viewEventsButton, "#1F3A5F");
        viewEventsButton.setTooltip(new Tooltip("View all events"));
        viewEventsButton.setOnAction(e -> new EvenementController().show(stage)); // Revert to reusing the same stage

        Button addButton = new Button("Ajouter un type d'événement");
        styleButton(addButton, "#28A745");
        addButton.setTooltip(new Tooltip("Add a new event type"));
        addButton.setOnAction(e -> {
            Stage newStage = new Stage();
            newStage.setTitle("Ajouter un type d'événement");
            showTypeForm(newStage, null);
        });

        HBox navButtons = new HBox(15, viewEventsButton, addButton);
        navButtons.setAlignment(Pos.CENTER_LEFT);
        navButtons.setPadding(new Insets(10));

        // Table
        TableView<TypeEvent> table = new TableView<>();
        table.setItems(typeEvents);
        table.setStyle("-fx-background-color: white; -fx-background-radius: 8; -fx-border-radius: 8; -fx-border-color: #dee2e6; -fx-padding: 10;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(10);
        table.setEffect(shadow);

        // Define columns
        TableColumn<TypeEvent, String> nomCol = new TableColumn<>("nom");
        nomCol.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        styleColumn(nomCol);

        TableColumn<TypeEvent, Void> actionsCol = new TableColumn<>("");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Modifier");
            private final Button deleteButton = new Button("Supprimer");

            {
                styleButton(editButton, "#28A745");
                editButton.setTooltip(new Tooltip("Edit this event type"));
                styleButton(deleteButton, "#DC3545");
                deleteButton.setTooltip(new Tooltip("Delete this event type"));
                editButton.setOnAction(e -> {
                    TypeEvent type = getTableView().getItems().get(getIndex());
                    Stage newStage = new Stage();
                    newStage.setTitle("Modifier un type d'événement");
                    showTypeForm(newStage, type);
                });
                deleteButton.setOnAction(e -> {
                    TypeEvent type = getTableView().getItems().get(getIndex());
                    if (confirm("Confirm Delete", "Are you sure you want to delete " + type.getNom() + "?")) {
                        typeService.delete(type.getId());
                        loadTypeEvents();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(5, editButton, deleteButton);
                    setGraphic(buttons);
                }
            }
        });

        table.getColumns().addAll(nomCol, actionsCol);

        // Alternating row colors and hover effect
        table.setRowFactory(tv -> {
            TableRow<TypeEvent> row = new TableRow<>() {
                @Override
                protected void updateItem(TypeEvent item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setStyle("");
                        setTooltip(null);
                    } else {
                        if (getIndex() % 2 == 0) {
                            setStyle("-fx-background-color: #f8f9fa;");
                        } else {
                            setStyle("-fx-background-color: white;");
                        }
                        setTooltip(new Tooltip("Nom: " + item.getNom()));
                    }
                }
            };
            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("-fx-background-color: #e9ecef;");
                }
            });
            row.setOnMouseExited(e -> {
                if (!row.isEmpty()) {
                    row.setStyle(row.getIndex() % 2 == 0 ? "-fx-background-color: #f8f9fa;" : "-fx-background-color: white;");
                }
            });
            return row;
        });

        // Main Layout
        VBox mainLayout = new VBox(15, titleBox, navButtons, table);
        mainLayout.setStyle("-fx-background-color: #E9F9EF;");
        mainLayout.setPadding(new Insets(20));
        Scene scene = new Scene(mainLayout, 800, 600);
        stage.setScene(scene);
        stage.setTitle("Gestion des Types d'Événements");
        stage.show();
    }

    private void showTypeForm(Stage stage, TypeEvent type) {
        VBox form = new VBox(15);
        form.setPadding(new Insets(20));
        form.setAlignment(Pos.CENTER);
        form.setStyle("-fx-background-color: #E9F9EF; -fx-border-color: #dee2e6; -fx-border-radius: 5; -fx-background-radius: 5;");
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.web("#000000", 0.2));
        shadow.setRadius(10);
        form.setEffect(shadow);

        Label formTitle = new Label(type == null ? "Ajouter un type d'événement" : "Modifier un type d'événement");
        formTitle.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        formTitle.setTextFill(Color.web("#1F3A5F"));

        TextField nomField = new TextField(type != null ? type.getNom() : "");
        nomField.setPromptText("Nom du type d'événement");
        nomField.setStyle("-fx-font-size: 14px; -fx-border-color: #dee2e6; -fx-background-radius: 5;");

        Button saveButton = new Button("Enregistrer");
        styleButton(saveButton, "#28A745");
        saveButton.setTooltip(new Tooltip("Save event type"));
        saveButton.setOnAction(e -> {
            try {
                TypeEvent newType = new TypeEvent(type != null ? type.getId() : 0, nomField.getText());
                typeService.save(newType);
                loadTypeEvents();
                stage.close();
            } catch (Exception ex) {
                showAlert("Error", "Invalid input: " + ex.getMessage());
            }
        });

        Button cancelButton = new Button("Annuler");
        styleButton(cancelButton, "#DC3545");
        cancelButton.setTooltip(new Tooltip("Cancel and return"));
        cancelButton.setOnAction(e -> stage.close());

        HBox buttons = new HBox(10, saveButton, cancelButton);
        buttons.setAlignment(Pos.CENTER);
        form.getChildren().addAll(formTitle, nomField, buttons);

        Scene scene = new Scene(form, 400, 300);
        stage.setScene(scene);
        stage.show();
    }

    private void styleButton(Button button, String color) {
        button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;");
        button.setOnMouseEntered(e -> button.setStyle("-fx-background-color: " + darkenColor(color) + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;"));
        button.setOnMouseExited(e -> button.setStyle("-fx-background-color: " + color + "; -fx-text-fill: white; -fx-font-size: 14px; -fx-background-radius: 5; -fx-padding: 8 15 8 15;"));
    }

    private void styleColumn(TableColumn<TypeEvent, ?> column) {
        column.setStyle("-fx-font-size: 14px; -fx-font-family: 'Arial'; -fx-alignment: CENTER; -fx-background-color: #e9ecef; -fx-text-fill: #1F3A5F; -fx-font-weight: bold;");
    }

    private String darkenColor(String hexColor) {
        Color color = Color.web(hexColor);
        double factor = 0.8;
        return String.format("#%02X%02X%02X",
                (int) (color.getRed() * 255 * factor),
                (int) (color.getGreen() * 255 * factor),
                (int) (color.getBlue() * 255 * factor));
    }

    private void loadTypeEvents() {
        typeEvents.setAll(typeService.findAll());
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private boolean confirm(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        return alert.showAndWait().get() == ButtonType.OK;
    }
}