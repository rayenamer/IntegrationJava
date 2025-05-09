package services;

import entities.Offre;
import entities.TypeContrat;
import entities.TypeOffre;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OffreService implements Service<Offre> {

    private final Connection connection;

    public OffreService() {
        try {
            this.connection = MyDatabase.getInstance().getCnx();
            if (this.connection == null) {
                throw new IllegalStateException("La connexion à la base de données n'a pas pu être établie.");
            }
            if (this.connection.isClosed()) {
                throw new IllegalStateException("La connexion à la base de données est fermée.");
            }
        } catch (SQLException e) {
            throw new IllegalStateException("Erreur lors de la vérification de la connexion à la base de données.", e);
        }
    }

    @Override
    public void ajouter(Offre offre) throws SQLException {
        String req = "INSERT INTO offre (typecontrat_id, typeoffre_id, nomposte, entreprise, localisation, salaire, disponibilite, image, utilisateur) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, offre.getTypeContrat().getId());
            ps.setInt(2, offre.getTypeOffre().getId());
            ps.setString(3, offre.getNomposte());
            ps.setString(4, offre.getEntreprise());
            ps.setString(5, offre.getLocalisation());
            ps.setDouble(6, offre.getSalaire());
            ps.setBoolean(7, offre.isDisponibilite());
            ps.setString(8, offre.getImage());
            ps.setString(9, offre.getUtilisateur());

            ps.executeUpdate();
            System.out.println("Offre ajoutée avec succès.");
        } catch (SQLException e) {
            throw new SQLException("Erreur lors de l'ajout de l'offre.", e);
        }
    }

    @Override
    public void modifier(Offre offre) {
        String req = "UPDATE offre SET typecontrat_id=?, typeoffre_id=?, nomposte=?, entreprise=?, localisation=?, salaire=?, disponibilite=?, image=?, utilisateur=? WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, offre.getTypeContrat().getId());
            ps.setInt(2, offre.getTypeOffre().getId());
            ps.setString(3, offre.getNomposte());
            ps.setString(4, offre.getEntreprise());
            ps.setString(5, offre.getLocalisation());
            ps.setDouble(6, offre.getSalaire());
            ps.setBoolean(7, offre.isDisponibilite());
            ps.setString(8, offre.getImage());
            ps.setString(9, offre.getUtilisateur());
            ps.setInt(10, offre.getId());

            ps.executeUpdate();
            System.out.println("Offre modifiée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification de l'offre.");
        }
    }

    @Override
    public void supprimer(Offre offre) {
        String req = "DELETE FROM offre WHERE id=?";

        try (PreparedStatement ps = connection.prepareStatement(req)) {
            ps.setInt(1, offre.getId());
            ps.executeUpdate();
            System.out.println("Offre supprimée avec succès.");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression de l'offre.");
        }
    }

    @Override
    public List<Offre> recuperer() throws SQLException {
        return rechercher();
    }

    public List<Offre> rechercher() {
        String req = "SELECT o.*, " +
                "tc.id AS tc_id, tc.nom AS tc_nom, " +
                "toff.id AS toff_id, toff.nom AS toff_nom " +
                "FROM offre o " +
                "JOIN type_contrat tc ON o.typecontrat_id = tc.id " +
                "JOIN type_offre toff ON o.typeoffre_id = toff.id";

        List<Offre> offres = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(req);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                TypeContrat typeContrat = new TypeContrat();
                typeContrat.setId(rs.getInt("tc_id"));
                typeContrat.setNom(rs.getString("tc_nom"));

                TypeOffre typeOffre = new TypeOffre();
                typeOffre.setId(rs.getInt("toff_id"));
                typeOffre.setNom(rs.getString("toff_nom"));

                Offre offre = new Offre();
                offre.setId(rs.getInt("id"));
                offre.setTypeContrat(typeContrat);
                offre.setTypeOffre(typeOffre);
                offre.setNomposte(rs.getString("nomposte"));
                offre.setEntreprise(rs.getString("entreprise"));
                offre.setLocalisation(rs.getString("localisation"));
                offre.setSalaire(rs.getDouble("salaire"));
                offre.setDisponibilite(rs.getBoolean("disponibilite"));
                offre.setImage(rs.getString("image"));
                offre.setUtilisateur(rs.getString("utilisateur"));

                offres.add(offre);
            }

        } catch (SQLException e) {
            System.err.println("Erreur lors de la récupération des offres.");
        }

        return offres;
    }

    // Example method for statistics
    public List<Offre> recupstat() {
        return List.of(
                new Offre(new TypeContrat("CDI"), null, "", "", "", 1200, true, "", ""),
                new Offre(new TypeContrat("CDD"), null, "", "", "", 1000, true, "", ""),
                new Offre(new TypeContrat("CIVP"), null, "", "", "", 1500, true, "", "")
        );
    }

    // Method to fetch all offers
    public List<Offre> getAll() throws SQLException {
        return rechercher();
    }

    public Offre getOffreById(int id) throws SQLException {
        String query = "SELECT * FROM offre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                TypeContrat typeContrat = new TypeContrat();
                typeContrat.setId(rs.getInt("typecontrat_id"));

                TypeOffre typeOffre = new TypeOffre();
                typeOffre.setId(rs.getInt("typeoffre_id"));

                Offre offre = new Offre();
                offre.setId(rs.getInt("id"));
                offre.setTypeContrat(typeContrat);
                offre.setTypeOffre(typeOffre);
                offre.setNomposte(rs.getString("nomposte"));
                offre.setEntreprise(rs.getString("entreprise"));
                offre.setLocalisation(rs.getString("localisation"));
                offre.setSalaire(rs.getDouble("salaire"));
                offre.setDisponibilite(rs.getBoolean("disponibilite"));
                offre.setImage(rs.getString("image"));
                offre.setUtilisateur(rs.getString("utilisateur"));

                return offre;
            }
        }
        return null;
    }
}
