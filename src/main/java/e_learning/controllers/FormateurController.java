package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.services.ServiceImpl.FormateurService;
import e_learning.services.ServiceImpl.ResponsableFormationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@RestController
@RequestMapping("/formateurs")
@AllArgsConstructor
public class FormateurController {
    private final FormateurService formateurService;

    @PostMapping
    public FormateurDto createFormateur(@RequestBody FormateurDto formateurDto) {
        return formateurService.saveFormateur(formateurDto);
    }

    @GetMapping("/{id}")
    public FormateurDto getFormateurById(@PathVariable Long id) {
        return formateurService.getFormateurById(id);
    }

    @GetMapping
    public List<FormateurDto> getAllFormateurs() {
        return formateurService.getAllFormateurs();
    }

    @PutMapping("/{id}")
    public FormateurDto updateFormateur(@PathVariable Long id, @RequestBody FormateurDto formateurDto) {
        return formateurService.updateFormateur(id, formateurDto);
    }

    @DeleteMapping("/{id}")
    public void deleteFormateur(@PathVariable Long id) {
        formateurService.deleteFormateur(id);
    }

    @GetMapping("/{id}/participants")
    public List<ParticipantDto> getParticipantsByFormateurId(@PathVariable Long id) {
        return formateurService.getParticipantsByFormateurId(id);
    }

    @GetMapping("/{id}/classes")
    public List<ClassEntityDto> getClassesByFormateurId(@PathVariable Long id) {
        return formateurService.getClassesByFormateurId(id);
    }

    @GetMapping("/{id}/sessions")
    public List<SessionDto> getSessionsByFormateurId(@PathVariable Long id) {
        List<SessionDto> sessions = formateurService.getSessionByFormateurId(id);
        return sessions;
    }

    @GetMapping("/{id}/sections")
    public List<SectionDto> getSectionsByFormateurId(@PathVariable Long id) {
        return formateurService.geySectionsByFormateurId(id);
    }

    @GetMapping("/{id}/questions")
    public List<QuestionDto> getQuestionsByResponsableId(@PathVariable Long id) {
        return formateurService.getQuestionsByFourmateurId(id);
    }


    @GetMapping("/{id}/average-score")
    public ResponseEntity<Double> getAverageScore(@PathVariable Long id) {
        Double averageScore = formateurService.getAverageScoreByFourmateurId(id);
        return ResponseEntity.ok(averageScore);
    }


    @GetMapping("/{id}/latest-evaluations")
    public ResponseEntity<List<EvaluationDto>> getLatestEvaluations(@PathVariable Long id) {
        List<EvaluationDto> evaluations = formateurService.getLatestEvaluationsByFormateurId(id);
        return ResponseEntity.ok(evaluations);
    }

 @GetMapping("/{id}/evaluations")
    public ResponseEntity<Page<EvaluationDto>> getEvaluations(
            @PathVariable Long id,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long quizId, // Optional quiz name filter
            @RequestParam(defaultValue = "0") int page,         // Default to page 0
            @RequestParam(defaultValue = "10") int size
 ) {      // Default to size 10

        Page<EvaluationDto> evaluations = formateurService
                .getEvaluationsByFormateurAndSection(id, sectionId,quizId, page, size);
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/{id}/evaluations/chart")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsforChart(
            @PathVariable Long id
            ) {      // Default to size 10

        List<EvaluationDto> evaluations = formateurService
                .getEvaluationsByFormateur(id);
        return ResponseEntity.ok(evaluations);
    }
//    @GetMapping("/{id}/feedbacks")
//    public ResponseEntity<List<FeedbackDto>> getFeedbackByFormateurId(@PathVariable Long id) {
//        List<FeedbackDto> feedbacks = formateurService.getFeedbackByFormateurId(id);
//        return ResponseEntity.ok(feedbacks);
//    }


    @GetMapping("/{id}/feedback-submitted")
    public ResponseEntity<Boolean> hasSubmittedFeedback(@PathVariable Long id) {
        boolean hasSubmitted = formateurService.hasSubmittedFeedback(id);
        return new ResponseEntity<>(hasSubmitted, HttpStatus.OK);
    }

    @GetMapping("/{id}/maxSize")
    public ResponseEntity<Integer> getMaxSizeByLibraryId(@PathVariable Long id) {
        Integer maxSize = formateurService.getMaxSizeByLibraryId(id);
        return ResponseEntity.ok(maxSize);
    }

    @GetMapping("/{id}/hierarchical-units")
    public ResponseEntity<List<HierarchicalUnitDto>> getHierarchicalUnitsByFormateurId(@PathVariable Long id) {
        List<HierarchicalUnitDto> hierarchicalUnits = formateurService.getHierarchicalUnitsByFormateurId(id);
        return ResponseEntity.ok(hierarchicalUnits);
    }

   @GetMapping("/{id}/contacts")
   public ResponseEntity<List<UserDTO>> getContactsByFormateurId(@PathVariable Long id) {
       List<UserDTO> contacts = formateurService.getContactsByFormateurId(id);
       return ResponseEntity.ok(contacts);
   }

   @GetMapping("/{id}/contacts/search")
    public ResponseEntity<List<UserDTO>> searchContactsByFormateurId(@PathVariable Long id, @RequestParam String name) {
         List<UserDTO> contacts = formateurService.searchContactsByFormateurId(id, name);
         return ResponseEntity.ok(contacts);
    }


    @GetMapping("/{id}/entreprise")
    public EntrepriseDto getEntrepriseByFormateurId(@PathVariable Long id) {
        return formateurService.getEntrepriseByFormateurId(id);
    }
}
