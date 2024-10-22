package e_learning.repositories;

import e_learning.entity.RoleApp;
import e_learning.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleAppRepository extends JpaRepository<RoleApp,Long> {
    RoleApp findFirstByRole(UserRole userRole);
}
