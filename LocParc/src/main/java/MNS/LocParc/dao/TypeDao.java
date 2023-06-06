package MNS.LocParc.dao;

import MNS.LocParc.models.Type;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TypeDao extends JpaRepository<Type,Integer> {

    Type findByNom(String nom);
}
