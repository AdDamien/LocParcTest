package MNS.LocParc.models;


import MNS.LocParc.view.VueUtilisateur;
import com.fasterxml.jackson.annotation.*;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import javax.persistence.*;

@Entity
@Getter
@Setter
@CrossOrigin
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 50, nullable = false)
    private String prenom;

    @Column(length = 50, nullable = false)
    private String nom;

    @Column(length = 50, nullable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String telephone;

    private String motDePasse;

    private String nomImageProfil;

    @ManyToOne
    private Role role;

    @ManyToOne
    private Pret pret;

    @CreationTimestamp
    private LocalDate dateCreation;

    @UpdateTimestamp
    private LocalDateTime dateMiseAJour;

    @JsonView(VueUtilisateur.class)
    @JsonIgnoreProperties("utilisateur")
    @OneToMany(mappedBy = "utilisateur")
    private List<Materiel> listeMateriel;

    public Utilisateur() {

    }

    public Utilisateur(String prenom, String nom, String email, String telephone) {
        this.prenom = prenom;
        this.nom = nom;
        this.email = email;
        this.telephone = telephone;
    }
}


