package MNS.LocParc.dao;

import MNS.LocParc.models.Declaration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DeclarationDao extends JpaRepository<Declaration, Declaration.IdDeclaration> {

}
