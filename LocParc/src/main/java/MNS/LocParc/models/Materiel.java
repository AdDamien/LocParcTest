package MNS.LocParc.models;

import MNS.LocParc.view.VueMateriel;
import MNS.LocParc.view.VueUtilisateur;
import com.fasterxml.jackson.annotation.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.web.bind.annotation.CrossOrigin;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@CrossOrigin
public class Materiel {

    @Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
private Integer id;


    @Column(length = 50, nullable = false)
    private String modele;

    @Column(length = 50, nullable = false)
    private String marque;

    @Column(length = 50, nullable = false)
    private String reference;

    private String nomImageMateriel;

    @Transient
    private int prixAchat;

    @Transient
    private LocalDate date_acquisition;

    @Transient
    private LocalDate date_finGarantie;


    private LocalDateTime date_debutEmprunt;

    private LocalDateTime date_finEmprunt;

    @ManyToOne
    @JoinColumn(name = "type_id")
    private Type type;

    @ManyToOne
    @JoinColumn(name = "etat_id")
    private Etat etat;

    @ManyToOne
    @JoinColumn(name = "pret_id")
    private Pret pret;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "utilisateur_id")
    private Utilisateur utilisateur;


    @ManyToOne
    @JoinColumn(name = "statut_id")
    private Statut statut;

}
