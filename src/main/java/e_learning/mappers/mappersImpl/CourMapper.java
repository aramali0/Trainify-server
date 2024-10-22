package e_learning.mappers.mappersImpl;

import e_learning.DTO.CourDto;
import e_learning.DTO.UserDTO;
import e_learning.entity.*;
import e_learning.enums.Langue;
import e_learning.mappers.UserMapper;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class CourMapper {


    private final FormateurRepository formateurRepository;
    private final ClassEntityRepository classEntityRepository;
    private final EntrepriseRepository entrepriseRepository;

    private SessionRepository sessionRepository;

    public CourDto toDto(Cour cour) {
        return new CourDto(
                cour.getId(),
                cour.getTitre(),
                cour.getSubTitre(),
                cour.getDescription(),
                cour.getCreatedAt(),
                cour.getMiseAJour(),
                cour.getLangue().toString(),
                cour.getDuree(),
                cour.isApproved(),
                cour.isSent(),
                cour.getEntreprise() != null ? cour.getEntreprise().getId() : null,
                cour.getFormateurs() != null ? cour.getFormateurs().stream().map(Formateur::getUserId).collect(Collectors.toList()) : null,
                cour.getClasses() != null ? cour.getClasses().stream().map(ClassEntity::getId).collect(Collectors.toList()) : null,
                cour.getSessions() != null ? cour.getSessions().stream().map(Session::getId).collect(Collectors.toList()) : null,
                cour.getImagePath(),
                cour.isFavorite()
        );
    }

    public Cour toEntity(CourDto courDto) {
        Cour cour = new Cour();
        if (courDto.id() != null) cour.setId(courDto.id());
        cour.setTitre(courDto.titre());
        cour.setSubTitre(courDto.subTitre());
        cour.setDescription(courDto.description());
        cour.setCreatedAt(courDto.createdAt());
        cour.setMiseAJour(courDto.miseAJour());
        cour.setLangue(Langue.valueOf(courDto.langue()));
        cour.setDuree(courDto.duree());
        cour.setApproved(courDto.isApproved());
        cour.setFavorite(Boolean.TRUE.equals(courDto.isFavorite()));
        cour.setSent(courDto.isSent());

        if(courDto.entrepriseId() != null)
        {
            Entreprise entreprise = entrepriseRepository.findById(courDto.entrepriseId())
                    .orElse(null);
            cour.setEntreprise(entreprise);
        }

        if (courDto.formateurIds() == null)
            cour.setFormateurs(null);
        else {
            List<Formateur> formateurs = formateurRepository.findAllById(courDto.formateurIds());
            cour.setFormateurs(formateurs);
        }

        if (courDto.sessionIds() == null)
            cour.setSessions(null);
        else {
            List<Session> sessions = sessionRepository.findAllById(courDto.sessionIds());
            cour.setSessions(sessions);
        }

        if (courDto.classIds() == null)
        {
            cour.setClasses(null);
        }
        else {
           List<ClassEntity> classes = classEntityRepository.findAllById(courDto.classIds());
           cour.setClasses(classes);
        }

        if(courDto.imagePath() != null)
        {
            cour.setImagePath(courDto.imagePath());
        }

        return cour;
    }
}
