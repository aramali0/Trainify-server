package e_learning.mappers.mappersImpl;

import e_learning.DTO.CourDto;
import e_learning.DTO.QuestionDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class QuestionMapper {

    private final EvaluationRepository evaluationRepository;

    private final ResponseRepository responseRepository;
    private final OptionRepository optionRepository;
    private final SectionRepository sectionRepository;
    private final QuizRepository quizRepository;

    public QuestionDto toDto(Question question) {
        return new QuestionDto(
                question.getId() != null ? question.getId() : null,
                question.getText(),
                question.getType(),
                question.getResponses() != null ? question.getResponses().stream().map(Response::getId).collect(Collectors.toList()) : null,
                question.getOptions() != null ? question.getOptions().stream().map(Option::getId).collect(Collectors.toList()) : null,
                question.getQuiz() != null ? question.getQuiz().getId() : null

        );
    }

    public Question toEntity(QuestionDto questionDto) {
        Question question = new Question();
        if(questionDto.id() != null)
        question.setId(questionDto.id());
        question.setText(questionDto.text());
        question.setType(questionDto.type());

        if (questionDto.responseIds() != null) {
            List<Response> responses = responseRepository.findAllById(questionDto.responseIds());
            question.setResponses(responses);
        }

        if (questionDto.optionIds() != null) {
            List<Option> options = optionRepository.findAllById(questionDto.optionIds());
            question.setOptions(options);
        }

        if (questionDto.quizId() != null) {
            Quiz quiz = quizRepository.findById(questionDto.quizId())
                    .orElseThrow(() -> new EntityNotFoundException("Quiz not found"));
            question.setQuiz(quiz);
        }

        return question;
    }
}
