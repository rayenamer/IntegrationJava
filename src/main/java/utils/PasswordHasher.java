package utils;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHasher {

    // Génère un hash compatible Symfony ($2y$)
    public static String hashpw(String password) {
        String hash = BCrypt.hashpw(password, BCrypt.gensalt());
        if (hash.startsWith("$2a$") || hash.startsWith("$2n$")) {
            hash = "$2y$" + hash.substring(4);
        }
        return hash;
    }

    // Vérifie un mot de passe contre un hash $2y$ ou $2a$/$2n$
    public static boolean checkpw(String password, String hash) {
        String realHash = hash;
        if (hash.startsWith("$2y$")) {
            // jBCrypt ne supporte pas $2y$, on le remplace par $2a$
            realHash = "$2a$" + hash.substring(4);
        }
        return BCrypt.checkpw(password, realHash);
    }
}
