package MNS.LocParc.controller;

import MNS.LocParc.dao.MaterielDao;
import MNS.LocParc.dao.StatutDao;
import MNS.LocParc.dao.TypeDao;
import MNS.LocParc.models.Materiel;
import MNS.LocParc.models.Role;
import MNS.LocParc.models.Type;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.services.FichierService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
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
import java.security.SecureRandom;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private int compteurMaterielImporter;

    private CompletableFuture<Integer> compteurPostImportationMateriel;





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
/*
    @PostMapping("/import-materiels")
    public ResponseEntity<List<Materiel>> importerMateriels(@RequestParam("fichier") MultipartFile fichier) {
        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Fichier manquant, erreur 400 Bad Request
        }
        System.out.println("Je suis ici");
        if (!fichier.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().build(); // Extension de fichier incorrecte, erreur 400 Bad Request
        }
        System.out.println("J'ouvre le fichier et le parcours");
        try (Workbook classeur = new XSSFWorkbook(fichier.getInputStream())) {
            Sheet feuille = classeur.getSheetAt(0);
            List<Materiel> listeMateriel = new ArrayList<>();

            Iterator<Row> iterator = feuille.iterator();
            int compteurMateriel = 0;
            String referencePrecedente = null;
            while (iterator.hasNext()) {
                Row ligne = iterator.next();

                Cell celluleMarque = ligne.getCell(0);
                Cell celluleModele = ligne.getCell(1);
                Cell celluleReference = ligne.getCell(2);
                Cell celluleDateAcquisition = ligne.getCell(3);
                Cell celluleDateFinGarantie = ligne.getCell(4);

                if (celluleMarque != null && celluleModele != null && celluleReference != null) {
                    String marque = celluleMarque.getStringCellValue().trim();
                    String modele = celluleModele.getStringCellValue().trim();
                    String reference = celluleReference.getStringCellValue().trim();
                    LocalDate dateAcquisition = null;
                    LocalDate dateFinGarantie = null;

                    if (celluleDateAcquisition != null && celluleDateAcquisition.getCellType() == CellType.NUMERIC) {
                        dateAcquisition = celluleDateAcquisition.getLocalDateTimeCellValue().toLocalDate();
                    }

                    if (celluleDateFinGarantie != null && celluleDateFinGarantie.getCellType() == CellType.NUMERIC) {
                        dateFinGarantie = celluleDateFinGarantie.getLocalDateTimeCellValue().toLocalDate();
                    }

                    if (referencePrecedente != null && !referencePrecedente.equals(reference)) {
                        return ResponseEntity.badRequest().build(); // Références différentes, erreur 400 Bad Request
                    }
                    referencePrecedente = reference;

                    // Vérifier si la référence existe déjà dans la base de données
                    List<Materiel> materielExistant = materielDao.findMaterielByReference(reference);
                    if (materielExistant != null) {
                        return ResponseEntity.badRequest().build(); // Référence existante, erreur 400 Bad Request
                    }

                    Materiel nouveauMateriel = new Materiel(marque, modele, reference, dateAcquisition, dateFinGarantie);
                    System.out.println(nouveauMateriel);
                    listeMateriel.add(nouveauMateriel);


                    compteurMateriel++;
                }
            }
            compteurMaterielImporter = compteurMateriel;

            // Créer un nouveau CompletableFuture avec la valeur mise à jour du compteur
            compteurPostImportationMateriel = CompletableFuture.completedFuture(compteurMaterielImporter);

            return ResponseEntity.ok(listeMateriel);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur lors de la lecture du fichier, erreur 500 Internal Server Error
        }
    }
*/
    @PostMapping("/import-materiels")
    public ResponseEntity<List<Materiel>> importerMateriels(@RequestParam("fichier") MultipartFile fichier) {
        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().build(); // Fichier manquant, erreur 400 Bad Request
        }

        if (!fichier.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().build(); // Extension de fichier incorrecte, erreur 400 Bad Request
        }
        System.out.println("Je vais traiter le fichier");
        try (Workbook classeur = new XSSFWorkbook(fichier.getInputStream())) {
            Sheet feuille = classeur.getSheetAt(0);
            List<Materiel> listeMateriel = new ArrayList<>();

            Iterator<Row> iterator = feuille.iterator();
            int compteurMateriel = 0;
            String referencePrecedente = null;

            // Vérifier si la première ligne contient les en-têtes des colonnes
            Row premiereLigne = iterator.next();
            boolean premiereLigneEnTetes = false;

            Cell celluleMarqueEnTete = premiereLigne.getCell(0);
            Cell celluleModeleEnTete = premiereLigne.getCell(1);
            Cell celluleReferenceEnTete = premiereLigne.getCell(2);
            Cell celluleDateAcquisitionEnTete = premiereLigne.getCell(3);
            Cell celluleDateFinGarantieEnTete = premiereLigne.getCell(4);

            System.out.println(" a "+celluleMarqueEnTete+" b "+celluleModeleEnTete+" c "+celluleReferenceEnTete+" d "+celluleDateAcquisitionEnTete+" e "+celluleDateFinGarantieEnTete);

            if (celluleMarqueEnTete != null && celluleModeleEnTete != null && celluleReferenceEnTete != null &&
                    celluleMarqueEnTete.getStringCellValue().equalsIgnoreCase("Marque") &&
                    celluleModeleEnTete.getStringCellValue().equalsIgnoreCase("Modèle") &&
                    celluleReferenceEnTete.getStringCellValue().equalsIgnoreCase("Référence")) {
                premiereLigneEnTetes = true;
            }

            while (iterator.hasNext()) {
                Row ligne = iterator.next();

                // Ignorer la première ligne si elle contient les en-têtes des colonnes
                if (premiereLigneEnTetes && ligne.getRowNum() == 1) {
                    continue;
                }

                Cell celluleMarque = ligne.getCell(0);
                Cell celluleModele = ligne.getCell(1);
                Cell celluleReference = ligne.getCell(2);
                Cell celluleDateAcquisition = ligne.getCell(3);
                Cell celluleDateFinGarantie = ligne.getCell(4);

                System.out.println(" a "+celluleMarque+" b "+celluleModele+" c "+celluleReference+" d "+celluleDateAcquisition+" e "+celluleDateFinGarantie);

                if (celluleMarque != null && celluleReference != null) {
                    String marque = celluleMarque.getStringCellValue().trim();
                    String modele = null;
                    if (celluleModele != null) {
                        if (celluleModele.getCellType() == CellType.STRING) {
                            modele = celluleModele.getStringCellValue().trim();
                        } else if (celluleModele.getCellType() == CellType.NUMERIC) {
                            double numericValue = celluleModele.getNumericCellValue();
                            modele = String.valueOf((int) numericValue);
                        }

                        System.out.println(celluleModele.getCellType());
                    }
                    String reference = celluleReference.getStringCellValue().trim();
                    LocalDate dateAcquisition = null;
                    LocalDate dateFinGarantie = null;

                    if (celluleDateAcquisition != null && celluleDateAcquisition.getCellType() == CellType.NUMERIC) {
                        dateAcquisition = celluleDateAcquisition.getLocalDateTimeCellValue().toLocalDate();
                    }

                    if (celluleDateFinGarantie != null && celluleDateFinGarantie.getCellType() == CellType.NUMERIC) {
                        dateFinGarantie = celluleDateFinGarantie.getLocalDateTimeCellValue().toLocalDate();
                    }

                    if (referencePrecedente != null && !referencePrecedente.equals(reference)) {
                        return ResponseEntity.badRequest().build(); // Références différentes, erreur 400 Bad Request
                    }
                    referencePrecedente = reference;

                    // Vérifier si la référence existe déjà dans la base de données
                    List<Materiel> materielExistant = materielDao.findMaterielByReference(reference);
                    if (materielExistant != null) {
                        return ResponseEntity.badRequest().build(); // Référence existante, erreur 400 Bad Request
                    }

                    Materiel nouveauMateriel = new Materiel(marque, modele, reference, dateAcquisition, dateFinGarantie);
                    System.out.println("a"+ marque+"b"+modele+"c"+reference+"d"+dateAcquisition+"e"+dateFinGarantie);
                    listeMateriel.add(nouveauMateriel);

                    compteurMateriel++;
                }
            }

            compteurMaterielImporter = compteurMateriel;

            // Créer un nouveau CompletableFuture avec la valeur mise à jour du compteur
            compteurPostImportationMateriel = CompletableFuture.completedFuture(compteurMaterielImporter);

            return ResponseEntity.ok(listeMateriel);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur lors de la lecture du fichier, erreur 500 Internal Server Error
        }
    }




    @GetMapping("/compteur-import-materiel")
    public ResponseEntity<Integer> getCompteurUtilisateurImporter() {
        if (compteurPostImportationMateriel != null && compteurPostImportationMateriel.isDone()) {
            try {
                // Récupérer la valeur du compteur à partir du CompletableFuture
                Integer compteurMaterielImporterFinale = compteurPostImportationMateriel.get();
                return ResponseEntity.ok(compteurMaterielImporterFinale);
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Le traitement de l'import n'est pas encore terminé, renvoyer une valeur par défaut
            return ResponseEntity.ok(0);
        }
    }
}
