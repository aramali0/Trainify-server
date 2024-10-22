package e_learning.mappers.mappersImpl;

import e_learning.entity.Option;
import e_learning.repositories.QuestionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class OptionMapper {
    private final QuestionRepository questionRepository;

    public e_learning.entity.Option toEntity(e_learning.DTO.OptionDto optionDto) {
        Option option = new Option();
        if (optionDto.id() != null)
            option.setId(optionDto.id());
        option.setOptionText(optionDto.optionText());
        option.setCorrect(optionDto.isCorrect());
        if(optionDto.questionId() != null) {
        option.setQuestion(questionRepository.findById(optionDto.questionId()).orElse(null));
        }
        return option;
    }

    public e_learning.DTO.OptionDto toDto(e_learning.entity.Option option) {
        return new e_learning.DTO.OptionDto(
                option.getId() != null ? option.getId() : null,
                option.getOptionText(),
                option.isCorrect(),
                option.getQuestion() != null ? option.getQuestion().getId() : null
        );
    }
}
