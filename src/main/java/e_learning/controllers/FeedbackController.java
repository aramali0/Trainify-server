package e_learning.controllers;

import e_learning.DTO.FeedbackDto;
import e_learning.entity.Feedback;
import e_learning.entity.Formateur;
import e_learning.entity.UserApp;
import e_learning.enums.SatisfactionLevel;
import e_learning.mappers.mappersImpl.FeedbackMapper;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.FeedbackService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/feedback")
@AllArgsConstructor
public class FeedbackController {
    private final FeedbackService feedbackService;
    private final UserAppRepository userAppRepository;
    private final FeedbackMapper feedbackMapper;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Feedback> submitFeedback(@RequestBody Map<String, Object> feedbackData, Principal principal) {
        // Create a new Feedback instance
        Feedback feedback = new Feedback();

        // Get the current authenticated Formateur
        UserApp userApp =  userAppRepository.findUserAppByEmail(principal.getName());
        feedback.setUserApp(userApp);

        // Extract and set the fields from the map
        if (feedbackData.containsKey("organization")) {
            feedback.setOrganization((String) feedbackData.get("organization"));
        }
        if (feedbackData.containsKey("platformExperience")) {
            feedback.setPlatformExperience((String) feedbackData.get("platformExperience"));
        }
        if (feedbackData.containsKey("remarks")) {
            feedback.setRemarks((String) feedbackData.get("remarks"));
        }
        if (feedbackData.containsKey("satisfactionLevel")) {
            // Assuming satisfactionLevel is sent as a string that matches the enum name
            String satisfactionLevel = (String) feedbackData.get("satisfactionLevel");
            feedback.setSatisfactionLevel(SatisfactionLevel.valueOf(satisfactionLevel));
        }

        // Save the feedback and return the response
        Feedback savedFeedback = feedbackService.saveFeedback(feedback);
        return ResponseEntity.ok(savedFeedback);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackDto>> getAllFeedback() {
        List<Feedback> feedbackList = feedbackService.getAllFeedback();
        List feedbackDtos = feedbackList.stream().map(feedbackMapper::toDto).collect(Collectors.toList());
        return ResponseEntity.ok(feedbackDtos);
    }
}
