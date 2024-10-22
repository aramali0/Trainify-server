package e_learning.repositories;

import e_learning.entity.Cour;
import e_learning.entity.ResponsableFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsableFormationRepository extends JpaRepository<ResponsableFormation,Long> {
    public ResponsableFormation findResponsableFormationByEmail(String email);

}
