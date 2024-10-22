package e_learning.mappers.mappersImpl;

import e_learning.DTO.FeedbackDto;
import e_learning.entity.Feedback;
import e_learning.entity.Formateur;
import e_learning.entity.UserApp;
import e_learning.repositories.FormateurRepository;
import e_learning.repositories.UserAppRepository;
import org.springframework.stereotype.Component;

@Component
public class FeedbackMapper {

    private final UserAppRepository userAppRepository;

    public FeedbackMapper(FormateurRepository formateurRepository, UserAppRepository userAppRepository) {
        this.userAppRepository = userAppRepository;
    }

    public FeedbackDto toDto(Feedback feedback) {
        FeedbackDto dto = new FeedbackDto();
        if (feedback.getId() != null) {
            dto.setId(feedback.getId());
        } else {
            dto.setId(null);
        }
        dto.setOrganization(feedback.getOrganization());
        dto.setPlatformExperience(feedback.getPlatformExperience());
        dto.setMaterials(feedback.getMaterials());
        dto.setParticipantGroup(feedback.getParticipantGroup());
        dto.setAssimilation(feedback.getAssimilation());
        dto.setDuration(feedback.getDuration());
        dto.setSatisfactionLevel(feedback.getSatisfactionLevel());
        dto.setRemarks(feedback.getRemarks());
        if(feedback.getUserApp() != null) {
            dto.setFormateurId(feedback.getUserApp().getUserId());
        }
        else {
            dto.setFormateurId(null);
        }
        return dto;
    }

    public Feedback toEntity(FeedbackDto dto) {
        Feedback feedback = new Feedback();
        if (dto.getId() != null) {
            feedback.setId(dto.getId());
        } else {
            feedback.setId(null);
        }
        feedback.setOrganization(dto.getOrganization());
        feedback.setPlatformExperience(dto.getPlatformExperience());
        feedback.setMaterials(dto.getMaterials());
        feedback.setParticipantGroup(dto.getParticipantGroup());
        feedback.setAssimilation(dto.getAssimilation());
        feedback.setDuration(dto.getDuration());
        feedback.setSatisfactionLevel(dto.getSatisfactionLevel());
        feedback.setRemarks(dto.getRemarks());
        if (dto.getFormateurId() != null) {
            UserApp userApp = userAppRepository.getReferenceById(dto.getFormateurId());
            feedback.setUserApp(userApp);
        }
        return feedback;
    }
}
