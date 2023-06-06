package MNS.LocParc.controller;

import MNS.LocParc.dao.EtatDao;
import MNS.LocParc.dao.PretDao;
import MNS.LocParc.models.Etat;
import MNS.LocParc.models.Materiel;
import MNS.LocParc.models.Pret;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@CrossOrigin
public class EtatController {

    @Autowired
    EtatDao etatDao;


    @GetMapping("/liste-etat")
    public List<Etat> getEtat() {
        List<Etat> ListeEtat = etatDao.findAll();
        return ListeEtat;
    }
    @DeleteMapping("/admin/etat/{id}")
    public ResponseEntity<Etat> supprimerEtat(@PathVariable int id) {

        Optional<Etat> etatASupprimer = etatDao.findById(id);

        if (etatASupprimer.isPresent()) {
            etatDao.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin/modifetat")
    public ResponseEntity<Etat> modifEtat(
            @RequestPart("etat") Etat nouvelEtat,
            @Nullable @RequestParam("fichier") MultipartFile fichier) {
        // si le materiel fournit possède un id
        if (nouvelEtat.getId() != null) {
            Optional<Etat> optional = etatDao.findById(nouvelEtat.getId()); // s'il ne trouve rien il retournera cela.
            if (optional.isPresent()) {

                Etat etatToUpdate = optional.get();
                etatToUpdate.setNom(nouvelEtat.getNom());

                etatDao.save(etatToUpdate);
            }
            // si il y a une tentative d'insertion d'un materiel avec un id qui n'existait pas
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        etatDao.save(nouvelEtat);
        return new ResponseEntity<>(nouvelEtat, HttpStatus.CREATED);
    }
}
