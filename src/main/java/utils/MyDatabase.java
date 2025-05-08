package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/careera";
    private static final String USER = "root";
    private static final String PASSWORD = "";
    private static MyDatabase instance;
    private Connection connection;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("✅ Driver MySQL chargé avec succès");
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver MySQL non trouvé");
            e.printStackTrace();
        }
    }

    private MyDatabase() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            if (connection != null && !connection.isClosed()) {
                System.out.println("✅ Connexion à la base de données établie avec succès");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la connexion à la base de données:");
            System.err.println("URL: " + URL);
            System.err.println("Utilisateur: " + USER);
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
            if (connection == null || connection.isClosed()) {
                System.out.println("🔄 Reconnexion à la base de données...");
                connection = DriverManager.getConnection(URL, USER, PASSWORD);
            }
            return connection;
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la connexion:");
            e.printStackTrace();
            throw new RuntimeException("Impossible d'obtenir une connexion à la base de données", e);
        }
    }

    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✅ Connexion fermée avec succès");
            } catch (SQLException e) {
                System.err.println("❌ Erreur lors de la fermeture de la connexion:");
                e.printStackTrace();
            }
        }
    }
}
