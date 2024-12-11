package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.ClassEntity;
import e_learning.entity.Participant;
import e_learning.entity.Session;
import e_learning.entity.Unavailability;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.ParticipantRepository;
import e_learning.repositories.UnavailabilityRepository;
import e_learning.services.ServiceImpl.ParticipantService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.parser.Part;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/participants")
@AllArgsConstructor
public class ParticipantController {

    private final ParticipantService participantService;
    private final ParticipantRepository participantRepository;
    private final ClassEntityMapper classEntityMapper;
    private final CourMapper courMapper;
    private final SessionMapper sessionMapper;
    private final SectionMapper sectionMapper;
    private final UnavailabilityMapper unavailabilityMapper;
    private final UnavailabilityRepository unavailabilityRepository;

    @GetMapping("/{participantId}/same-classes")
    public ResponseEntity<List<ParticipantDto>> getParticipantsInSameClasses(@PathVariable Long participantId) {
        List<ParticipantDto> participants = participantService.getParticipantsInSameClasses(participantId);
        return ResponseEntity.ok(participants);
    }

    @GetMapping
    public ResponseEntity<List<ParticipantDto>> getAllParticipants() {
        List<ParticipantDto> participants = participantService.getAllParticipants();
        return ResponseEntity.ok(participants);
    }

    @GetMapping("/{participantId}/classes")
    public ResponseEntity<List<ClassEntityDto>> getClassesForParticipant(@PathVariable Long participantId) {
        List<ClassEntityDto> classes = participantService.getClassesForParticipant(participantId);
        return ResponseEntity.ok(classes);
    }

    @GetMapping("/{participantId}/cours")
    public ResponseEntity<List<CourDto>> getCoursesForParticipant(
            @PathVariable Long participantId,
            @RequestParam(required = false) String className) {
        List<CourDto> courses = participantService.getCoursesForParticipant(participantId, className);
        return ResponseEntity.ok(courses);
    }

    @GetMapping("/{participantId}/sessions")
    public ResponseEntity<List<SessionDto>> getSessionsForParticipant(@PathVariable Long participantId) {
        List<SessionDto> sessions = participantService.getSessionsForParticipant(participantId);
        return ResponseEntity.ok(sessions);
    }

    @GetMapping("/{participantId}/sections")
    public ResponseEntity<List<SectionDto>> getSectionsForParticipant(@PathVariable Long participantId) {
        List<SectionDto> sections = participantService.getSectionsForParticipant(participantId);
        return ResponseEntity.ok(sections);
    }

     @PostMapping("/{participantId}/unavailability")
    public ResponseEntity<UnavailabilityDto> createUnavailability(@PathVariable Long participantId,
                                                                  @RequestBody UnavailabilityDto unavailabilityDto) {
        Unavailability unavailability = unavailabilityMapper.toEntity(unavailabilityDto);
        Participant participant = participantRepository.findById(participantId).orElseThrow();
        unavailability.setParticipant(participant);
        Unavailability savedUnavailability = unavailabilityRepository.save(unavailability);
         System.out.println("savedUnavailability = " + savedUnavailability);
        participant.getUnavailabilities().add(savedUnavailability);
        Participant savedParticipant = participantRepository.save(participant);
         System.out.println("savedParticipant = " + savedParticipant.getUnavailabilities());
        return ResponseEntity.ok(unavailabilityMapper.toDto(savedUnavailability));
    }

    @GetMapping("/{participantId}/unavailability")
    public ResponseEntity<List<UnavailabilityDto>> getUnavailabilityForParticipant(@PathVariable Long participantId) {
        List<Unavailability> unavailabilityList = unavailabilityRepository.findByParticipantUserId(participantId);
        List<UnavailabilityDto> unavailabilityDtos = unavailabilityList.stream()
                .map(unavailabilityMapper::toDto)
                .collect(Collectors.toList());
        return ResponseEntity.ok(unavailabilityDtos);
    }

      @DeleteMapping("/{participantId}/unavailability/{unavailabilityId}")
    public ResponseEntity<Void> deleteUnavailability(@PathVariable Long participantId,
                                                     @PathVariable Long unavailabilityId) {
        unavailabilityRepository.deleteById(unavailabilityId);
        return ResponseEntity.noContent().build();
    }


    @GetMapping("/{participantId}/libraries")
    public ResponseEntity<List<LibraryDto>> getLibrariesByParticipantId(@PathVariable Long participantId , @RequestParam(required = false) String libraryName , @RequestParam(required = false) Long formationId) {
        List<LibraryDto> libraries = participantService.getLibrariesByParticipantId(participantId , libraryName , formationId);
        return ResponseEntity.ok(libraries);
    }

    @GetMapping("/{participantId}/resources")
    public ResponseEntity<List<ResourceDto>> getResourcesByParticipantId(@PathVariable Long participantId) {
        List<ResourceDto> resources = participantService.getResourcesByParticipantId(participantId);
        return ResponseEntity.ok(resources);
    }

    @GetMapping("/{participantId}/max-evaluation-score")
    public ResponseEntity<Long> getMaxEvaluationScore(@PathVariable Long participantId) {
        Long maxScore = participantService.getMaxEvaluationScore(participantId);
        return ResponseEntity.ok(maxScore);
    }


    @GetMapping("/{participantId}/evaluations")
    public ResponseEntity<List<EvaluationDto>> getEvaluationsForParticipant(@PathVariable Long participantId) {
        List<EvaluationDto> evaluations = participantService.getEvaluationsForParticipant(participantId);
        return ResponseEntity.ok(evaluations);
    }


    @GetMapping("/{participantId}/evaluations-pagination")
    public ResponseEntity<Page<EvaluationDto>> getEvaluationsForParticipantPagination(
        @PathVariable Long participantId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<EvaluationDto> evaluations = participantService.getEvaluationsForParticipant(participantId, pageable);
    return ResponseEntity.ok(evaluations);
}


    @GetMapping("/{participantId}/certificates")
    public ResponseEntity<List<CertificateDto>> getCertificatesForParticipant(@PathVariable Long participantId) {
        List<CertificateDto> certificates = participantService.getCertificatesForParticipant(participantId);
        return ResponseEntity.ok(certificates);
    }

    @GetMapping("/{participantId}/certificates/count")
    public ResponseEntity<Long> getTotalCertificatesForParticipant(@PathVariable Long participantId) {
        Long totalCertificates = participantService.getTotalCertificatesForParticipant(participantId);
        return ResponseEntity.ok(totalCertificates);
    }


    @GetMapping("/{id}/contacts")
    public ResponseEntity<List<UserDTO>> getContactsByParticipantId(@PathVariable Long id) {
        List<UserDTO> contacts = participantService.getContactsByParticipantId(id);
        return ResponseEntity.ok(contacts);
    }

    @GetMapping("/{id}/contacts/search")
    public ResponseEntity<List<UserDTO>> searchContactsByParticipantId(@PathVariable Long id, @RequestParam String name) {
        List<UserDTO> contacts = participantService.searchContactsByParticipantId(id, name);
        return ResponseEntity.ok(contacts);
    }

}
