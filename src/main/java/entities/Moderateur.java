package entities;

public class Moderateur extends User {
    private String societe;


    public Moderateur() {
        super();
    }

    public Moderateur(int id, String email, String password, String nom, String prenom,
                      String domaine, String sexe, String tel, String type, String roles,
                      String societe) {
        super(id, email, password, nom, prenom, domaine, sexe, tel, type, roles);
        this.societe = societe;

    }

    // Getters et Setters
    public String getSociete() {
        return societe;
    }

    public void setSociete(String photo) {
        this.societe = societe;
    }


    @Override
    public String toString() {
        return super.toString() + ", Moderateur{" +
                "societe='" + societe + '\'' ;
    }
}
