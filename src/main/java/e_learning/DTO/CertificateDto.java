package e_learning.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CertificateDto {
    private Long id;
    private String title;
    private Date issueDate;
    private String filePath;
    private Long participantId; // or ParticipantDto if you have it
}
