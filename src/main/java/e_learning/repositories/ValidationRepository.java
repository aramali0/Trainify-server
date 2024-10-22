package e_learning.repositories;

import e_learning.entity.UserApp;
import e_learning.entity.Validation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ValidationRepository extends JpaRepository<Validation,String> {
    public Validation findValidationByCode(String code);

    Validation findValidationByUserApp(UserApp userApp);
}
