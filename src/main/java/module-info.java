module tn.esprit.careera {
    requires javafx.fxml;
    requires javafx.controls;
    requires javafx.graphics;
    requires com.google.protobuf;
    requires java.mail;
    requires cloudinary.core;
    requires com.google.gson;
    requires java.sql;
    requires com.github.librepdf.openpdf;
    requires de.jensd.fx.glyphs.fontawesome;
    requires jbcrypt;
    requires javafx.web;
    requires java.desktop;
    requires cage;
    requires java.net.http;

    // Ouvrir les packages pour FXML
    opens controllers to javafx.fxml;
    opens entities to javafx.fxml;
    opens services to javafx.fxml;
    opens utils to javafx.fxml;

    // Exporter les packages
    exports controllers;
    exports entities;
    exports services;
    exports utils;
    exports test;
}