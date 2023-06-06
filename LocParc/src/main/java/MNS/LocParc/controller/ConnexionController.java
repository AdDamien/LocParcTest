package MNS.LocParc.controller;

import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Utilisateur;
import MNS.LocParc.securite.JwtUtils;
import MNS.LocParc.securite.MonUserDetails;
import MNS.LocParc.securite.MonUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.regex.Pattern;

@RestController
@CrossOrigin
public class ConnexionController  {

    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    UtilisateurDao utilisateurDao ;

    @Autowired
    MonUserDetailsService userDetailsService;

    @Autowired
    JwtUtils jwtUtils;
    @Autowired
    AuthenticationManager authenticationManager;



    @PostMapping("/connexion")
    public ResponseEntity <String> connexion(@RequestBody Utilisateur utilisateur) {
        MonUserDetails userDetails;

        try {
            userDetails = (MonUserDetails) authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                            utilisateur.getEmail(),
                            utilisateur.getMotDePasse()
                    )
            ).getPrincipal();
        } catch (BadCredentialsException e) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        MonUserDetails monUserDetails = (MonUserDetails)userDetailsService.loadUserByUsername(utilisateur.getEmail());
        return new ResponseEntity<>(jwtUtils.generateJwt(monUserDetails),HttpStatus.OK);
    }

    @PostMapping("/inscription")
    public ResponseEntity <Utilisateur> inscription (@RequestBody Utilisateur utilisateur) {
        if (utilisateur.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        if (utilisateur.getMotDePasse().length() <= 3) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        String regex = "(.+)@(.+)$";
        Pattern pattern = Pattern.compile(regex);

        if (!pattern.matcher(utilisateur.getEmail()).matches()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        Optional<Utilisateur> optional = utilisateurDao.findByEmail(utilisateur.getEmail());
        if (optional.isPresent()) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        String passwordCrypter = passwordEncoder.encode(utilisateur.getMotDePasse());
        utilisateur.setMotDePasse(passwordCrypter);

        utilisateurDao.save(utilisateur);
        return new ResponseEntity<>(utilisateur,HttpStatus.CREATED);
    }
}


