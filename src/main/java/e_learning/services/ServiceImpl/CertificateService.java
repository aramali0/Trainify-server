package e_learning.services.ServiceImpl;

import e_learning.DTO.CertificateDto;
import e_learning.entity.Certificate;
import e_learning.entity.Participant;
import e_learning.exceptions.FileStorageException;
import e_learning.repositories.CertificateRepository;
import e_learning.repositories.ParticipantRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;

@Service
@AllArgsConstructor
public class CertificateService {
    private final CertificateRepository certificateRepository;
    private final ResourceLoader resourceLoader;
    private final ParticipantRepository participantRepository;

    public CertificateDto createCertificate(CertificateDto certificateDto) throws FileStorageException {
        Participant participant = participantRepository.findById(certificateDto.getParticipantId())
                .orElseThrow(() -> new EntityNotFoundException("Participant not found"));
        // Save certificate details to the database
        Certificate certificate = new Certificate();
        certificate.setTitle(certificateDto.getTitle());
        certificate.setFilePath(certificateDto.getFilePath());
        certificate.setParticipant(participant);
        certificate.setIssueDate(new Date());
        certificate = certificateRepository.save(certificate);
        return CertificateDto.builder()
                .id(certificate.getId())
                .title(certificate.getTitle())
                .filePath(certificate.getFilePath())
                .participantId(certificate.getParticipant().getUserId())
                .build();
    }

    public Resource loadCertificate(Long id) throws FileStorageException {
        Certificate certificate = certificateRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Certificate not found"));
        Path filePath = Paths.get(certificate.getFilePath()).normalize();
        return resourceLoader.getResource("file:" + filePath.toString());
    }
}
