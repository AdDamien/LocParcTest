package MNS.LocParc.services;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

        private final JavaMailSender emailSender;

        @Autowired
        public EmailService(JavaMailSender emailSender) {
            this.emailSender = emailSender;
        }

        public void sendEmail(String emaildureceveur, String sujet, String text) {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(emaildureceveur);
            message.setSubject(sujet);
            message.setText(text);
            emailSender.send(message);
        }
    }

