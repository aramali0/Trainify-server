package e_learning.repositories;

import e_learning.entity.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ContactMassageRepository extends JpaRepository<ContactMessage, Long> {
    List<ContactMessage> findByResponseIsNotNull();
}
