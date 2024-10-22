package e_learning.mappers.mappersImpl;

import e_learning.DTO.ClassEntityDto;
import e_learning.DTO.SessionDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.Mapping;

@Component
@AllArgsConstructor
public class SessionMapper {

    private final CourRepository courRepository;

    private final SectionRepository sectionRepository;
    private final VideoConferenceRepository videoConferenceRepository;

    public SessionDto toDto(Session session) {
        return SessionDto.builder()
                .id(session.getId())
                .name(session.getName())
                .sessionDate(session.getSessionDate())
                .duree(session.getDuree())
                .startDate(session.getStartDate())
                .endDate(session.getEndDate())
                .isApproved(session.isApproved())
                .isSent(session.isSent())
                .courId(session.getCour() != null ? session.getCour().getId() : null)
                .createdById(session.getCreatedBy() != null ? session.getCreatedBy().getUserId() : null)
                .sectionIds(session.getSections() != null ? session.getSections().stream().map(Section::getId).collect(Collectors.toList()) : Collections.emptyList())
                .videoConferenceIds(session.getVideoConferences() != null ? session.getVideoConferences().stream().map(VideoConference::getId).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }

    public Session toEntity(SessionDto sessionDto) {
        Session session = new Session();
        session.setId(sessionDto.id());
        session.setName(sessionDto.name());
        session.setSessionDate(sessionDto.sessionDate());
        session.setDuree(sessionDto.duree());
        session.setStartDate(sessionDto.startDate());
        session.setEndDate(sessionDto.endDate());
        session.setApproved(sessionDto.isApproved());
        session.setSent(sessionDto.isSent());

        if (sessionDto.courId() != null) {
            Cour cour = courRepository.findById(sessionDto.courId())
                    .orElseThrow(() -> new EntityNotFoundException("Cour not found"));
            session.setCour(cour);
        }

        if (sessionDto.sectionIds() != null && !sessionDto.sectionIds().isEmpty()) {
            List<Section> sections = sectionRepository.findAllById(sessionDto.sectionIds());
            session.setSections(sections);
        } else {
            session.setSections(Collections.emptyList());
        }

        if (sessionDto.videoConferenceIds() != null && !sessionDto.videoConferenceIds().isEmpty()) {
            List<VideoConference> videoConferences = videoConferenceRepository.findAllById(sessionDto.videoConferenceIds());
            session.setVideoConferences(videoConferences);
        } else {
            session.setVideoConferences(Collections.emptyList());
        }

        return session;
    }
}
