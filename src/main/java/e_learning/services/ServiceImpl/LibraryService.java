package e_learning.services.ServiceImpl;

import e_learning.DTO.LibraryDto;
import e_learning.DTO.ResourceDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.LibraryMapper;
import e_learning.mappers.mappersImpl.ResourceMapper;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.apache.catalina.util.LifecycleBase;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;


@Service
@AllArgsConstructor
public class LibraryService {

    private final LibraryRepository libraryRepository;
    private final LibraryMapper libraryMapper;
    private final ResponsableFormationRepository responsableFormationRepository;
    private final ResourceMapper resourceMapper;
    private final FormateurRepository formateurRepository;
    private final ParticipantRepository participantRepository;
    private final ChargeFormationRepository chargeFormationRepository;
    private final ActionApprovalRepository actionApprovalRepository;

    public LibraryDto saveLibrary(LibraryDto libraryDto, UserApp userApp) {
        System.out.println("LibraryDto: " + libraryDto);
        Library library = libraryMapper.toEntity(libraryDto);
        System.out.println("Library: " + library);
        library.setApproved(true);
        library.setCreatedAt(new Date());
        library.setCreatedBy(userApp);
        library = libraryRepository.save(library);

        if(userApp instanceof ChargeFormation)
        {

            ActionApproval actionApproval = new ActionApproval();
            actionApproval.setApproved(false);
            actionApproval.setObjectId(library.getId());
            actionApproval.setActionType("LIBRARY");
            actionApproval.setCreatedDate(LocalDateTime.now());
            actionApproval.setChargeFormationId(userApp.getUserId());
            actionApproval.setEntrepriseId(library.getCour().getEntreprise().getId());

            actionApprovalRepository.save(actionApproval);
            library.setApproved(false);
            libraryRepository.save(library);

        }

        return libraryMapper.toDto(library);
    }

    public List<LibraryDto> getAllLibraries() {
        List<Library>  libraries = libraryRepository.findAll();
        return libraries.stream().filter(Library::isApproved).map(libraryMapper::toDto).toList();

    }

    public Optional<LibraryDto> getLibraryById(Long id) {
        return libraryRepository.findById(id).map(libraryMapper::toDto);
    }

    public List<LibraryDto> getLibrariesByCourId(Long courId) {
        List<Library> libraries = libraryRepository.findByCourId(courId);
        return libraries.stream().filter(Library::isApproved).map(libraryMapper::toDto).toList();
    }

    public List<LibraryDto> getResourcesByResponsableId(Long responsableId) {
        ResponsableFormation responsableFormation = responsableFormationRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("ResponsableFormation not found"));

        List<Cour> cours = responsableFormation.getEntreprise().getCours();
        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(libraryMapper::toDto)
                .collect(toList());
    }
    public List<ResourceDto> getResourcesByResponsableFormationId(Long id)
    {
         ResponsableFormation responsableFormation = responsableFormationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("ResponsableFormation not found"));

        List<Cour> cours = responsableFormation.getEntreprise().getCours();
        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(library -> library.getResources().stream())
                .distinct()
                .flatMap(resource -> resource)
                .map(resourceMapper::toDto)
                .collect(toList());
    }

    public void deleteLibrary(Long id) {
        libraryRepository.deleteById(id);
    }

    public List<LibraryDto> getResourcesByFormateurId(Long formateurId) {
        Formateur formateur = formateurRepository.findById(formateurId)
                .orElseThrow(() -> new RuntimeException("formateur not found"));

        List<Cour> cours = formateur.getCours();
        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(libraryMapper::toDto)
                .collect(toList());
    }

    public List<LibraryDto> getResourcesByParticipant(Long participantId) {
         Participant participant = participantRepository.findById(participantId)
                .orElseThrow(() -> new RuntimeException("formateur not found"));

        List<Cour> cours = participant.getClasses()
                .stream().filter(ClassEntity::isApproved)
                .map(ClassEntity::getCours)   // returns a Stream<List<Cour>>
                .flatMap(List::stream)        // flattens the Stream<List<Cour>> to Stream<Cour>
                .distinct()                   // ensures distinct elements
                .collect(Collectors.toList()); // collects the Stream<Cour> into a List<Cour>

        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(libraryMapper::toDto)
                .collect(toList());
    }

    public List<LibraryDto> getResourcesByChargeFormationId(Long responsableId) {
        ChargeFormation responsableFormation = chargeFormationRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("charge formation not found"));

        List<Cour> cours = responsableFormation.getEntreprise().getCours();
        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(libraryMapper::toDto)
                .collect(toList());
    }

    public List<ResourceDto> getResourcesByResponsableChargeFormationId(Long responsableId) {
        ChargeFormation responsableFormation = chargeFormationRepository.findById(responsableId)
                .orElseThrow(() -> new RuntimeException("charge formation not found"));

        List<Cour> cours = responsableFormation.getEntreprise().getCours();
        List<Library> libraries = libraryRepository.findByCourIn(cours);
        return libraries.stream()
                .filter(Library::isApproved)
                .map(library -> library.getResources().stream())
                .distinct()
                .flatMap(resource -> resource)
                .map(resourceMapper::toDto)
                .collect(toList());
    }
}
