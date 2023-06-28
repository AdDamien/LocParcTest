package MNS.LocParc.services;
import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.FileSystem;
import java.util.Objects;
import java.util.Optional;

@Service
public class EmailService {

        private final JavaMailSender emailSender;

       @Autowired
        UtilisateurDao utilisateurDao;

       Utilisateur utilisateur;


        @Autowired
        public EmailService(JavaMailSender emailSender) {
            this.emailSender = emailSender;
        }

        public void sendEmail(String emaildureceveur, String sujet, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("locmnsmailservice@gmail.com"); // set de mon email d'envoi
            message.setTo(emaildureceveur);
            message.setSubject(sujet);
            message.setText(text);
            emailSender.send(message);

        }

        public void sendEmailWithAttachment(String emaildureceveur, String sujet, String text,String piecejointe) throws MessagingException {

            MimeMessage mimeMessage = emailSender.createMimeMessage();

            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage,true);

            mimeMessageHelper.setFrom("locmnsmailservice@gmail.com");
            mimeMessageHelper.setTo(emaildureceveur);
            mimeMessageHelper.setSubject(sujet);
            mimeMessageHelper.setText(text);

            FileSystemResource fileSystemResource = new FileSystemResource(new File(piecejointe));

            mimeMessageHelper.addAttachment(Objects.requireNonNull(fileSystemResource.getFilename()),fileSystemResource);
            // requireNonNull car il pourrait y avoir une filenotfound exception.
            emailSender.send(mimeMessage);


    }
    public void transmettrePassNewUtilisateur(String destinataire, String pass , String contactNom , String contactPrenom) {
        String objet = "BIENVENUE CHEZ LOCMNS";

        String message = "Bonjour " + contactNom + " " + contactPrenom + ",\n\n"
                + "Nous vous remercions pour votre adhésion à l'application LOCMNS.\n\n"
                + "Voici votre mot de passe actuel : " + pass + "\n\n"
                + "Afin de garantir la sécurité de votre compte, nous vous invitons à créer un mot de passe robuste et complexe. Nous vous conseillons d'utiliser une combinaison de lettres, de chiffres et de caractères spéciaux pour renforcer la sécurité de votre compte.\n\n"
                + "Veuillez noter qu'il est important de ne pas partager votre mot de passe avec qui que ce soit afin de garantir la confidentialité de vos informations personnelles.\n\n"
                + "En cas de problème ou de question, n'hésitez pas à nous contacter.\n\n"
                + "Merci de votre confiance.\n\n"
                + "Cordialement,\n"
                + "L'équipe LOCMNS";

        SimpleMailMessage email = new SimpleMailMessage();
        // adresse no reply pour eviter les réponse.
        email.setFrom("no-reply@locmnsservice.com");
        email.setTo(destinataire);
        email.setSubject(objet);
        email.setText(message);

        emailSender.send(email);

    }

    }

