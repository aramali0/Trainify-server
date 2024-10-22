package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.ActionApproval;
import e_learning.entity.ChargeFormation;
import e_learning.services.ServiceImpl.ChargeFormationService;
import e_learning.services.ServiceImpl.ResponsableFormationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/charge-formation")
@AllArgsConstructor
public class ChargeFormationController {

    private final ChargeFormationService chargeFormationService;

    @GetMapping("/{id}/participants/verification")
    public ResponseEntity<List<UserDTO>> getAllParticipants(@PathVariable Long id) {
        List<UserDTO> participants = chargeFormationService.getAllParticipants(id);
        return ResponseEntity.ok(participants);
    }

    @PutMapping("/{responsableId}/participants/{participantId}/verify")
    public ResponseEntity<String> verifyParticipant(
            @PathVariable Long responsableId,
            @PathVariable Long participantId,
            @RequestParam boolean verify) {
        boolean result = chargeFormationService.verifyParticipant(responsableId, participantId, verify);
        return result
                ? ResponseEntity.ok("Participant successfully processed.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant not found.");
    }

    @PostMapping
    public ResponseEntity<ChargeFormationDto> createChargeForamtion(
            @RequestBody ChargeFormationDto chargeFormationDto) {
        ChargeFormationDto savedFormation = chargeFormationService.saveChargeFormation(chargeFormationDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedFormation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ChargeFormationDto> getChargeFormationById(@PathVariable Long id) {
        ChargeFormationDto chargeFormation = chargeFormationService.getChargeFormationById(id);
        return ResponseEntity.ok(chargeFormation);
    }

    @GetMapping
    public ResponseEntity<List<ChargeFormationDto>> getAllChargeFormations() {
        List<ChargeFormationDto> formations = chargeFormationService.getAllChargeFormations();
        return ResponseEntity.ok(formations);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ChargeFormationDto> updateChargeFormation(
            @PathVariable Long id,
            @RequestBody ChargeFormationDto chargeFormationDto) {
        ChargeFormationDto updatedFormation = chargeFormationService.updateChargeFormation(id, chargeFormationDto);
        return ResponseEntity.ok(updatedFormation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteChargeFormation(@PathVariable Long id) {
        chargeFormationService.deleteChargeFormation(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantDto>> getParticipantsByChargeId(@PathVariable Long id) {
        List<ParticipantDto> participants = chargeFormationService.getParticipantsByChargeId(id);
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{id}/classes")
    public ResponseEntity<List<ClassEntityDto>> getClassesByChargeId(@PathVariable Long id) {
        List<ClassEntityDto> classes = chargeFormationService.getClassesByChargeId(id);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{id}/formateurs")
    public ResponseEntity<List<FormateurDto>> getFormateursByChargeId(@PathVariable Long id) {
        List<FormateurDto> formateurs = chargeFormationService.getFormateursByChargeId(id);
        return ResponseEntity.ok(formateurs);
    }

    @GetMapping("/{id}/sessions")
    public ResponseEntity<List<SessionDto>> getSessionsByChargeId(@PathVariable Long id) {
        List<SessionDto> sessions = chargeFormationService.getSessionsByChargeId(id);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{id}/sections")
    public ResponseEntity<List<SectionDto>> getSectionsByChargeId(@PathVariable Long id) {
        List<SectionDto> sections = chargeFormationService.getSectionsByChargeId(id);
        return ResponseEntity.ok(sections);
    }

    @GetMapping("/{id}/questions")
    public ResponseEntity<List<QuestionDto>> getQuestionsByChargeId(@PathVariable Long id) {
        List<QuestionDto> questions = chargeFormationService.getQuestionsByChargeId(id);
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/{id}/unavailability")
    public ResponseEntity<List<ParticipantUnavailabilityDto>> getParticipantsUnavailability(@PathVariable Long id) {
        List<ParticipantUnavailabilityDto> unavailability = chargeFormationService.getParticipantsUnavailabilityByChargeId(id);
        return ResponseEntity.ok(unavailability);
    }

    @GetMapping("/{id}/average-score")
    public ResponseEntity<Double> getAverageScore(@PathVariable Long id) {
        Double averageScore = chargeFormationService.getAverageScoreByChargeId(id);
        return ResponseEntity.ok(averageScore);
    }

    @GetMapping("/{id}/latest-evaluations")
    public ResponseEntity<List<EvaluationDto>> getLatestEvaluations(@PathVariable Long id) {
        List<EvaluationDto> evaluations = chargeFormationService.getLatestEvaluationsByChargeId(id);
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/{id}/evaluations")
    public ResponseEntity<Page<EvaluationDto>> getEvaluations(
            @PathVariable Long id,
            @RequestParam(required = false) Long sectionId,
            @RequestParam(required = false) Long quizId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Page<EvaluationDto> evaluations = chargeFormationService
                .getEvaluationsByChargeAndSection(id, sectionId, quizId, page, size);
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/{id}/evaluations/chart")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsForChart(@PathVariable Long id) {
        List<EvaluationDto> evaluations = chargeFormationService.getEvaluationsByCharge(id);
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/{id}/maxSize")
    public ResponseEntity<Integer> getMaxSize(@PathVariable Long id) {
        Integer maxSize = chargeFormationService.getMaxSize(id);
        return ResponseEntity.ok(maxSize);
    }

    @PatchMapping("/{id}/maxSize")
    public ResponseEntity<String> updateMaxSize(@PathVariable Long id, @RequestParam Integer maxSize) {
        boolean result = chargeFormationService.updateMaxSize(id, maxSize);
        return result
                ? ResponseEntity.ok("Max size updated successfully.")
                : ResponseEntity.status(HttpStatus.NOT_FOUND).body("Charge de formation not found.");
    }

    @GetMapping("/{id}/hierarchical-units")
    public ResponseEntity<List<HierarchicalUnitDto>> getHierarchicalUnitsByChargeId(@PathVariable Long id) {
        List<HierarchicalUnitDto> hierarchicalUnits = chargeFormationService.getHierarchicalUnitsByChargeId(id);
        return ResponseEntity.ok(hierarchicalUnits);
    }

    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<UserDTO>> getContactsByChargeFormationId(@PathVariable Long id) {
        List<UserDTO> contacts = chargeFormationService.getContactsByChargeFormationId(id);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}/contacts/search")
    public ResponseEntity<List<UserDTO>> searchContactsByChargeFormationId(@PathVariable Long id, @RequestParam String name) {
        List<UserDTO> contacts = chargeFormationService.searchContactsByChargeFormationId(id, name);
        return ResponseEntity.ok(contacts);
    }


    @GetMapping("/{id}/approvals")
    public ResponseEntity<List<ActionApproval>> getApprovalsByChargeFormationId(@PathVariable Long id) {
        List<ActionApproval> approvals = chargeFormationService.getApprovalsByChargeFormationId(id);
        return ResponseEntity.ok(approvals);
    }


    @GetMapping("/{id}/entreprise")
    public ResponseEntity<EntrepriseDto> getEntrepriseByChargeFormationId(@PathVariable Long id) {
        EntrepriseDto entreprise = chargeFormationService.getEntrepriseByChargeFormationId(id);
        return ResponseEntity.ok(entreprise);
    }
}
