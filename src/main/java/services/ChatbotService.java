package services;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

public class ChatbotService {
    private static final List<String> GREETINGS = Arrays.asList("bonjour", "salut", "hello", "hi", "hey");
    private static final List<String> FAREWELLS = Arrays.asList("au revoir", "bye", "goodbye", "à plus");
    private static final List<String> THANKS = Arrays.asList("merci", "thanks", "thank you");

    private static final Pattern QUESTION_PATTERN = Pattern.compile(".*\\?$");
    private static final Pattern INCOMPLETE_PATTERN = Pattern.compile("^(comment|pourquoi|quand|où|qui|que|quel|quelle|quelles|quels|est-ce que|peux-tu|pouvez-vous).*");

    public String getResponse(String userInput) {
        String input = userInput.toLowerCase().trim();

        // Handle empty or very short inputs
        if (input.isEmpty() || input.length() < 3) {
            return "Je n'ai pas bien compris votre message. Pourriez-vous reformuler votre question de manière plus détaillée ?";
        }

        // Check for greetings
        if (GREETINGS.stream().anyMatch(input::contains)) {
            return "Bonjour ! Je suis votre assistant virtuel spécialisé dans l'orientation professionnelle. Comment puis-je vous aider aujourd'hui ?";
        }

        // Check for farewells
        if (FAREWELLS.stream().anyMatch(input::contains)) {
            return "Au revoir ! N'hésitez pas à revenir si vous avez d'autres questions sur votre carrière.";
        }

        // Check for thanks
        if (THANKS.stream().anyMatch(input::contains)) {
            return "Je vous en prie ! N'hésitez pas à me poser d'autres questions sur votre développement professionnel.";
        }

        // Handle incomplete questions
        if (INCOMPLETE_PATTERN.matcher(input).matches() && !QUESTION_PATTERN.matcher(input).matches()) {
            return "Je remarque que votre question semble incomplète. Pourriez-vous la formuler de manière plus précise ? Par exemple :\n" +
                    "- Comment puis-je améliorer mon CV ?\n" +
                    "- Quelles sont les étapes pour préparer un entretien d'embauche ?\n" +
                    "- Comment négocier mon salaire ?";
        }

        // Career-focused responses
        if (input.contains("cv") || input.contains("curriculum vitae")) {
            return "Pour un CV efficace, je vous conseille de :\n" +
                    "1. Adapter votre CV à chaque offre d'emploi\n" +
                    "2. Mettre en avant vos réalisations concrètes\n" +
                    "3. Utiliser des verbes d'action\n" +
                    "4. Garder une mise en page claire et professionnelle\n" +
                    "5. Ne pas dépasser 2 pages\n\n" +
                    "Souhaitez-vous des conseils plus spécifiques sur l'un de ces points ?";
        }

        if (input.contains("entretien") || input.contains("interview")) {
            return "Pour bien préparer un entretien d'embauche :\n" +
                    "1. Recherchez l'entreprise et le poste\n" +
                    "2. Préparez des exemples concrets de vos réalisations\n" +
                    "3. Anticipez les questions courantes\n" +
                    "4. Préparez vos propres questions\n" +
                    "5. Habillez-vous de manière professionnelle\n\n" +
                    "Voulez-vous que je vous aide à préparer des réponses à des questions spécifiques ?";
        }

        if (input.contains("salaire") || input.contains("rémunération")) {
            return "Pour négocier votre salaire :\n" +
                    "1. Faites des recherches sur les salaires moyens du poste\n" +
                    "2. Préparez des arguments basés sur vos compétences\n" +
                    "3. Attendez que l'employeur aborde le sujet\n" +
                    "4. Soyez prêt à discuter d'autres avantages\n" +
                    "5. Restez professionnel et positif\n\n" +
                    "Avez-vous besoin d'aide pour évaluer votre valeur sur le marché ?";
        }

        if (input.contains("lettre") || input.contains("motivation")) {
            return "Pour une lettre de motivation efficace :\n" +
                    "1. Adressez-la à la bonne personne\n" +
                    "2. Expliquez pourquoi vous êtes intéressé par ce poste\n" +
                    "3. Mettez en avant vos compétences pertinentes\n" +
                    "4. Montrez comment vous pouvez apporter de la valeur\n" +
                    "5. Gardez une longueur d'une page maximum\n\n" +
                    "Souhaitez-vous des conseils pour structurer votre lettre ?";
        }

        if (input.contains("réseau") || input.contains("networking")) {
            return "Pour développer votre réseau professionnel :\n" +
                    "1. Participez à des événements sectoriels\n" +
                    "2. Utilisez LinkedIn de manière stratégique\n" +
                    "3. Maintenez des relations authentiques\n" +
                    "4. Offrez de l'aide avant d'en demander\n" +
                    "5. Suivez les actualités de votre secteur\n\n" +
                    "Voulez-vous des conseils sur l'utilisation de LinkedIn ?";
        }

        // Default response for unrecognized queries
        return "Je suis spécialisé dans l'accompagnement professionnel. Voici quelques sujets sur lesquels je peux vous aider :\n" +
                "- Rédaction de CV et lettres de motivation\n" +
                "- Préparation aux entretiens d'embauche\n" +
                "- Négociation salariale\n" +
                "- Développement de réseau professionnel\n" +
                "- Orientation de carrière\n\n" +
                "Sur quel aspect souhaitez-vous en savoir plus ?";
    }
}