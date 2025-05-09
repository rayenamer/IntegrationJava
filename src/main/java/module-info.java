module java {
    requires javafx.fxml;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires cloudinary.core;
    requires com.google.gson;
    requires java.sql;
    requires java.mail;
    requires com.github.librepdf.openpdf;
    requires de.jensd.fx.glyphs.fontawesome;
    requires jbcrypt;
    requires javafx.web;
    requires java.desktop;
    requires cage;

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