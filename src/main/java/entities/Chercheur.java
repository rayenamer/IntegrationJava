package entities;

public class Chercheur extends User {
    private String photo;
    private String cv;

    public Chercheur() {
        super();
    }

    public Chercheur(int id, String email, String password, String nom, String prenom,
                     String domaine, String sexe, String tel, String type, String roles,
                     String photo, String cv) {
        super(id, email, password, nom, prenom, domaine, sexe, tel, type, roles);
        this.photo = photo;
        this.cv = cv;
    }

    // Getters et Setters
    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    @Override
    public String toString() {
        return super.toString() + ", Chercheur{" +
                "photo='" + photo + '\'' +
                ", cv='" + cv + '\'' +
                '}';
    }
}
