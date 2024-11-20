package e_learning.services.ServiceImpl;

import e_learning.DTO.ResponseFormationDto;
import e_learning.entity.EvaluationFormation;
import e_learning.entity.ResponseFormation;
import e_learning.mappers.mappersImpl.ResponseFormationMapper;
import e_learning.repositories.EvaluationFormationRepository;
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
    private final ResponseFormationMapper responseFormationMapper;


    public ResponseFormationDto submitResponse(ResponseFormationDto dto) {
        System.out.println("dto = " + dto);
        ResponseFormation response = responseFormationMapper.toEntity(dto);

        double totalScore = 0.0;
        double maxScore = 0.0;

        for (Map.Entry<String, Integer> entry : dto.answers().entrySet()) {
            int score = entry.getValue();
            totalScore += score;
            maxScore += 4;
        }

        response.setPercentage((totalScore / maxScore) * 100);
        return responseFormationMapper.toDto(responseRepository.save(response));
    }

    public List<ResponseFormationDto> getResponsesByEvaluationId(Long evaluationId) {
        return responseRepository.findByEvaluationId(evaluationId).stream().map(responseFormationMapper::toDto).collect(Collectors.toList());
    }
}
