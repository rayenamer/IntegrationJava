package entities;

public class Chercheur extends User {
    private String photo;
    private String cv;
    private String specialite;

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

    public String getSpecialite() {
        return specialite;
    }

    public void setSpecialite(String specialite) {
        this.specialite = specialite;
    }

    @Override
    public String toString() {
        return super.toString() + ", Chercheur{" +
                "photo='" + photo + '\'' +
                ", cv='" + cv + '\'' +
                '}';
    }
}
