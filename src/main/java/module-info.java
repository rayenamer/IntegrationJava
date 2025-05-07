module wiemwebsite.demo {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.protobuf;
    requires jakarta.mail;
    requires cloudinary.core;
    requires java.net.http;
    requires com.google.gson;
    requires java.sql;
    requires java.desktop;
    opens wiemwebsite.demo to javafx.fxml;
    opens wiemwebsite.demo.controllers to javafx.fxml;
    opens wiemwebsite.demo.models to javafx.fxml;

    exports wiemwebsite.demo;
    exports wiemwebsite.demo.controllers;
    exports wiemwebsite.demo.models;
}