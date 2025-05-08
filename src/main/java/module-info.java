module java {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires cloudinary.core;
    requires java.net.http;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;

    // Ouvrir les packages pour FXML
    opens controllers to javafx.fxml;
    opens entities to javafx.fxml;
    opens services to javafx.fxml;
    opens utils to javafx.fxml; // Add this if utils needs to be accessed by FXML

    // Exporter les packages
    exports controllers;
    exports entities;
    exports services;
    exports utils;
    exports test;// Add this if utils needs to be accessed by other modules
}