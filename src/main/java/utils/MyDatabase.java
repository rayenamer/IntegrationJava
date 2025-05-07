package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    private final String url = "jdbc:mysql://localhost:3306/careera";
    private final String user = "root";
    private final String password = "";
    private Connection cnx;
    private static MyDatabase instance;

    static {
        try {
            // Load the MySQL JDBC driver
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("MySQL JDBC Driver not found.");
            e.printStackTrace();
        }
    }

    private MyDatabase() {
        try {
            // Ensure the connection is established
            cnx = DriverManager.getConnection(url, user, password);
            if (cnx != null) {
                System.out.println("Connexion à la base de données établie avec succès");
            } else {
                System.err.println("Échec de la connexion à la base de données");
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la connexion à la base de données:");
            System.err.println("URL: " + url);
            System.err.println("Utilisateur: " + user);
            e.printStackTrace();
        }
    }

    public static synchronized MyDatabase getInstance() {
        if (instance == null) {
            instance = new MyDatabase();
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            // Check if connection is still valid
            if (cnx == null || cnx.isClosed()) {
                System.out.println("Reconnexion à la base de données...");
                cnx = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la vérification de la connexion:");
            e.printStackTrace();
        }
        return cnx;
    }
}
