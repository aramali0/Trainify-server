package e_learning.mappers.mappersImpl;

import e_learning.DTO.ResponseFormationDto;
import e_learning.entity.ResponseFormation;
import e_learning.repositories.EntrepriseRepository;
import e_learning.repositories.EvaluationFormationRepository;
import e_learning.repositories.ResponseFormationRepository;
import e_learning.repositories.UserAppRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ResponseFormationMapper {

    private final ResponseFormationRepository responseFormationRepository;
    private final UserAppRepository userAppRepository;
    private final EntrepriseRepository entrepriseRepository;
    private final EvaluationFormationRepository evaluationFormationRepository;

    public ResponseFormationDto toDto(ResponseFormation entity) {
        return new ResponseFormationDto(
                entity.getId() != null ? entity.getId() : null,
                entity.getUser() != null ? entity.getUser().getUserId() : null,
                entity.getEntreprise() != null ? entity.getEntreprise().getId() : null,
                entity.getEvaluation() != null ? entity.getEvaluation().getId() : null,
                entity.getAnswers(),
                entity.getPercentage()
        );
    }


    public ResponseFormation toEntity(ResponseFormationDto dto) {
        ResponseFormation response = new ResponseFormation();
        response.setUser(userAppRepository.findById(dto.userId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        response.setEntreprise(entrepriseRepository.findById(dto.entrepriseId())
                .orElseThrow(() -> new RuntimeException("Entreprise not found")));
        response.setEvaluation(evaluationFormationRepository.findById(dto.evaluationId())
                .orElseThrow(() -> new RuntimeException("Evaluation not found")));
        response.setAnswers(dto.answers());
        response.setPercentage(dto.percentage());
        return responseFormationRepository.save(response);
    }
}
