package services;

import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import entities.Candidature;
import entities.Offre;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.logging.Level;
import java.util.logging.Logger;

public class PDFService {
    private static final Logger LOGGER = Logger.getLogger(PDFService.class.getName());
    private static final String LOGO_PATH = "src/main/resources/images/careeramain.jpg";

    public void generateCandidaturePDF(Candidature candidature, String outputPath) {
        try {
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(outputPath));
            document.open();

            // Add logo to the right
            try {
                Image logo = Image.getInstance(LOGO_PATH);
                logo.scaleToFit(100, 100);
                logo.setAlignment(Element.ALIGN_RIGHT);
                
                // Create a paragraph for the logo to ensure it stays on top
                Paragraph logoParagraph = new Paragraph();
                logoParagraph.add(logo);
                logoParagraph.setAlignment(Element.ALIGN_RIGHT);
                document.add(logoParagraph);
                
                // Add some space after the logo
                document.add(new Paragraph(" "));
            } catch (Exception e) {
                LOGGER.log(Level.WARNING, "Could not load logo image", e);
            }

            // Add title
            Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            Paragraph title = new Paragraph("Candidature Acceptée", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            title.setSpacingAfter(20);
            document.add(title);

            // Add candidature details
            Font headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            Font contentFont = FontFactory.getFont(FontFactory.HELVETICA, 12);

            // Candidate Information
            document.add(new Paragraph("Informations du Candidat:", headerFont));
            document.add(new Paragraph("Nom: " + candidature.getUtilisateur(), contentFont));
            document.add(new Paragraph("Date de soumission: " +
                    candidature.getDateSoumission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")), contentFont));

            // Position Information
            document.add(new Paragraph("\nInformations sur le Poste:", headerFont));
            document.add(new Paragraph("Poste: " + candidature.getOffre().getNomposte(), contentFont));

            // Documents
            document.add(new Paragraph("\nDocuments:", headerFont));
            document.add(new Paragraph("CV: " + candidature.getCv(), contentFont));
            document.add(new Paragraph("Lettre de motivation: " + candidature.getLettreMotivation(), contentFont));

            // Status
            document.add(new Paragraph("\nStatut: " + candidature.getStatut().name(), headerFont));

            // Add space before signature
            document.add(new Paragraph("\n\n\n"));

            // Add signature at the bottom right
            Font signatureFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            signatureFont.setColor(66, 56, 202); // Couleur bleue indigo (comme votre logo)
            Paragraph signature = new Paragraph("CareEra", signatureFont);
            signature.setAlignment(Element.ALIGN_RIGHT);
            
            // Add current date under signature
            Font dateFont = FontFactory.getFont(FontFactory.HELVETICA, 10);
            Paragraph date = new Paragraph(LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy")), dateFont);
            date.setAlignment(Element.ALIGN_RIGHT);
            
            document.add(signature);
            document.add(date);

            document.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Error generating PDF", e);
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
