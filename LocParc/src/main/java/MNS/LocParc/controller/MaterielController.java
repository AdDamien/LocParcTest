package MNS.LocParc.controller;

import MNS.LocParc.dao.MaterielDao;
import MNS.LocParc.dao.StatutDao;
import MNS.LocParc.dao.TypeDao;
import MNS.LocParc.models.Materiel;
import MNS.LocParc.models.Type;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.services.FichierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

@RestController
@CrossOrigin
public class MaterielController {


    @Autowired
    FichierService fichierService;

    @Autowired
    MaterielDao materielDao;

    @Autowired
    TypeDao typeDao;

    @Autowired
    StatutDao statutDao;



    @GetMapping("/materiel-count/{type}")
    public Integer countMaterielByType(@PathVariable("type") String type) {
        List<Materiel> materiels = materielDao.findMaterielByType(type);
        int quantity = materiels.size();
        return quantity;
    }

    @GetMapping("/materiel-count-disponible/{type}")
    public int countMaterielByTypeAndDisponibilite(@PathVariable("type") String type) {
        List<Materiel> materielDispo = materielDao.findByEtatAndStatutAndType(type);
        int quantityDispo =  materielDispo.size();
        return quantityDispo;
    }

    @GetMapping("/badmateriel")
    public List<Materiel>getBadMateriel(){
        List<Materiel> ListeBadMateriel = materielDao.findMaterielByBadEtat();
        return ListeBadMateriel;
    }

    @GetMapping("/materiel-disponible")
    public List<Materiel>getMaterielDisponible() {
        List<Materiel> ListeMaterielDispo = materielDao.findByEtatAndStatut();
        return ListeMaterielDispo;
    }

    @GetMapping("/materiel-type-dispo/{nomType}")
    public List<Materiel>getMaterielDispoParType(@PathVariable String nomType) {
        List<Materiel> ListeMaterielDispoType = materielDao.findByEtatAndStatutAndType(nomType);
        return ListeMaterielDispoType;
    }


    @GetMapping("/liste-bonmateriel")
    public List<Materiel>getMaterielByBonEtat() {
        List<Materiel> ListeBonMateriel = materielDao.findMaterielByBonEtat();
        return ListeBonMateriel;
    }
    @GetMapping("/liste-materiel")
    public List<Materiel>getMateriel() {
        List<Materiel> ListeMateriel = materielDao.findAll();
        return ListeMateriel;
    }

    @GetMapping("/liste-materiel-typer")
    public List<String> getMaterielTypes() {
        List<String> listeTypesMateriel = materielDao.findDistinctTypesNames();
        return listeTypesMateriel;
    }

    @GetMapping("/materiel-par-etat/{nomEtat}")
    public List<Materiel> getMaterielByEtat(@PathVariable String nomEtat) {
        return materielDao.findMaterielByEtat(nomEtat);
    }

    @GetMapping("/materiel-par-type/{nomType}")
    public List<Materiel> getMaterielByType(@PathVariable String nomType) {
        return materielDao.findMaterielByType(nomType);
    }

    @GetMapping("/materiel-par-modele/{nomModele}")
    public List<Materiel> getMaterielByModele(@PathVariable String nomModele) {
        return materielDao.findMaterielByModele(nomModele);
    }

    @GetMapping("/materiel-par-reference/{nomReference}")
    public List<Materiel> getMaterielByReference(@PathVariable String nomReference) {
        return materielDao.findMaterielByReference(nomReference);
    }


    @GetMapping("/materiel/{id}")
    public ResponseEntity<Materiel> getMaterielById(@PathVariable int id) {

        Optional<Materiel> optional = materielDao.findById(id); // s'il ne trouve rien il retournera cela.
        if (optional.isPresent()) {
            return new ResponseEntity<Materiel>(optional.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
    @DeleteMapping("/admin/materiel/{id}")
    public ResponseEntity<Materiel> supprimerMateriel(@PathVariable int id) {

        Optional<Materiel> materielASupprimer = materielDao.findById(id);

        if (materielASupprimer.isPresent()) {
            materielDao.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/admin/modifmateriel")
    public ResponseEntity<Materiel> modifMateriel(
            @RequestPart("materiel") Materiel nouveauMateriel,
            @Nullable @RequestParam("fichier") MultipartFile fichier) {
        // si le materiel fournit possède un id
        if (nouveauMateriel.getId() != null) {
            Optional<Materiel> optional = materielDao.findById(nouveauMateriel.getId()); // s'il ne trouve rien il retournera cela.
            if (optional.isPresent()) {

                Materiel materielToUpdate = optional.get();
                materielToUpdate.setType(nouveauMateriel.getType());
                materielToUpdate.setEtat(nouveauMateriel.getEtat());
                materielToUpdate.setReference(nouveauMateriel.getReference());
                materielToUpdate.setModele(nouveauMateriel.getModele());


                materielDao.save(materielToUpdate);
                if (fichier != null) {
                    try {
                        fichierService.transfertVersDossierUpload(fichier, "image-materiel");
                    } catch (IOException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity<Materiel>(optional.get(), HttpStatus.OK);
            }
            // si il y a une tentative d'insertion d'un materiel avec un id qui n'existait pas
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        if (fichier != null) {
            try {
                String nomImageM = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
                nouveauMateriel.setNomImageMateriel(nomImageM);

                fichierService.transfertVersDossierUpload(fichier, nomImageM);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        materielDao.save(nouveauMateriel);
        return new ResponseEntity<>(nouveauMateriel, HttpStatus.CREATED);
    }

    @GetMapping("/image-materiel/{idMateriel}")
    public ResponseEntity<byte[]> getPictureMateriel(@PathVariable int idMateriel) {
        Optional<Materiel> optional = materielDao.findById(idMateriel);
        if (optional.isPresent()) {
            String nomImageM = optional.get().getNomImageMateriel();
            try {
                byte[] image = fichierService.getImageByName(nomImageM);
                HttpHeaders enTete = new HttpHeaders();
                String mimeType = Files.probeContentType(new File(nomImageM).toPath());
                enTete.setContentType(MediaType.valueOf(mimeType));
                return new ResponseEntity<>(image, enTete, HttpStatus.OK);
            } catch (FileNotFoundException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
