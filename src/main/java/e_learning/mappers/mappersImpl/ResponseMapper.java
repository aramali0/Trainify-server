package e_learning.mappers.mappersImpl;

import e_learning.DTO.ResponseDto;
import e_learning.entity.*;
import e_learning.repositories.EvaluationRepository;
import e_learning.repositories.OptionRepository;
import e_learning.repositories.ParticipantRepository;
import e_learning.repositories.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@AllArgsConstructor
public class ResponseMapper {

    private final QuestionRepository questionRepository;
    private final OptionRepository optionRepository;
    private final EvaluationRepository evaluationRepository;

    public ResponseDto toDto(Response response) {
        return new ResponseDto(
                response.getId() != null ?  response.getId() : null,
                response.getOption() != null ? response.getOption().stream().map(Option::getId).toList() : null,
                response.getEvaluation() != null ? response.getEvaluation().getId() : null,
                response.getQuestion() != null ? response.getQuestion().getId() : null
        );
    }

    public Response toEntity(ResponseDto responseDto) {
        Response response = new Response();
        if (responseDto.id() != null)
        response.setId(responseDto.id());
        if (responseDto.optionId() != null) {
            List<Option> option = optionRepository.findAllById(responseDto.optionId());
            response.setOption(option);
        }
        if (responseDto.evaluationId() != null) {
            Evaluation evaluation = evaluationRepository.findById(responseDto.evaluationId()).orElse(null);
            response.setEvaluation(evaluation);
        }
        if (responseDto.questionId() != null) {
            Question question = questionRepository.findById(responseDto.questionId()).orElse(null);
            response.setQuestion(question);
        }

        return response;
    }
}
