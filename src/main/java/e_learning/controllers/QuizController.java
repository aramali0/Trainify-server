package e_learning.controllers;

import e_learning.DTO.EvaluationDto;
import e_learning.DTO.QuestionDto;
import e_learning.DTO.QuizDto;
import e_learning.DTO.ResponseDto;
import e_learning.entity.ChargeFormation;
import e_learning.entity.Participant;
import e_learning.entity.Section;
import e_learning.entity.UserApp;
import e_learning.repositories.ParticipantRepository;
import e_learning.repositories.SectionRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.EmailService;
import e_learning.services.ServiceImpl.EvaluationService;
import e_learning.services.ServiceImpl.QuizService;
import e_learning.services.ServiceImpl.SectionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/quizzes")
@RequiredArgsConstructor
public class QuizController {
    private final QuizService quizService;
    private final SectionRepository sectionRepository;
    private final ParticipantRepository participantRepository;
    private final SimpMessagingTemplate brokerMessagingTemplate;
    private final EmailService emailService;
    private final SectionService sectionService;
    private final EvaluationService evaluationService;
    private final UserAppRepository userAppRepository;

    @PostMapping("/sections/{sectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> addQuizToSection(
            @PathVariable Long sectionId,
            @RequestBody QuizDto quizDto
            , Principal principal
    ) throws Exception {

        UserApp user = userAppRepository.findUserAppByEmail(principal.getName());
        Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new EntityNotFoundException("Section not found"));
        // Assuming the section has participants
        List<Participant> participants = participantRepository.findByClassesCoursSessionsSectionsId(sectionId);
        QuizDto dto = quizService.addQuizToSection(sectionId, quizDto,user);

        if(user instanceof ChargeFormation)
        {
            return ResponseEntity.ok(dto);
        }

        for (Participant participant : participants) {
            // Prepare notification data
            Map<String, Object> notificationData = new HashMap<>();
            notificationData.put("message", "A new quiz has been created in your section." + section.getTitle());
            notificationData.put("quizId", section.getId());

            // Send real-time notification via WebSocket
            brokerMessagingTemplate.convertAndSendToUser(String.valueOf(participant.getUserId()), "/queue/quiz's", notificationData);

            // Send Email (assuming emailService is already set up)
            String emailBody = "Hello " + participant.getFirstName() + " "+ participant.getLastName() + ",\n\n" +
                    "A new quiz is available in the section "+ section.getTitle() + " you are enrolled in. Visit your dashboard to participate.\n\n" +
                    "Best regards,\nEHC Group";
            emailService.sendEmail(participant.getEmail(), "New Quiz Notification", emailBody);
        }
    return ResponseEntity.ok(dto);
    }

    @PostMapping("/{quizId}/questions")
    public QuestionDto addQuestionToQuiz(@PathVariable Long quizId, @RequestBody QuestionDto questionDto) {
        return quizService.addQuestionToQuiz(quizId, questionDto);
    }

    @GetMapping("/section/{sectionId}")
    public List<QuizDto> getQuizzesBySection(@PathVariable Long sectionId) {
        return quizService.getQuizzesBySection(sectionId);
    }

    @PutMapping("/{quizId}")
    public QuizDto updateQuiz(
            @PathVariable Long quizId,
            @RequestBody QuizDto quizDto) {
        return quizService.updateQuiz(quizId, quizDto);
    }

    @DeleteMapping("/{quizId}")
    public ResponseEntity<Void> deleteQuiz(@PathVariable Long quizId) {
        quizService.deleteQuiz(quizId);
        return ResponseEntity.noContent().build();
    }


    @PostMapping("/{sectionId}/submit-quiz")
    public ResponseEntity<String> submitQuiz(
            @PathVariable Long sectionId,
            @RequestBody List<ResponseDto> responseDtos,
            @RequestParam Long participantId,
            @RequestParam Long timeTaken
            ) {

        try {
            boolean hasSubmitted = quizService.hasParticipantSubmittedQuiz(sectionId, participantId);
            if (hasSubmitted) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("You have already submitted this quiz.");
            }
            quizService.submitQuiz(sectionId, responseDtos, participantId,timeTaken);
            return ResponseEntity.ok("Quiz submitted successfully");

        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to submit quiz");
        }
    }

    @GetMapping("/{quizId}/result")
    public ResponseEntity<EvaluationDto> getResult(
            @PathVariable Long quizId,
            @RequestParam Long participantId) {
        EvaluationDto result = evaluationService.getEvaluationResult(quizId, participantId);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public List<QuizDto> getAllQuizzes() {
        return quizService.getAllQuizzes();
    }


}
