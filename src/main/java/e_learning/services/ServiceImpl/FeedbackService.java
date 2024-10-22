package e_learning.services.ServiceImpl;

import e_learning.entity.Feedback;
import e_learning.entity.Formateur;
import e_learning.repositories.FeedbackRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class FeedbackService {
    private final FeedbackRepository feedbackRepository;

    public Feedback saveFeedback(Feedback feedback) {
        return feedbackRepository.save(feedback);
    }

//    public List<Feedback> getFeedbackByFormateurId(Long formateurId) {
//        return feedbackRepository.findByFormateurUserId(formateurId);
//    }

    public List<Feedback> getAllFeedback() {
        return feedbackRepository.findAll();
    }
}
