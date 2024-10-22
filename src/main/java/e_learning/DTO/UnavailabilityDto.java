package e_learning.DTO;

import lombok.Data;

import java.util.Date;

public record UnavailabilityDto (
    Long id,
    Long participantId,
    Date startDate,
    Date endDate
){}
