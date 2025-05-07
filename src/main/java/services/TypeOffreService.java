package services;

import entities.TypeOffre;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeOffreService {
    private Connection connection;

    public TypeOffreService() {
        connection = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(TypeOffre typeOffre) {
        String sql = "INSERT INTO type_offre(nom) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeOffre.getNom());
            stmt.executeUpdate();
            System.out.println("TypeOffre ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public void modifier(TypeOffre typeOffre) {
        String sql = "UPDATE type_offre SET nom = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeOffre.getNom());
            stmt.setInt(2, typeOffre.getId());
            stmt.executeUpdate();
            System.out.println("TypeOffre modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM type_offre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("TypeOffre supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public List<TypeOffre> afficherTous() {
        List<TypeOffre> list = new ArrayList<>();
        String sql = "SELECT * FROM type_offre";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TypeOffre to = new TypeOffre(rs.getInt("id"), rs.getString("nom"));
                list.add(to);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage : " + e.getMessage());
        }
        return list;
    }

    public TypeOffre getById(int id) {
        String sql = "SELECT * FROM type_offre WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TypeOffre(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return null;
    }

    // Méthode rechercher pour filtrer les TypeOffres par nom
    public List<TypeOffre> rechercher(String nom) {
        List<TypeOffre> list = new ArrayList<>();
        String sql = "SELECT * FROM type_offre WHERE LOWER(nom) LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nom.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TypeOffre to = new TypeOffre(rs.getInt("id"), rs.getString("nom"));
                list.add(to);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return list;
    }

    // Ajout de la méthode rechercher() sans paramètre pour compatibilité
    public List<TypeOffre> rechercher() {
        return afficherTous();
    }

    public List<TypeOffre> recuperer() throws SQLException {
        return rechercher();
    }
}
