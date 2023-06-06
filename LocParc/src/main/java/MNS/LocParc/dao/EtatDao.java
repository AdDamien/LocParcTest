package MNS.LocParc.dao;


import MNS.LocParc.models.Etat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface EtatDao extends JpaRepository<Etat,Integer> {
}
