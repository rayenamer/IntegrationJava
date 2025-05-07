package utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MyDatabase {

    // Informations de connexion
    private static final String URL = "jdbc:mysql://localhost:3306/careera?createDatabaseIfNotExist=true";
    private static final String USER = "root";     // Ton nom d'utilisateur MySQL
    private static final String PASSWORD = "";     // Ton mot de passe MySQL

    private Connection cnx;
    private static MyDatabase instance;

    private MyDatabase() {
        try {
            // Charger le driver JDBC
            Class.forName("com.mysql.cj.jdbc.Driver");
            
            // Établir la connexion
            cnx = DriverManager.getConnection(URL, USER, PASSWORD);
            
            if (cnx != null) {
                System.out.println("✅ Connexion à la base de données établie avec succès.");
            } else {
                System.err.println("❌ Échec de la connexion à la base de données.");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("❌ Driver JDBC non trouvé : " + e.getMessage());
            e.printStackTrace();
        } catch (SQLException e) {
            System.err.println("❌ Erreur de connexion à la base de données : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Méthode statique pour obtenir l'instance unique de la connexion.
     * @return instance unique de MyDatabase
     */
    public static MyDatabase getInstance() {
        if (instance == null) {
            synchronized (MyDatabase.class) {
                if (instance == null) {
                    instance = new MyDatabase();
                }
            }
        }
        return instance;
    }

    public Connection getCnx() {
        try {
            if (cnx == null || cnx.isClosed()) {
                // Tenter de rétablir la connexion si elle est fermée
                cnx = DriverManager.getConnection(URL, USER, PASSWORD);
                System.out.println("✅ Connexion rétablie avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la vérification de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
        return cnx;
    }

    public void closeConnection() {
        try {
            if (cnx != null && !cnx.isClosed()) {
                cnx.close();
                System.out.println("✅ Connexion fermée avec succès.");
            }
        } catch (SQLException e) {
            System.err.println("❌ Erreur lors de la fermeture de la connexion : " + e.getMessage());
            e.printStackTrace();
        }
    }
}
