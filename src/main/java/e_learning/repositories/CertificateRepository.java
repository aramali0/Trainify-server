package e_learning.repositories;

import e_learning.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Collection;
import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    Long countByParticipantUserId(Long participantId);

    List<Certificate> findByParticipantUserId(Long participantId);
}
