package e_learning.controllers;

import e_learning.DTO.CourDto;
import e_learning.DTO.SectionDto;
import e_learning.DTO.SessionDto;
import e_learning.entity.ChargeFormation;
import e_learning.entity.Session;
import e_learning.entity.UserApp;
import e_learning.repositories.FormateurRepository;
import e_learning.repositories.ResponsableFormationRepository;
import e_learning.repositories.SessionRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.CourService;
import e_learning.services.ServiceImpl.SessionService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.*;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/sessions")
@AllArgsConstructor
public class SessionController {
    private final SessionService sessionService;
    private final ResponsableFormationRepository ResponsableFormationRepository;
    private final FormateurRepository formateurRepository;
    private final UserAppRepository userAppRepository;
    private final SessionRepository sessionRepository;

    @GetMapping
    public List<SessionDto> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @GetMapping("/{id}")
    public ResponseEntity<SessionDto> getSessionById(@PathVariable Long id) {
        Optional<SessionDto> session = sessionService.getSessionById(id);
        return session.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createSession(@RequestBody SessionDto sessionDto, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());

        // Check for overlapping sessions
        List<Session> existingSessions = sessionService.findSessionsByCourId(sessionDto.courId());

        Session overlappingSession = existingSessions.stream()
                .filter(session ->
                        session.getStartDate().before(sessionDto.endDate()) && session.getEndDate().after(sessionDto.startDate())
                ).findFirst().orElse(null);

        if (overlappingSession != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");  // Format: 21 Sep 2024
            String formattedStartDate = dateFormatter.format(overlappingSession.getStartDate());
            String formattedEndDate = dateFormatter.format(overlappingSession.getEndDate());

            return ResponseEntity.badRequest().body(
                    "You cannot create this session as it overlaps with an existing one. "
                            + "Existing Session Start Date: " + formattedStartDate
                            + ", End Date: " + formattedEndDate
            );
        }
        // Create or update the session
        SessionDto dto = sessionService.createOrUpdateSession(sessionDto, userApp);

        return ResponseEntity.ok().body(dto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateSession(@PathVariable Long id, @RequestBody SessionDto sessionDto, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());

        // Check for overlapping sessions
        List<Session> existingSessions = sessionService.findSessionsByCourId(sessionDto.courId());

        Session overlappingSession = existingSessions.stream()
                .filter(session ->
                        session.getStartDate().before(sessionDto.endDate()) && session.getEndDate().after(sessionDto.startDate())
                ).findFirst().orElse(null);

        if (overlappingSession != null) {
            SimpleDateFormat dateFormatter = new SimpleDateFormat("dd MMM yyyy");  // Format: 21 Sep 2024
            String formattedStartDate = dateFormatter.format(overlappingSession.getStartDate());
            String formattedEndDate = dateFormatter.format(overlappingSession.getEndDate());

            return ResponseEntity.badRequest().body(
                    "You cannot create this session as it overlaps with an existing one. "
                            + "Existing Session Start Date: " + formattedStartDate
                            + ", End Date: " + formattedEndDate
            );
        }

        SessionDto session = sessionService.createOrUpdateSession(sessionDto,userApp);

        if(userApp instanceof ChargeFormation)
        {
            return ResponseEntity.ok("This session needs Approval");
        }
        return ResponseEntity.ok().body(session);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteSession(@PathVariable Long id, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());

        Optional<Session> sessionOpt = sessionRepository.findById(id);
        if (sessionOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        Session session = sessionOpt.get();

        if(!session.getSections().isEmpty())
        {
            return ResponseEntity.badRequest().body("You cannot delete this session as it has sections");
        }

        if(!session.getVideoConferences().isEmpty())
        {
            return ResponseEntity.badRequest().body("You cannot delete this session as it has video conferences");
        }

        if(!Objects.equals(session.getCreatedBy().getUserId(), userApp.getUserId()))
        {
            return ResponseEntity.badRequest().body("You cannot delete this session as you are not the creator");
        }


        sessionService.deleteSession(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/sections")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSectionsBySessionId(@PathVariable Long id, Principal principal) {

        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());

        if(userApp == null){
            ResponseEntity.badRequest().body("User not found");
        }
        List<SectionDto> sections = sessionService.getSectionsBySessionId(id,userApp);
        if (sections.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sections);
    }

}
