package e_learning.services.ServiceImpl;

import e_learning.DTO.ResponseFormationDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.ResponseFormationMapper;
import e_learning.repositories.EvaluationFormationRepository;
import e_learning.repositories.ResponsableFormationRepository;
import e_learning.repositories.ResponseFormationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ResponseFormationService {
    private final ResponseFormationRepository responseRepository;
    private final ResponseFormationMapper responseMapper;
    private final EvaluationFormationRepository evaluationRepository;
    private final ResponseFormationRepository responseFormationRepository;

    public ResponseFormationDto submitResponse(ResponseFormationDto dto) {
        ResponseFormation response = responseMapper.toEntity(dto);

        EvaluationFormation evaluation = evaluationRepository.findById(dto.evaluationId())
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));

        double totalWeightedScore = 0.0;
        double maxWeightedScore = 0.0;

        for (EvaluationBlock block : evaluation.getBlocks()) {
            double blockScore = 0.0;
            double blockMaxScore = block.getQuestions().size() * 4;

            Map<String, Integer> answers = response.getBlockAnswers().stream()
                    .filter(ba -> ba.getBlockId().equals(block.getId()))
                    .findFirst()
                    .map(BlockAnswer::getAnswers)
                    .orElse(null);

            if (answers != null) {
                for (int score : answers.values()) {
                    blockScore += score;
                }
                double finalBlockScore = blockScore;
                response.getBlockAnswers().stream()
                    .filter(ba -> ba.getBlockId().equals(block.getId()))
                    .findFirst().ifPresent(ba -> ba.setTotalScore(finalBlockScore / blockMaxScore * 100));
            }

            totalWeightedScore += (blockScore / blockMaxScore) * block.getWeightage();
            maxWeightedScore += block.getWeightage();
        }

        response.setTotalScore((totalWeightedScore / maxWeightedScore) * 100);

        response.setEvaluation(evaluation);
        ResponseFormation newResponse = responseFormationRepository.save(response);

        return responseMapper.toDto(newResponse);
    }

    public ResponseFormationDto getResponse(Long responseId) {
        return responseRepository.findById(responseId)
                .map(responseMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Response not found"));
    }

    public List<ResponseFormationDto> getResponsesByEvaluation(Long evaluationId) {
        return responseRepository.findByEvaluationId(evaluationId).stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ResponseFormationDto> checkUserHasResponse(Long userId, Long evaluationId) {
        return responseRepository.findByEvaluationIdAndUserUserId(evaluationId, userId).stream()
                .map(responseMapper::toDto)
                .collect(Collectors.toList());
    }
}
