package e_learning.repositories;

import e_learning.entity.ClassEntity;
import e_learning.entity.ResourceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ResourceRepository extends JpaRepository<ResourceEntity,Long> {
    List<ResourceEntity> findBySectionSessionCourId(Long courId);
    List<ResourceEntity> findByLibraryId(Long libraryId);
    List<ResourceEntity> findBySectionId(Long sectionId);
    ResourceEntity findByTitleAndLibraryId(String fileName, Long libraryId);

    ResourceEntity findByTitle(String fileName);
}
