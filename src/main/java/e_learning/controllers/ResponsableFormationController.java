package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.ActionApproval;
import e_learning.services.ServiceImpl.ResponsableFormationService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.hateoas.PagedModel;

import java.util.List;

@RestController
@RequestMapping("/responsables")
@AllArgsConstructor
public class ResponsableFormationController {
    private final ResponsableFormationService responsableFormationService;

    @GetMapping("/{id}/participants/verification")
    public List<UserDTO> getAllParticipants(@PathVariable Long id) {
        return responsableFormationService.getAllParticipants(id);
    }

    @PutMapping("/{responsableId}/participants/{participantId}/verify")
    public ResponseEntity<String> verifyParticipant(@PathVariable Long responsableId,
                                                    @PathVariable Long participantId,
                                                    @RequestParam boolean verify) {
        boolean result = responsableFormationService.verifyParticipant(responsableId, participantId, verify);
        if (result) {
            return ResponseEntity.ok("Participant successfully processed.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Participant not found.");
        }
    }

    @PostMapping
    public ResponsableFormationDto createResponsableFormation(@RequestBody ResponsableFormationDto responsableFormationDto) {
        return responsableFormationService.saveResponsableFormation(responsableFormationDto);
    }

    @GetMapping("/{id}")
    public ResponsableFormationDto getResponsableFormationById(@PathVariable Long id) {
        return responsableFormationService.getResponsableFormationById(id);
    }

    @GetMapping
    public List<ResponsableFormationDto> getAllResponsableFormations() {
        return responsableFormationService.getAllResponsableFormations();
    }

    @PutMapping("/{id}")
    public ResponsableFormationDto updateResponsableFormation(@PathVariable Long id, @RequestBody ResponsableFormationDto responsableFormationDto) {
        return responsableFormationService.updateResponsableFormation(id, responsableFormationDto);
    }

    @DeleteMapping("/{id}")
    public void deleteResponsableFormation(@PathVariable Long id) {
        responsableFormationService.deleteResponsableFormation(id);
    }

    @GetMapping("/{id}/participants")
    public List<ParticipantDto> getParticipantsByResponsableId(@PathVariable Long id) {
        return responsableFormationService.getParticipantsByResponsableId(id);
    }
    @GetMapping("/{id}/classes")
    public List<ClassEntityDto> getClassesByFormateurId(@PathVariable Long id) {
        return responsableFormationService.getClassesByResponsableId(id);
       }


    @GetMapping("/{id}/formateurs")
    public List<FormateurDto> getFormateursByResponsableId(@PathVariable Long id) {
        return responsableFormationService.getFormateursByResponsableId(id);
    }

    @GetMapping("/{id}/sessions")
    public List<SessionDto> getSessionsByResponsableId(@PathVariable Long id) {
        return responsableFormationService.getSessionByResponsableId(id);
    }

    @GetMapping("/{id}/sections")
    public List<SectionDto> getSectionsByResponsableId(@PathVariable Long id) {
        return responsableFormationService.geySectionsByResponsableId(id);}

    @GetMapping("/{id}/questions")
    public List<QuestionDto> getQuestionsByResponsableId(@PathVariable Long id) {
        return responsableFormationService.getQuestionsByResponsableId(id);
    }

    @GetMapping("/{id}/unavailability")
    public ResponseEntity<List<ParticipantUnavailabilityDto>> getParticipantsUnavailability(@PathVariable Long id) {
        List<ParticipantUnavailabilityDto> unavailability = responsableFormationService.getParticipantsUnavailabilityByResponsableId(id);
        return ResponseEntity.ok(unavailability);
}

    @GetMapping("/{id}/average-score")
    public ResponseEntity<Double> getAverageScore(@PathVariable Long id) {
        Double averageScore = responsableFormationService.getAverageScoreByResponsableId(id);
        return ResponseEntity.ok(averageScore);
    }


    @GetMapping("/{id}/latest-evaluations")
    public ResponseEntity<List<EvaluationDto>> getLatestEvaluations(@PathVariable Long id) {
        List<EvaluationDto> evaluations = responsableFormationService.getLatestEvaluationsByResponsableId(id);
        return ResponseEntity.ok(evaluations);
    }

   @GetMapping("/{id}/evaluations")
public ResponseEntity<PagedModel<EntityModel<EvaluationDto>>> getEvaluations(
        @PathVariable Long id,
        @RequestParam(required = false) Long sectionId, // Optional section filter
        @RequestParam(required = false) Long quizId,    // Optional quiz type filter
        @RequestParam(defaultValue = "0") int page,     // Default to page 0
        @RequestParam(defaultValue = "10") int size,    // Default to size 10
        PagedResourcesAssembler<EvaluationDto> pagedResourcesAssembler) {


    Page<EvaluationDto> evaluations = responsableFormationService
            .getEvaluationsByResponsableAndSection(id, sectionId, quizId, page, size);
    // Convert to a PagedModel to ensure stable JSON serialization
    PagedModel<EntityModel<EvaluationDto>> pagedModel = pagedResourcesAssembler.toModel(evaluations);
    return ResponseEntity.ok(pagedModel);
}

    @GetMapping("/{id}/evaluations/chart")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsForChart(
            @PathVariable Long id
    ) {

        List<EvaluationDto> evaluations = responsableFormationService
                .getEvaluationsByResponsable(id);
        return ResponseEntity.ok(evaluations);
    }

    @GetMapping("/{id}/maxSize")
    public ResponseEntity<Integer> getMaxSize(@PathVariable Long id) {

        Integer maxSize = responsableFormationService.getMaxSize(id);
        return ResponseEntity.ok(maxSize);
    }

    @PatchMapping("/{id}/maxSize")
    public ResponseEntity<String> updateMaxSize(@PathVariable Long id, @RequestParam(required = false) Integer maxSize) {
        if(maxSize == null) {
            return ResponseEntity.badRequest().body("Max size is required.");
        }

        boolean result = responsableFormationService.updateMaxSize(id, maxSize);
        if (result) {
            return ResponseEntity.ok("Max size updated successfully.");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Responsable not found.");
        }
    }

    @GetMapping("/{id}/hierarchical-units")
    public ResponseEntity<List<HierarchicalUnitDto>> getHierarchicalUnitsByResponsableId(@PathVariable Long id) {
        List<HierarchicalUnitDto> hierarchicalUnits = responsableFormationService.getHierarchicalUnitsByResponsableId(id);
        return ResponseEntity.ok(hierarchicalUnits);
    }

    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<UserDTO>> getContactsByResponsableId(@PathVariable Long id) {
        List<UserDTO> contacts = responsableFormationService.getContactsByResponsableId(id);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}/contacts/search")
    public ResponseEntity<List<UserDTO>> searchContactsByResponsableId(@PathVariable Long id, @RequestParam String name) {
        List<UserDTO> contacts = responsableFormationService.searchContactsByResponsableId(id, name);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{responsableId}/requests")
    public ResponseEntity<List<ActionApproval>> getPendingRequests(
            @PathVariable Long responsableId) {
        System.out.println("ResponsableFormationController.getPendingRequests");
        System.out.println("responsableId = " + responsableId);
        List<ActionApproval> pendingRequests = responsableFormationService.getPendingRequests(responsableId);
        System.out.println("pendingRequests = " + pendingRequests);
        return ResponseEntity.ok(pendingRequests);
    }

    @PutMapping("/requests/approve")
    public ResponseEntity<String> approveRequest(@RequestBody ActionApproval actionRequestDTO) throws Exception {
        responsableFormationService.approveAction(
                actionRequestDTO.getResponsableFormationId(),
                actionRequestDTO.getId(),
                actionRequestDTO.getComment()
        );
        return ResponseEntity.ok("Request approved successfully.");
    }

    // Endpoint to reject a specific request with comment
    @PutMapping("/requests/reject")
    public ResponseEntity<String> rejectRequest(@RequestBody ActionApproval actionRequestDTO) throws Exception {
        responsableFormationService.rejectAction(
                actionRequestDTO.getResponsableFormationId(),
                actionRequestDTO.getId(),
                actionRequestDTO.getComment()
        );
        return ResponseEntity.ok("Request rejected successfully.");
    }

    // Endpoint to request an update for a specific request with comment
    @PutMapping("/requests/update")
    public ResponseEntity<String> requestUpdate(@RequestBody ActionApproval actionRequestDTO) throws Exception {
        responsableFormationService.requestUpdate(
                actionRequestDTO.getResponsableFormationId(),
                actionRequestDTO.getId(),
                actionRequestDTO.getComment()
        );
        return ResponseEntity.ok("Update requested successfully.");
    }

    @GetMapping("/{id}/entreprise")
    public EntrepriseDto getEntrepriseByResponsableFormationId(@PathVariable Long id) {
        return responsableFormationService.getEntrepriseByResponsableFormationId(id);
    }
}
