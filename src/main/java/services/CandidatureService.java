package services;

import entities.Candidature;
import entities.Candidature.StatutCandidature;
import entities.Offre;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.io.File;

public class CandidatureService {

    private final Connection connection;
    private final PDFService pdfService;

    public CandidatureService() {
        this.connection = MyDatabase.getInstance().getCnx(); // Assure-toi que getCnx() retourne bien un Connection
        this.pdfService = new PDFService();
    }

    public List<Candidature> getAll() throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String query = "SELECT * FROM candidature";
        PreparedStatement ps = connection.prepareStatement(query);
        ResultSet rs = ps.executeQuery();

        while (rs.next()) {
            Candidature c = new Candidature();
            c.setId(rs.getInt("id"));

            Offre offre = new Offre();
            offre.setId(rs.getInt("offre_id"));
            c.setOffre(offre);

            // Correction ici pour gérer l'enum de manière sécurisée
            String statutStr = rs.getString("statut");
            StatutCandidature statut = null;
            if (statutStr != null) {
                try {
                    statut = StatutCandidature.valueOf(statutStr.toUpperCase()); // Conversion en majuscule
                } catch (IllegalArgumentException e) {
                    statut = StatutCandidature.EN_ATTENTE; // Statut par défaut en cas d'erreur
                }
            }
            c.setStatut(statut);

            Timestamp ts = rs.getTimestamp("datesoumission");
            if (ts != null) {
                c.setDateSoumission(ts.toLocalDateTime());
            }

            c.setUtilisateur(rs.getString("utilisateur"));
            c.setCv(rs.getString("cv"));
            c.setLettreMotivation(rs.getString("lettremotivation"));
            candidatures.add(c);
        }

        return candidatures;
    }

    public List<Candidature> recuperer(int limit, int offset) throws SQLException {
        List<Candidature> candidatures = new ArrayList<>();
        String query = "SELECT * FROM candidature LIMIT ? OFFSET ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, limit);
        ps.setInt(2, offset);

        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            Candidature c = new Candidature();
            c.setId(rs.getInt("id"));

            Offre offre = new Offre();
            offre.setId(rs.getInt("offre_id"));
            c.setOffre(offre);

            // Correction ici aussi pour récupérer le statut en toute sécurité
            String statutStr = rs.getString("statut");
            StatutCandidature statut = null;
            if (statutStr != null) {
                try {
                    statut = StatutCandidature.valueOf(statutStr.toUpperCase()); // Conversion en majuscule
                } catch (IllegalArgumentException e) {
                    statut = StatutCandidature.EN_ATTENTE; // Statut par défaut en cas d'erreur
                }
            }
            c.setStatut(statut);

            Timestamp ts = rs.getTimestamp("datesoumission");
            if (ts != null) {
                c.setDateSoumission(ts.toLocalDateTime());
            }

            c.setUtilisateur(rs.getString("utilisateur"));
            c.setCv(rs.getString("cv"));
            c.setLettreMotivation(rs.getString("lettremotivation"));
            candidatures.add(c);
        }

        return candidatures;
    }

    public void ajouter(Candidature c) throws SQLException {
        String query = "INSERT INTO candidature (offre_id, utilisateur, cv, lettremotivation, datesoumission, statut) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, c.getOffre().getId());  // Assure-toi que l'objet Offre est correctement défini
        ps.setString(2, c.getUtilisateur());
        ps.setString(3, c.getCv());
        ps.setString(4, c.getLettreMotivation());
        ps.setTimestamp(5, Timestamp.valueOf(c.getDateSoumission()));  // Conversion de LocalDateTime en Timestamp
        ps.setString(6, c.getStatut() != null ? c.getStatut().name() : StatutCandidature.EN_ATTENTE.name()); // Défaut à EN_ATTENTE
        ps.executeUpdate();
    }

    /**
     * Supprime une candidature selon l'ID
     */
    public boolean supprimer(int id) throws SQLException {
        String query = "DELETE FROM candidature WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setInt(1, id);
        return ps.executeUpdate() > 0;
    }

    /**
     * Modifie le statut d'une candidature
     */
    public boolean modifier(Candidature c) throws SQLException {
        String query = "UPDATE candidature SET statut = ? WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(query);
        ps.setString(1, c.getStatut().name());
        ps.setInt(2, c.getId());
        return ps.executeUpdate() > 0;
    }
    public List<Candidature> recupstat() {
        Offre mockOffre = new Offre();
        return List.of(
                new Candidature(1, mockOffre, Candidature.StatutCandidature.EN_ATTENTE, LocalDateTime.now(), "user1", "cv1", "lettre1"),
                new Candidature(2, mockOffre, Candidature.StatutCandidature.ACCEPTEE, LocalDateTime.now(), "user2", "cv2", "lettre2"),
                new Candidature(3, mockOffre, Candidature.StatutCandidature.REFUSEE, LocalDateTime.now(), "user3", "cv3", "lettre3"),
                new Candidature(4, mockOffre, Candidature.StatutCandidature.EN_ATTENTE, LocalDateTime.now(), "user4", "cv4", "lettre4")
        );
    }

    public void generatePDFForAcceptedCandidature(Candidature candidature) {
        if (candidature.getStatut() == StatutCandidature.ACCEPTEE) {
            // Use absolute path for PDF generation
            String userHome = System.getProperty("user.home");
            String outputPath = userHome + File.separator + "Downloads" + File.separator + "candidature_" + candidature.getId() + ".pdf";
            pdfService.generateCandidaturePDF(candidature, outputPath);
        }
    }
}