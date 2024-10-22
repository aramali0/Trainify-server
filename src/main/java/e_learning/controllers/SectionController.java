package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.ChargeFormation;
import e_learning.entity.Participant;
import e_learning.entity.Section;
import e_learning.entity.UserApp;
import e_learning.repositories.ParticipantRepository;
import e_learning.repositories.SectionRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.EmailService;
import e_learning.services.ServiceImpl.EvaluationService;
import e_learning.services.ServiceImpl.SectionService;
import e_learning.services.ServiceImpl.SessionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/sections")
@AllArgsConstructor
public class SectionController {
    private final SectionService sectionService;
    private final UserAppRepository userAppRepository;
    private final EvaluationService evaluationService;
    private final SectionRepository sectionRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;
    private final EmailService emailService;

    @GetMapping
    public List<SectionDto> getAllSections() {
        return sectionService.getAllSections();

    }

    @GetMapping("/{id}")
    public ResponseEntity<SectionDto> getSectionById(@PathVariable Long id) {
        Optional<SectionDto> section = sectionService.getSectionById(id);
        return section.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createSection(@RequestBody SectionDto sectionDto, Principal principal) throws AccessDeniedException {
       UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        // Check for overlapping sessions
        List<Section> existingSection= sectionService.findSectionsBySessionId(sectionDto.sessionId());

        Section overlappingSection = existingSection.stream()
                .filter(section ->
                        section.getStartDate().before(sectionDto.endDate()) && section.getEndDate().after(sectionDto.startDate())
                ).findFirst().orElse(null);

        if (overlappingSection != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");  // Format: 21 Sep 2024
            String formattedStartDate = dateFormatter.format(overlappingSection.getStartDate());
            String formattedEndDate = dateFormatter.format(overlappingSection.getEndDate());

            return ResponseEntity.badRequest().body(
                    "You cannot create this session as it overlaps with an existing one. "
                            + "Existing Session Start Date: " + formattedStartDate
                            + ", End Date: " + formattedEndDate
            );
        }
        SectionDto dto = sectionService.createOrUpdateSection(sectionDto, userApp);

        return ResponseEntity.ok().body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateSection(@PathVariable Long id, @RequestBody SectionDto sectionDto,Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        // Check for overlapping sessions
        List<Section> existingSection= sectionService.findSectionsBySessionId(sectionDto.sessionId());

        Section overlappingSection = existingSection.stream()
                .filter(section ->
                        section.getStartDate().before(sectionDto.endDate()) && section.getEndDate().after(sectionDto.startDate())
                ).findFirst().orElse(null);

        if (overlappingSection != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");  // Format: 21 Sep 2024
            String formattedStartDate = dateFormatter.format(overlappingSection.getStartDate());
            String formattedEndDate = dateFormatter.format(overlappingSection.getEndDate());

            return ResponseEntity.badRequest().body(
                    "You cannot create this session as it overlaps with an existing one. "
                            + "Existing Session Start Date: " + formattedStartDate
                            + ", End Date: " + formattedEndDate
            );
        }
        SectionDto dto = sectionService.createOrUpdateSection(sectionDto,userApp);
        return ResponseEntity.ok().body(dto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSection(@PathVariable Long id, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        Optional<Section> section = sectionRepository.findById(id);
        if (section.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(!Objects.equals(userApp.getUserId(), section.get().getCreatedBy().getUserId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You are not authorized to delete this section as you are not the creator");
        }
        sectionService.deleteSection(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/resources")
    public ResponseEntity<List<ResourceDto>> getResourcesBySectionId(@PathVariable Long id) {
        List<ResourceDto> resources = sectionService.getResourcesBySectionId(id);
        if (resources.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(resources);
    }

//    @GetMapping("/{id}/questions")
//    public ResponseEntity<List<QuestionDto>> getQuestionsBySectionId(@PathVariable Long id) {
//        List<QuestionDto> questions = sectionService.getQuestionsBySectionId(id);
//        if (questions.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(questions);
//    }
//    @GetMapping("/{id}/options")
//    public ResponseEntity<List<OptionDto>> getOptionsBySectionId(@PathVariable Long id) {
//        List<OptionDto> optionDtos = sectionService.getOptionsBySectionId(id);
//        if (optionDtos.isEmpty()) {
//            return ResponseEntity.notFound().build();
//        }
//        return ResponseEntity.ok(optionDtos);
//    }

}
