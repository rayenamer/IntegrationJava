package services;

import entities.TypeContrat;
import entities.TypeOffre;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TypeContratService {
    private Connection connection;

    public TypeContratService() {
        connection = MyDatabase.getInstance().getCnx();
    }

    public void ajouter(TypeContrat typeContrat) {
        String sql = "INSERT INTO type_contrat(nom) VALUES (?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeContrat.getNom());
            stmt.executeUpdate();
            System.out.println("TypeContrat ajouté avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'ajout : " + e.getMessage());
        }
    }

    public void modifier(TypeContrat typeContrat) {
        String sql = "UPDATE type_contrat SET nom = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, typeContrat.getNom());
            stmt.setInt(2, typeContrat.getId());
            stmt.executeUpdate();
            System.out.println("TypeContrat modifié avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la modification : " + e.getMessage());
        }
    }

    public void supprimer(int id) {
        String sql = "DELETE FROM type_contrat WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
            System.out.println("TypeContrat supprimé avec succès !");
        } catch (SQLException e) {
            System.err.println("Erreur lors de la suppression : " + e.getMessage());
        }
    }

    public List<TypeContrat> afficherTous() {
        List<TypeContrat> list = new ArrayList<>();
        String sql = "SELECT * FROM type_contrat";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                TypeContrat tc = new TypeContrat(rs.getInt("id"), rs.getString("nom"));
                list.add(tc);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de l'affichage : " + e.getMessage());
        }
        return list;
    }


    public TypeContrat getById(int id) {
        String sql = "SELECT * FROM type_contrat WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new TypeContrat(rs.getInt("id"), rs.getString("nom"));
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return null;
    }
    // Méthode rechercher pour filtrer les TypeOffres par nom
    public List<TypeContrat> rechercher(String nom) {
        List<TypeContrat> list = new ArrayList<>();
        String sql = "SELECT * FROM type_Contrat WHERE LOWER(nom) LIKE ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, "%" + nom.toLowerCase() + "%");
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                TypeContrat to = new TypeContrat(rs.getInt("id"), rs.getString("nom"));
                list.add(to);
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche : " + e.getMessage());
        }
        return list;
    }

    // Ajout de la méthode rechercher() sans paramètre pour compatibilité
    public List<TypeContrat> rechercher() {
        return afficherTous();
    }

    public List<TypeContrat> recuperer() throws SQLException {
        return rechercher();
    }
}
