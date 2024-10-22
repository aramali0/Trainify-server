package e_learning.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ParticipantUnavailabilityDto {
    private Long id;
    private String participantName;
    private Date startDate;
    private Date endDate;
}
