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

    @GetMapping("/{evaluationId}")
    public List<ResponseFormationDto> getResponses(@PathVariable Long evaluationId) {
        return responseService.getResponsesByEvaluationId(evaluationId);
    }

}
