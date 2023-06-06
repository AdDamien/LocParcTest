package MNS.LocParc.dao;

import MNS.LocParc.models.Pret;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface PretDao extends JpaRepository<Pret,Integer> {


    Optional<Pret> findById(Integer id);


    @Query("SELECT p FROM Pret p JOIN FETCH p.materiel m JOIN FETCH p.utilisateur u WHERE u.id = :utilisateurId")
    List<Pret> findPretByUtilisateurId(@Param("utilisateurId") Integer utilisateurId);

}
