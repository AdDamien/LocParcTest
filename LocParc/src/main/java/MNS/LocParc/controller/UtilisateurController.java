package MNS.LocParc.controller;

import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Role;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.securite.JwtUtils;
import MNS.LocParc.services.EmailService;
import MNS.LocParc.services.FichierService;
import io.jsonwebtoken.Claims;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class UtilisateurController {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    UtilisateurDao utilisateurDao;
    @Autowired
    FichierService fichierService;
    @Autowired
    EmailService emailService;

    private int compteurUtilisateurImporter;

    private int compteurUtilisateurDejaPresent;


    // Utilisitation d'un completable future pour que l'importation soit terminé avant de récup le compteur
    private CompletableFuture<Integer> compteurPostImportation;

    private CompletableFuture<Integer> compteurPostImportationDejaPresent;

    private List<Utilisateur> listeUtilisateurDejaPresent;

    private List<Utilisateur> listeUtilisateurImporter;
    private CompletableFuture<List<Utilisateur>> listeUtilisateurPostImportation;
    private CompletableFuture<List<Utilisateur>> listeUtilisateurNotImported;


    @GetMapping("/utilisateur")
    public List<Utilisateur> getUtilisateur() {
        List<Utilisateur> ListeUtilisateur = utilisateurDao.findAll();
        return ListeUtilisateur;

    }

    @GetMapping("/utilisateur/{id}")
    public ResponseEntity<Utilisateur> getUtilisateurById(@PathVariable int id) {

        Optional<Utilisateur> optional = utilisateurDao.findById(id); // s'il ne trouve rien il retournera cela.
        if (optional.isPresent()) {
            return new ResponseEntity<Utilisateur>(optional.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/utilisateur-phone")
    public Utilisateur getUtilisateurByPhone() {
        Utilisateur utilisateur = utilisateurDao.findByTelephone("0123456789");
        return utilisateur;
    }

    @GetMapping("/utilisateur-prenom")
    public Utilisateur getUtilisateurPrenom() {
        Utilisateur utilisateur = utilisateurDao.findByPrenom("Kevin");
        return utilisateur;
    }

    @DeleteMapping("/admin/utilisateur/{id}")
    public ResponseEntity<Utilisateur> supprimerUtilisateur(@PathVariable int id) {

        Optional<Utilisateur> utilisateurASupprimer = utilisateurDao.findById(id);

        if (utilisateurASupprimer.isPresent()) {
            utilisateurDao.deleteById(id);
            return new ResponseEntity<>(null, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @GetMapping("/profil")
    public ResponseEntity<Utilisateur> getProfil(@RequestHeader("Authorization") String bearer) {
        String jwt = bearer.substring(7);
        Claims donnees = jwtUtils.getData(jwt);
        Optional<Utilisateur> utilisateur = utilisateurDao.findByEmail(donnees.getSubject());

        if (utilisateur.isPresent()) {
            return new ResponseEntity<>(utilisateur.get(), HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);

    }


    @PostMapping("/admin/utilisateur")
    public ResponseEntity<Utilisateur> ajoutUtilisateur(
            @RequestPart("utilisateur") Utilisateur nouvelUtilisateur,
            @Nullable @RequestParam("fichier") MultipartFile fichier) {
        // si l'utilisateur fournit possède un id
        if (nouvelUtilisateur.getId() != null) {
            Optional<Utilisateur> optional = utilisateurDao.findById(nouvelUtilisateur.getId()); // s'il ne trouve rien il retournera cela.
            if (optional.isPresent()) {

                Utilisateur userToUpdate = optional.get();
                userToUpdate.setNom(nouvelUtilisateur.getNom());
                userToUpdate.setPrenom(nouvelUtilisateur.getPrenom());
                userToUpdate.setEmail(nouvelUtilisateur.getEmail());
                userToUpdate.setTelephone(nouvelUtilisateur.getTelephone());

                utilisateurDao.save(userToUpdate);
                if (fichier != null) {
                    try {
                        fichierService.transfertVersDossierUpload(fichier, "image-profil");
                    } catch (IOException e) {
                        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
                    }
                }
                return new ResponseEntity<Utilisateur>(optional.get(), HttpStatus.OK);
            }
            // si il y a une tentative d'insertion d'un utilisateur avec un id qui n'existait pas
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        Role role = new Role();
        role.setId(3);
        nouvelUtilisateur.setRole(role);

        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*/!@#$%^&";
        SecureRandom secureRandom = new SecureRandom();
        StringBuilder sb = new StringBuilder(10);
        for (int i = 0; i < 10; i++) {
            int randomIndex = secureRandom.nextInt(characters.length());
            sb.append(characters.charAt(randomIndex));
        }
        String newPassword = sb.toString();
        String userPassword = newPassword;
        String passwordCrypter = passwordEncoder.encode(newPassword);
        nouvelUtilisateur.setMotDePasse(passwordCrypter);

        emailService.transmettrePassNewUtilisateur(nouvelUtilisateur.getEmail(), userPassword, nouvelUtilisateur.getNom(), nouvelUtilisateur.getPrenom());


        if (fichier != null) {
            try {
                String nomImage = UUID.randomUUID() + "_" + fichier.getOriginalFilename();
                nouvelUtilisateur.setNomImageProfil(nomImage);

                fichierService.transfertVersDossierUpload(fichier, nomImage);
            } catch (IOException e) {
                return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        utilisateurDao.save(nouvelUtilisateur);
        return new ResponseEntity<>(nouvelUtilisateur, HttpStatus.CREATED);

    }

    @GetMapping("/image-profil/{idUtilisateur}")
    public ResponseEntity<byte[]> getImageProfil(@PathVariable int idUtilisateur) {
        Optional<Utilisateur> optional = utilisateurDao.findById(idUtilisateur);
        if (optional.isPresent()) {
            String nomImage = optional.get().getNomImageProfil();
            try {
                byte[] image = fichierService.getImageByName(nomImage);
                HttpHeaders enTete = new HttpHeaders();
                String mimeType = Files.probeContentType(new File(nomImage).toPath());
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

    @PostMapping("/import-utilisateurs")
    public ResponseEntity<List<Utilisateur>> importerUtilisateurs(@RequestParam("fichier") MultipartFile fichier) {
        if (fichier.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        if (!fichier.getOriginalFilename().endsWith(".xlsx")) {
            return ResponseEntity.badRequest().build();
        }

        try (Workbook classeur = new XSSFWorkbook(fichier.getInputStream())) {
            Sheet feuille = classeur.getSheetAt(0);
            List<Utilisateur> listeUtilisateurs = new ArrayList<>();

            final String emailRegex = "(.+)@(.+)$";
            Pattern pattern = Pattern.compile(emailRegex);
            Iterator<Row> iterator = feuille.iterator();
            int compteur = 0;
            int compteurExistant = 0;
            List<Utilisateur> listeUtilisateurPresent = new ArrayList<>();

            // Vérifier si la première ligne contient les en-têtes des colonnes
            Row premiereLigne = iterator.next();
            boolean premiereLigneEnTetes = false;

            Cell cellulePrenomEnTete = premiereLigne.getCell(0);
            Cell celluleNomEnTete = premiereLigne.getCell(1);
            Cell celluleEmailEnTete = premiereLigne.getCell(2);
            Cell celluleTelephoneEnTete = premiereLigne.getCell(3);

            if (cellulePrenomEnTete != null && celluleNomEnTete != null && celluleEmailEnTete != null && celluleTelephoneEnTete != null &&
                    cellulePrenomEnTete.getStringCellValue().equalsIgnoreCase("Prenom") &&
                    celluleNomEnTete.getStringCellValue().equalsIgnoreCase("Nom") &&
                    celluleEmailEnTete.getStringCellValue().equalsIgnoreCase("Email") &&
                    celluleTelephoneEnTete.getStringCellValue().equalsIgnoreCase("Telephone")) {
                premiereLigneEnTetes = true;
            }

            while (iterator.hasNext()) {
                Row ligne = iterator.next();

                Cell cellulePrenom = ligne.getCell(0);
                Cell celluleNom = ligne.getCell(1);
                Cell celluleEmail = ligne.getCell(2);
                Cell celluleTelephone = ligne.getCell(3);

                if (celluleNom != null && celluleEmail != null && cellulePrenom != null && celluleTelephone != null) {
                    String prenom = cellulePrenom.getStringCellValue().trim();
                    String nom = celluleNom.getStringCellValue().trim();
                    String email = celluleEmail.getStringCellValue().trim();
                    String telephone = celluleTelephone.getStringCellValue().trim();

                    Matcher matcher = pattern.matcher(email);
                    if (!matcher.matches()) {
                        return ResponseEntity.badRequest().build(); // E-mail invalide, erreur 400 Bad Request
                    }

                    Utilisateur nouvelUtilisateur = new Utilisateur(prenom, nom, email, telephone);
                    Role role = new Role();
                    role.setId(3);
                    nouvelUtilisateur.setRole(role);

                    String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789*/!@#$%^&";
                    SecureRandom secureRandom = new SecureRandom();
                    StringBuilder sb = new StringBuilder(10);
                    for (int i = 0; i < 10; i++) {
                        int randomIndex = secureRandom.nextInt(characters.length());
                        sb.append(characters.charAt(randomIndex));
                    }
                    String newPassword = sb.toString();
                    String userPassword = newPassword;
                    String passwordCrypter = passwordEncoder.encode(newPassword);
                    nouvelUtilisateur.setMotDePasse(passwordCrypter);


                    Optional<Utilisateur> utilisateurExistant = utilisateurDao.findByEmail(email);

                    if (utilisateurExistant.isPresent()) {
                        compteurExistant++;
                        Utilisateur utilisateur = new Utilisateur(utilisateurExistant.get().getPrenom(),utilisateurExistant.get().getNom(), utilisateurExistant.get().getEmail(),utilisateurExistant.get().getTelephone());
                        listeUtilisateurPresent.add(utilisateur);

                    } else {
                        emailService.transmettrePassNewUtilisateur(nouvelUtilisateur.getEmail(), userPassword , nouvelUtilisateur.getNom() , nouvelUtilisateur.getPrenom());
                        utilisateurDao.save(nouvelUtilisateur);
                        compteur++;
                        listeUtilisateurs.add(nouvelUtilisateur);
                    }
                }
            }
            compteurUtilisateurImporter = compteur;
            compteurUtilisateurDejaPresent = compteurExistant;


            listeUtilisateurImporter = listeUtilisateurs;
            listeUtilisateurDejaPresent = listeUtilisateurPresent;

            // Créer un nouveau CompletableFuture pour mettre a jour les compteurs une fois le traitement fini
            compteurPostImportation = CompletableFuture.completedFuture(compteurUtilisateurImporter);
            compteurPostImportationDejaPresent = CompletableFuture.completedFuture(compteurUtilisateurDejaPresent);

            // Mettre a jour les listes avec le CompletableFuture une fois le traitement fini
            listeUtilisateurPostImportation = CompletableFuture.completedFuture(listeUtilisateurImporter);
            listeUtilisateurNotImported = CompletableFuture.completedFuture(listeUtilisateurDejaPresent);

            return ResponseEntity.ok(listeUtilisateurs);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build(); // Erreur lors de la lecture du fichier, erreur 500 Internal Server Error
        }
    }

    @GetMapping("/compteur-import-utilisateur")
    public ResponseEntity<Integer> getCompteurUtilisateurImporter() {
        if (compteurPostImportation != null && compteurPostImportation.isDone()) {
            try {
                // Récupérer la valeur du compteur à partir du CompletableFuture
                Integer compteurUtilisateurImporterFinale = compteurPostImportation.get();
                return ResponseEntity.ok(compteurUtilisateurImporterFinale);
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Le traitement de l'import n'est pas encore terminé, renvoyer une valeur par défaut
            return ResponseEntity.ok(0);
        }
    }

    @GetMapping("/compteur-utilisateurexistant")
    public ResponseEntity<Integer> getCompteurUtilisateurDejaPresent() {
        if (compteurPostImportationDejaPresent != null && compteurPostImportationDejaPresent.isDone()) {
            try {
                // Récupérer la valeur du compteur à partir du CompletableFuture
                Integer compteurUtilisateurNonImporterFinale = compteurPostImportationDejaPresent.get();
                return ResponseEntity.ok(compteurUtilisateurNonImporterFinale);
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Le traitement de l'import n'est pas encore terminé, renvoyer une valeur par défaut
            return ResponseEntity.ok(0);
        }
    }

    @GetMapping("/liste-utilisateur-importe")
    public ResponseEntity<List<Utilisateur>> getListeUtilisateurImporter() {
        if (listeUtilisateurPostImportation != null && listeUtilisateurPostImportation.isDone()) {
            try {
                // Récupérer la valeur de la liste d'utilisateurs importés à partir du CompletableFuture
                List<Utilisateur> listeUtilisateurImporterFinale = listeUtilisateurPostImportation.get();
                return ResponseEntity.ok(listeUtilisateurImporterFinale);
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Le traitement de l'import n'est pas encore terminé, renvoyer une valeur par défaut (par exemple, une liste vide)
            return ResponseEntity.ok(new ArrayList<>());
        }
    }

    @GetMapping("/liste-utilisateur-nonimporte")
    public ResponseEntity<List<Utilisateur>> getListeNonImporte() {
        if (listeUtilisateurNotImported != null && listeUtilisateurNotImported.isDone()) {
            try {
                // Récupérer la valeur de la liste d'utilisateurs non importés à partir du CompletableFuture
                List<Utilisateur> listeUtilisateurNonImporteFinale = listeUtilisateurNotImported.get();
                return ResponseEntity.ok(listeUtilisateurNonImporteFinale);
            } catch (InterruptedException | ExecutionException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        } else {
            // Le traitement de l'import n'est pas encore terminé en attendant je mets une liste vide par default)
            return ResponseEntity.ok(new ArrayList<>());
        }
    }
}

