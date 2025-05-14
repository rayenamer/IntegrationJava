package services;

import java.util.Properties;
import javax.mail.*;
import javax.mail.internet.*;

public class EmailSender {
    public static void sendEmail(String to, String subject, String content) {
        final String from = "choubeniighoffrane@gmail.com"; // ton email ici
        final String password = System.getenv("GMAIL_APP_PASSWORD"); // mot de passe d'application sécurisé

        Properties props = new Properties();
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");

        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(from, password);
                    }
                });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipients(
                    Message.RecipientType.TO,
                    InternetAddress.parse(to)
            );
            message.setSubject(subject);
            message.setText(content);

            Transport.send(message);
            System.out.println("Email envoyé à " + to);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}