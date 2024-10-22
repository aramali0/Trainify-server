package e_learning.services.ServiceImpl;


import e_learning.DTO.OptionDto;
import e_learning.DTO.QuestionDto;
import e_learning.DTO.ResponseDto;
import e_learning.entity.Option;
import e_learning.entity.Question;
import e_learning.entity.Response;
import e_learning.mappers.mappersImpl.OptionMapper;
import e_learning.mappers.mappersImpl.QuestionMapper;
import e_learning.mappers.mappersImpl.ResponseMapper;
import e_learning.repositories.OptionRepository;
import e_learning.repositories.QuestionRepository;
import e_learning.repositories.ResponseRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuestionMapper questionMapper;
    private final ResponseRepository responseRepository;
    private final PermissionService permissionService;
    private final ResponseMapper responseMapper;
    private final OptionMapper optionMapper;
    private final OptionRepository optionRepository;

    public QuestionDto createQuestion(QuestionDto questionDto) {
//        if (!permissionService.canManageQuestion(questionDto.evaluationId())) {
//            throw new AccessDeniedException("User does not have permission to manage questions");
//        }

        Question question = questionMapper.toEntity(questionDto);
        System.out.println("question from the service : "+ question.getQuiz());
        question = questionRepository.save(question);
        return questionMapper.toDto(question);
    }

    public QuestionDto updateQuestion(Long id, QuestionDto questionDto) {
        Question existingQuestion = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        final Question finalExistingQuestion = existingQuestion;

        finalExistingQuestion.setText(questionDto.text());
        finalExistingQuestion.setType(questionDto.type());

        existingQuestion = questionRepository.save(finalExistingQuestion);
        return questionMapper.toDto(existingQuestion);
    }

    public void deleteQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        questionRepository.delete(question);
    }

    public QuestionDto getQuestion(Long id) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return questionMapper.toDto(question);
    }

    public List<ResponseDto> getQuestionResponses(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return question.getResponses().stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<OptionDto> getQuestionOptions(Long questionId) {
        Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));
        return question.getOptions().stream().map(optionMapper::toDto).collect(Collectors.toList());
    }

    public OptionDto addOptionToQuestion(Long id, OptionDto optionDto) {
        Question question = questionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Question not found"));

        Option option = optionMapper.toEntity(optionDto);
        Option newOption = optionRepository.save(option);
        question.getOptions().add(newOption);
        question = questionRepository.save(question);
        newOption.setQuestion(question);
        newOption = optionRepository.save(newOption);
        return optionMapper.toDto(newOption);
    }

    public List<QuestionDto> getQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId).stream()
                .map(questionMapper::toDto)
                .collect(Collectors.toList());
    }
}
