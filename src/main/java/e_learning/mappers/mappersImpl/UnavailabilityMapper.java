package e_learning.mappers.mappersImpl;

import e_learning.DTO.UnavailabilityDto;
import e_learning.entity.Participant;
import e_learning.entity.Unavailability;
import e_learning.repositories.ParticipantRepository;
import org.springframework.stereotype.Component;

@Component
public class UnavailabilityMapper {

    private final ParticipantRepository participantRepository;

    public UnavailabilityMapper(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public UnavailabilityDto toDto(Unavailability unavailability) {
        return new UnavailabilityDto(
                unavailability.getId() != null ? unavailability.getId() : null,
                unavailability.getParticipant().getUserId(),
                unavailability.getStartDate(),
                unavailability.getEndDate()
        );
    }

    public Unavailability toEntity(UnavailabilityDto dto) {
        Unavailability unavailability = new Unavailability();
        if (dto.id() != null) {
            unavailability.setId(dto.id());
        }
        unavailability.setStartDate(dto.startDate());
        unavailability.setEndDate(dto.endDate());
        if (dto.participantId() != null) {
            Participant participant =  participantRepository.findById(dto.participantId()).orElseThrow();
            unavailability.setParticipant(participant);
        }
        return unavailability;
    }
}
