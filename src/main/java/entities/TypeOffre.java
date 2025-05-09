package entities;

public class TypeOffre {
    private int id;
    private String nom;

    public TypeOffre() {
    }

    public TypeOffre(int id, String nom) {
        this.id = id;
        this.nom = nom;
    }

    public TypeOffre(String nom) {
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
        return "TypeOffre{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
