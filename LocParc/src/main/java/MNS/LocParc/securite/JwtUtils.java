package MNS.LocParc.securite;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class JwtUtils {

    public String generateJwt(MonUserDetails userDetails){

        Map<String,Object> donnees = new HashMap<>();
        donnees.put("prenom",userDetails.getUtilisateur().getPrenom());
        donnees.put("nom",userDetails.getUtilisateur().getNom());
        donnees.put("role",userDetails.getUtilisateur().getRole().getNom());
        return Jwts.builder()
                .setClaims(donnees)
                .setSubject(userDetails.getUsername())
                .signWith(SignatureAlgorithm.HS256,"azerty")
                .compact();
    }
    public Claims getData(String jwt){
        return Jwts.parser()
                .setSigningKey("azerty")
                .parseClaimsJws(jwt)
                .getBody();
    }

    public boolean isTokenValide(String jwt){
        try {
            Claims donnee = getData(jwt);
        }catch (SignatureException e){

            return false;
        }
        return true;

    }
}

