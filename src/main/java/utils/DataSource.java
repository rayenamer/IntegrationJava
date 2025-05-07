package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataSource {
    private static DataSource instance;
    private Connection connection;

    // Remplace par tes infos de connexion
    private final String URL = "jdbc:mysql://localhost:3306/careera";
    private final String USER = "root";
    private final String PASSWORD = "";

    private DataSource() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            System.out.println("Connexion établie à la base de données.");
        } catch (SQLException e) {
            System.err.println("Erreur de connexion à la base : " + e.getMessage());
        }
    }

    public static DataSource getInstance() {
        if (instance == null) {
            instance = new DataSource();
        }
        return instance;
    }

    public Connection getConnection() {
        return connection;
    }
}
