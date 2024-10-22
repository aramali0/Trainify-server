package e_learning.mappers.mappersImpl;
import java.util.List;
import java.util.stream.Collectors;

import e_learning.DTO.ClassEntityDto;
import e_learning.entity.ClassEntity;
import e_learning.entity.Cour;
import e_learning.entity.Participant;
import e_learning.repositories.CourRepository;
import e_learning.repositories.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ClassEntityMapper {

    private final ParticipantRepository participantRepository;
    private final CourRepository courRepository;

    public ClassEntityDto toDto(ClassEntity classEntity) {
        return new ClassEntityDto(
                classEntity.getId(),
                classEntity.getTitre(),
                classEntity.isApproved(),
                classEntity.getCreatedBy() != null ? classEntity.getCreatedBy().getUserId() : null,
                classEntity.getParticipants() != null ? classEntity.getParticipants().stream().map(Participant::getUserId).collect(Collectors.toList()) : null,
                classEntity.getCours() != null ? classEntity.getCours().stream().map(Cour::getId).collect(Collectors.toList()) : null
        );
    }

    public ClassEntity toEntity(ClassEntityDto classEntityDto) {
        ClassEntity classEntity = new ClassEntity();
        classEntity.setId(classEntityDto.id());
        classEntity.setTitre(classEntityDto.titre());

        if (classEntityDto.participantIds() != null) {
            List<Participant> participants = participantRepository.findAllById(classEntityDto.participantIds());
            classEntity.setParticipants(participants);
        }

        if (classEntityDto.courIds() != null) {
            List<Cour> cours = courRepository.findAllById(classEntityDto.courIds());
            classEntity.setCours(cours);
        }

        if (classEntityDto.createdBy() != null) {
            classEntity.setCreatedBy(participantRepository.findById(classEntityDto.createdBy()).orElse(null));
        }
        classEntity.setApproved(classEntityDto.isApproved());

        return classEntity;
    }
}
