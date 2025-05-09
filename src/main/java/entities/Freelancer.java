package entities;

public class Freelancer extends User {
    private String photo; // Photo de profil
    private String cv;    // Lien vers le CV
    private String adresse; // Adresse
    private int anneesExperience; // Années d'expérience

    // Constructeur par défaut
    public Freelancer() {
        super();
    }

    // Constructeur avec paramètres
    public Freelancer(int id, String email, String password, String nom, String prenom,
                      String domaine, String sexe, String tel, String type, String roles,
                      String photo, String cv, String adresse, int anneesExperience) {
        super(id, email, password, nom, prenom, domaine, sexe, tel, type, roles);
        this.photo = photo;
        this.cv = cv;
        this.adresse = adresse;
        this.anneesExperience = anneesExperience;
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

    public String getAdresse() {
        return adresse;
    }

    public void setAdresse(String adresse) {
        this.adresse = adresse;
    }

    public int getAnnees_Experience() {
        return anneesExperience;
    }

    public void setAnnees_Experience(int anneesExperience) {
        this.anneesExperience = anneesExperience;
    }

    @Override
    public String toString() {
        return super.toString() + ", Freelancer{" +
                "photo='" + photo + '\'' +
                ", cv='" + cv + '\'' +
                ", adresse='" + adresse + '\'' +
                ", anneesExperience=" + anneesExperience +
                '}';
    }
}
