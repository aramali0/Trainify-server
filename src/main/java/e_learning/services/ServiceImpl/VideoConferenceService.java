package e_learning.services.ServiceImpl;


import e_learning.DTO.VideoConferenceDto;
import e_learning.entity.Session;
import e_learning.entity.VideoConference;
import e_learning.repositories.SessionRepository;
import e_learning.repositories.VideoConferenceRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class VideoConferenceService {
    private final VideoConferenceRepository videoConferenceRepository;
    private final SessionRepository sessionRepository;

    public VideoConferenceDto createOrUpdateVideoConference(VideoConferenceDto dto) {
        Optional<Session> sessionOpt = sessionRepository.findById(dto.sessionId());

        if (sessionOpt.isEmpty()) {
            throw new RuntimeException("Session not found");
        }

        VideoConference videoConference = new VideoConference();
        videoConference.setTitle(dto.title());
        videoConference.setPlatform(dto.platform());
        videoConference.setUrl(dto.url());
        videoConference.setStartTime(dto.startTime());
        videoConference.setSession(sessionOpt.get());

        videoConference = videoConferenceRepository.save(videoConference);

        return VideoConferenceDto.builder()
                .id(videoConference.getId())
                .title(videoConference.getTitle())
                .platform(videoConference.getPlatform())
                .url(videoConference.getUrl())
                .startTime(videoConference.getStartTime())
                .sessionId(videoConference.getSession().getId())
                .build();
    }

    public List<VideoConferenceDto> getVideoConferencesBySessionId(Long sessionId) {
        return videoConferenceRepository.findBySessionId(sessionId).stream().map(conference ->
                VideoConferenceDto.builder()
                        .id(conference.getId())
                        .title(conference.getTitle())
                        .platform(conference.getPlatform())
                        .url(conference.getUrl())
                        .startTime(conference.getStartTime())
                        .sessionId(conference.getSession().getId())
                        .build()
        ).collect(Collectors.toList());
    }
}
