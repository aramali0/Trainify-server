package e_learning.services.ServiceImpl;

import e_learning.DTO.OptionDto;
import e_learning.entity.Option;
import e_learning.entity.UserApp;
import e_learning.exceptions.ResourceNotFoundException;
import e_learning.mappers.mappersImpl.OptionMapper;
import e_learning.repositories.OptionRepository;
import e_learning.repositories.QuestionRepository;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class OptionService {
    private final OptionRepository optionRepository;
    private final OptionMapper optionMapper;
    private final QuestionRepository questionRepository;
    private final PermissionService permissionService;

    @Transactional
    public OptionDto createOption(OptionDto optionDto, UserApp userApp) throws AccessDeniedException {
        if (!permissionService.canCreateOption(optionDto.questionId(),userApp)) {
            throw new AccessDeniedException("User does not have permission to create option");
        }

        Option option = optionMapper.toEntity(optionDto);
        Option savedOption = optionRepository.save(option);
        return optionMapper.toDto(savedOption);
    }

    @Transactional
    public OptionDto updateOption(Long id, OptionDto optionDto) {
        Option existingOption = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        existingOption.setOptionText(optionDto.optionText());
        existingOption.setQuestion(questionRepository.findById(optionDto.questionId())
                .orElseThrow(() -> new ResourceNotFoundException("Question not found")));
        return optionMapper.toDto(optionRepository.save(existingOption));
    }

    @Transactional
    public void deleteOption(Long id) {
        Option existingOption = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        optionRepository.delete(existingOption);
    }

    public List<OptionDto> getOptionsByQuestionId(Long questionId) {
        List<Option> options = optionRepository.findByQuestionId(questionId);
        return options.stream()
                .map(optionMapper::toDto)
                .collect(Collectors.toList());
    }

    public OptionDto getOption(Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Option not found"));
        return optionMapper.toDto(option);
    }
}
