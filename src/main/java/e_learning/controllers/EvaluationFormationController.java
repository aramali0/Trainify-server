package e_learning.controllers;

import e_learning.DTO.EvaluationFormationDto;
import e_learning.services.ServiceImpl.EvaluationFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/evaluations-formation")
@RequiredArgsConstructor
public class EvaluationFormationController {

    private final EvaluationFormationService evaluationService;


    @PostMapping
    public EvaluationFormationDto createEvaluation(@RequestBody EvaluationFormationDto dto) {
        return evaluationService.createEvaluation(dto);
    }

    @GetMapping("/{userId}/{type}")
    public List<EvaluationFormationDto> getEvaluations(@PathVariable String type , @PathVariable Long userId) {
        System.out.println("type = " + type);
        System.out.println("userId = " + userId);
        return evaluationService.getEvaluationsByType(type , userId);
    }
// there is this function in the service layer
    @GetMapping("/{userId}")
    public List<EvaluationFormationDto> getEvaluations(@PathVariable Long userId) {
        return evaluationService.getEvaluations(userId);
    }


    @GetMapping("/evaluation/{evaluationId}")
    public EvaluationFormationDto getEvaluation(@PathVariable Long evaluationId) {
        return evaluationService.getEvaluation(evaluationId);
    }
}
