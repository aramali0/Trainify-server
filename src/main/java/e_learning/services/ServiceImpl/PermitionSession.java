package e_learning.services.ServiceImpl;

import e_learning.entity.Cour;
import e_learning.entity.Session;
import e_learning.entity.UserApp;
import e_learning.repositories.CourRepository;
import e_learning.repositories.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermitionSession {

    private final CourRepository courRepository;

    public boolean canCreateSession(Long courId, UserApp currentUser) {
        Cour cour = courRepository.findById(courId).orElse(null);

        if (cour == null) {
            return false;
        }


        // Check if the current user is the responsableFormation or a formateur of the course
        return cour.getEntreprise().getResponsableFormations().stream().anyMatch((r) -> r.getUserId().equals(currentUser.getUserId())) ||
                cour.getFormateurs().stream().anyMatch(f -> f.getUserId().equals(currentUser.getUserId()));
    }

}
