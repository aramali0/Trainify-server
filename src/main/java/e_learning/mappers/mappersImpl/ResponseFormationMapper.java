package e_learning.mappers.mappersImpl;

import e_learning.DTO.BlockAnswerDto;
import e_learning.DTO.ResponseFormationDto;
import e_learning.entity.BlockAnswer;
import e_learning.entity.ResponseFormation;
import e_learning.repositories.EvaluationBlockRepository;
import e_learning.repositories.EvaluationFormationRepository;
import e_learning.repositories.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class ResponseFormationMapper {
    private final UserAppRepository userAppRepository;

    public ResponseFormationDto toDto(ResponseFormation entity) {
        return new ResponseFormationDto(
                entity.getId(),
                entity.getEvaluation() != null ? entity.getEvaluation().getId() : null,
                entity.getUser() != null ? entity.getUser().getUserId() : null,
                entity.getBlockAnswers().stream().map(this::toDto).toList(),
                entity.getTotalScore()
        );
    }

    public ResponseFormation toEntity(ResponseFormationDto dto) {
        ResponseFormation response = new ResponseFormation();
        response.setUser(userAppRepository.findById(dto.userId()).orElse(null));
        List<BlockAnswer> blockAnswers = dto.blockAnswers().stream()
                .map(blockDto -> {
                    BlockAnswer blockAnswer = toEntity(blockDto);
                    blockAnswer.setResponse(response); // Set the parent response
                    return blockAnswer;
                })
                .toList();
        response.setBlockAnswers(blockAnswers);
        return response;
    }

    private BlockAnswerDto toDto(BlockAnswer entity) {
        return new BlockAnswerDto(entity.getBlockId(),entity.getTotalScore(), entity.getAnswers());
    }

    private BlockAnswer toEntity(BlockAnswerDto dto) {
        BlockAnswer blockAnswer = new BlockAnswer();
        blockAnswer.setBlockId(dto.blockId());
        blockAnswer.setTotalScore(dto.totalScore());
        blockAnswer.setAnswers(dto.answers());
        return blockAnswer;
    }
}
