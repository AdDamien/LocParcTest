package MNS.LocParc.dao;

import MNS.LocParc.models.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurDao extends JpaRepository<Utilisateur,Integer> {

    Utilisateur findByPrenom(String prenom);
    Utilisateur findByTelephone(String telephone);
    Optional<Utilisateur> findByEmail(String email);
    Optional<Utilisateur> findById(Integer integer);

    Optional<Utilisateur> findByNom(String nom);




}
