package e_learning.services.ServiceImpl;

import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.exceptions.ResourceNotFoundException;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.apache.el.lang.ELArithmetic;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResponsableFormationService {
    private final ResponsableFormationRepository responsableFormationRepository;
    private final ResponsableFormationMapper responsableFormationMapper;
    private final ParticipantMapper participantMapper;
    private final ClassEntityMapper classEntityMapper;
    private final FormateurMapper formateurMapper;
    private final SessionMapper sessionMapper;
    private final SectionMapper sectionMapper;
    private final QuestionMapper questionMapper;
    private final EvaluationMapper evaluationMapper;
    private final UserMapperImpl userMapperImpl;
    private final EvaluationRepository evaluationRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final HierarchicalUnitMapper hierarchicalUnitMapper;
    private final ActionApprovalRepository actionApprovalRepository;  // To store pending actions
    private final CourRepository courRepository;
    private final SessionRepository sessionRepository;
    private final SectionRepository sectionRepository;
    private final ClassEntityRepository classEntityRepository;
    private final LibraryRepository libraryRepository;
    private final QuizRepository quizRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;
    private final EmailService emailService;
    private final ResourceRepository resourceRepository;
    private final EntrepriseMapper entrepriseMapper;
    private final UserAppRepository userAppRepository;

    public List<UserDTO> getAllParticipants(Long responsableId) {
        ResponsableFormation responsable = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));
        Entreprise entreprise = entrepriseRepository.findById(responsable.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        return entreprise.getAppendingParticipants().stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }

    public boolean verifyParticipant(Long responsableId, Long participantId, boolean verify) {
        ResponsableFormation responsable = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsable.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        Participant participant = entreprise.getAppendingParticipants().stream()
                .filter(p -> p.getUserId().equals(participantId))
                .findFirst()
                .orElse(null);

        if (participant != null) {
            if (verify) {
                participant.setVerified(true);
            }
            entreprise.getAppendingParticipants().remove(participant);
            responsableFormationRepository.save(responsable);
            return true;
        }
        return false;
    }

    public ResponsableFormationDto saveResponsableFormation(ResponsableFormationDto responsableFormationDto) {
        ResponsableFormation responsableFormation = responsableFormationMapper.toEntity(responsableFormationDto);
        responsableFormation = responsableFormationRepository.save(responsableFormation);
        return responsableFormationMapper.toDto(responsableFormation);
    }

    public ResponsableFormationDto getResponsableFormationById(Long id) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResponsableFormation not found"));
        return responsableFormationMapper.toDto(responsableFormation);
    }

    public List<ResponsableFormationDto> getAllResponsableFormations() {
        return responsableFormationRepository.findAll().stream()
                .map(responsableFormationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ResponsableFormationDto updateResponsableFormation(Long id, ResponsableFormationDto responsableFormationDto) {
        ResponsableFormation responsableFormation = responsableFormationMapper.toEntity(responsableFormationDto);
        responsableFormation.setUserId(id);
        responsableFormation = responsableFormationRepository.save(responsableFormation);
        return responsableFormationMapper.toDto(responsableFormation);
    }

    public void deleteResponsableFormation(Long id) {
        responsableFormationRepository.deleteById(id);
    }

     @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipantsByResponsableId(Long formateurId) {
        ResponsableFormation formateur = responsableFormationRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

         Entreprise entreprise = entrepriseRepository.findById(formateur.getEntreprise().getId())
                 .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        List<Participant> participants = entreprise.getParticipants()
                .stream().filter(UserApp::isVerified).toList();

        return participants.stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }


   @Transactional(readOnly = true)
    public List<ClassEntityDto> getClassesByResponsableId(Long formateurId) {


        ResponsableFormation formateur = responsableFormationRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Responsable de formation not found"));

       Entreprise entreprise = entrepriseRepository.findById(formateur.getEntreprise().getId())
               .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<ClassEntity> classes = cours.stream()
                .flatMap(cour -> cour.getClasses().stream())
                .distinct() // To avoid duplicates
                .filter(ClassEntity::isApproved)
                .collect(Collectors.toList());


        return classes.stream()
                .map(classEntityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<FormateurDto> getFormateursByResponsableId(Long formateurId) {


        ResponsableFormation formateur = responsableFormationRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        Entreprise entreprise = entrepriseRepository.findById(formateur.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        List<Formateur> formateurs = entreprise.getFormateurs();


        return formateurs.stream()
                .map(formateurMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionByResponsableId(Long formateurId) {


        ResponsableFormation formateur = responsableFormationRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        Entreprise entreprise = entrepriseRepository.findById(formateur.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .filter(Session::isApproved)
                .toList();


        return sessions.stream()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SectionDto> geySectionsByResponsableId(Long responsableId) {


        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsableFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Section> sections = sessions.stream()
                .flatMap(cour -> cour.getSections().stream())
                .distinct() // To avoid duplicates
                .filter(Section::isApproved)
                .collect(Collectors.toList());

        return sections.stream()
                .map(sectionMapper::toDto)
                .collect(Collectors.toList());
    }
    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByResponsableId(Long responableId) {


        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responableId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsableFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Section> sections = sessions.stream()
                .flatMap(cour -> cour.getSections().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Quiz> quizzes  = sections.stream()
                .flatMap(section -> section.getQuizzes().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Question> questions = quizzes.stream()
                .flatMap(quiz -> quiz.getQuestions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ParticipantUnavailabilityDto> getParticipantsUnavailabilityByResponsableId(Long responsableId) {
        List<ParticipantDto> participantDtos = getParticipantsByResponsableId(responsableId);

        // Fetch participants managed by the Responsable
        List<Participant> participants = participantDtos.stream()
                .map(participantMapper::toEntity)
                .collect(Collectors.toList());
        // Convert to ParticipantUnavailabilityDto
        return participants.stream()
                .flatMap(participant -> participant.getUnavailabilities().stream()
                        .map(unavailability -> {
                            ParticipantUnavailabilityDto dto = new ParticipantUnavailabilityDto();
                            dto.setId(unavailability.getId());
                            dto.setParticipantName(participant.getFirstName());
                            dto.setStartDate(unavailability.getStartDate());
                            dto.setEndDate(unavailability.getEndDate());
                            return dto;
                        }))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageScoreByResponsableId(Long responsableId) {
        ResponsableFormation responsable = responsableFormationRepository.findById(responsableId)
            .orElseThrow(() -> new RuntimeException("Responsable not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsable.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        List<Evaluation> evaluations = entreprise.getCours().stream()
            .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
            .flatMap(classEntity -> classEntity.getParticipants().stream())
            .flatMap(participant -> participant.getEvaluations().stream())
            .collect(Collectors.toList());

        return evaluations.stream()
            .mapToDouble(Evaluation::getScore)
            .average()
            .orElse(0.0);
    }

    @Transactional(readOnly = true)
    public List<EvaluationDto> getLatestEvaluationsByResponsableId(Long responsableId) {
        ResponsableFormation responsable = responsableFormationRepository.findById(responsableId)
            .orElseThrow(() -> new RuntimeException("Responsable not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsable.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        return entreprise.getCours().stream()
            .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
            .flatMap(classEntity -> classEntity.getParticipants().stream())
            .flatMap(participant -> participant.getEvaluations().stream())
            .sorted(Comparator.comparing(Evaluation::getCreatedAt).reversed())
            .limit(10)
            .map(evaluationMapper::toDto)
            .collect(Collectors.toList());
    }

      public Page<EvaluationDto> getEvaluationsByResponsableAndSection(
            Long responsableId, Long sectionId,Long quizeId, int page, int size) {

          Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
          Page<Evaluation> evaluations = evaluationRepository
                  .findByResponsableIdAndSectionId(responsableId,sectionId ,quizeId, pageable);
          // Convert the page of Evaluation entities to a page of EvaluationDto objects
          return evaluations.map(evaluationMapper::toDto);
      }

    public List<EvaluationDto> getEvaluationsByResponsable(
            Long responsableId) {

        List<Evaluation> evaluations = evaluationRepository
                .findByResponsableUserId(responsableId);

        // Convert the page of Evaluation entities to a page of EvaluationDto objects
        return evaluations.stream().map(evaluationMapper::toDto).toList();
    }


    public Integer getMaxSize(Long id) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsableFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        return entreprise.getMaxSize();
    }

    public boolean updateMaxSize(Long id, Integer maxSize) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        Entreprise entreprise = entrepriseRepository.findById(responsableFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));


        entreprise.setMaxSize(maxSize);
        responsableFormationRepository.save(responsableFormation);
        return true;
    }

    public List<HierarchicalUnitDto> getHierarchicalUnitsByResponsableId(Long id) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        if(responsableFormation.getEntreprise() == null) {
            throw new ResourceNotFoundException("Entreprise not found");
        }

        return responsableFormation.getEntreprise().getHierarchicalUnits().stream()
                .map(hierarchicalUnitMapper::toDto)
                .collect(Collectors.toList());


    }

    public List<UserDTO> getContactsByResponsableId(Long id) {

        List<UserApp> contacts = new ArrayList<>();
        Entreprise entrepriseOptional = entrepriseRepository.findByResponsableFormationsUserId(id);
        if(entrepriseOptional == null) {
            throw new ResourceNotFoundException("Entreprise not found");
        }
        contacts.addAll(entrepriseOptional.getResponsableFormations());
        contacts.addAll(entrepriseOptional.getFormateurs());
        contacts.addAll(entrepriseOptional.getParticipants());
        contacts.addAll(entrepriseOptional.getChargeFormations());


        return  contacts.stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }

    public List<UserDTO> searchContactsByResponsableId(Long id, String name) {
        List<UserApp> contacts = new ArrayList<>();
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        if(responsableFormation.getEntreprise() == null) {
            throw new ResourceNotFoundException("Entreprise not found");
        }
        contacts.addAll(responsableFormation.getEntreprise().getResponsableFormations());
        contacts.addAll(responsableFormation.getEntreprise().getFormateurs());
        contacts.addAll(responsableFormation.getEntreprise().getParticipants());
        contacts.addAll(responsableFormation.getEntreprise().getChargeFormations());

        return  contacts.stream()
                .filter(user -> user.getFirstName().contains(name) || user.getLastName().contains(name))
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }


    // Method to approve an action
    public void approveAction(Long responsableId, Long requestId, String comment) throws Exception {
        ActionApproval actionApproval = actionApprovalRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));

        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        // Validate if the action belongs to the same entreprise
        if (!responsableFormation.getEntreprise().getId().equals(actionApproval.getEntrepriseId())) {
            throw new AccessDeniedException("You are not allowed to approve this action.");
        }

        actionApproval.setApproved(true);
        actionApproval.setApprovedDate(LocalDateTime.now()); // Log the approval timestamp
        actionApproval.setResponsableFormationId(responsableId);
        actionApproval.setComment(comment);
        actionApprovalRepository.save(actionApproval);

        // Perform the actual action (like adding a participant, modifying a class)
        performAction(actionApproval);
    }

    public void requestUpdate(Long responsableId, Long requestId,String comment) throws Exception {
        ActionApproval actionApproval = actionApprovalRepository.findById(requestId)
                .orElseThrow(() -> new Exception("Request not found"));

        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        if (!responsableFormation.getEntreprise().getId().equals(actionApproval.getEntrepriseId())) {
            throw new AccessDeniedException("You are not allowed to approve this action.");
        }
        actionApproval.setUpdateRequested(true);
        actionApproval.setUpdateRequestedDate(LocalDateTime.now());
        actionApproval.setComment(comment);
        actionApproval.setResponsableFormationId(responsableId);
        actionApprovalRepository.save(actionApproval);

        actionApprovalRepository.save(actionApproval);
        updateAction(actionApproval);
    }
    // Method to reject an action
    public void rejectAction(Long responsableId, Long requestId,String comment) throws AccessDeniedException {
        ActionApproval actionApproval = actionApprovalRepository.findById(requestId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));

        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        // Validate if the action belongs to the same entreprise
        if (!responsableFormation.getEntreprise().getId().equals(actionApproval.getEntrepriseId())) {
            throw new AccessDeniedException("You are not allowed to reject this action.");
        }

        // Reject the action
        actionApproval.setRejected(true);
        actionApproval.setApprovedDate(LocalDateTime.now()); // Log the rejection timestamp
        actionApproval.setResponsableFormationId(responsableId);
        actionApproval.setComment(comment);
        actionApprovalRepository.save(actionApproval);
    }

    private void updateAction(ActionApproval actionApproval) {
        switch (actionApproval.getActionType()) {
            case "COURSE":
                Cour cour = courRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                cour.setApproved(false);
                cour.setSent(false);
                courRepository.save(cour);
                cour.getSessions().forEach(session -> {
                    session.setApproved(false);
                    session.setSent(false);
                    sessionRepository.save(session);
                    session.getSections().forEach(section -> {
                        section.setApproved(false);
                        section.setSent(false);
                        sectionRepository.save(section);
                    });
                });
                break;
            case "CLASS":
                ClassEntity classEntity = classEntityRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
                classEntity.setApproved(false);
                classEntityRepository.save(classEntity);
                break;
            case "LIBRARY":
                Library library = libraryRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Library not found"));
                library.setApproved(false);
                libraryRepository.save(library);
                break;
            case "QUIZ":
                Quiz quiz = quizRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
                quiz.setApproved(false);
                quizRepository.save(quiz);
                break;
            case "RESOURCE":
                ResourceEntity resource = resourceRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
                resource.setApproved(false);
                resourceRepository.save(resource);
                break;
        }
    }
    // This method would contain the logic for actually performing the action
    private void performAction(ActionApproval actionApproval) throws Exception {
        // You can call different services here depending on action type (like adding a participant, etc.)
        switch (actionApproval.getActionType()) {
            case "COURSE":
               Cour cour = courRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Course not found"));
                cour.setApproved(true);
                courRepository.save(cour);
                cour.getSessions().forEach(session -> {
                    session.setApproved(true);
                    session.setSent(false);
                    sessionRepository.save(session);
                    session.getSections().forEach(section -> {
                        section.setApproved(true);
                        section.setSent(false);
                        sectionRepository.save(section);
                    });
                });
                break;
            case "CLASS":
                ClassEntity classEntity = classEntityRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Class not found"));
                classEntity.setApproved(true);
                classEntityRepository.save(classEntity);
                break;
            case "LIBRARY":
                Library library = libraryRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Library not found"));
                library.setApproved(true);
                libraryRepository.save(library);
                break;
            case "QUIZ":
                Quiz quiz = quizRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Quiz not found"));
                quiz.setApproved(true);
                Section section1 = quiz.getSection();
                List<Participant> participants = participantRepository.findByClassesCoursSessionsSectionsId(quiz.getSection().getId());

                for (Participant participant : participants) {
                    // Prepare notification data
                    Map<String, Object> notificationData = new HashMap<>();
                    notificationData.put("message", "A new quiz has been created in your section." + section1.getTitle());
                    notificationData.put("quizId", section1.getId());

                    // Send real-time notification via WebSocket
                    brokerMessagingTemplate.convertAndSendToUser(String.valueOf(participant.getUserId()), "/queue/quiz's", notificationData);

                    // Send Email (assuming emailService is already set up)
                    String emailBody = "Hello " + participant.getFirstName() + " "+ participant.getLastName() + ",\n\n" +
                            "A new quiz is available in the section "+ section1.getTitle() + " you are enrolled in. Visit your dashboard to participate.\n\n" +
                            "Best regards,\nEHC Group";
                    emailService.sendEmail(participant.getEmail(), "New Quiz Notification", emailBody);
                }
                quizRepository.save(quiz);
                break;
            case "RESOURCE":
                ResourceEntity resource = resourceRepository.findById(actionApproval.getObjectId())
                        .orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
                resource.setApproved(true);
                resourceRepository.save(resource);
                break;
        }
    }

    // Helper method to check if an action is approved (used in Chargé de formation service)
    public boolean isActionApproved(Long actionId) {
        ActionApproval actionApproval = actionApprovalRepository.findById(actionId)
                .orElseThrow(() -> new ResourceNotFoundException("Action not found"));
        return actionApproval.isApproved();
    }

    public List<ActionApproval> getPendingRequests(Long responsableFormationId) {

        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableFormationId)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        return actionApprovalRepository.findByEntrepriseIdAndApprovedFalseAndRejectedFalseAndUpdateRequestedFalse(responsableFormation.getEntreprise().getId());
    }


    public EntrepriseDto getEntrepriseByResponsableFormationId(Long id) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ResponsableFormation not found"));

        return entrepriseRepository.findById(responsableFormation.getEntreprise().getId())
                .map(entrepriseMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));
    }




}
