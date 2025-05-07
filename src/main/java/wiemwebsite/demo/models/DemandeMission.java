package wiemwebsite.demo.models;

public class DemandeMission {
    private int id;
    private int offremissionId; // Référence à l'offre de mission
    private String message;     // Message de motivation
    private String portfolio;   // Portfolio du candidat (lien vers son travail)
    private String status;      // Status de la candidature (par exemple "En attente", "Acceptée", "Rejetée")
    private int userid;         // Référence à l'utilisateur (candidat)

    // Constructeur
    public DemandeMission(int id, int offremissionId, String message, String portfolio, String status, int userid) {
        this.id = id;
        this.offremissionId = offremissionId;
        this.message = message;
        this.portfolio = portfolio;
        this.status = status;
        this.userid = userid;
    }

    // Getters et setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getOffremissionId() { return offremissionId; }
    public void setOffremissionId(int offremissionId) { this.offremissionId = offremissionId; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPortfolio() { return portfolio; }
    public void setPortfolio(String portfolio) { this.portfolio = portfolio; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getUserid() { return userid; }
    public void setUserid(int userid) { this.userid = userid; }

    @Override
    public String toString() {
        return "DemandeMission{" +
                "id=" + id +
                ", offremissionId=" + offremissionId +
                ", message='" + message + '\'' +
                ", portfolio='" + portfolio + '\'' +
                ", status='" + status + '\'' +
                ", userid=" + userid +
                '}';
    }
}
