package MNS.LocParc.securite;

import MNS.LocParc.dao.UtilisateurDao;
import MNS.LocParc.models.Utilisateur;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

    @Service
    public class MonUserDetailsService implements UserDetailsService {
        @Autowired
        private UtilisateurDao utilisateurDao;

        @Override
        public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException { // ici email est le parametre pour se co

            Optional<Utilisateur> optional = utilisateurDao.findByEmail(email);
            if (optional.isEmpty()){
                throw new UsernameNotFoundException("L'utilisateur n'existe pas");
            }
            return new MonUserDetails((optional.get()));
        }
    }


