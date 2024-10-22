package e_learning.repositories;

import e_learning.entity.ClassEntity;
import e_learning.entity.RoleApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassEntityRepository extends JpaRepository<ClassEntity,Long> {

    List<ClassEntity> findByCoursId(Long courId);

    List<ClassEntity> findByParticipantsUserId(Long participantId);

    List<ClassEntity> findClassesByParticipantsUserId(Long participantId);
}
