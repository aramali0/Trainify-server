package e_learning.repositories;

import e_learning.entity.VideoConference;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VideoConferenceRepository extends JpaRepository<VideoConference, Long> {
    List<VideoConference> findBySessionId(Long sessionId);
}
