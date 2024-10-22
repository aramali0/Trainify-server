package e_learning.services.ServiceImpl;

import e_learning.entity.RoleApp;
import e_learning.enums.UserRole;
import e_learning.repositories.RoleAppRepository;
import e_learning.services.RoleService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleAppRepository roleAppRepository;
    @Override
    public RoleApp saveRole(RoleApp roleApp) {
        return roleAppRepository.save(roleApp);
    }

    public RoleApp findByRole(UserRole userRole) {
        return roleAppRepository.findFirstByRole(userRole);
    }
}
