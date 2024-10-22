package e_learning.repositories;

import e_learning.entity.ChargeFormation;
import e_learning.entity.ResponsableFormation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ChargeFormationRepository extends JpaRepository<ChargeFormation,Long> {
    public ResponsableFormation findByEmail(String email);

}
