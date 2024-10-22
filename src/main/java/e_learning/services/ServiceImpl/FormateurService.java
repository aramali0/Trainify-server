package e_learning.services.ServiceImpl;

import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FormateurService {
    private final FormateurRepository formateurRepository;
    private final FormateurMapper formateurMapper;
    private final ParticipantMapper participantMapper;
    private final ClassEntityMapper classEntityMapper;
    private final SessionMapper sessionMapper;
    private final SectionMapper sectionMapper;
    private final QuestionMapper questionMapper;
    private final EvaluationMapper evaluationMapper;
    private final FeedbackMapper feedbackMapper;
    private final FeedbackRepository feedbackRepository;
    private final EvaluationRepository evaluationRepository;
    private final LibraryRepository libraryRepository;
    private final HierarchicalUnitMapper hierarchicalUnitMapper;
    private final UserMapperImpl userMapperImpl;
    private final EntrepriseMapper entrepriseMapper;
    private final UserAppRepository userAppRepository;

    public FormateurDto saveFormateur(FormateurDto formateurDto) {
        Formateur formateur = formateurMapper.toEntity(formateurDto);
        formateur = formateurRepository.save(formateur);
        return formateurMapper.toDto(formateur);
    }

    public FormateurDto getFormateurById(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));
        return formateurMapper.toDto(formateur);
    }

    public List<FormateurDto> getAllFormateurs() {
        return formateurRepository.findAll().stream()
                .map(formateurMapper::toDto)
                .collect(Collectors.toList());
    }

    public FormateurDto updateFormateur(Long id, FormateurDto formateurDto) {
        Formateur formateur = formateurMapper.toEntity(formateurDto);
        formateur.setUserId(id);
        formateur = formateurRepository.save(formateur);
        return formateurMapper.toDto(formateur);
    }

    public void deleteFormateur(Long id) {
        formateurRepository.deleteById(id);
    }



    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipantsByFormateurId(Long formateurId) {


        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        List<Cour> cours = formateur.getCours();

        List<ClassEntity> classes = cours.stream()
                .flatMap(cour -> cour.getClasses().stream())
                .distinct() // To avoid duplicates
                .filter(ClassEntity::isApproved)
                .collect(Collectors.toList());

        List<Participant> participants = classes.stream()
                .flatMap(classEntity -> classEntity.getParticipants().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        return participants.stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

   @Transactional(readOnly = true)
    public List<ClassEntityDto> getClassesByFormateurId(Long formateurId) {


        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        List<Cour> cours = formateur.getCours();

        List<ClassEntity> classes = cours.stream()
                .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());


        return classes.stream()
                .map(classEntityMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SessionDto> getSessionByFormateurId(Long formateurId) {


        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        List<Cour> cours = formateur.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        return sessions.stream()
                .filter(Session::isApproved)
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<SectionDto> geySectionsByFormateurId(Long formateurId) {


        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        List<Cour> cours = formateur.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .toList();

        List<Section> sections = sessions.stream()
                .flatMap(cour -> cour.getSections().stream())
                .distinct() // To avoid duplicates
                .filter(Section::isApproved)
                .toList();

        return sections.stream()
                .map(sectionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByFourmateurId(Long formateurId) {


        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        List<Cour> cours = formateur.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Section> sections = sessions.stream()
                .flatMap(cour -> cour.getSections().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        List<Question> resourceEntities  = sections.stream()
                .flatMap(section -> section.getQuizzes().stream())
                .flatMap(quiz -> quiz.getQuestions().stream())
                .distinct() // To avoid duplicates
                .collect(Collectors.toList());

        return resourceEntities.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Double getAverageScoreByFourmateurId(Long responsableId) {
        Formateur formateur = formateurRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("Responsable not found"));

        List<Evaluation> evaluations = formateur.getCours().stream()
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
    public List<EvaluationDto> getLatestEvaluationsByFormateurId(Long responsableId) {
        Formateur responsable = formateurRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("Responsable not found"));

        return responsable.getCours().stream()
                .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
                .flatMap(classEntity -> classEntity.getParticipants().stream())
                .flatMap(participant -> participant.getEvaluations().stream())
                .sorted(Comparator.comparing(Evaluation::getCreatedAt).reversed())
                .limit(10)
                .map(evaluationMapper::toDto)
                .collect(Collectors.toList());
    }

//    @Transactional(readOnly = true)
//    public List<FeedbackDto> getFeedbackByFormateurId(Long formateurId) {
//        Formateur formateur = formateurRepository.findById(formateurId)
//                .orElseThrow(() -> new RuntimeException("Formateur not found"));
//
//        List<Feedback> feedbacks = formateur.getFeedbacks();
//
//        return feedbacks.stream()
//                .map(feedbackMapper::toDto)
//                .collect(Collectors.toList());
//    }

    public boolean hasSubmittedFeedback(Long userId) {
        return feedbackRepository.existsByUserAppUserId(userId);
    }

     public Page<EvaluationDto> getEvaluationsByFormateurAndSection(
            Long responsableId, Long sectionId,Long quizId, int page, int size) {

          Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
          Page<Evaluation> evaluations = evaluationRepository.findByFormateurIdAndSectionId(responsableId, sectionId,quizId, pageable);

          // Convert the page of Evaluation entities to a page of EvaluationDto objects
          return evaluations.map(evaluationMapper::toDto);
      }

    public List<EvaluationDto> getEvaluationsByFormateur(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        return formateur.getCours().stream()
                .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
                .flatMap(classEntity -> classEntity.getParticipants().stream())
                .flatMap(participant -> participant.getEvaluations().stream())
                .map(evaluationMapper::toDto)
                .collect(Collectors.toList());
    }


    public Integer getMaxSizeByLibraryId(Long id) {
        Library library = libraryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Library not found"));

        return  library.getCour().getEntreprise().getMaxSize();
    }

    public List<HierarchicalUnitDto> getHierarchicalUnitsByFormateurId(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        if(formateur.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");

        return formateur.getEntreprise().getHierarchicalUnits().stream()
                .map(hierarchicalUnitMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<UserDTO> getContactsByFormateurId(Long id) {
        List<UserApp> contacts = new ArrayList<>();
        Formateur formateur = (Formateur) userAppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        if(formateur.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = formateur.getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }


    public List<UserDTO> searchContactsByFormateurId(Long id, String name) {
        List<UserApp> contacts = new ArrayList<>();
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        if(formateur.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = formateur.getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .filter(userApp -> userApp.getFirstName().contains(name) || userApp.getLastName().contains(name))
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());


    }


    public EntrepriseDto getEntrepriseByFormateurId(Long id) {
        Formateur formateur = formateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Formateur not found"));

        if(formateur.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");

        return entrepriseMapper.toDto(formateur.getEntreprise());
    }
}

