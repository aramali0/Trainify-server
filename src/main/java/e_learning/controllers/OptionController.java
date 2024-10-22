package e_learning.controllers;

import e_learning.DTO.OptionDto;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.OptionService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/options")
@AllArgsConstructor
public class OptionController {
    private final OptionService optionService;
    private final UserAppRepository userAppRepository;

    @GetMapping("/{id}")
    public ResponseEntity<OptionDto> getOption(@PathVariable Long id) {
        OptionDto option = optionService.getOption(id);
        return ResponseEntity.ok(option);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OptionDto> createOption(@RequestBody OptionDto optionDto , Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        OptionDto createdOption = optionService.createOption(optionDto, userApp);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdOption);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OptionDto> updateOption(@PathVariable Long id, @RequestBody OptionDto optionDto) {
        OptionDto updatedOption = optionService.updateOption(id, optionDto);
        return ResponseEntity.ok(updatedOption);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        optionService.deleteOption(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/by-question/{questionId}")
    public ResponseEntity<List<OptionDto>> getOptionsByQuestionId(@PathVariable Long questionId) {
        List<OptionDto> options = optionService.getOptionsByQuestionId(questionId);
        if (options.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(options);
    }
}
