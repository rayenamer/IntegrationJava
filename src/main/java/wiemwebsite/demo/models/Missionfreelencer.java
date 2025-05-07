package wiemwebsite.demo.models;

import java.sql.Date;

public class Missionfreelencer {
    private int id;
    private String titre;
    private double prix;
    private String description;
    private Date datelimite;
    private String specialite;
    private Date date;
    private int userid;
    private String image_url;

    public Missionfreelencer(int id, String titre, double prix, String description, Date datelimite, String specialite, Date date, int userid, String image_url) {
        this.id = id;
        this.titre = titre;
        this.prix = prix;
        this.description = description;
        this.datelimite = datelimite;
        this.specialite = specialite;
        this.date = date;
        this.userid = userid;
        this.image_url = image_url;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getTitre() { return titre; }
    public void setTitre(String titre) { this.titre = titre; }
    public double getPrix() { return prix; }
    public void setPrix(double prix) { this.prix = prix; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Date getDatelimite() { return datelimite; }
    public void setDatelimite(Date datelimite) { this.datelimite = datelimite; }
    public String getSpecialite() { return specialite; }
    public void setSpecialite(String specialite) { this.specialite = specialite; }
    public Date getDate() { return date; }
    public void setDate(Date date) { this.date = date; }
    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }
    public String getImage_url() { return image_url; }
    public void setImage_url(String image_url) { this.image_url = image_url; }

    @Override
    public String toString() {
        return "Missionfreelencer{" +
                "id=" + id +
                ", titre='" + titre + '\'' +
                ", prix=" + prix +
                ", description='" + description + '\'' +
                ", datelimite=" + datelimite +
                ", specialite='" + specialite + '\'' +
                ", date=" + date +
                ", userid=" + userid +
                ", image_url='" + image_url + '\'' +
                '}';
    }
}