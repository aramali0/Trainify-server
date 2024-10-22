package e_learning.controllers;

import e_learning.DTO.VideoConferenceDto;
import e_learning.services.ServiceImpl.VideoConferenceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/video-conferences")
@AllArgsConstructor
public class VideoConferenceController {
    private final VideoConferenceService videoConferenceService;

    @PostMapping
    public ResponseEntity<VideoConferenceDto> createOrUpdateVideoConference(@RequestBody VideoConferenceDto videoConferenceDto) {
        VideoConferenceDto created = videoConferenceService.createOrUpdateVideoConference(videoConferenceDto);
        return ResponseEntity.ok(created);
    }

    @GetMapping("/session/{sessionId}")
    public ResponseEntity<List<VideoConferenceDto>> getVideoConferencesBySessionId(@PathVariable Long sessionId) {
        List<VideoConferenceDto> conferences = videoConferenceService.getVideoConferencesBySessionId(sessionId);
        return ResponseEntity.ok(conferences);
    }
}
