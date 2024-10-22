package e_learning.controllers;

import e_learning.entity.Event;
import e_learning.repositories.EventRepository;
import e_learning.services.ServiceImpl.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/events")
@AllArgsConstructor
public class EventController {
    private final EventRepository eventRepository;
    private final FileStorageService fileStorageService;

    @GetMapping
    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Event> getEventById(@PathVariable Long id) {
        Optional<Event> event = eventRepository.findById(id);
        return event.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public Event createEvent(
            @RequestParam("title_en") String titleEn,
            @RequestParam("description_en") String descriptionEn,
            @RequestParam("title_fr") String titleFr,
            @RequestParam("description_fr") String descriptionFr,
            @RequestParam("title_ar") String titleAr,
            @RequestParam("description_ar") String descriptionAr,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        String imagePath = null;
        if (image != null) {
            imagePath = fileStorageService.storeFile(image);
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
        }
        Event event = new Event();
        event.setTitle_en(titleEn);
        event.setDescription_en(descriptionEn);
        event.setTitle_fr(titleFr);
        event.setDescription_fr(descriptionFr);
        event.setTitle_ar(titleAr);
        event.setDescription_ar(descriptionAr);
        event.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        event.setLocation(location);
        event.setImagePath(imagePath);
        return eventRepository.save(event);
    }

    @PutMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public Event updateEvent(
            @PathVariable Long id,
            @RequestParam("title_en") String titleEn,
            @RequestParam("description_en") String descriptionEn,
            @RequestParam("title_fr") String titleFr,
            @RequestParam("description_fr") String descriptionFr,
            @RequestParam("title_ar") String titleAr,
            @RequestParam("description_ar") String descriptionAr,
            @RequestParam("date") String date,
            @RequestParam("location") String location,
            @RequestPart(value = "image", required = false) MultipartFile image
    ) throws Exception {
        Event event = eventRepository.findById(id).orElseThrow(() -> new RuntimeException("Event not found"));
        if (image != null) {
            String imagePath = fileStorageService.storeFile(image);
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            event.setImagePath(imagePath);
        }
        event.setTitle_en(titleEn);
        event.setDescription_en(descriptionEn);
        event.setTitle_fr(titleFr);
        event.setDescription_fr(descriptionFr);
        event.setTitle_ar(titleAr);
        event.setDescription_ar(descriptionAr);
        event.setDate(new SimpleDateFormat("yyyy-MM-dd").parse(date));
        event.setLocation(location);
        return eventRepository.save(event);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id) {
        eventRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
