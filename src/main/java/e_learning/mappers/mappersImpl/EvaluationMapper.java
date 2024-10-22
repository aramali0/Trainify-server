package e_learning.mappers.mappersImpl;

import e_learning.DTO.ClassEntityDto;
import e_learning.DTO.EvaluationDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class EvaluationMapper {

    private final SectionRepository sectionRepository;

    private final ResponseRepository responseRepository;
    private final ParticipantRepository participantRepository;
    private final QuizRepository quizRepository;

    public EvaluationDto toDto(Evaluation evaluation) {
        return new EvaluationDto(
                evaluation.getId() != null ? evaluation.getId() : null,
                evaluation.getType(),
                evaluation.getScore(),
               evaluation.getCreatedAt(),
               evaluation.getTimeTaken(),
               evaluation.getResponses() !=null ? evaluation.getResponses().stream().map(Response::getId).collect(Collectors.toList()) : null,
                evaluation.getParticipant() != null ? evaluation.getParticipant().getUserId() : null,
                evaluation.getQuiz() != null ? evaluation.getQuiz().getId() : null
        );
    }

    public Evaluation toEntity(EvaluationDto evaluationDto) {
        Evaluation evaluation = new Evaluation();
        if (evaluationDto.id() != null) {
            evaluation.setId(evaluationDto.id());
        }
        evaluation.setType(evaluationDto.type());
        if(evaluationDto.createdAt() ==null)
        {
            evaluation.setCreatedAt(new Date());
        }
        else {
            evaluation.setCreatedAt(evaluationDto.createdAt());
        }
        if (evaluationDto.score() != null) {
            evaluation.setScore(evaluationDto.score());
        }

        if (evaluationDto.responseIds() != null) {

            List<Response> responses = responseRepository.findAllById(evaluationDto.responseIds());
            evaluation.setResponses(responses);
        }

        if (evaluationDto.participantId() != null) {
            Participant participant = participantRepository.findById(evaluationDto.participantId()).orElse(null);
            evaluation.setParticipant(participant);
        }

        if(evaluationDto.quizId() != null) {
            Quiz quiz = quizRepository.findById(evaluationDto.quizId()).orElse(null);
            evaluation.setQuiz(quiz);
        }

        if(evaluationDto.timeTaken() != null) {
            evaluation.setTimeTaken(evaluationDto.timeTaken());
        }

        return evaluation;
    }
}
