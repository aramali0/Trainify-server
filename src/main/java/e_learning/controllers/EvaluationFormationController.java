package e_learning.controllers;

import e_learning.DTO.BlockAnswerDto;
import e_learning.DTO.EvaluationBlockDto;
import e_learning.DTO.EvaluationFormationDto;
import e_learning.entity.EvaluationBlock;
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

    @GetMapping("/{evaluationId}")
    public EvaluationFormationDto getEvaluation(@PathVariable Long evaluationId) {
        return evaluationService.getEvaluation(evaluationId);
    }

    @GetMapping("/responsable/{userId}")
    public List<EvaluationFormationDto> getEvaluationsByResponsable(@PathVariable Long userId, @RequestParam(required = false) Long courId) {
        return evaluationService.getEvaluationsByResponsable(userId, courId);
    }

    @GetMapping("/{userId}/{type}")
    public List<EvaluationFormationDto> getEvaluationsByType(@PathVariable Long userId, @PathVariable String type) {
        return evaluationService.getEvaluationsByType(userId, type);

    }

    @GetMapping("/block/{blockId}")
    public EvaluationBlockDto getBlockEvaluation(@PathVariable Long blockId) {
        return evaluationService.getBlockEvaluation(blockId);
    }

}
