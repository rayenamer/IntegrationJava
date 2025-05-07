package utils;

import entities.User;
import entities.Freelancer;
import entities.Chercheur;

public class Session {
    private static User currentUser;

    public static User getCurrentUser() {
        if (currentUser == null) {
            System.out.println("Aucun utilisateur dans la session");
        } else {
            System.out.println("Utilisateur dans la session : " + currentUser.getEmail() + " (Type: " + currentUser.getType() + ")");
        }
        return currentUser;
    }

    public static void setCurrentUser(User user) {
        if (user == null) {
            System.out.println("Déconnexion de l'utilisateur");
        } else {
            System.out.println("Connexion de l'utilisateur : " + user.getEmail() + " (Type: " + user.getType() + ")");
        }
        currentUser = user;
    }

    public static void clear() {
        System.out.println("Nettoyage de la session");
        currentUser = null;
    }
}
