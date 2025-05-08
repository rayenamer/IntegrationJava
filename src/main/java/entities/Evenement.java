package entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class Evenement {
    private int id;
    private String nom;
    private String description;
    private LocalDateTime date;
    private String lieu;
    private boolean disponibilite;
    private TypeEvent typeEvent;
    private String imageUrl;
    private Integer userId;
    private List<Integer> participantsIds;

    // Constructor
    public Evenement(int id, String nom, String description, LocalDateTime date, String lieu, boolean disponibilite,
                     TypeEvent typeEvent, String imageUrl, Integer userId, List<Integer> participantsIds) {
        this.id = id;
        this.nom = nom;
        this.description = description;
        this.date = date;
        this.lieu = lieu;
        this.disponibilite = disponibilite;
        this.typeEvent = typeEvent;
        this.imageUrl = imageUrl;
        this.userId = userId;
        this.participantsIds = participantsIds != null ? new ArrayList<>(participantsIds) : new ArrayList<>();
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public LocalDateTime getDate() { return date; }
    public void setDate(LocalDateTime date) { this.date = date; }
    public String getLieu() { return lieu; }
    public void setLieu(String lieu) { this.lieu = lieu; }
    public boolean isDisponibilite() { return disponibilite; }
    public void setDisponibilite(boolean disponibilite) { this.disponibilite = disponibilite; }
    public TypeEvent getTypeEvent() { return typeEvent; }
    public void setTypeEvent(TypeEvent typeEvent) { this.typeEvent = typeEvent; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public Integer getUserId() { return userId; }
    public void setUserId(Integer userId) { this.userId = userId; }
    public List<Integer> getParticipantsIds() { return new ArrayList<>(participantsIds); }
    public void setParticipantsIds(List<Integer> participantsIds) {
        this.participantsIds = participantsIds != null ? new ArrayList<>(participantsIds) : new ArrayList<>();
    }

    // Method to get participantsIds as a [1.2.3] formatted string for database storage
    public String getParticipantsIdsAsString() {
        if (participantsIds.isEmpty()) {
            return "[]";
        }
        String result = "[" + participantsIds.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(".")) + "]";
        System.out.println("Serialized participantsIds: " + result);
        return result;
    }

    // Method to set participantsIds from a [1.2.3] formatted string (e.g., from database)
    public void setParticipantsIdsFromString(String participantsIdsString) {
        this.participantsIds = new ArrayList<>();
        if (participantsIdsString == null || participantsIdsString.equals("[]") || participantsIdsString.trim().isEmpty()) {
            System.out.println("Empty or null participantsIds string: " + participantsIdsString);
            return;
        }
        try {
            String cleanedString = participantsIdsString.replaceAll("[\\[\\]]", "").trim();
            if (cleanedString.isEmpty()) {
                System.out.println("Cleaned participantsIds string is empty");
                return;
            }
            for (String id : cleanedString.split("\\.")) {
                if (!id.trim().isEmpty()) { // Skip empty entries
                    int parsedId = Integer.parseInt(id.trim());
                    if (!this.participantsIds.contains(parsedId)) { // Avoid duplicates
                        this.participantsIds.add(parsedId);
                    }
                }
            }
            System.out.println("Parsed participantsIds: " + participantsIds);
        } catch (NumberFormatException e) {
            System.err.println("Error parsing participantsIds string: '" + participantsIdsString + "' - " + e.getMessage());
            this.participantsIds = new ArrayList<>(); // Reset to empty list on error
        }
    }

    // Participant management methods
    public boolean isParticipant(Integer userId) {
        if (userId == null) {
            return false;
        }
        return participantsIds.contains(userId);
    }

    public Evenement addParticipant(Integer userId) {
        if (userId != null && !participantsIds.contains(userId)) {
            participantsIds.add(userId);
            System.out.println("Added participant " + userId + " to event " + id + ", new participantsIds: " + getParticipantsIdsAsString());
        } else if (userId == null) {
            System.out.println("Attempted to add null participant to event " + id);
        } else {
            System.out.println("Participant " + userId + " already exists in event " + id);
        }
        return this;
    }

    public void removeParticipant(Integer userId) {
        if (userId != null) {
            participantsIds.removeIf(id -> Objects.equals(id, userId));
            System.out.println("Removed participant " + userId + " from event " + id + ", new participantsIds: " + getParticipantsIdsAsString());
        }
    }

    @Override
    public String toString() {
        return "Evenement{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                ", description='" + description + '\'' +
                ", date=" + date +
                ", lieu='" + lieu + '\'' +
                ", disponibilite=" + disponibilite +
                ", typeEvent=" + (typeEvent != null ? typeEvent.getNom() : null) +
                ", imageUrl='" + imageUrl + '\'' +
                ", userId=" + userId +
                ", participantsIds=" + participantsIds +
                '}';
    }
}