package e_learning.services.ServiceImpl;

import e_learning.DTO.SectionDto;
import e_learning.DTO.SessionDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.ResponsableFormationMapper;
import e_learning.mappers.mappersImpl.SectionMapper;
import e_learning.mappers.mappersImpl.SessionMapper;
import e_learning.repositories.ActionApprovalRepository;
import e_learning.repositories.SessionRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class SessionService {
    private final SessionRepository sessionRepository;
    private final SessionMapper sessionMapper;
    private final SectionMapper sectionMapper;
    private final ResponsableFormationMapper responsableFormationMapper;
    private final ActionApprovalRepository actionApprovalRepository;

    public List<SessionDto> getAllSessions() {
        return sessionRepository.findAll().stream().map(sessionMapper::toDto).collect(Collectors.toList());
    }

    public Optional<SessionDto> getSessionById(Long id) {
        return sessionRepository.findById(id).map(sessionMapper::toDto);
    }


    @Transactional
    public SessionDto createOrUpdateSession(SessionDto sessionDto , UserApp userApp) throws AccessDeniedException {
//        if (!permissionService.canCreateSession(sessionDto.courId(),userApp)) {
//            throw new AccessDeniedException("User does not have permission to create session");
//        }
        Session session = sessionMapper.toEntity(sessionDto);

        if (session.getId() == null) { // New session
            session.setCreatedBy(userApp);
        }

        session.setApproved(true);
        Session session1 = sessionRepository.save(session);
        if(userApp instanceof ChargeFormation)
        {
            session1.setApproved(false);
            sessionRepository.save(session1);
        }

        return sessionMapper.toDto(session1);
    }


    @Transactional
    public void deleteSession(Long id) {
        sessionRepository.deleteById(id);
    }

    public List<SectionDto> getSectionsBySessionId(Long sessionId , UserApp userApp) {
        Optional<Session> session = sessionRepository.findById(sessionId);
        return session.map(value -> value.getSections().stream().filter(section -> (section.isApproved() || (Objects.equals(section.getCreatedBy().getUserId(), userApp.getUserId()) && !section.isSent())) || section.isSent() && section.getSession().getCour().getEntreprise().getResponsableFormations().stream().anyMatch(res -> Objects.equals(res.getUserId(),userApp.getUserId())) ).distinct().map(sectionMapper::toDto).collect(Collectors.toList())).orElse(Collections.emptyList());
    }

    public List<Session> findSessionsByCourId(Long aLong) {
        return sessionRepository.findSessionsByCourId(aLong);
    }
}
