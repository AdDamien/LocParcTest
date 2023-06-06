package MNS.LocParc.dao;

import MNS.LocParc.models.Statut;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

@Repository
public interface StatutDao extends JpaRepository<Statut,Integer> {

    static boolean isDisponible(String nomStatut) {
        if (StringUtils.hasText(nomStatut)) {
            if (nomStatut.equalsIgnoreCase("disponible") || nomStatut.equalsIgnoreCase("indisponible")) {
                return true;
            }
        }
        return false;
    }

}
