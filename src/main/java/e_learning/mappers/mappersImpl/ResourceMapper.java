package e_learning.mappers.mappersImpl;

import e_learning.DTO.ResourceDto;
import e_learning.entity.*;
import e_learning.repositories.LibraryRepository;
import e_learning.repositories.SectionRepository;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ResourceMapper {

    private final SectionRepository sectionRepository;

    private final LibraryRepository libraryRepository;
    private final UserAppRepository userAppRepository;

    public ResourceDto toDto(ResourceEntity resource) {
        return new ResourceDto(
               resource.getId() !=null ? resource.getId() : null,
                resource.getTitle(),
                resource.getType(),
                resource.getFilePath(),
                resource.isDownloadable(),
                resource.isApproved(),
                resource.getLibrary() != null ? resource.getLibrary().getId() : null,
                resource.getSection() != null ? resource.getSection().getId() : null,
                resource.getCreatedBy() != null ? resource.getCreatedBy().getUserId() : null
        );
    }

    public ResourceEntity toEntity(ResourceDto resourceDto) {
        ResourceEntity resource = new ResourceEntity();
        if(resourceDto.id() != null)
        resource.setId(resourceDto.id());
        resource.setTitle(resourceDto.title());
        resource.setType(resourceDto.type());
        resource.setFilePath(resourceDto.filePath());
        resource.setApproved(resourceDto.isApproved());
        resource.setDownloadable(resourceDto.isDownloadable());

        if (resourceDto.sectionId() != null) {
            Section section = sectionRepository.findById(resourceDto.sectionId()).orElse(null);
            resource.setSection(section);
        }

        if (resourceDto.libraryId() != null) {
            Library library = libraryRepository.findById(resourceDto.libraryId()).orElse(null);
            resource.setLibrary(library);
        }

        if(resourceDto.createdBy() != null)
        {
            resource.setCreatedBy(userAppRepository.findById(resourceDto.createdBy()).orElse(null));
        }


        return resource;
    }
}
