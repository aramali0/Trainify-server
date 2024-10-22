package e_learning.services.ServiceImpl;


import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.*;
import e_learning.security.SecurityService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SectionService {
    private final SectionRepository sectionRepository;
    private final SectionMapper sectionMapper;
    private final SecurityService securityService;
    private final PermissionService permissionService;
    private final QuestionMapper questionMapper;
    private final ResourceMapper resourceMapper;
    private final SessionRepository sessionRepository;
    private final OptionMapper optionMapper;
    private final ParticipantRepository participantRepository;
    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final ResponseRepository responseRepository;
    private final EvaluationRepository evaluationRepository;
    private final ResponseMapper responseMapper;
    private final ActionApprovalRepository actionApprovalRepository;

    public List<SectionDto> getAllSections() {
        return sectionRepository.findAll().stream().map(sectionMapper::toDto).collect(Collectors.toList());
    }

    public Optional<SectionDto> getSectionById(Long id) {
        return sectionRepository.findById(id).map(sectionMapper::toDto);
    }

    @Transactional
    public SectionDto createOrUpdateSection(SectionDto sectionDto, UserApp userApp) throws AccessDeniedException {
//        if (!permissionService.canCreateSection(sectionDto.sessionId(),userApp)) {
//            throw new AccessDeniedException("User does not have permission to create section");
//        }

        Section section = sectionMapper.toEntity(sectionDto);
        if (section.getId() == null) { // New section
            section.setCreatedBy(userApp);
            section.setApproved(true);
        }
        section = sectionRepository.save(section);


        if(userApp instanceof ChargeFormation)
        {
            section.setApproved(false);
            sectionRepository.save(section);
        }

        return sectionMapper.toDto(section);
    }
    @Transactional
    public void deleteSection(Long id) {
        sectionRepository.deleteById(id);
    }

    public List<ResourceDto> getResourcesBySectionId(Long sectionId) {
        Optional<Section> section = sectionRepository.findById(sectionId);
        if (section.isPresent()) {
            return section.get().getResources().stream().map(resourceMapper::toDto).collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

    public List<Section> findSectionsBySessionId(Long aLong) {
        return sectionRepository.findSectionsBySessionId(aLong);
    }


//    public List<OptionDto> getOptionsBySectionId(Long sectionId) {
//        Optional<Section> section = sectionRepository.findById(sectionId);
//        if (section.isPresent()) {
//            List<Question> questions = section.get().getQuestions().stream().collect(Collectors.toList());
//
//            return questions.stream().flatMap(question -> question.getOptions().stream()).map(optionMapper::toDto).collect(Collectors.toList());
//        }
//        return Collections.emptyList();
//    }

}
