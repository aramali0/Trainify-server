package e_learning.mappers.mappersImpl;

import e_learning.DTO.CertificateDto;
import e_learning.entity.Certificate;
import e_learning.entity.Participant;
import e_learning.repositories.ParticipantRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
public class CertificateMapper {

    private final ParticipantRepository participantRepository;

    public CertificateMapper(ParticipantRepository participantRepository) {
        this.participantRepository = participantRepository;
    }

    public CertificateDto toDto(Certificate certificate) {
        return CertificateDto.builder()
                .id(certificate.getId())
                .title(certificate.getTitle())
                .filePath(certificate.getFilePath())
                .issueDate(certificate.getIssueDate())
                .participantId(certificate.getParticipant().getUserId())
                .build();
    }


    public Certificate toEntity(CertificateDto certificateDto) {
        Participant participant = participantRepository.findById(certificateDto.getParticipantId())
                .orElseThrow(() -> new IllegalArgumentException("Participant not found"));
        return Certificate.builder()
                .id(certificateDto.getId())
                .title(certificateDto.getTitle())
                .filePath(certificateDto.getFilePath())
                .issueDate(certificateDto.getIssueDate())
                .participant(participant)
                .build();
    }
}
