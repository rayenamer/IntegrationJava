
package controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import javafx.scene.web.WebEngine;

import java.io.File;

public class PDFViewerController {
    @FXML private WebView webView;
    @FXML private Button closeButton;
    @FXML private Button printButton;
    @FXML private Button zoomInButton;
    @FXML private Button zoomOutButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageLabel;
    @FXML private Label statusLabel;

    private String pdfPath;
    private WebEngine webEngine;
    private double zoomFactor = 1.0;
    private int currentPage = 1;
    private int totalPages = 1;

    @FXML
    public void initialize() {
        webEngine = webView.getEngine();

        // Set up close button
        closeButton.setOnAction(event -> {
            Stage stage = (Stage) closeButton.getScene().getWindow();
            stage.close();
        });

        // Set up print button
        printButton.setOnAction(event -> {
            webEngine.print(null);
        });

        // Set up zoom buttons
        zoomInButton.setOnAction(event -> {
            zoomFactor += 0.1;
            webView.setZoom(zoomFactor);
        });

        zoomOutButton.setOnAction(event -> {
            if (zoomFactor > 0.2) {
                zoomFactor -= 0.1;
                webView.setZoom(zoomFactor);
            }
        });

        // Set up page navigation buttons
        prevPageButton.setOnAction(event -> {
            if (currentPage > 1) {
                currentPage--;
                updatePageLabel();
                // TODO: Implement page navigation
            }
        });

        nextPageButton.setOnAction(event -> {
            if (currentPage < totalPages) {
                currentPage++;
                updatePageLabel();
                // TODO: Implement page navigation
            }
        });

        // Set up WebEngine listeners
        webEngine.setOnStatusChanged(event -> {
            statusLabel.setText(event.getData());
        });

        webEngine.setOnError(event -> {
            statusLabel.setText("Erreur: " + event.getMessage());
        });
    }

    private void updatePageLabel() {
        pageLabel.setText("Page: " + currentPage + "/" + totalPages);
    }

    public void setPdfPath(String path) {
        this.pdfPath = path;
        File pdfFile = new File(path);
        if (pdfFile.exists()) {
            webEngine.load(pdfFile.toURI().toString());
            statusLabel.setText("Chargement du PDF...");
        } else {
            statusLabel.setText("Erreur: Fichier PDF non trouvé");
        }
    }
} 
