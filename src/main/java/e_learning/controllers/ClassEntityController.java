package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.ChargeFormation;
import e_learning.entity.ClassEntity;
import e_learning.entity.UserApp;
import e_learning.repositories.ClassEntityRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.ClassEntityService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/classes")
@AllArgsConstructor
public class ClassEntityController {

    private final ClassEntityService classEntityService;
    private final UserAppRepository userAppRepository;
    private final ClassEntityRepository classEntityRepository;

    @GetMapping
    public ResponseEntity<List<ClassEntityDto>> getAllClasses() {
        List<ClassEntityDto> classEntities = classEntityService.getAllClasses();
        return ResponseEntity.ok(classEntities);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ClassEntityDto> getClassById(@PathVariable Long id) {
        Optional<ClassEntityDto> classEntityDto = classEntityService.getClassById(id);
        return classEntityDto.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createClass(@RequestBody ClassEntityDto classEntityDto, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        ClassEntityDto createdClass = classEntityService.createClass(classEntityDto,userApp);
        if(userApp instanceof ChargeFormation)
        {
            return ResponseEntity.ok("Class needs approval");
        }
        return ResponseEntity.ok(createdClass);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ClassEntityDto> updateClass(@PathVariable Long id, @RequestBody ClassEntityDto classEntityDto) {
        Optional<ClassEntityDto> updatedClass = classEntityService.updateClass(id, classEntityDto);
        return updatedClass.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteClass(@PathVariable Long id, Principal principal) {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        Optional<ClassEntity> classEntity = classEntityRepository.findById(id);

        if (classEntity.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        if(!Objects.equals(userApp.getUserId(), classEntity.get().getCreatedBy().getUserId()))
        {
            return ResponseEntity.status(403).body("You are not allowed to delete this class as you are not the creator");
        }

        classEntityService.deleteClass(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{classId}/courses/{courId}")
    public ResponseEntity<ClassEntityDto> addClassToCour(@PathVariable Long classId, @PathVariable Long courId) {
        Optional<ClassEntityDto> updatedClass = classEntityService.addClassToCour(classId, courId);
        return updatedClass.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{classId}/participants/{participantId}")
    public ResponseEntity<ClassEntityDto> addParticipantToClass(@PathVariable Long classId, @PathVariable Long participantId) {
        Optional<ClassEntityDto> updatedClass = classEntityService.addParticipantToClass(classId, participantId);
        return updatedClass.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/{classId}/participants")
    public ResponseEntity<ClassEntityDto> addParticipantsToClass(@PathVariable Long classId, @RequestBody List<Long> participantIds) {
        Optional<ClassEntityDto> updatedClass = classEntityService.addParticipantsToClass(classId, participantIds);
        return updatedClass.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/participant/{participantId}")
    public ResponseEntity<List<ClassEntityDto>> getClassesByParticipant(@PathVariable Long participantId) {
        List<ClassEntityDto> classEntities = classEntityService.getClassesByParticipant(participantId);
        return ResponseEntity.ok(classEntities);
    }

    @GetMapping("/cour/{courId}")
    public ResponseEntity<List<ClassEntityDto>> getClassesByCour(@PathVariable Long courId) {
        List<ClassEntityDto> classEntities = classEntityService.getClassesByCour(courId);
        return ResponseEntity.ok(classEntities);
    }

    @GetMapping("/{classId}/active-users")
    public ResponseEntity<List<UserDTO>> getActiveUsersByClass(@PathVariable Long classId) {
        List<UserDTO> activeUserDtos = classEntityService.getActiveUsersByClass(classId);
        return ResponseEntity.ok(activeUserDtos);
    }


}
