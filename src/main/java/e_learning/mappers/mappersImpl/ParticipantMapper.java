package e_learning.mappers.mappersImpl;

import e_learning.DTO.ParticipantDto;
import e_learning.DTO.UserDTO;
import e_learning.entity.ClassEntity;
import e_learning.entity.Participant;
import e_learning.entity.Unavailability;
import e_learning.entity.UserApp;
import e_learning.mappers.UserMapper;
import e_learning.repositories.ClassEntityRepository;
import e_learning.repositories.HierarchicalUnitRepository;
import e_learning.repositories.UnavailabilityRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ParticipantMapper {

    private final ClassEntityRepository classEntityRepository;
    private final UnavailabilityRepository unavailabilityRepository;
    private final HierarchicalUnitRepository hierarchicalUnitRepository;

    public ParticipantDto toDto(Participant participant) {
        return new ParticipantDto(
                participant.getUserId() != null ? participant.getUserId() : null,
                participant.getProfileImagePath() != null ? participant.getProfileImagePath() : null,
                participant.getFirstName(),
                participant.getLastName(),
                participant.getEmail(),
                participant.getPassword(),
                participant.getNum(),
                participant.getCIN(),
                participant.getGender(),
                participant.getAge(),
                participant.getCreatedAt(),
                participant.getClasses() != null ? participant.getClasses().stream().map(ClassEntity::getId).collect(Collectors.toList()) : null,
                participant.getUnavailabilities() != null ? participant.getUnavailabilities().stream().map(Unavailability::getId).collect(Collectors.toList()) : null,
                participant.getHierarchicalUnit() != null ? participant.getHierarchicalUnit().getId() : null
        );
    }

    public Participant toEntity(ParticipantDto participantDto) {
        Participant participant = new Participant();
        if (participantDto.id() != null) {
            participant.setUserId(participantDto.id());
        }
        participant.setFirstName(participantDto.firstName());
        participant.setLastName(participantDto.lastName());
        participant.setEmail(participantDto.email());
        participant.setPassword(participantDto.password());
        participant.setNum(participantDto.num());
        participant.setGender(participantDto.gender());
        participant.setAge(participantDto.age());
        participant.setCIN(participantDto.cin());
        participant.setCreatedAt(participantDto.createdAt());

        if(participantDto.imagePath() != null) {
            participant.setProfileImagePath(participantDto.imagePath());
        }
        if (participantDto.classIds() != null) {
            List<ClassEntity> classes = classEntityRepository.findAllById(participantDto.classIds());
            participant.setClasses(classes);
        }

        if (participantDto.unavailabilities() != null) {
            List<Unavailability> unavailabilities = unavailabilityRepository.findAllById(participantDto.unavailabilities());
            participant.setUnavailabilities(unavailabilities);
        }
        if(participantDto.hierarchicalUnitId() != null) {
            participant.setHierarchicalUnit(hierarchicalUnitRepository.findById(participantDto.hierarchicalUnitId()).orElse(null));
        }

        return participant;
    }
}
