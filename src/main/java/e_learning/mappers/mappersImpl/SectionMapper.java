package e_learning.mappers.mappersImpl;

import e_learning.DTO.ClassEntityDto;
import e_learning.DTO.SectionDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SectionMapper {

    private final SessionRepository sessionRepository;

    private final QuestionRepository questionRepository;

    private final ResourceRepository resourceRepository;
    private final UserAppRepository userAppRepository;
    private final QuizRepository quizRepository;

    public SectionDto toDto(Section section) {
        return new SectionDto(
                section.getId() != null ? section.getId() : null,
                section.getTitle(),
                section.getContent(),
                section.getCreatedAt(),
                section.getStartDate(),
                section.getEndDate(),
                section.isApproved(),
                section.isSent(),
                section.getSession() != null ? section.getSession().getId() : null,
                section.getCreatedBy() != null ? section.getCreatedBy().getUserId() : null,
                section.getResources() != null ? section.getResources().stream().map(ResourceEntity::getId).collect(Collectors.toList()) : null,
                section.getQuizzes() != null ? section.getQuizzes().stream().map(Quiz::getId).collect(Collectors.toList()) : null
        );
    }

    public Section toEntity(SectionDto sectionDto) {
        Section section = new Section();
        if(sectionDto.id() != null) section.setId(sectionDto.id());
        section.setTitle(sectionDto.title());
        section.setContent(sectionDto.content());
        section.setApproved(sectionDto.isApproved());
        section.setSent(sectionDto.isSent());
        section.setCreatedBy(userAppRepository.findById(sectionDto.createdById()).orElse(null)); ;

        if (sectionDto.sessionId() != null) {
            Session session = sessionRepository.findById(sectionDto.sessionId()).orElse(null);
            section.setSession(session);
        }

        if(sectionDto.resourceIds() != null) {
            List<ResourceEntity> resources = resourceRepository.findAllById(sectionDto.resourceIds());
            section.setResources(resources);
        }

        if(sectionDto.quizIds() != null) {
            List<Quiz> quizzes = quizRepository.findAllById(sectionDto.quizIds());
            section.setQuizzes(quizzes);
        }

        section.setCreatedAt(sectionDto.createdAt());
        section.setStartDate(sectionDto.startDate());
        section.setEndDate(sectionDto.endDate());
        return section;
    }
}
