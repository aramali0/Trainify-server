package e_learning.repositories;

import e_learning.entity.ClassEntity;
import e_learning.entity.Evaluation;
import e_learning.entity.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation,Long> {
    @Query("SELECT e FROM Evaluation e JOIN e.responses r JOIN r.question q JOIN q.quiz qz JOIN qz.section s WHERE s.id = :sectionId AND e.participant.userId = :participantId ORDER BY e.id DESC")
    List<Evaluation> findLatestBySectionIdAndParticipantId(@Param("sectionId") Long sectionId, @Param("participantId") Long participantId);
    Page<Evaluation> findByParticipantUserId(Long participantId, Pageable pageable);

    Optional<Evaluation> findByQuizSectionIdAndParticipantUserId(Long sectionId, Long participantId);

    @Query("SELECT e FROM Evaluation e " +
            "JOIN e.quiz q " +
            "JOIN q.section sc " +
            "JOIN sc.session s " +
            "JOIN s.cour c " +
            "JOIN c.entreprise et " +
            "JOIN et.responsableFormations r " +
            "WHERE r.userId = :responsableId " +
            "AND (:sectionId IS NULL OR sc.id = :sectionId) " +
            "AND (:quizId IS NULL OR q.id = :quizId)")           // Quiz type filter
    Page<Evaluation> findByResponsableIdAndSectionId(
        @Param("responsableId") Long responsableId,
        @Param("sectionId") Long sectionId,
        @Param("quizId") Long quizId,
        Pageable pageable);

    @Query("SELECT e FROM Evaluation e " +
            "JOIN e.quiz q " +
            "JOIN q.section sc " +
            "JOIN sc.session s " +
            "JOIN s.cour c " +
            "JOIN c.entreprise et " +
            "JOIN et.chargeFormations r " +
            "WHERE r.userId = :chargeId " +
            "AND (:sectionId IS NULL OR sc.id = :sectionId) " +
            "AND (:quizId IS NULL OR q.id = :quizId)")           // Quiz type filter
    Page<Evaluation> findByChargeIdAndSectionId(
            @Param("chargeId") Long chargeId,
            @Param("sectionId") Long sectionId,
            @Param("quizId") Long quizId,
            Pageable pageable);

    @Query("SELECT e FROM Evaluation e " +
            "JOIN e.quiz q " +
            "JOIN q.section sc " +
            "JOIN sc.session s " +
            "JOIN s.cour c " +
            "JOIN c.formateurs f " +
            "WHERE f.userId = :formateurId " +
            "AND (:sectionId IS NULL OR sc.id = :sectionId) " +
            "AND (:quizId IS NULL OR q.id = :quizId)")           // Quiz type filter
    Page<Evaluation> findByFormateurIdAndSectionId(
            @Param("formateurId") Long responsableId,
            @Param("sectionId") Long sectionId,
            @Param("quizId") Long quizId,
            Pageable pageable);

    @Query("SELECT e FROM Evaluation e " +
           "JOIN e.quiz q " +
            "JOIN q.section sc " +
            "JOIN sc.session s " +
            "JOIN s.cour c " +
            "JOIN c.entreprise et " +
            "JOIN et.responsableFormations f " +
            "WHERE f.userId = :responsableId " )
    List<Evaluation> findByResponsableUserId(@Param("responsableId") Long responsableId);

    @Query("SELECT e FROM Evaluation e " +
            "JOIN e.quiz q " +
            "JOIN q.section sc " +
            "JOIN sc.session s " +
            "JOIN s.cour c " +
            "JOIN c.entreprise et " +
            "JOIN et.chargeFormations f " +
            "WHERE f.userId = :responsableId " )
    List<Evaluation> findByChargeUserId(@Param("responsableId") Long responsableId);

    Optional<Evaluation> findByQuizIdAndParticipantUserId(Long quizId, Long participantId);

    List<Evaluation> findLatestByQuizIdAndParticipantUserId(Long quizId, Long participantId);

}
