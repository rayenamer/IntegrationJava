package test;

import services.OffreService;

import java.sql.SQLException;

public class Main {

    public static void main(String[] args) {
        OffreService os = new OffreService();
        try {
//            ps.modifier(new Personne(1,25, "Ben Foulen","Foulen"));
            System.out.println(os.recuperer());
        } catch (SQLException e) {
            System.err.println(e.getMessage());
        }
    } // ajouter index
}
