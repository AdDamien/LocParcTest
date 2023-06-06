package MNS.LocParc.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@IdClass(Declaration.IdDeclaration.class)
public class Declaration {

    @Id
    private int materielId;

    @Id
    private int pretId;

    @Id
    private int etatId;

    @Id
    private int causeId;

    private LocalDate dateDeclaration;

    private LocalDate dateRetourAnticipe;


    @ManyToOne
    @MapsId("materiel_id")
    @JoinColumn(name = "materiel_id")
    Materiel materiel;

    @ManyToOne
    @MapsId("pret_id")
    @JoinColumn(name = "pret_id")
    Pret pret;

    @ManyToOne
    @MapsId("etat_id")
    @JoinColumn(name = "etat_id")
    Etat etat;

    @ManyToOne
    @MapsId("cause_id")
    @JoinColumn(name = "cause_id")
    Cause cause;

    @Embeddable // s'utilise pour dire que cette classe servira de propriété.
    @Getter
    @Setter
    public static class IdDeclaration implements Serializable {

        @Column(name = "pret_id")
        private int pretId;

        @Column(name = "materiel_id")
        private int materielId;

        @Column(name = "etat_id")
        private int etatId;

        @Column(name = "cause_id")
        private int causeId;


    }

}
