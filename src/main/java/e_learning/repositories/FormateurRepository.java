package e_learning.repositories;

import e_learning.entity.Evaluation;
import e_learning.entity.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FormateurRepository extends JpaRepository<Formateur,Long> {}
