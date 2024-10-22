package e_learning.controllers;

import e_learning.DTO.OptionDto;
import e_learning.DTO.QuestionDto;
import e_learning.DTO.ResponseDto;
import e_learning.entity.Option;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.QuestionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/questions")
@AllArgsConstructor
public class QuestionController {

    private final QuestionService questionService;
    private final UserAppRepository userAppRepository;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity createOrUpdateQuestion(@RequestBody QuestionDto questionDto, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        System.out.println("question dro : "+ questionDto);
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        QuestionDto savedQuestion = questionService.createQuestion(questionDto);
        return ResponseEntity.ok(savedQuestion);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionDto> getQuestion(@PathVariable Long id) {
        QuestionDto question = questionService.getQuestion(id);
        return ResponseEntity.ok(question);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/responses")
    public ResponseEntity<List<ResponseDto>> getQuestionResponses(@PathVariable Long id) {
        List<ResponseDto> responses = questionService.getQuestionResponses(id);
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}/options")
    public ResponseEntity<List<OptionDto>> getQuestionOptions(@PathVariable Long id) {
        List<OptionDto> options = questionService.getQuestionOptions(id);
        return ResponseEntity.ok(options);
    }

    @PostMapping("/{id}/options")
    public ResponseEntity<OptionDto> addOptionToQuestion(@PathVariable Long id, @RequestBody OptionDto optionDto) {
        OptionDto savedOption = questionService.addOptionToQuestion(id, optionDto);
        return ResponseEntity.ok(savedOption);
    }


    @PutMapping("/{id}")
    public ResponseEntity<QuestionDto> updateQuestion(@PathVariable Long id, @RequestBody QuestionDto questionDto) {
    QuestionDto updatedQuestion = questionService.updateQuestion(id, questionDto);
    return ResponseEntity.ok(updatedQuestion);
    }

    @GetMapping("/quiz/{quizId}")
    public ResponseEntity<List<QuestionDto>> getQuestionsByQuizId(@PathVariable Long quizId) {
        List<QuestionDto> questions = questionService.getQuestionsByQuizId(quizId);
        return ResponseEntity.ok(questions);
    }
}
