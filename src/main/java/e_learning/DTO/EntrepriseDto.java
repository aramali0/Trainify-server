package e_learning.DTO;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Getter
@Setter
@ToString
public class EntrepriseDto {
    private Long id;
    private String nomCommercial;
    private String numeroRC;
    private String numeroCNSS;
    private String numeroIF;
    private String numeroTP;
    private boolean showQuizResult;
    private boolean showQuizCorrection;
    private boolean showExcelMethode;
    private boolean showDownloadVideo;
    private int nombreSalaries;
    private List<Long> responsableFormationIds;
    private List<Long> coursIds;
    private List<Long> appendingParticipantsIds;
    private List<Long> participantsIds;
    private List<Long> formateursIds;
    private List<Long> chargeFormationsIds;
    private String logo;
    private Integer maxSize;
    private List<String> hierarchicalUnitIds;




}
