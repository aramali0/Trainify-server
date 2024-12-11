package e_learning.specifications;

import e_learning.entity.Library;
import org.springframework.data.jpa.domain.Specification;

public class LibrarySpecifications {

    public static Specification<Library> hasNameContaining(String name) {
        return (root, query, criteriaBuilder) ->
                name == null ? null : criteriaBuilder.like(criteriaBuilder.lower(root.get("name")), "%" + name.toLowerCase() + "%");
    }

    public static Specification<Library> hasFormationId(Long formationId) {
        return (root, query, criteriaBuilder) ->
                formationId == null ? null : criteriaBuilder.equal(root.get("cour").get("id"), formationId);
    }
}
