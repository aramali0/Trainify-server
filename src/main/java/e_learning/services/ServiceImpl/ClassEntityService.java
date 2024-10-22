package e_learning.services.ServiceImpl;

import e_learning.DTO.ClassEntityDto;
import e_learning.DTO.UserDTO;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.ClassEntityMapper;
import e_learning.mappers.mappersImpl.UserMapperImpl;
import e_learning.repositories.*;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ClassEntityService {

    private final ClassEntityRepository classEntityRepository;
    private final CourRepository courRepository;
    private final ParticipantRepository participantRepository;
    private final ClassEntityMapper classEntityMapper;
    private final UserAppRepository userAppRepository;
    private final UserMapperImpl userMapperImpl;
    private final ActionApprovalRepository actionApprovalRepository;

    public List<ClassEntityDto> getAllClasses() {
        List<ClassEntity> classEntities = classEntityRepository.findAll();
        return classEntities.stream().map(classEntityMapper::toDto).collect(Collectors.toList());
    }

    public Optional<ClassEntityDto> getClassById(Long id) {
        return classEntityRepository.findById(id).map(classEntityMapper::toDto);
    }

    public ClassEntityDto createClass(ClassEntityDto classEntityDto , UserApp userApp) {
        ClassEntity classEntity = classEntityMapper.toEntity(classEntityDto);
        classEntity.setApproved(true);
        classEntity.setCreatedBy(userApp);
        classEntity = classEntityRepository.save(classEntity);
        updateAssociations(classEntity, classEntityDto);
        ClassEntity newClass = classEntityRepository.save(classEntity);

        if(userApp instanceof ChargeFormation)
        {
            ActionApproval actionApproval = new ActionApproval();
            actionApproval.setApproved(false);
            actionApproval.setObjectId(newClass.getId());
            actionApproval.setActionType("CLASS");
            actionApproval.setCreatedDate(LocalDateTime.now());
            actionApproval.setChargeFormationId(userApp.getUserId());
            actionApproval.setEntrepriseId(newClass.getCours().get(0).getEntreprise().getId());

            actionApprovalRepository.save(actionApproval);
            newClass.setApproved(false);
            classEntityRepository.save(newClass);

        }

        return classEntityMapper.toDto(newClass);
    }

    public Optional<ClassEntityDto> updateClass(Long id, ClassEntityDto classEntityDto) {
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(id);
        if (classEntityOptional.isPresent()) {
            ClassEntity classEntity = classEntityMapper.toEntity(classEntityDto);
            classEntity.setId(id);
            updateAssociations(classEntity, classEntityDto);
            ClassEntity newClass = classEntityRepository.save(classEntity);
            return Optional.of(classEntityMapper.toDto(newClass));
        }
        return Optional.empty();
    }


    private void updateAssociations(ClassEntity classEntity, ClassEntityDto classEntityDto) {
        if (classEntityDto.participantIds() != null) {
            List<Participant> participants = participantRepository.findAllById(classEntityDto.participantIds());

            // Clear existing participants before adding new ones
            classEntity.getParticipants().clear();
            classEntity.getParticipants().addAll(participants);

            // Update the other side of the relationship
            for (Participant participant : participants) {
                if (!participant.getClasses().contains(classEntity)) {
                    participant.getClasses().add(classEntity);
                }
            }
            participantRepository.saveAll(participants);
        }
        if (classEntityDto.courIds() != null) {
            List<Cour> cours = courRepository.findAllById(classEntityDto.courIds());

            // Clear existing cours before adding new ones
            classEntity.getCours().clear();
            classEntity.getCours().addAll(cours);

            // Update the other side of the relationship
            for (Cour cour : cours) {
                if (!cour.getClasses().contains(classEntity)) {
                    cour.getClasses().add(classEntity);
                }
            }
            courRepository.saveAll(cours);
        }
    }

    public void deleteClass(Long id) {
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(id);
        if (classEntityOptional.isPresent()) {
            ClassEntity classEntity = classEntityOptional.get();

            // Clear associations with 'Cours'
            for (Cour cour : classEntity.getCours()) {
                cour.getClasses().remove(classEntity);
            }
            courRepository.saveAll(classEntity.getCours());  // Save the updated Cours

            // Clear the list of courses in the class before deleting it
            classEntity.getCours().clear();
            classEntityRepository.save(classEntity);

            // Now you can safely delete the class
            classEntityRepository.deleteById(id);
        }
    }
    public Optional<ClassEntityDto> addClassToCour(Long classId, Long courId) {
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(classId);
        Optional<Cour> courOptional = courRepository.findById(courId);

        if (classEntityOptional.isPresent() && courOptional.isPresent()) {
            ClassEntity classEntity = classEntityOptional.get();
            Cour cour = courOptional.get();

            if (!classEntity.getCours().contains(cour)) {
                classEntity.getCours().add(cour);
                cour.getClasses().add(classEntity);
            }

            classEntityRepository.save(classEntity);
            courRepository.save(cour);

            return Optional.of(classEntityMapper.toDto(classEntity));
        }
        return Optional.empty();
    }

    public Optional<ClassEntityDto> addParticipantToClass(Long classId, Long participantId) {
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(classId);
        Optional<Participant> participantOptional = participantRepository.findById(participantId);

        if (classEntityOptional.isPresent() && participantOptional.isPresent()) {
            ClassEntity classEntity = classEntityOptional.get();
            Participant participant = participantOptional.get();

            if (!classEntity.getParticipants().contains(participant)) {
                classEntity.getParticipants().add(participant);
            }

            classEntity = classEntityRepository.save(classEntity);
            return Optional.of(classEntityMapper.toDto(classEntity));
        }
        return Optional.empty();
    }

    public Optional<ClassEntityDto> addParticipantsToClass(Long classId, List<Long> participantIds) {
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(classId);
        if (classEntityOptional.isPresent()) {
            ClassEntity classEntity = classEntityOptional.get();
            List<Participant> participants = participantRepository.findAllById(participantIds);
            classEntity.getParticipants().addAll(participants);
            classEntity = classEntityRepository.save(classEntity);
            return Optional.of(classEntityMapper.toDto(classEntity));
        }
        return Optional.empty();
    }

    public List<ClassEntityDto> getClassesByParticipant(Long participantId) {
        List<ClassEntity> classEntities = classEntityRepository.findByParticipantsUserId(participantId);
        return classEntities.stream().map(classEntityMapper::toDto).collect(Collectors.toList());
    }

    public List<ClassEntityDto> getClassesByCour(Long courId) {
        List<ClassEntity> classEntities = classEntityRepository.findByCoursId(courId);
        return classEntities.stream().map(classEntityMapper::toDto).collect(Collectors.toList());
    }

     public List<UserDTO> getActiveUsersByClass(Long classId) {
        // Retrieve the class entity
        Optional<ClassEntity> classEntityOptional = classEntityRepository.findById(classId);
        if (!classEntityOptional.isPresent()) {
            return Collections.emptyList(); // Or throw an exception if preferred
        }

        ClassEntity classEntity = classEntityOptional.get();
        List<Participant> participants = classEntity.getParticipants();

        // Get active users from the participant list
        LocalDateTime activeThreshold = LocalDateTime.now().minusMinutes(5);
        List<UserApp> activeUsers = userAppRepository.findByLastActiveTimeAfter(activeThreshold);

        // Filter the active users to those who are participants of the class
        List<UserDTO> activeUserDtos = activeUsers.stream()
            .filter(user -> participants.stream()
                .anyMatch(participant -> participant.getUserId().equals(user.getUserId())))
            .map(userMapperImpl::fromEntity) // Map UserApp to UserAppDto
            .collect(Collectors.toList());

        return activeUserDtos;
    }
}
