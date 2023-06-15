package MNS.LocParc.controller;

import org.apache.commons.io.FilenameUtils;
import com.github.miachm.sods.*;

import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Role;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.securite.JwtUtils;
import MNS.LocParc.securite.MonUserDetails;
import MNS.LocParc.services.EmailService;
import MNS.LocParc.services.FichierService;
import com.fasterxml.jackson.annotation.JsonView;
import io.jsonwebtoken.Claims;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.security.SecureRandom;
import java.util.*;

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
    public ResponseEntity<Utilisateur> getProfil (@RequestHeader("Authorization") String bearer) {
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
                userToUpdate.setListeMateriel(nouvelUtilisateur.getListeMateriel());


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
        //String passwordCrypter = passwordEncoder.encode("root");
        nouvelUtilisateur.setMotDePasse(passwordCrypter);

        emailService.transmettrePassNewUtilisateur(nouvelUtilisateur.getEmail(),userPassword);


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
                System.out.println("Le test du mimeType a echoue");
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/import-utilisateurs")
    public List<Utilisateur> importerUtilisateurs
            (@RequestParam("fichier") MultipartFile fichier) throws IOException {
        List<Utilisateur> listeUtilisateurs = new ArrayList<>();

        // Vérifier si le fichier est un fichier ODS
        if (!fichier.isEmpty() && fichier.getOriginalFilename().endsWith(".xlsx")) {
            System.out.println("Le fichier est bon");
            Workbook workbook = new XSSFWorkbook(fichier.getInputStream());
            Sheet feuille = workbook.getSheetAt(0);

            // Parcourir les lignes de la feuille de calcul
            Iterator<Row> iterator = feuille.iterator();
            while (iterator.hasNext()) {
                Row ligne = iterator.next();

                // Récupérer les valeurs des cellules
                Cell cellulePrenom = ligne.getCell(0);
                Cell celluleNom = ligne.getCell(1);
                Cell celluleEmail = ligne.getCell(2);
                Cell celluleTelephone = ligne.getCell(3);

                // Vérifier si les cellules sont non vides
                if (celluleNom != null && celluleEmail != null && cellulePrenom != null && celluleTelephone != null) {
                    String prenom = cellulePrenom != null ? cellulePrenom.getStringCellValue().trim() : "";
                    String nom = celluleNom.getStringCellValue().trim();
                    String email = celluleEmail.getStringCellValue().trim();
                    String telephone = celluleTelephone != null ? celluleTelephone.getStringCellValue().trim() : "";



                    // Créer un nouvel utilisateur
                    Utilisateur nouvelUtilisateur = new Utilisateur(prenom, nom, email, telephone);
                    System.out.println(nouvelUtilisateur.getPrenom() + ", " + nouvelUtilisateur.getNom() + ", " + nouvelUtilisateur.getEmail() + ", " + nouvelUtilisateur.getTelephone());

                    //Assigner un role a l'utilisateur , générer son mot de passe et lui le transmettre par email

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
                    //String passwordCrypter = passwordEncoder.encode("root");
                    nouvelUtilisateur.setMotDePasse(passwordCrypter);

                    // emailService.transmettrePassNewUtilisateur(nouvelUtilisateur.getEmail(),userPassword);
                    // Enregistrer l'utilisateur dans la base de données
                    utilisateurDao.save(nouvelUtilisateur);
                    // Ajouter l'utilisateur à la liste
                    listeUtilisateurs.add(nouvelUtilisateur);
                }
            }

            workbook.close();
        }

        return listeUtilisateurs;
    }

}

