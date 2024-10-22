package e_learning.services.ServiceImpl;
import e_learning.DTO.ResourceDto;
import e_learning.entity.*;
import e_learning.exceptions.FileStorageException;
import e_learning.mappers.mappersImpl.ResourceMapper;
import e_learning.mappers.mappersImpl.SectionMapper;
import e_learning.repositories.ActionApprovalRepository;
import e_learning.repositories.LibraryRepository;
import e_learning.repositories.ResourceRepository;
import e_learning.repositories.SectionRepository;
import e_learning.security.FileStorageProperties;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.util.StringUtils;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.Resource;
import java.io.IOException;
import java.nio.file.AccessDeniedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;
    private final PermissionService permissionService;
    private final ResourceLoader resourceLoader;
    private final SectionRepository sectionRepository;

    private final Path fileStorageLocation;
    private final LibraryRepository libraryRepository;
    private final ActionApprovalRepository actionApprovalRepository;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, ResourceMapper resourceMapper, PermissionService permissionService, FileStorageProperties fileStorageProperties, ResourceLoader resourceLoader, SectionRepository sectionRepository, LibraryRepository libraryRepository, ActionApprovalRepository actionApprovalRepository) throws FileStorageException {
        this.resourceRepository = resourceRepository;
        this.resourceMapper = resourceMapper;
        this.permissionService = permissionService;
        this.resourceLoader = resourceLoader;
        this.sectionRepository = sectionRepository;
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored.");
        }
        this.libraryRepository = libraryRepository;
        this.actionApprovalRepository = actionApprovalRepository;
    }

    @Transactional
    public ResourceDto uploadResource(Long sectionId, MultipartFile file, UserApp userApp) throws AccessDeniedException, FileStorageException {
        if (!permissionService.canAddResourceOrEvaluation(sectionId, userApp)) {
            throw new AccessDeniedException("User does not have permission to manage resources");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));

        try {
            if (fileName.contains("..")) {
                throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
            }

            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation);
            Section section = sectionRepository.findById(sectionId).orElseThrow(() -> new EntityNotFoundException("Section not found"));

            ResourceEntity resource = new ResourceEntity();
            resource.setTitle(fileName);
            resource.setType(file.getContentType());
            resource.setFilePath(targetLocation.toString());
            resource.setSection(section);

            resource = resourceRepository.save(resource);
            return resourceMapper.toDto(resource);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    public Resource loadResourceAsSpringResource(Long id) throws FileStorageException {
        ResourceEntity resource = resourceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        try {
            Path filePath = Paths.get(resource.getFilePath()).normalize();
            Resource springResource = resourceLoader.getResource("file:" + filePath.toString());
            if (springResource.exists()) {
                return springResource;
            } else {
                throw new FileStorageException("File not found " + resource.getFilePath());
            }
        } catch (Exception ex) {
            throw new FileStorageException("File not found " + resource.getFilePath());
        }
    }

    @Transactional
    public void deleteResource(Long id) throws FileStorageException {
        ResourceEntity resource = resourceRepository.findById(id).orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        Path filePath = Paths.get(resource.getFilePath()).normalize();

        try {
            Files.deleteIfExists(filePath);
        } catch (IOException ex) {
            throw new FileStorageException("Could not delete file " + filePath.toString());
        }

        resourceRepository.delete(resource);
    }

    public List<ResourceDto> getResourcesBySectionId(Long sectionId) {
        List<ResourceEntity> resources = resourceRepository.findBySectionId(sectionId);
        return resources.stream()
                .filter(ResourceEntity::isApproved)
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<ResourceDto> getResourcesByLibraryId(Long libraryId) {
        List<ResourceEntity> resources = resourceRepository.findByLibraryId(libraryId);
        return resources.stream()
                .filter(ResourceEntity::isApproved)
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    public List<Resource> loadResourcesAsSpringResourcesBySectionId(Long sectionId) throws FileStorageException {
        List<ResourceEntity> resources = resourceRepository.findBySectionId(sectionId);
        return resources.stream()
                .map(resource -> {
                    try {
                        return loadResourceAsSpringResource(resource.getId());
                    } catch (FileStorageException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }

    public List<Resource> loadResourcesAsSpringResourcesByLibraryId(Long libraryId) throws FileStorageException {
        List<ResourceEntity> resources = resourceRepository.findByLibraryId(libraryId);
        return resources.stream()
                .map(resource -> {
                    try {
                        return loadResourceAsSpringResource(resource.getId());
                    } catch (FileStorageException e) {
                        throw new RuntimeException(e);
                    }
                })
                .collect(Collectors.toList());
    }
    @Transactional
    public ResourceDto addResourceToLibraryById(Long resourceId, Long libraryId) throws FileStorageException {
        ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));

        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new EntityNotFoundException("Library not found"));

        resource.setLibrary(library); // Assuming ResourceEntity has a library field
        resource = resourceRepository.save(resource);

        return resourceMapper.toDto(resource);
    }

    @Transactional
    public ResourceDto uploadOrUpdateResourceByLibraryId(Long libraryId, MultipartFile file, UserApp userApp) throws FileStorageException, AccessDeniedException {
        // Check if the user has permission to add or modify resources in the library
        if (!permissionService.canAddResourceOrEvaluationToLibrary(libraryId, userApp)) {
            throw new AccessDeniedException("User does not have permission to manage resources in this library");
        }

        String fileName = StringUtils.cleanPath(Objects.requireNonNull(file.getOriginalFilename()));
        Library library = libraryRepository.findById(libraryId)
                .orElseThrow(() -> new EntityNotFoundException("Library not found"));

        // Check if a resource with the same file name already exists in the library
        ResourceEntity existingResource = resourceRepository.findByTitleAndLibraryId(fileName, libraryId);

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);

            if (existingResource != null) {
                // Resource exists, update library association if needed
                existingResource.setLibrary(library);
                existingResource.setFilePath(targetLocation.toString());
                existingResource = resourceRepository.save(existingResource);
            } else {
                // Resource does not exist, create a new one
                if (fileName.contains("..")) {
                    throw new FileStorageException("Sorry! Filename contains invalid path sequence " + fileName);
                }

                Files.copy(file.getInputStream(), targetLocation);

                ResourceEntity resource = new ResourceEntity();
                resource.setTitle(fileName);
                resource.setType(file.getContentType());
                resource.setFilePath(targetLocation.toString());
                resource.setLibrary(library);
                resource.setApproved(true);
                resource.setCreatedBy(userApp);
                existingResource = resourceRepository.save(resource);

                if(userApp instanceof ChargeFormation)
                {
                    ActionApproval actionApproval = new ActionApproval();
                    actionApproval.setApproved(false);
                    actionApproval.setObjectId(resource.getId());
                    actionApproval.setActionType("RESOURCE");
                    actionApproval.setCreatedDate(LocalDateTime.now());
                    actionApproval.setChargeFormationId(userApp.getUserId());
                    actionApproval.setEntrepriseId(resource.getLibrary().getCour().getEntreprise().getId());

                    actionApprovalRepository.save(actionApproval);
                    resource.setApproved(false);
                    resourceRepository.save(resource);

                }

            }


            return resourceMapper.toDto(existingResource);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!");
        }
    }

    @Transactional
    public void removeResourceFromLibrary(Long resourceId, Long libraryId, UserApp userApp) throws AccessDeniedException {

        if (!permissionService.canAddResourceOrEvaluationToLibrary(libraryId, userApp)) {
            throw new AccessDeniedException("User does not have permission to manage resources in this library");
        }

        ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));

        if (!libraryId.equals(resource.getLibrary().getId())) {
            throw new IllegalArgumentException("Resource does not belong to the specified library");
        }

        resource.setLibrary(null);
        resourceRepository.save(resource);
    }

    @Transactional
    public void removeResourceFromSection(Long resourceId, Long sectionId, UserApp userApp) throws AccessDeniedException {

        if (!permissionService.canAddResourceOrEvaluation(sectionId, userApp)) {
            throw new AccessDeniedException("User does not have permission to manage resources");
        }

        ResourceEntity resource = resourceRepository.findById(resourceId)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));

        if (!sectionId.equals(resource.getSection().getId())) {
            throw new IllegalArgumentException("Resource does not belong to the specified section");
        }

        resource.setSection(null);
        resourceRepository.save(resource);
    }

    public List<ResourceDto> getAllResources() {
        List<ResourceEntity> resources = resourceRepository.findAll();
        return resources.stream()
                .filter(ResourceEntity::isApproved)
                .map(resourceMapper::toDto)
                .collect(Collectors.toList());
    }

    public ResourceDto getResourceDto(Long id) {
        ResourceEntity resource = resourceRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Resource not found"));
        return resourceMapper.toDto(resource);
    }
}
