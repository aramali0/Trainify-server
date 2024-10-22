package e_learning.services.ServiceImpl;

import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.exceptions.ResourceNotFoundException;
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
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ChargeFormationService {
    private final ChargeFormationRepository chargeFormationRepository;
    private final ChargeFormationMapper chargeFormationMapper;
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
    private final ActionApprovalRepository actionApprovalRepository;
    private final EntrepriseMapper entrepriseMapper;
    private final UserAppRepository userAppRepository;

    // Method 1: Get all participants for a charge de formation
    public List<UserDTO> getAllParticipants(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));
        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        return entreprise.getAppendingParticipants().stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }

    // Method 2: Verify a participant
    public boolean verifyParticipant(Long chargeId, Long participantId, boolean verify) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
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
            entrepriseRepository.save(entreprise);
            return true;
        }
        return false;
    }

    // Method 3: Save charge de formation
    public ChargeFormationDto saveChargeFormation(ChargeFormationDto chargeFormationDto) {
        ChargeFormation chargeFormation = chargeFormationMapper.toEntity(chargeFormationDto);
        chargeFormation = chargeFormationRepository.save(chargeFormation);
        return chargeFormationMapper.toDto(chargeFormation);
    }

    // Method 4: Get charge de formation by ID
    public ChargeFormationDto getChargeFormationById(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));
        return chargeFormationMapper.toDto(chargeFormation);
    }

    // Method 5: Get participants by charge de formation ID
    @Transactional(readOnly = true)
    public List<ParticipantDto> getParticipantsByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Participant> participants = entreprise.getParticipants().stream()
                .filter(UserApp::isVerified)
                .collect(Collectors.toList());

        return participants.stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 6: Get classes by charge de formation ID
    @Transactional(readOnly = true)
    public List<ClassEntityDto> getClassesByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<ClassEntity> classes = cours.stream()
                .flatMap(cour -> cour.getClasses().stream())
                .filter(ClassEntity::isApproved)
                .distinct()
                .collect(Collectors.toList());

        return classes.stream()
                .map(classEntityMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 7: Get formateurs by charge de formation ID
    @Transactional(readOnly = true)
    public List<FormateurDto> getFormateursByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Formateur> formateurs = entreprise.getFormateurs();

        return formateurs.stream()
                .map(formateurMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 8: Get sessions by charge de formation ID
    @Transactional(readOnly = true)
    public List<SessionDto> getSessionsByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = new ArrayList<>(cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct()
                .filter(Session::isApproved)
                .toList());

        sessions.addAll( cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct()
                .filter(session -> Objects.equals(session.getCreatedBy().getUserId(), chargeId))
                .filter(session -> !session.isSent())
                .toList()
        );

        return sessions.stream()
                .distinct()
                .map(sessionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 9: Get sections by charge de formation ID
    @Transactional(readOnly = true)
    public List<SectionDto> getSectionsByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct()
                .filter(Session::isApproved)
                .toList();

        List<Section> sections = new ArrayList<>(sessions.stream()
                .flatMap(session -> session.getSections().stream())
                .distinct()
                .filter(Section::isApproved)
                .toList());

        sections.addAll(sessions.stream()
                .flatMap(session -> session.getSections().stream())
                .distinct()
                .filter(section -> Objects.equals(section.getCreatedBy().getUserId(), chargeId))
                .filter(section -> !section.isSent())
                .toList()
        );

        return sections.stream()
                .map(sectionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 10: Get questions by charge de formation ID
    @Transactional(readOnly = true)
    public List<QuestionDto> getQuestionsByChargeId(Long chargeId) {
        ChargeFormation charge = chargeFormationRepository.findById(chargeId)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(charge.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        List<Cour> cours = entreprise.getCours();

        List<Session> sessions = cours.stream()
                .flatMap(cour -> cour.getSessions().stream())
                .distinct()
                .collect(Collectors.toList());

        List<Section> sections = sessions.stream()
                .flatMap(session -> session.getSections().stream())
                .distinct()
                .collect(Collectors.toList());

        List<Quiz> quizzes = sections.stream()
                .flatMap(section -> section.getQuizzes().stream())
                .distinct()
                .collect(Collectors.toList());

        List<Question> questions = quizzes.stream()
                .flatMap(quiz -> quiz.getQuestions().stream())
                .distinct()
                .collect(Collectors.toList());

        return questions.stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }

    // Method 11: Get participants' unavailability by charge de formation ID
    @Transactional(readOnly = true)
    public List<ParticipantUnavailabilityDto> getParticipantsUnavailabilityByChargeId(Long chargeId) {
        List<ParticipantDto> participantDtos = getParticipantsByChargeId(chargeId);

        List<Participant> participants = participantDtos.stream()
                .map(participantMapper::toEntity)
                .collect(Collectors.toList());

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

    // Method 12: Get average score by charge de formation ID
    @Transactional(readOnly = true)
    public Double getAverageScoreByChargeId(Long chargeId) {

        List<Evaluation> evaluations = evaluationRepository.findByChargeUserId(chargeId);
        return evaluations.stream()
                .mapToDouble(Evaluation::getScore)
                .average()
                .orElse(0.0);
    }

    public List<ChargeFormationDto> getAllChargeFormations() {
        return chargeFormationRepository.findAll().stream()
                .map(chargeFormationMapper::toDto)
                .collect(Collectors.toList());
    }

    public ChargeFormationDto updateChargeFormation(Long id, ChargeFormationDto chargeFormationDto) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        chargeFormation.setFirstName(chargeFormationDto.firstName());
        chargeFormation.setLastName(chargeFormationDto.lastName());
        chargeFormation.setEmail(chargeFormationDto.email());
        chargeFormation.setPassword(chargeFormationDto.password());
        chargeFormation.setNum(chargeFormationDto.num());
        chargeFormation.setGender(chargeFormation.getGender());
        chargeFormation.setAge(chargeFormation.getAge());
        chargeFormation.setCIN(chargeFormation.getCIN());
        return chargeFormationMapper.toDto(chargeFormation);
    }

    public void deleteChargeFormation(Long id) {
        chargeFormationRepository.deleteById(id);
    }

    public List<EvaluationDto> getLatestEvaluationsByChargeId(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        List<Evaluation> evaluations = evaluationRepository.findByChargeUserId(id);
        return evaluations.stream()
                .map(evaluationMapper::toDto)
                .collect(Collectors.toList());
    }

    public Page<EvaluationDto> getEvaluationsByChargeAndSection(Long id, Long sectionId, Long quizId, int page, int size) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("id").descending());
        return evaluationRepository.findByChargeIdAndSectionId(id, sectionId, quizId, pageable)
                .map(evaluationMapper::toDto);
    }

    public List<EvaluationDto> getEvaluationsByCharge(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        List<Evaluation> evaluations = evaluationRepository.findByChargeUserId(id);
        return evaluations.stream()
                .map(evaluationMapper::toDto)
                .collect(Collectors.toList());
    }

    public Integer getMaxSize(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(chargeFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));
        return entreprise.getMaxSize();
    }

    public boolean updateMaxSize(Long id, Integer maxSize) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(chargeFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));
        entreprise.setMaxSize(maxSize);
        entrepriseRepository.save(entreprise);
        return true;
    }

    public List<HierarchicalUnitDto> getHierarchicalUnitsByChargeId(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Charge de formation not found"));

        Entreprise entreprise = entrepriseRepository.findById(chargeFormation.getEntreprise().getId())
                .orElseThrow(() -> new ResourceNotFoundException("Entreprise not found"));

        return entreprise.getHierarchicalUnits().stream()
                .map(hierarchicalUnitMapper::toDto)
                .collect(Collectors.toList());
    }


    public List<UserDTO> getContactsByChargeFormationId(Long id) {
        List<UserApp> contacts = new ArrayList<>();
        ChargeFormation chargeFormation = (ChargeFormation) userAppRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        if(chargeFormation.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = chargeFormation.getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());
    }


    public List<UserDTO> searchContactsByChargeFormationId(Long id, String name) {
        List<UserApp> contacts = new ArrayList<>();
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        if(chargeFormation.getEntreprise() == null)
            throw new RuntimeException("Formateur is not associated with an entreprise");
        Entreprise en = chargeFormation.getEntreprise();

        contacts.addAll(en.getChargeFormations());
        contacts.addAll(en.getResponsableFormations());
        contacts.addAll(en.getFormateurs());
        contacts.addAll(en.getParticipants());

        return contacts.stream()
                .filter(userApp -> userApp.getFirstName().contains(name) || userApp.getLastName().contains(name))
                .map(userMapperImpl::fromEntity)
                .collect(Collectors.toList());


    }

    public List<ActionApproval> getApprovalsByChargeFormationId(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        return actionApprovalRepository.findByChargeFormationId(id);
    }

    public EntrepriseDto getEntrepriseByChargeFormationId(Long id) {
        ChargeFormation chargeFormation = chargeFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Charge de formation not found"));

        return entrepriseRepository.findById(chargeFormation.getEntreprise().getId())
                .map(entrepriseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Entreprise not found"));
    }
}
