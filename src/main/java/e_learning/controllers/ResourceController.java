package e_learning.controllers;


import e_learning.DTO.ResourceDto;
import e_learning.entity.ChargeFormation;
import e_learning.entity.Option;
import e_learning.entity.ResourceEntity;
import e_learning.entity.UserApp;
import e_learning.exceptions.FileStorageException;
import e_learning.repositories.ResourceRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.ResourceService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.AccessDeniedException;

import org.springframework.core.io.Resource ;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.Principal;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@RestController
@RequestMapping("/resources")
@AllArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;
    private final UserAppRepository userAppRepository;
    private final ResourceRepository resourceRepository;

    @GetMapping
    public ResponseEntity<List<ResourceDto>> getAllResources() {
        List<ResourceDto> resourceDtos = resourceService.getAllResources();
        return ResponseEntity.ok(resourceDtos);
    }

    @PostMapping("/upload/{sectionId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadResource(@PathVariable Long sectionId, @RequestParam("file") MultipartFile file, Principal principal) throws AccessDeniedException, FileStorageException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        ResourceDto savedResource = resourceService.uploadResource(sectionId, file, userApp);
        return ResponseEntity.ok(savedResource);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Resource> getResource(@PathVariable Long id) throws FileStorageException, IOException {
        Resource resource = resourceService.loadResourceAsSpringResource(id);
        Path path = Paths.get(resource.getFile().getAbsolutePath());
        String contentType = Files.probeContentType(path);

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);
    }

    @GetMapping("{id}/dto")
    public ResponseEntity<ResourceDto> getResourceDto(@PathVariable Long id) {
        ResourceDto resourceDto = resourceService.getResourceDto(id);
        return ResponseEntity.ok(resourceDto);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteResource(@PathVariable Long id,Principal principal) throws FileStorageException {

        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        Optional<ResourceEntity> responseEntity = resourceRepository.findById(id);

        if (userApp == null) {
            return ResponseEntity.status(403).body("User not found");
        }

        if(responseEntity.isEmpty())
        {
            return ResponseEntity.notFound().build();
        }

        if(!Objects.equals(userApp.getUserId(), responseEntity.get().getCreatedBy().getUserId()))
        {
            return ResponseEntity.status(403).body("You are not allowed to delete this resource as you are not the creator");
        }

        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/section/{sectionId}/dtos")
    public ResponseEntity<List<ResourceDto>> getResourceDtosBySectionId(@PathVariable Long sectionId) {
        List<ResourceDto> resourceDtos = resourceService.getResourcesBySectionId(sectionId);
        return ResponseEntity.ok(resourceDtos);
    }

    @GetMapping("/library/{libraryId}/dtos")
    public ResponseEntity<List<ResourceDto>> getResourceDtosByLibraryId(@PathVariable Long libraryId) {
        List<ResourceDto> resourceDtos = resourceService.getResourcesByLibraryId(libraryId);
        return ResponseEntity.ok(resourceDtos);
    }
//
//    @GetMapping("/section/{sectionId}/files")
//    public ResponseEntity<List<Resource>> getResourcesAsSpringResourcesBySectionId(@PathVariable Long sectionId) throws FileStorageException {
//        List<Resource> resources = resourceService.loadResourcesAsSpringResourcesBySectionId(sectionId);
//        return ResponseEntity.ok(resources);
//    }
//
//    @GetMapping("/library/{libraryId}/files")
//    public ResponseEntity<List<Resource>> getResourcesAsSpringResourcesByLibraryId(@PathVariable Long libraryId) throws FileStorageException {
//        List<Resource> resources = resourceService.loadResourcesAsSpringResourcesByLibraryId(libraryId);
//        return ResponseEntity.ok(resources);
//    }
//
    @PostMapping("/{resourceId}/add-to-library/{libraryId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ResourceDto> addResourceToLibraryById(@PathVariable Long resourceId, @PathVariable Long libraryId) throws FileStorageException {
        ResourceDto resourceDto = resourceService.addResourceToLibraryById(resourceId, libraryId);
        return ResponseEntity.ok(resourceDto);
    }

    @PostMapping("/library/{libraryId}/upload-or-update")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> uploadOrUpdateResourceByLibraryId(@PathVariable Long libraryId, @RequestParam("file") MultipartFile file, Principal principal) throws FileStorageException, AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        ResourceDto resourceDto = resourceService.uploadOrUpdateResourceByLibraryId(libraryId, file, userApp);

        if(userApp instanceof ChargeFormation)
        {
            return ResponseEntity.ok("Resource needs approval");
        }
        return ResponseEntity.ok(resourceDto);
    }

    @DeleteMapping("/library/{libraryId}/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeResourceFromLibrary(@PathVariable Long libraryId, @PathVariable Long resourceId, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        resourceService.removeResourceFromLibrary(resourceId, libraryId, userApp);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/section/{sectionId}/resource/{resourceId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeResourceFromSection(@PathVariable Long sectionId, @PathVariable Long resourceId, Principal principal) throws AccessDeniedException {
        UserApp userApp = userAppRepository.findUserAppByEmail(principal.getName());
        if (userApp == null) {
            throw new AccessDeniedException("User not found");
        }
        resourceService.removeResourceFromSection(resourceId, sectionId, userApp);
        return ResponseEntity.noContent().build();
    }
}
