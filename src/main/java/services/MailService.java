package services;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class MailService {

    public static boolean envoyerMail(String destinataire, String code) {
        String emailExpediteur = "choubeniighoffrane@gmail.com";  // Ton email
        String motDePasse = System.getenv("GMAIL_APP_PASSWORD"); // Mot de passe d'application sécurisé
        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailExpediteur, motDePasse);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailExpediteur));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(destinataire));
            message.setSubject("Code de vérification");
            message.setText("Votre code de vérification est : " + code);

            Transport.send(message);
            System.out.println("✅ Email envoyé avec succès à " + destinataire);
            return true;  // Retourner true si l'envoi a réussi
        } catch (MessagingException e) {
            e.printStackTrace();
            return false; // Retourner false si une erreur se produit
        }
    }
}