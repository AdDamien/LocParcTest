package MNS.LocParc.controller;

import MNS.LocParc.dao.StatutDao;
import MNS.LocParc.models.Pret;
import MNS.LocParc.models.Statut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@CrossOrigin
public class StatutController {

    @Autowired
    StatutDao statutDao;

    @GetMapping("/liste-statut")
    public List<Statut> getStatut() {
        List<Statut> ListeStatut = statutDao.findAll();
        return ListeStatut;
    }


}
