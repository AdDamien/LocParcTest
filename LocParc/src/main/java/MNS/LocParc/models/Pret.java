package MNS.LocParc.models;


import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.format.annotation.DateTimeFormat;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.Random;

@Entity
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class Pret {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;

    private String reference_pret;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateDebut;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime dateFin;

    private String motif;

    private String commentaire;


    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "materiel_id")
    private Materiel materiel;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;


    @OneToMany(mappedBy = "pret")
    @JsonIgnore
    List<Materiel> listeMaterielEmprunte;

    @PrePersist
    public void generateReference() {
        // Générer une référence de prêt
        String entreprise = "MNS"; // Entreprise fixe actuellement, vous pouvez la modifier selon vos besoins
        String codeNumerique = generateRandomNumericCode(8); // Générer un code numérique aléatoire de 8 chiffres
        String codeLettre = generateRandomLetterCode(); // Générer une lettre aléatoire
        this.reference_pret = "P" + entreprise + codeNumerique + codeLettre;
    }

    private String generateRandomNumericCode(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }

    private String generateRandomLetterCode() {
        Random random = new Random();
        char randomLetter = (char) (random.nextInt(26) + 'A');
        return String.valueOf(randomLetter);
    }
}
