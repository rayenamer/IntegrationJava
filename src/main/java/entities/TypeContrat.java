package entities;

import java.util.Objects;

public class TypeContrat {
    private int id;
    private String nom;

    public TypeContrat() {
    }

    public TypeContrat(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public TypeContrat(String nom) {
        this.nom = nom;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    @Override
    public String toString() {
        return "TypeContrat{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
