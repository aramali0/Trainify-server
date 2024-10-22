package e_learning.repositories;

import e_learning.entity.Response;
import e_learning.entity.Section;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SectionRepository extends JpaRepository<Section,Long> {
    List<Section> findByCreatedByUserId(Long userId);

    List<Section> findSectionsBySessionId(Long aLong);
}
