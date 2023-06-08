package MNS.LocParc.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.nio.file.FileSystem;
import java.util.Objects;

@Service
public class EmailService {

        private final JavaMailSender emailSender;

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
            System.out.println("Email envoyer");
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
            System.out.println("Email avec Piece Jointe envoyer");

    }

    public void transmettrePassNewUtilisateur(String destinataire, String Pass) {
        String objet = "Votre mot de passe";
        String message = "Voici votre mot de passe temporaire : " + Pass +
                "Ceci est un message automatique merci de ne pas répondre";


        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("noreply@locmnsmailservice.com"); // test avec adresse fictive pour refuser les mails retour
        email.setTo(destinataire);
        email.setSubject(objet);
        email.setText(message);

        emailSender.send(email);
    }
    }

