package e_learning.controllers;

import e_learning.DTO.CertificateDto;
import e_learning.exceptions.FileStorageException;
import e_learning.security.FileStorageProperties;
import e_learning.services.ServiceImpl.CertificateService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.core.io.Resource ;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;

@RestController
@RequestMapping("/certificates")
@AllArgsConstructor
public class CertificateController {
    private final CertificateService certificateService;
    private final FileStorageProperties fileStorageProperties;

    @PostMapping("/upload")
    public ResponseEntity<CertificateDto> uploadCertificate(
            @RequestParam("file") MultipartFile file,
            @RequestParam("participantId") Long participantId) throws FileStorageException, IOException {

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Path targetLocation = Paths.get(fileStorageProperties.getUploadDir()).resolve(fileName);
        Files.copy(file.getInputStream(), targetLocation);

        CertificateDto certificateDto = new CertificateDto();
        certificateDto.setTitle(fileName);
        certificateDto.setFilePath(targetLocation.toString());
        certificateDto.setParticipantId(participantId);

        CertificateDto savedCertificate = certificateService.createCertificate(certificateDto);
        return ResponseEntity.ok(savedCertificate);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> downloadCertificate(@PathVariable Long id) throws FileStorageException, IOException {
        Resource resource = certificateService.loadCertificate(id);
        Path path = Paths.get(resource.getFile().getAbsolutePath());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }
}
