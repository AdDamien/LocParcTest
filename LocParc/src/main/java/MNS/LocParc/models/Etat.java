package MNS.LocParc.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.tool.schema.extract.internal.AbstractInformationExtractorImpl;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.util.List;

@Entity
    @Getter
    @Setter
    @EntityListeners(AuditingEntityListener.class)
    public class Etat {

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Integer id;

        private String nom;

        @OneToMany(mappedBy = "etat")
                @JsonIgnore
        List<Materiel> listeMateriel;

       // @OneToMany(mappedBy = "declaration")
       // List<Declaration> listeDeclaration;
}
