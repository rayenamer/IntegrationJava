package wiemwebsite.demo.services;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import wiemwebsite.demo.models.CandidatureMission;
import wiemwebsite.demo.models.DemandeMission;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import java.sql.*;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DemandeMissionService {

    private static final Logger LOGGER = Logger.getLogger(DemandeMissionService.class.getName());
    private Connection connection;

    // Paramètres de connexion à la base de données
    private static final String URL = "jdbc:mysql://localhost:3306/careera?useSSL=false&serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASSWORD = "";

    public DemandeMissionService() {
        try {
            connection = DriverManager.getConnection(URL, USER, PASSWORD);
            LOGGER.info("Connexion à la base de données établie : " + URL);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'initialisation de la connexion : " + e.getMessage(), e);
            throw new RuntimeException("Impossible d'établir la connexion à la base de données", e);
        }
    }
    public boolean hasAcceptedCandidature(int missionId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM candidaturemission WHERE mission_id = ? AND etat = 'Acceptée'";
        try (Connection conn = DriverManager.getConnection(URL, USER, PASSWORD);
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, missionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    int count = rs.getInt(1);
                    LOGGER.info("Nombre de candidatures acceptées pour mission ID=" + missionId + " : " + count);
                    return count > 0;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de la vérification des candidatures acceptées pour mission ID=" + missionId + " : " + e.getMessage(), e);
            throw e;
        }
        return false;
    }
    // Méthode pour récupérer le titre de la mission à partir de offremission_id
    public String getMissionTitreByOffreMissionId(int offreMissionId) throws SQLException {
        String query = "SELECT titre FROM missionfreelencer WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, offreMissionId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("titre");
                }
            }
        }
        LOGGER.warning("Aucun titre de mission trouvé pour offremission_id=" + offreMissionId);
        return "Mission inconnue";
    }

    // CREATE: Ajouter une nouvelle demande et candidature
    public void ajouterDemande(DemandeMission demande) throws SQLException {
        // Étape 1 : Insérer dans demande_mission
        String demandeQuery = "INSERT INTO demande_mission (offremission_id, message, portfolio, status, userid) VALUES (?, ?, ?, ?, ?)";
        int demandeId;
        try (PreparedStatement stmt = connection.prepareStatement(demandeQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, demande.getOffremissionId());
            stmt.setString(2, demande.getMessage());
            stmt.setString(3, demande.getPortfolio());
            stmt.setString(4, demande.getStatus());
            stmt.setInt(5, demande.getUserid());
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Candidature insérée dans demande_mission pour mission ID=" + demande.getOffremissionId() + ", lignes affectées : " + rowsAffected);

            // Récupérer l'ID généré pour la demande
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    demandeId = generatedKeys.getInt(1);
                    demande.setId(demandeId);
                    LOGGER.info("ID généré pour la demande : " + demandeId);
                } else {
                    throw new SQLException("Échec de la récupération de l'ID pour demande_mission");
                }
            }
        }

        // Étape 2 : Récupérer le nom de l'utilisateur
        String userName = getUserName(demande.getUserid());

        // Étape 3 : Insérer dans candidaturemission
        String candidatureQuery = "INSERT INTO candidaturemission (mission_id, demande_id, etat, score, userid, user) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(candidatureQuery, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, demande.getOffremissionId());
            stmt.setInt(2, demandeId);
            stmt.setString(3, "En attente");
            stmt.setInt(4, 0); // Score initialisé à 0
            stmt.setInt(5, demande.getUserid());
            stmt.setString(6, userName);
            int rowsAffected = stmt.executeUpdate();
            LOGGER.info("Candidature insérée dans candidaturemission pour mission ID=" + demande.getOffremissionId() + ", lignes affectées : " + rowsAffected);

            // Récupérer l'ID généré pour la candidature
            try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    LOGGER.info("ID généré pour la candidature : " + generatedKeys.getInt(1));
                }
            }
        }
    }

    // Méthode pour récupérer le nom de l'utilisateur
    private String getUserName(int userId) throws SQLException {
        String query = "SELECT nom FROM user WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nom");
                }
            }
        }
        return "Utilisateur inconnu";
    }

    // Nouvelle méthode : Récupérer l'email de l'utilisateur
    public String getUserEmail(int userId) throws SQLException {
        String query = "SELECT email FROM user WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, userId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    String email = rs.getString("email");
                    return email != null ? email : "";
                }
            }
        }
        LOGGER.warning("Aucun email trouvé pour userId=" + userId);
        return "";
    }

    // Envoyer un e-mail d'acceptation
    public void sendAcceptanceEmail(String recipientEmail, String userName, String missionTitre) throws MessagingException {
        String host = "smtp.gmail.com";
        String port = "465";
        String senderEmail = "khwiemmm@gmail.com";
        String senderPassword = "hrdo qtwn sugr buxk";

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.ssl.enable", "true");
        props.put("mail.smtp.host", host);
        props.put("mail.smtp.port", port);
        props.put("mail.smtp.timeout", "30000");
        props.put("mail.smtp.connectiontimeout", "30000");
        props.put("mail.debug", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(senderEmail, senderPassword);
            }
        });
        session.setDebug(true);

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(senderEmail));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipientEmail));
            message.setSubject("Votre candidature a été acceptée !");
            message.setText("Bonjour " + userName + ",\n\n" +
                    "Félicitations ! Votre candidature pour la mission \"" + missionTitre + "\" a été acceptée.\n" +
                    "Vous serez contacté prochainement pour les prochaines étapes.\n\n" +
                    "Cordialement,\nL'équipe Careera");

            Transport.send(message);
            LOGGER.info("Email d'acceptation envoyé à " + recipientEmail);
        } catch (MessagingException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de l'email à " + recipientEmail + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // READ: Récupérer toutes les candidatures avec le titre de la mission
    public ObservableList<CandidatureMission> getAllCandidatures() throws SQLException {
        ObservableList<CandidatureMission> candidatures = FXCollections.observableArrayList();
        String query = "SELECT c.*, m.titre AS mission_titre " +
                "FROM candidaturemission c " +
                "LEFT JOIN missionfreelencer m ON c.mission_id = m.id";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                CandidatureMission candidature = new CandidatureMission(
                        rs.getInt("id"),
                        rs.getInt("mission_id"),
                        rs.getInt("demande_id"),
                        rs.getString("etat"),
                        rs.getInt("score"),
                        rs.getInt("userid"),
                        rs.getString("user"),
                        rs.getString("mission_titre") != null ? rs.getString("mission_titre") : "Mission inconnue"
                );
                candidatures.add(candidature);
            }
            LOGGER.info("Nombre de candidatures récupérées : " + candidatures.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération des candidatures : " + e.getMessage(), e);
            throw e;
        }
        return candidatures;
    }

    // READ: Récupérer les candidatures par mission_id avec le titre de la mission
    public ObservableList<CandidatureMission> getCandidaturesByMissionId(int missionId) throws SQLException {
        ObservableList<CandidatureMission> candidatures = FXCollections.observableArrayList();
        String query = "SELECT c.*, m.titre AS mission_titre " +
                "FROM candidaturemission c " +
                "LEFT JOIN missionfreelencer m ON c.mission_id = m.id " +
                "WHERE c.mission_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, missionId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    CandidatureMission candidature = new CandidatureMission(
                            rs.getInt("id"),
                            rs.getInt("mission_id"),
                            rs.getInt("demande_id"),
                            rs.getString("etat"),
                            rs.getInt("score"),
                            rs.getInt("userid"),
                            rs.getString("user"),
                            rs.getString("mission_titre") != null ? rs.getString("mission_titre") : "Mission inconnue"
                    );
                    candidatures.add(candidature);
                }
                LOGGER.info("Nombre de candidatures récupérées pour mission_id=" + missionId + " : " + candidatures.size());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération des candidatures pour mission_id=" + missionId + " : " + e.getMessage(), e);
            throw e;
        }
        return candidatures;
    }

    // READ: Récupérer une candidature par ID avec le titre de la mission
    public CandidatureMission getCandidatureById(int id) throws SQLException {
        String query = "SELECT c.*, m.titre AS mission_titre " +
                "FROM candidaturemission c " +
                "LEFT JOIN missionfreelencer m ON c.mission_id = m.id " +
                "WHERE c.id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    CandidatureMission candidature = new CandidatureMission(
                            rs.getInt("id"),
                            rs.getInt("mission_id"),
                            rs.getInt("demande_id"),
                            rs.getString("etat"),
                            rs.getInt("score"),
                            rs.getInt("userid"),
                            rs.getString("user"),
                            rs.getString("mission_titre") != null ? rs.getString("mission_titre") : "Mission inconnue"
                    );
                    LOGGER.info("Candidature récupérée : ID=" + id);
                    return candidature;
                } else {
                    LOGGER.warning("Aucune candidature trouvée pour ID=" + id);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération de la candidature ID=" + id + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // UPDATE: Mettre à jour une candidature
    public void modifierCandidature(CandidatureMission candidature) throws SQLException {
        String query = "UPDATE candidaturemission SET mission_id = ?, demande_id = ?, etat = ?, score = ?, userid = ?, user = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, candidature.getMissionId());
            stmt.setInt(2, candidature.getDemandeId());
            stmt.setString(3, candidature.getEtat());
            stmt.setInt(4, candidature.getScore());
            stmt.setInt(5, candidature.getUserid());
            stmt.setString(6, candidature.getUser());
            stmt.setInt(7, candidature.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Candidature mise à jour : ID=" + candidature.getId());
            } else {
                LOGGER.warning("Aucune candidature mise à jour pour ID=" + candidature.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la mise à jour de la candidature ID=" + candidature.getId() + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // DELETE: Supprimer une candidature
    public void supprimerCandidature(int id) throws SQLException {
        String query = "DELETE FROM candidaturemission WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Candidature supprimée : ID=" + id);
            } else {
                LOGGER.warning("Aucune candidature supprimée pour ID=" + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de la candidature ID=" + id + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // READ: Récupérer toutes les demandes
    public ObservableList<DemandeMission> getAllDemandes() throws SQLException {
        ObservableList<DemandeMission> demandes = FXCollections.observableArrayList();
        String query = "SELECT * FROM demande_mission";
        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                DemandeMission demande = new DemandeMission(
                        rs.getInt("id"),
                        rs.getInt("offremission_id"),
                        rs.getString("message"),
                        rs.getString("portfolio"),
                        rs.getString("status"),
                        rs.getInt("userid")
                );
                demandes.add(demande);
            }
            LOGGER.info("Nombre de demandes récupérées : " + demandes.size());
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération des demandes : " + e.getMessage(), e);
            throw e;
        }
        return demandes;
    }

    // READ: Récupérer une demande par ID
    public DemandeMission getDemandeById(int id) throws SQLException {
        String query = "SELECT * FROM demande_mission WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    DemandeMission demande = new DemandeMission(
                            rs.getInt("id"),
                            rs.getInt("offremission_id"),
                            rs.getString("message"),
                            rs.getString("portfolio"),
                            rs.getString("status"),
                            rs.getInt("userid")
                    );
                    LOGGER.info("Demande récupérée : ID=" + id);
                    return demande;
                } else {
                    LOGGER.warning("Aucune demande trouvée pour ID=" + id);
                    return null;
                }
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la récupération de la demande ID=" + id + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // UPDATE: Mettre à jour une demande
    public void modifierDemande(DemandeMission demande) throws SQLException {
        String query = "UPDATE demande_mission SET offremission_id = ?, message = ?, portfolio = ?, status = ?, userid = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, demande.getOffremissionId());
            stmt.setString(2, demande.getMessage());
            stmt.setString(3, demande.getPortfolio());
            stmt.setString(4, demande.getStatus());
            stmt.setInt(5, demande.getUserid());
            stmt.setInt(6, demande.getId());
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Demande mise à jour : ID=" + demande.getId());
            } else {
                LOGGER.warning("Aucune demande mise à jour pour ID=" + demande.getId());
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la mise à jour de la demande ID=" + demande.getId() + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // DELETE: Supprimer une demande
    public void supprimerDemande(int id) throws SQLException {
        String query = "DELETE FROM demande_mission WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                LOGGER.info("Demande supprimée : ID=" + id);
            } else {
                LOGGER.warning("Aucune demande supprimée pour ID=" + id);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Erreur SQL lors de la suppression de la demande ID=" + id + " : " + e.getMessage(), e);
            throw e;
        }
    }

    // Fermer la connexion
    public void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                LOGGER.info("Connexion à la base de données fermée.");
            } catch (SQLException e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la fermeture de la connexion : " + e.getMessage(), e);
            }
        }
    }
}