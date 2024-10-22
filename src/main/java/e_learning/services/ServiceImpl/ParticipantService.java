package e_learning.services.ServiceImpl;

import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ParticipantService {

    private final ClassEntityRepository classEntityRepository;
    private final ParticipantRepository participantRepository;
    private final ParticipantMapper participantMapper;
    private final CourMapper courMapper;
    private final ClassEntityMapper classEntityMapper;
    private final SessionMapper sessionMapper;
    private final SectionMapper sectionMapper;
    private final ResourceMapper resourceMapper;
    private final LibraryMapper libraryMapper;
    private final EvaluationMapper evaluationMapper;
    private final EvaluationRepository evaluationRepository;
    private final CertificateRepository certificateRepository;
    private final CertificateMapper certificateMapper;
    private final UserMapperImpl userMapperImpl;
    private final UserAppRepository userAppRepository;

    public List<ParticipantDto> getParticipantsInSameClasses(Long participantId) {
        // Step 1: Get all classes of the given participant
        List<ClassEntity> classes = classEntityRepository.findClassesByParticipantsUserId(participantId).stream().filter(ClassEntity::isApproved).toList();

        // Step 2: Get all participants from those classes
        List<Participant> participants = classes.stream()
                .flatMap(c -> participantRepository.findParticipantsByClassesId(c.getId()).stream())
                .distinct() // Ensure there are no duplicates
                .filter(p -> !p.getUserId().equals(participantId)) // Exclude the original participant
                .collect(Collectors.toList());

        // Step 3: Map to DTOs
        return participants.stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ParticipantDto> getAllParticipants() {
        return participantRepository.findAll().stream().map(participantMapper::toDto).collect(Collectors.toList());
    }

    // Method to get all classes for a participant
    public List<ClassEntityDto> getClassesForParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        return participant.getClasses().stream().filter(ClassEntity::isApproved).map(classEntityMapper::toDto).collect(Collectors.toList());
    }

    // Method to get all courses for a participant
    public List<CourDto> getCoursesForParticipant(Long participantId, String className) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        List<ClassEntity> classes = participant.getClasses().stream()
                .filter(c -> className == null || c.getTitre().equalsIgnoreCase(className))
                .filter(ClassEntity::isApproved)
                .collect(Collectors.toList());

        return classes.stream()
                .filter(ClassEntity::isApproved)
                .flatMap(classEntity -> classEntity.getCours().stream())
                .distinct()
                .map(courMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method to get all sessions for all courses of a participant
    public List<SessionDto> getSessionsForParticipant(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));

        return participant.getClasses().stream()
                .filter(ClassEntity::isApproved)
                .flatMap(classEntity -> classEntity.getCours().stream())
                .flatMap(cour -> cour.getSessions().stream())
                .distinct()
                .filter(Session::isApproved)
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method to get all sections for all sessions of a participant
    public List<SectionDto> getSectionsForParticipant(Long participantId) {
        List<SessionDto> sessionDtos = getSessionsForParticipant(participantId);
        List<Session> sessions = sessionDtos.stream().map(sessionMapper::toEntity).collect(Collectors.toList());

        return sessions.stream()
                .flatMap(session -> session.getSections().stream())
                .distinct()
                .filter(Section::isApproved)
                .map(sectionMapper::toDto)
                .collect(Collectors.toList());

    }

    public List<ResourceDto> getResourcesByParticipantId(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        // Fetch all classes of the participant
        List<ClassEntity> classes = participant.getClasses();

        // Extract courses associated with the participant's classes
        List<Cour> cours = classes.stream()
                .filter(ClassEntity::isApproved)
                .flatMap(cl -> cl.getCours().stream())
                .distinct()
                .collect(Collectors.toList());

        // Extract libraries associated with the courses
        List<Library> libraries = cours.stream()
                .flatMap(cour -> cour.getLibraries().stream())
                .distinct()
                .collect(Collectors.toList());

        // Extract resources from the libraries
        List<ResourceDto> resources = libraries.stream()
                .flatMap(library -> library.getResources().stream())
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());

        return resources;
    }


    public List<LibraryDto> getLibrariesByParticipantId(Long participantId) {
        Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));

        // Fetch all classes of the participant
        List<ClassEntity> classes = participant.getClasses().stream().filter(ClassEntity::isApproved).toList();

        // Extract courses associated with the participant's classes
        List<Cour> cours = classes.stream()
                .flatMap(cl -> cl.getCours().stream())
                .distinct()
                .collect(Collectors.toList());

        // Extract libraries associated with the courses
        List<Library> libraries = cours.stream()
                .flatMap(cour -> cour.getLibraries().stream())
                .distinct()
                .collect(Collectors.toList());



        return libraries.stream().map(libraryMapper::toDto).collect(Collectors.toList());
    }


    public Long getMaxEvaluationScore(Long participantId) {
        return participantRepository.findById(participantId)
                .map(participant -> participant.getEvaluations().stream()
                        .mapToLong(Evaluation::getScore)
                        .max()
                        .orElse(0L))
                .orElse(0L);
    }


    public List<EvaluationDto> getEvaluationsForParticipant(Long participantId) {
        return participantRepository.findById(participantId)
                .map(participant -> participant.getEvaluations().stream()
                        .map(evaluationMapper::toDto)  // Assuming you have an EvaluationMapper
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }

    public Page<EvaluationDto> getEvaluationsForParticipant(Long participantId, Pageable pageable) {
    Page<Evaluation> evaluations = evaluationRepository.findByParticipantUserId(participantId, pageable);
    return evaluations.map(evaluationMapper::toDto);
}

    public Long getTotalCertificatesForParticipant(Long participantId) {
        // Logic to count the total certificates for the participant
        return certificateRepository.countByParticipantUserId(participantId);
    }

    public List<CertificateDto> getCertificatesForParticipant(Long participantId) {
        // Logic to retrieve the certificates for the participant
        return certificateRepository.findByParticipantUserId(participantId)
                .stream()
                .map(certificateMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getContactsByParticipantId(Long id) {
        List<UserApp> contacts = new ArrayList<>();
        UserApp participant =  userAppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Particiapant not found"));

        if(((Participant)participant).getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = ((Participant)participant).getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }


    public List<UserDTO> searchContactsByParticipantId(Long id, String name) {
        List<UserApp> contacts = new ArrayList<>();
        Participant participant = participantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Particiapant not found"));

        if(participant.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = participant.getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .filter(userApp -> userApp.getFirstName().contains(name) || userApp.getLastName().contains(name))
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());


    }

}
