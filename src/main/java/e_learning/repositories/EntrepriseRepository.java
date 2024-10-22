package e_learning.repositories;

import e_learning.entity.Entreprise;
import e_learning.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EntrepriseRepository extends JpaRepository<Entreprise, Long> {
    Optional<Entreprise> findByNomCommercial(String nomCommercial);

    Entreprise findByResponsableFormationsUserId(Long id);

    Optional<Entreprise> findByParticipantsUserId( Long id);
    Optional<Entreprise> findByFormateursUserId( Long id);
    Optional<Entreprise> findByChargeFormationsUserId( Long id);
}
