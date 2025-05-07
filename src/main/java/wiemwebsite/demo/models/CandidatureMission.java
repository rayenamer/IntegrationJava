package wiemwebsite.demo.models;

public class CandidatureMission {
    private int id;
    private int missionId;
    private int demandeId;
    private String etat;
    private int score;
    private int userid;
    private String user;
    private String missionTitre; // Nouveau champ pour le titre de la mission

    public CandidatureMission(int id, int missionId, int demandeId, String etat, int score, int userid, String user, String missionTitre) {
        this.id = id;
        this.missionId = missionId;
        this.demandeId = demandeId;
        this.etat = etat;
        this.score = score;
        this.userid = userid;
        this.user = user;
        this.missionTitre = missionTitre;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getMissionId() { return missionId; }
    public void setMissionId(int missionId) { this.missionId = missionId; }
    public int getDemandeId() { return demandeId; }
    public void setDemandeId(int demandeId) { this.demandeId = demandeId; }
    public String getEtat() { return etat; }
    public void setEtat(String etat) { this.etat = etat; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }
    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }
    public String getMissionTitre() { return missionTitre; }
    public void setMissionTitre(String missionTitre) { this.missionTitre = missionTitre; }
}