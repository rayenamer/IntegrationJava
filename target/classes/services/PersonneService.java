package services;

import entities.Personne;
import utils.MyDatabase;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PersonneService implements Service<Personne> {

    private Connection cnx;

    public PersonneService() {
        cnx = MyDatabase.getInstance().getCnx();
    }

    @Override
    public void ajouter(Personne personne) throws SQLException {
        String sql = "insert into personne(nom, prenom, age)" +
                " values('" + personne.getNom() + "', '" + personne.getPrenom()
                + "'," + personne.getAge() + ")";
        Statement st = cnx.createStatement();
        st.executeUpdate(sql);
    }

    @Override
    public void modifier(Personne personne) throws SQLException {
        String sql = "update personne set nom = ?, prenom = ?, age = ? where id= ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setString(1, personne.getNom());
        ps.setString(2, personne.getPrenom());
        ps.setInt(3, personne.getAge());
        ps.setInt(4, personne.getId());
        ps.executeUpdate();
    }

    @Override
    public void supprimer(Personne personne) throws SQLException {
        String sql = "delete from personne where id = ?";
        PreparedStatement ps = cnx.prepareStatement(sql);
        ps.setInt(1, personne.getId());
        ps.executeUpdate();
    }

    @Override
    public List<Personne> recuperer() throws SQLException {
        List<Personne> personnes = new ArrayList<>();
        String sql = "select * from personne";
        Statement st = cnx.createStatement();
        ResultSet rs = st.executeQuery(sql);

        while (rs.next()){
            int id = rs.getInt("id");
            String prenom = rs.getString("prenom");
            int age = rs.getInt("age");
            String nom = rs.getString("nom");
            Personne p = new Personne(id, age, nom, prenom);
            personnes.add(p);
        }

        return personnes;
    }
}
