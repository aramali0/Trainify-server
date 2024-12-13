package e_learning.controllers;

import e_learning.DTO.ResponseFormationDto;
import e_learning.services.ServiceImpl.ResponseFormationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/responses-formation")
@RequiredArgsConstructor
public class ResponseFormationController {
    private final ResponseFormationService responseService;

    @PostMapping
    public ResponseFormationDto submitResponse(@RequestBody ResponseFormationDto dto) {
        return responseService.submitResponse(dto);
    }

    @GetMapping("/{responseId}")
    public ResponseFormationDto getResponse(@PathVariable Long responseId) {
        return responseService.getResponse(responseId);
    }

    @GetMapping("/evaluation/{evaluationId}")
    public List<ResponseFormationDto> getResponsesByEvaluation(@PathVariable Long evaluationId) {
        return responseService.getResponsesByEvaluation(evaluationId);
    }

    @GetMapping("/check/{userId}/{evaluationId}")
    public List<ResponseFormationDto> checkUserHasResponse(@PathVariable Long userId, @PathVariable Long evaluationId) {
        return responseService.checkUserHasResponse(userId, evaluationId);
    }
}
