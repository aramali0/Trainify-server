package e_learning.repositories;

import e_learning.entity.Entreprise;
import e_learning.entity.HierarchicalUnit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HierarchicalUnitRepository extends JpaRepository<HierarchicalUnit, String> {
    List<HierarchicalUnit> findByEntrepriseId(Long entrepriseId);
    List<HierarchicalUnit> findByParentId(String parentId); // Get child units by parent

    Optional<HierarchicalUnit> findByName(String unitName);

    List<HierarchicalUnit> findByEntrepriseNomCommercial(String entrepriseNomCommercial);

    Optional<HierarchicalUnit> findByNameAndEntrepriseId(String unitName,Long entrepriseId);
}
