package MNS.LocParc.dao;


import MNS.LocParc.models.Materiel;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MaterielDao extends JpaRepository<Materiel,Integer>{

    Optional<Materiel> findById(Integer integer);

    @Query("SELECT COUNT(m) FROM Materiel m")
    @JsonProperty("quantiteMaterielTotal")
    int countQuantiteMateriel();

    @Query("SELECT COUNT(m) FROM Materiel m JOIN m.statut s WHERE s.nom='disponible'")
    @JsonProperty("totalMaterielDisponible")
    int countQuantiteMaterielDispoTotal();

    @Query("SELECT COUNT(m) FROM Materiel m WHERE m.type = :type")
    @JsonProperty("quantite{#type}")
    int countQuantiteMaterielByType(@Param("type") String type);

    //@Query("SELECT COUNT(m) FROM Materiel m JOIN m.statut s WHERE m.type = :type AND s.nom = 'disponible'")
    //@JsonProperty("quantite{#type}Disponible")
    //int countMaterielByTypeAndDisponibilite(@Param("type") String type);

    @Query("SELECT COUNT(m) FROM Materiel m WHERE m.type = :type")
    @JsonProperty("quantite{#type}Disponible")
    int countMaterielByTypeAndDisponibilite(@Param("type") String type);

    @Query("SELECT M FROM Materiel M JOIN M.etat E JOIN M.statut S WHERE E.id BETWEEN 1 AND 3 AND S.nom = 'disponible'")
    List<Materiel> findByEtatAndStatut();

    @Query("SELECT M FROM Materiel M JOIN M.etat E WHERE E.id <= 3")
    List<Materiel> findMaterielByBonEtat();
    @Query("From Materiel M JOIN M.etat E WHERE E.nom= ?1")
    List<Materiel> findMaterielByEtat(String nomEtat);

    @Query("SELECT DISTINCT m.type.nom FROM Materiel m")
    List<String> findDistinctTypesNames();
    @Query("From Materiel M JOIN M.type T WHERE T.nom= ?1")
    List<Materiel> findMaterielByType(String nomType);

    @Query("SELECT M FROM Materiel M JOIN M.etat E JOIN M.statut S JOIN M.type T WHERE E.id BETWEEN 1 AND 3 AND S.nom = 'disponible' AND T.nom = ?1")
    List<Materiel> findByEtatAndStatutAndType(String nomType);

    @Query("SELECT M FROM Materiel M JOIN M.etat E WHERE E.id > 3")
    List<Materiel> findMaterielByBadEtat();

    @Query("SELECT M FROM Materiel M JOIN M.etat E JOIN M.type T WHERE E.id > 3 AND T.nom = ?1")
    List<Materiel> findByBadEtatAndType(String nomType);
    List<Materiel> findMaterielByModele(String nomModele);

    List<Materiel> findMaterielByReference(String nomReference);


}
