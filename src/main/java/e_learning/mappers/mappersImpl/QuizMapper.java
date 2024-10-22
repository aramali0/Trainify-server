package e_learning.mappers.mappersImpl;

import e_learning.DTO.QuizDto;
import e_learning.controllers.QuizController;
import e_learning.entity.Evaluation;
import e_learning.entity.Question;
import e_learning.entity.Quiz;
import e_learning.repositories.EvaluationRepository;
import e_learning.repositories.QuestionRepository;
import e_learning.repositories.SectionRepository;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class QuizMapper {

    private final UserAppRepository userAppRepository;
    private final SectionRepository sectionRepository;
    private final QuestionRepository questionRepository;
    private final EvaluationRepository evaluationRepository;

    public QuizDto toDto(Quiz quiz) {
        return new QuizDto(
                quiz.getId() != null ? quiz.getId() : null,
                quiz.getTitle(),
                quiz.getType(),
                quiz.getCreatedBy() != null ? quiz.getCreatedBy().getUserId() : null,
                quiz.getSection() != null ? quiz.getSection().getId() : null,
                quiz.getQuestions() != null ? quiz.getQuestions().stream().map(Question::getId).collect(Collectors.toList()) : null,
                quiz.getEvaluations() != null ? quiz.getEvaluations().stream().map(Evaluation::getId).collect(Collectors.toList()) : null
        );
    }

    public Quiz toEntity(QuizDto quizDto) {
        Quiz quiz = new Quiz();
        quiz.setId(quizDto.id());
        quiz.setTitle(quizDto.title());
        quiz.setType(quizDto.type());

        if (quizDto.createdBy() != null) {
            quiz.setCreatedBy(userAppRepository.findById(quizDto.createdBy()).orElse(null));
        }

        if (quizDto.sectionId() != null) {
            quiz.setSection(sectionRepository.findById(quizDto.sectionId()).orElse(null));
        }

        if (quizDto.questionIds() != null) {
            List<Question> questions = questionRepository.findAllById(quizDto.questionIds());
            quiz.setQuestions(questions);
        }

        if (quizDto.evaluationsIds() != null) {
            List<Evaluation> evaluations = evaluationRepository.findAllById(quizDto.evaluationsIds());
            quiz.setEvaluations(evaluations);
        }

        return quiz;
    }
}
