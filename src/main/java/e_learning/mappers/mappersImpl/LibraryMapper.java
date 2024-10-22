package e_learning.mappers.mappersImpl;

import e_learning.DTO.EvaluationDto;
import e_learning.DTO.LibraryDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class LibraryMapper {

    private final ResourceRepository resourceRepository;
    private final CourRepository courRepository;
    private final UserAppRepository userAppRepository;

    public LibraryDto toDto(Library library) {
        return new LibraryDto(
                 library.getId() != null ? library.getId() : null,
                library.getName(),
                library.getCour() != null ? library.getCour().getId() : null,
                library.getCreatedBy() != null ? library.getCreatedBy().getUserId() : null,
                library.isApproved()    ,
               library.getResources() != null ? library.getResources().stream().map(ResourceEntity::getId).collect(Collectors.toList()) : null
        );
    }

    public Library toEntity(LibraryDto libraryDto) {
        Library library = new Library();
        if(libraryDto.id() != null)
        library.setId(libraryDto.id());
        library.setName(libraryDto.name());
        library.setApproved(libraryDto.isApproved());
        if (libraryDto.courId() != null) {
            library.setCour(courRepository.findById(libraryDto.courId()).orElse(null));
        }
        if (libraryDto.resourceIds() != null) {
            List<ResourceEntity> resources = resourceRepository.findAllById(libraryDto.resourceIds());
            library.setResources(resources);
        }

        if (libraryDto.createdBy() != null) {
            library.setCreatedBy(userAppRepository.findById(libraryDto.createdBy()).orElse(null));
        }

        return library;
    }
}
