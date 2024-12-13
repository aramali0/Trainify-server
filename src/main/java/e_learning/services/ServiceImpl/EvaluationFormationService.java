package e_learning.services.ServiceImpl;

import e_learning.DTO.EvaluationBlockDto;
import e_learning.DTO.EvaluationFormationDto;
import e_learning.entity.*;
import e_learning.enums.EvaluationType;
import e_learning.mappers.mappersImpl.EvaluationBlockMapper;
import e_learning.mappers.mappersImpl.EvaluationFormationMapper;
import e_learning.mappers.mappersImpl.EvaluationMapper;
import e_learning.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.jdbc.DataSourceTransactionManagerAutoConfiguration;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationFormationService {
    private final EvaluationFormationRepository evaluationRepository;
    private final EvaluationFormationMapper evaluationMapper;
    private final UserAppRepository userAppRepository;
    private final CourRepository coursRepository;
    private final ResponsableFormationRepository responsableFormationRepository;
    private final EvaluationBlockRepository evaluationBlockRepository;
    private final EvaluationBlockMapper evaluationBlockMapper;

    public EvaluationFormationDto createEvaluation(EvaluationFormationDto dto) {
        UserApp user = userAppRepository.findById(dto.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cour cour = coursRepository.findById(dto.coursId())
                .orElseThrow(() -> new RuntimeException("Cours not found"));

        EvaluationFormation evaluation = evaluationMapper.toEntity(dto);
        evaluation.setCreatedBy(user);
        evaluation.setCour(cour);
        evaluation.setCreatedAt(new Date());
        EvaluationFormation newEvaluation = evaluationRepository.save(evaluation);

        // Ensure blocks are modifiable
        List<EvaluationBlock> blocks = new ArrayList<>(evaluation.getBlocks());
        blocks.forEach(block -> block.setEvaluation(newEvaluation));
        evaluationBlockRepository.saveAll(blocks);

        return evaluationMapper.toDto(evaluationRepository.save(newEvaluation));
    }


    public EvaluationFormationDto getEvaluation(Long evaluationId) {
        return evaluationRepository.findById(evaluationId)
                .map(evaluationMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Evaluation not found"));
    }

    public List<EvaluationFormationDto> getEvaluationsByResponsable(Long userId, Long courId) {
        List<EvaluationFormation> evaluations = evaluationRepository.findByCourEntrepriseResponsableFormationsUserId(userId);

        return evaluations.stream()
                .filter(evaluation -> courId == null || evaluation.getCour().getId().equals(courId))
                .map(evaluationMapper::toDto)
                .sorted((e1, e2) -> e2.createdAt().compareTo(e1.createdAt()))
                .collect(Collectors.toList());
    }

    public List<EvaluationFormationDto> getEvaluationsByType(Long userId, String type) {
        List<EvaluationFormation> evaluations = new ArrayList<>();

        if(type.equalsIgnoreCase("participant"))
        {
            evaluations = evaluationRepository.findByCourEntrepriseParticipantsUserIdAndType(userId , EvaluationType.PARTICIPANT);
        }
        else if(type.equalsIgnoreCase("formateur"))
        {
            evaluations = evaluationRepository.findByCourEntrepriseFormateursUserIdAndType(userId , EvaluationType.FORMATEUR);
        }

        return evaluations.stream()
                .map(evaluationMapper::toDto)
                .sorted((e1, e2) -> e2.createdAt().compareTo(e1.createdAt()))
                .collect(Collectors.toList());
    }

    public EvaluationBlockDto getBlockEvaluation(Long blockId) {
        return evaluationBlockRepository.findById(blockId)
                .map(evaluationBlockMapper::toDto)
                .orElseThrow(() -> new RuntimeException("Block not found"));
    }
}
