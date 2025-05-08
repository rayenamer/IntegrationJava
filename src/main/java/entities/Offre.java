package entities;

public class Offre {
    private int id;
    private TypeContrat typeContrat;
    private TypeOffre typeOffre;
    private String nomposte;
    private String entreprise;
    private String localisation;
    private double salaire;
    private boolean disponibilite;
    private String image;
    private String utilisateur;

    public Offre() {}

    public Offre(String nomposte, String entreprise, String localisation, TypeContrat typeContrat, TypeOffre typeOffre, double salaire, boolean disponibilite, String image, String utilisateur) {
        this.nomposte = nomposte;
        this.entreprise = entreprise;
        this.localisation = localisation;
        this.typeContrat = typeContrat;
        this.typeOffre = typeOffre;
        this.salaire = salaire;
        this.disponibilite = disponibilite;
        this.image = image;
        this.utilisateur = utilisateur;
    }

    public Offre(int id, TypeContrat typeContrat, TypeOffre typeOffre, String nomposte, String entreprise,
                 String localisation, double salaire, boolean disponibilite, String image, String utilisateur) {
        this.id = id;
        this.typeContrat = typeContrat;
        this.typeOffre = typeOffre;
        this.nomposte = nomposte;
        this.entreprise = entreprise;
        this.localisation = localisation;
        this.salaire = salaire;
        this.disponibilite = disponibilite;
        this.image = image;
        this.utilisateur = utilisateur;
    }

    public Offre(TypeContrat typeContrat, TypeOffre typeOffre, String nomposte, String entreprise,
                 String localisation, double salaire, boolean disponibilite, String image, String utilisateur) {
        this.typeContrat = typeContrat;
        this.typeOffre = typeOffre;
        this.nomposte = nomposte;
        this.entreprise = entreprise;
        this.localisation = localisation;
        this.salaire = salaire;
        this.disponibilite = disponibilite;
        this.image = image;
        this.utilisateur = utilisateur;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public TypeContrat getTypeContrat() {
        return typeContrat;
    }

    public void setTypeContrat(TypeContrat typeContrat) {
        this.typeContrat = typeContrat;
    }

    public TypeOffre getTypeOffre() {
        return typeOffre;
    }

    public void setTypeOffre(TypeOffre typeOffre) {
        this.typeOffre = typeOffre;
    }

    public String getNomposte() {
        return nomposte;
    }

    public void setNomposte(String nomposte) {
        this.nomposte = nomposte;
    }

    public String getEntreprise() {
        return entreprise;
    }

    public void setEntreprise(String entreprise) {
        this.entreprise = entreprise;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public double getSalaire() {
        return salaire;
    }

    public void setSalaire(double salaire) {
        this.salaire = salaire;
    }

    public boolean isDisponibilite() {
        return disponibilite;
    }

    public void setDisponibilite(boolean disponibilite) {
        this.disponibilite = disponibilite;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(String utilisateur) {
        this.utilisateur = utilisateur;
    }

    @Override
    public String toString() {
        return "Offre{" +
                "id=" + id +
                ", typeContrat=" + typeContrat +
                ", typeOffre=" + typeOffre +
                ", nomposte='" + nomposte + '\'' +
                ", entreprise='" + entreprise + '\'' +
                ", localisation='" + localisation + '\'' +
                ", salaire=" + salaire +
                ", disponibilite=" + disponibilite +
                ", image='" + image + '\'' +
                ", utilisateur='" + utilisateur + '\'' +
                '}';
    }
}
