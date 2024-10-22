package e_learning.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.DTO.CourDto;
import e_learning.DTO.SessionDto;
import e_learning.entity.*;
import e_learning.enums.Langue;
import e_learning.exceptions.FileStorageException;
import e_learning.exceptions.ResourceNotFoundException;
import e_learning.mappers.mappersImpl.CourMapper;
import e_learning.repositories.*;
import e_learning.services.ServiceImpl.CourService;
import e_learning.services.ServiceImpl.FileStorageService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/cours")
@AllArgsConstructor
public class CourController {
    private final CourService courService;
    private final UserAppRepository userAppRepository;
    private final ClassEntityRepository classEntityRepository;
    private final FileStorageService fileStorageService;
    private final CourRepository courRepository;
    private final FormateurRepository formateurRepository;
    private final CourMapper courMapper;
    private final ActionApprovalRepository actionApprovalRepository;
    private final SessionRepository sessionRepository;
    private final SectionRepository sectionRepository;

    @GetMapping
    public List<CourDto> getAllCours() {
        return courService.getAllCours();
    }

    @GetMapping("/{id}")
    public ResponseEntity<CourDto> getCourById(@PathVariable Long id) {
        Optional<CourDto> cour = courService.getCourById(id);
        return cour.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createCour(
            @RequestParam("titre") String titre,
            @RequestParam("subTitre") String subTitre,
            @RequestParam("description") String description,
            @RequestParam("langue") String langue,
            @RequestParam("duree") int duree,
            @RequestPart(value = "image" , required = false) MultipartFile image,
            @RequestParam(value = "formateurIds", required = false) String formateurIdsJson,  // Change here
            @RequestParam(value = "classIds", required = false) String classIdsJson,  // Change here
            Principal principal
    ) throws FileStorageException, JsonProcessingException {


        UserApp responsableFormation =  userAppRepository.findUserAppByEmail(principal.getName());
        String imagePath = null;
        if(image != null) {
             imagePath = fileStorageService.storeFile(image);
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<Long> formateurIds = objectMapper.readValue(formateurIdsJson, new TypeReference<List<Long>>() {});
        List<Long> classIds = objectMapper.readValue(classIdsJson, new TypeReference<List<Long>>() {});

        CourDto courDto = new CourDto(
                null, titre, subTitre, description, new Date(), new Date(), langue.toUpperCase(), duree, true,false,
                responsableFormation.getUserId(), formateurIds, classIds, null, imagePath,false);

        CourDto newCourDto = courService.createOrUpdateCour(courDto, responsableFormation);
        if(responsableFormation.getRoles().stream().allMatch((role) -> role != null && "CHARGE".equalsIgnoreCase(String.valueOf(role.getRole())))) {
            return ResponseEntity.ok("The Course that you created needs approval from the responsable formation");

        }
        return ResponseEntity.ok(newCourDto);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public CourDto updateCour(
            @PathVariable Long id,
            @RequestParam("titre") String titre,
            @RequestParam("subTitre") String subTitre,
            @RequestParam("description") String description,
            @RequestParam("langue") String langue,
            @RequestParam("duree") int duree,
            @RequestPart(value = "image", required = false) MultipartFile image,
            @RequestParam(value = "formateurIds", required = false) String formateurIdsJson,
            @RequestParam(value = "classIds", required = false) String classIdsJson,
            Principal principal
    ) throws FileStorageException, JsonProcessingException, BadRequestException {

        // Validate language input
        Langue courseLangue;
        try {
            courseLangue = Langue.valueOf(langue.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid language value provided: " + langue);
        }

        // Handle image upload
        String imagePath = null;
        if (image != null) {
            imagePath = fileStorageService.storeFile(image);
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
        }

        // Parse formateurIds and classIds (handling possible null or empty values)
        ObjectMapper objectMapper = new ObjectMapper();
        List<Long> formateurIds = formateurIdsJson != null ? objectMapper.readValue(formateurIdsJson, new TypeReference<List<Long>>() {}) : new ArrayList<>();
        List<Long> classIds = classIdsJson != null ? objectMapper.readValue(classIdsJson, new TypeReference<List<Long>>() {}) : new ArrayList<>();

        // Fetch the existing course
        Optional<Cour> courOptional = courRepository.findById(id);
        if (courOptional.isEmpty()) {
            throw new ResourceNotFoundException("Course not found with id " + id);
        }
        Cour existingCour = courOptional.get();


        // Update course fields
        existingCour.setTitre(titre);
        existingCour.setSubTitre(subTitre);
        existingCour.setDescription(description);
        existingCour.setLangue(courseLangue);
        existingCour.setDuree(duree);
        if (imagePath != null) {
            existingCour.setImagePath(imagePath);
        }
        existingCour.setMiseAJour(new Date());

        // Update formateurs and classes (ensure they exist)
        List<Formateur> formateurs = formateurIds.stream()
                .map(userId -> formateurRepository.findById(userId)
                        .orElseThrow(() -> new ResourceNotFoundException("Formateur not found with id " + userId)))
                .collect(Collectors.toList());

        List<ClassEntity> classes = classIds.stream()
                .map(classId -> classEntityRepository.findById(classId)
                        .orElseThrow(() -> new ResourceNotFoundException("Class not found with id " +classId)))
                .collect(Collectors.toList());

        existingCour.setFormateurs(formateurs);
        existingCour.setClasses(classes);

        // Save updated course
        Cour updatedCour = courRepository.save(existingCour);
        return courMapper.toDto(updatedCour);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCour(@PathVariable Long id) {
        courService.deleteCour(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/responsables/{id}")
    public List<CourDto> getCoursByResponsableFormationId(@PathVariable Long id) {
        return courService.getCoursByResponsableFormationId(id);
    }

    @GetMapping("/charge-formation/{id}")
    public List<CourDto> getCoursByChargeFormation(@PathVariable Long id) {
        return courService.getCoursByChargeFormation(id);
    }
    @GetMapping("/formateurs/{id}")
    public List<CourDto> getCoursByFormateurId(@PathVariable Long id) {
        List<CourDto> courDtos = courService.getCoursByFormateurId(id);
        return courDtos;
    }

    @GetMapping("/participants/{id}")
    public List<CourDto> getCoursByParticipantId(@PathVariable Long id) {
        List<CourDto> courDtos = courService.getCoursByParticipant(id);
        return courDtos;
    }

    @GetMapping("/responsableFormation/email/{email}")
    public List<CourDto> getCoursByResponsableFormationEmail(@PathVariable String email) {
        return courService.getCoursByResponsableFormationEmail(email);
    }

    @GetMapping("/classes/{id}")
    public List<CourDto> getCoursByClasses(@PathVariable Long id) {
        ClassEntity classEntity = classEntityRepository.findById(id).orElseThrow(() -> new RuntimeException("Class not found"));
        return courService.getCoursByClasses(classEntity);
    }

    @GetMapping("/{id}/sessions")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getSessionsByCourId(@PathVariable Long id, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if(userApp == null)
        {
            return ResponseEntity.badRequest().body("User not found");
        }
        List<SessionDto> sessions = courService.getSessionsByCourId(id ,userApp);
        if (sessions.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(sessions);
    }


    @PatchMapping("/{id}/block")
    public void blockCour(@PathVariable Long id) {
        courService.blockCour(id);
    }

    @PatchMapping("/{id}/unblock")
    public void unblockCour(@PathVariable Long id) {
        courService.unblockCour(id);
    }

    @GetMapping("/favorites")
    public List<CourDto> getFavorites() {
        return courService.getAllFavoriteCours();
    }

    @PostMapping("/favorites/{id}")
    public void addFavorite(@PathVariable Long id) {
        courService.handleFavorite(id);
    }

    @PostMapping("/sendRequest/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> sendRequest(@PathVariable Long id ,Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if(!(userApp instanceof ChargeFormation))
        {
           return ResponseEntity.badRequest().body("You are not authorized to send request");
        }

        Cour cour = courRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Course not found with id " + id));
        cour.setSent(true);
        cour.getSessions().forEach(session -> {
            session.setSent(true);
            sessionRepository.save(session);
            session.getSections().forEach(section -> {
                section.setSent(true);
                sectionRepository.save(section);
            });
        });
        courRepository.save(cour);
        ActionApproval actionApproval = new ActionApproval();
        actionApproval.setApproved(false);
        actionApproval.setObjectId(cour.getId());
        actionApproval.setActionType("COURSE");
        actionApproval.setCreatedDate(LocalDateTime.now());
        actionApproval.setChargeFormationId(userApp.getUserId());
        actionApproval.setEntrepriseId(cour.getEntreprise().getId());

        actionApprovalRepository.save(actionApproval);

        return ResponseEntity.ok("Request sent successfully");
    }

}
