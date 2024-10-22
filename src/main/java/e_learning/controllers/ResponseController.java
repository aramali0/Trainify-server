package e_learning.controllers;

import e_learning.DTO.ResponseDto;
import e_learning.services.ServiceImpl.ResponseService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/responses")
@AllArgsConstructor
public class ResponseController {

    private final ResponseService responseService;

    @PostMapping
    public ResponseEntity<ResponseDto> createOrUpdateResponse(@RequestBody ResponseDto responseDto) {
        ResponseDto savedResponse = responseService.createResponse(responseDto);
        return ResponseEntity.ok(savedResponse);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ResponseDto> getResponse(@PathVariable Long id) {
        ResponseDto response = responseService.getResponse(id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteResponse(@PathVariable Long id) {
        responseService.deleteResponse(id);
        return ResponseEntity.noContent().build();
    }
}
