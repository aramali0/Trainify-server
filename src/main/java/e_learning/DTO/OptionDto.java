package e_learning.DTO;

public record OptionDto(
        Long id,
        String optionText,
        boolean isCorrect,
        Long questionId
) {}