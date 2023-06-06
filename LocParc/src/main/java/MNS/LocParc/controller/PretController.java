package MNS.LocParc.controller;

import MNS.LocParc.dao.PretDao;
import MNS.LocParc.models.Materiel;
import MNS.LocParc.models.Pret;
import MNS.LocParc.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@CrossOrigin
public class PretController {

    @Autowired
    PretDao pretDao;

    @GetMapping("/liste-pret")
    public List<Pret> getPret() {
        List<Pret> ListePret = pretDao.findAll();
        return ListePret;
    }

    @GetMapping("/pret-par-utilisateur/{id}")
    public List<Pret> getPretByUtilisateurId(@PathVariable int id) {
        return pretDao.findPretByUtilisateurId(id);
    }

    @PostMapping("/admin/modifPret")
    public ResponseEntity<Pret> modifPret(
            @RequestPart("materiel") Pret nouveauPret,
            @Nullable @RequestParam("fichier") MultipartFile fichier) {
        // si le prêt possède un id
        if (nouveauPret.getId() != null) {
            Optional<Pret> optional = pretDao.findById(nouveauPret.getId()); // s'il ne trouve rien il retournera cela.
            if (optional.isPresent()) {

                Pret pretToUpdate = optional.get();
                pretToUpdate.setDateDebut(nouveauPret.getDateDebut());
                pretToUpdate.setDateFin(nouveauPret.getDateFin());
                pretToUpdate.setListeMaterielEmprunte(nouveauPret.getListeMaterielEmprunte());

                pretDao.save(pretToUpdate);

                // si il y a une tentative d'insertion d'un pret avec un id qui n'existait pas
                return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
            }
        }

        pretDao.save(nouveauPret);
        return new ResponseEntity<>(nouveauPret, HttpStatus.CREATED);
    }
}