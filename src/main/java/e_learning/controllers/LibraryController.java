package e_learning.controllers;

import e_learning.DTO.LibraryDto;
import e_learning.DTO.ResourceDto;
import e_learning.entity.ChargeFormation;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.LibraryService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/libraries")
@AllArgsConstructor
public class LibraryController {

    private final LibraryService libraryService;
    private final UserAppRepository userAppRepository;


    // Example endpoint in your LibraryController
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createLibrary(@RequestBody LibraryDto libraryDto, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        LibraryDto createdLibrary = libraryService.saveLibrary(libraryDto,userApp);
        if(userApp instanceof ChargeFormation)
        {
            return ResponseEntity.ok("Library needs approval");
        }
        return ResponseEntity.ok(createdLibrary);
    }

    @GetMapping
    public ResponseEntity<List<LibraryDto>> getAllLibraries() {
        return ResponseEntity.ok(libraryService.getAllLibraries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LibraryDto> getLibraryById(@PathVariable Long id) {
        Optional<LibraryDto> library = libraryService.getLibraryById(id);
        return library.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/cour/{courId}")
    public ResponseEntity<List<LibraryDto>> getLibrariesByCourId(@PathVariable Long courId) {
        return ResponseEntity.ok(libraryService.getLibrariesByCourId(courId));
    }

    @GetMapping("/responsable/{responsableId}")
    public ResponseEntity<List<LibraryDto>> getLibrariesByResponsableId(@PathVariable Long responsableId) {
        return ResponseEntity.ok(libraryService.getResourcesByResponsableId(responsableId));
    }

    @GetMapping("/responsable/{responsableId}/resources")
    public ResponseEntity<List<ResourceDto>> getResourcesByResponsableId(@PathVariable Long responsableId) {
        return ResponseEntity.ok(libraryService.getResourcesByResponsableFormationId(responsableId));
    }

    @GetMapping("/charge-formation/{responsableId}")
    public ResponseEntity<List<LibraryDto>> getLibrariesByChargeFormation(@PathVariable Long responsableId) {
        return ResponseEntity.ok(libraryService.getResourcesByChargeFormationId(responsableId));
    }

    @GetMapping("/charge-formation/{responsableId}/resources")
    public ResponseEntity<List<ResourceDto>> getResourcesByChargeFormation(@PathVariable Long responsableId) {
        return ResponseEntity.ok(libraryService.getResourcesByResponsableChargeFormationId(responsableId));
    }
    @GetMapping("/formateur/{formateurId}")
    public ResponseEntity<List<LibraryDto>> getLibririesByFormateurId(@PathVariable Long formateurId) {
        return ResponseEntity.ok(libraryService.getResourcesByFormateurId(formateurId));
    }

  @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<LibraryDto>> getResourceByParticipant(@PathVariable Long participantId) {
        return ResponseEntity.ok(libraryService.getResourcesByParticipant(participantId));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteLibrary(@PathVariable Long id, Principal principal) {

        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        Optional<LibraryDto> library = libraryService.getLibraryById(id);

        if(library.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        if(!userApp.getUserId().equals(library.get().createdBy())) {
            return ResponseEntity.status(403).body("You are not allowed to delete this library as you are not the creator");
        }

        libraryService.deleteLibrary(id);
        return ResponseEntity.noContent().build();
    }
}
