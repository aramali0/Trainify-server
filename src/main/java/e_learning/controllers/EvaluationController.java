package e_learning.controllers;

import e_learning.DTO.EvaluationDto;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.EvaluationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;

@RestController
@RequestMapping("/evaluations")
@AllArgsConstructor
public class EvaluationController {

    private final EvaluationService evaluationService;
    private final UserAppRepository userAppRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<EvaluationDto> createOrUpdateEvaluation(@RequestBody EvaluationDto evaluationDto, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        EvaluationDto savedEvaluation = evaluationService.createOrUpdateEvaluation(evaluationDto, userApp);
        return ResponseEntity.ok(savedEvaluation);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EvaluationDto> getEvaluation(@PathVariable Long id) {
        EvaluationDto evaluation = evaluationService.getEvaluationById(id);
        return ResponseEntity.ok(evaluation);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEvaluation(@PathVariable Long id) {
        try {
            evaluationService.deleteEvaluation(id);
            return ResponseEntity.noContent().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            // Log the exception and return an appropriate response
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

     @GetMapping("/{id}/pdf")
    public ResponseEntity<byte[]> getEvaluationPdf(@PathVariable Long id) {
        byte[] pdf = evaluationService.generateEvaluationPdf(id);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("attachment", "evaluation.pdf");
        headers.setContentLength(pdf.length);

        return new ResponseEntity<>(pdf, headers, HttpStatus.OK);
    }
}
