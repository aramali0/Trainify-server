package e_learning.repositories;

import e_learning.entity.Feedback;
import e_learning.entity.Formateur;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
//    List<Feedback> findByFormateur(Formateur formateur);
//
////    List<Feedback> findByFormateurUserId(Long formateurId);

    boolean existsByUserAppUserId(Long userId);
}
