package e_learning.services.ServiceImpl;

import e_learning.DTO.EvaluationFormationDto;
import e_learning.entity.*;
import e_learning.enums.EvaluationType;
import e_learning.mappers.mappersImpl.EvaluationFormationMapper;
import e_learning.mappers.mappersImpl.EvaluationMapper;
import e_learning.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EvaluationFormationService {
    private final EvaluationFormationRepository evaluationRepository;
    private final ParticipantRepository participantRepository;
    private final FormateurRepository formateurRepository;
    private final EvaluationFormationMapper evaluationFormationMapper;
    private final UserAppRepository userAppRepository;


    public EvaluationFormationDto createEvaluation(EvaluationFormationDto dto) {

        UserApp user = userAppRepository.findById(dto.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Entreprise enterprise = null;

        if (user instanceof ResponsableFormation) {

            enterprise = ((ResponsableFormation) user).getEntreprise();
        }
        else
        {
            return null;
        }


        EvaluationFormation evaluation = new EvaluationFormation();
        evaluation.setTitle(dto.title());
        evaluation.setType(EvaluationType.valueOf(dto.type().toUpperCase()));
        evaluation.setQuestions(dto.questions());
        evaluation.setEntreprise(enterprise);
        evaluation.setCreatedBy(userAppRepository.findById(dto.createdBy())
                .orElseThrow(() -> new RuntimeException("User not found")));
        evaluation.setCreatedAt(new Date());
        return evaluationFormationMapper.toDto(evaluationRepository.save(evaluation));
    }

    public List<EvaluationFormationDto> getEvaluationsByType(String type , Long userId) {

        UserApp user = null;
        Long enterpriseId = null;

       if(type.equals("PARTICIPANT")) {
              user = participantRepository.findById(userId)
                     .orElseThrow(() -> new RuntimeException("User not found"));

       } else {
                user = formateurRepository.findById(userId)
                         .orElseThrow(() -> new RuntimeException("User not found"));
       }
       if(user != null)
       {
           if(user instanceof Participant)
           {
                enterpriseId = ((Participant) user).getEntreprise().getId();
              } else {
                enterpriseId = ((Formateur) user).getEntreprise().getId();
           }
       }
        return  evaluationRepository.findByEntrepriseIdAndType(enterpriseId, EvaluationType.valueOf(type.toUpperCase())).stream().map((evaluationFormationMapper::toDto)).collect(Collectors.toList());
    }

    public List<EvaluationFormationDto> getEvaluations(Long userId) {

        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Long enterpriseId = null;

        if (user instanceof Participant) {
           enterpriseId = ((Participant) user).getEntreprise().getId();
        }
        else if (user instanceof Formateur) {
            enterpriseId = ((Formateur) user).getEntreprise().getId();
        }
        else if (user instanceof ResponsableFormation) {
            enterpriseId = ((ResponsableFormation) user).getEntreprise().getId();
        }

        return evaluationRepository.findByEntrepriseId(enterpriseId).stream().map((evaluationFormationMapper::toDto)).collect(Collectors.toList());



    }

    public EvaluationFormationDto getEvaluation(Long evaluationId) {
        return evaluationFormationMapper.toDto(evaluationRepository.findById(evaluationId)
                .orElseThrow(() -> new RuntimeException("Evaluation not found")));
    }
}
