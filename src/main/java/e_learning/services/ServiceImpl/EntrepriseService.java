package e_learning.services.ServiceImpl;

import e_learning.DTO.*;
import e_learning.entity.Entreprise;
import e_learning.exceptions.FileStorageException;
import e_learning.mappers.mappersImpl.*;
import e_learning.repositories.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class EntrepriseService {
    private final EntrepriseRepository entrepriseRepository;
    private final EntrepriseMapper entrepriseMapper;
    private final FileStorageService fileStorageService;  // For handling logo storage
    private final ResponsableFormationRepository responsableFormationRepository;
    private final ParticipantMapper participantMapper;
    private final FormateurMapper formateurMapper;
    private final ResponsableFormationMapper responsableFormationMapper;
    private final HierarchicalUnitRepository hierarchicalUnitRepository;
    private final CourRepository courRepository;
    private final ParticipantRepository participantRepository;
    private final FormateurRepository formateurRepository;

    /**
     * Get all Entreprises
     */
    public List<EntrepriseDto> getAllEntreprises() {
        return entrepriseRepository.findAll()
                .stream()
                .map(entrepriseMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get an Entreprise by ID
     */
    public EntrepriseDto getEntrepriseById(Long id) {
        return entrepriseRepository.findById(id)
                .map(entrepriseMapper::toDto)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
    }

    /**
     * Create a new Entreprise
     */
    public EntrepriseDto createEntreprise(EntrepriseDto entrepriseDto, MultipartFile logo) throws FileStorageException {
        Entreprise entreprise = entrepriseMapper.toEntity(entrepriseDto);

        // Handle logo file upload
        if (logo != null) {
            String logoPath = fileStorageService.storeFile(logo);
            entreprise.setLogo(logoPath.replace("app\\uploads\\", "uploads/"));
        }

        entreprise.setShowQuizResult(true);
        entreprise.setShowQuizCorrection(true);

        Entreprise savedEntreprise = entrepriseRepository.save(entreprise);
        savedEntreprise.getHierarchicalUnits().forEach(hierarchicalUnit -> hierarchicalUnit.setEntreprise(savedEntreprise));
        hierarchicalUnitRepository.saveAll(savedEntreprise.getHierarchicalUnits());
        if(entreprise.getResponsableFormations() != null)
        {
            savedEntreprise.getResponsableFormations().forEach(responsableFormation -> responsableFormation.setEntreprise(savedEntreprise));
            responsableFormationRepository.saveAll(savedEntreprise.getResponsableFormations());
        }
        return entrepriseMapper.toDto(savedEntreprise);
    }

    /**
     * Update an existing Entreprise
     */
    public EntrepriseDto updateEntreprise(Long id, EntrepriseDto entrepriseDto, MultipartFile logo) throws FileStorageException {
        Entreprise existingEntreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));

        // Update basic fields from DTO
        existingEntreprise.setNomCommercial(entrepriseDto.getNomCommercial());
        existingEntreprise.setNumeroRC(entrepriseDto.getNumeroRC());
        existingEntreprise.setNumeroCNSS(entrepriseDto.getNumeroCNSS());
        existingEntreprise.setNumeroIF(entrepriseDto.getNumeroIF());
        existingEntreprise.setNumeroTP(entrepriseDto.getNumeroTP());
        existingEntreprise.setNombreSalaries(entrepriseDto.getNombreSalaries());

        // Update hierarchical units
        if (entrepriseDto.getHierarchicalUnitIds() != null) {
              existingEntreprise.setHierarchicalUnits(hierarchicalUnitRepository.findAllById(entrepriseDto.getHierarchicalUnitIds()));
        }

        // Update relationships with other entities
        if (entrepriseDto.getResponsableFormationIds() != null) {
            existingEntreprise.setResponsableFormations(responsableFormationRepository.findAllById(entrepriseDto.getResponsableFormationIds()));
        }

        if (entrepriseDto.getCoursIds() != null) {
            existingEntreprise.setCours(courRepository.findAllById(entrepriseDto.getCoursIds()));
        }

        if (entrepriseDto.getAppendingParticipantsIds() != null) {
            existingEntreprise.setAppendingParticipants(participantRepository.findAllById(entrepriseDto.getAppendingParticipantsIds()));
        }

        if (entrepriseDto.getParticipantsIds() != null) {
            existingEntreprise.setParticipants(participantRepository.findAllById(entrepriseDto.getParticipantsIds()));
        }

        if (entrepriseDto.getFormateursIds() != null) {
            existingEntreprise.setFormateurs(formateurRepository.findAllById(entrepriseDto.getFormateursIds()));
        }

        // Update logo
        if (logo != null) {
            String logoPath = fileStorageService.storeFile(logo);
            existingEntreprise.setLogo(logoPath.replace("app\\uploads\\", "uploads/"));
        }else {
            existingEntreprise.setLogo(entrepriseDto.getLogo());
        }

        Entreprise updatedEntreprise = entrepriseRepository.save(existingEntreprise);
        System.out.println("updatedEntreprise.getHierarchicalUnits() = " + updatedEntreprise.getHierarchicalUnits());
        return entrepriseMapper.toDto(updatedEntreprise);
    }

    /**
     * Delete an Entreprise
     */
    public void deleteEntreprise(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        entrepriseRepository.delete(entreprise);
    }

    /**
     * Get participants by Entreprise ID
     */
    public List<ParticipantDto> getParticipantsByEntrepriseId(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        return entreprise.getParticipants()
                .stream()
                .map(participantMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get formateurs by Entreprise ID
     */
    public List<FormateurDto> getFormateursByEntrepriseId(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        return entreprise.getFormateurs()
                .stream()
                .map(formateurMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get ResponsableFormation by Entreprise ID
     */
    public List<ResponsableFormationDto> getResponsableFormationByEntrepriseId(Long id) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        System.out.println("entreprise.getResponsableFormations() = " + entreprise.getResponsableFormations());
        return entreprise.getResponsableFormations()
                .stream()
                .map(responsableFormationMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Get Entreprise by ResponsableFormation ID
     */
    public EntrepriseDto getEntrepriseByResponsableFormationId(Long id) {
        return entrepriseMapper.toDto(entrepriseRepository.findByResponsableFormationsUserId(id));
    }

    public EntrepriseDto updateShowQuizResult(Long id, boolean showQuizResult) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        entreprise.setShowQuizResult(showQuizResult);
        Entreprise updatedEntreprise = entrepriseRepository.save(entreprise);
        return entrepriseMapper.toDto(updatedEntreprise);
    }

    public EntrepriseDto updateShowQuizCorrection(Long id, boolean showQuizCorrection) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        entreprise.setShowQuizCorrection(showQuizCorrection);
        Entreprise updatedEntreprise = entrepriseRepository.save(entreprise);
        return entrepriseMapper.toDto(updatedEntreprise);
    }

    public EntrepriseDto updateShowExcelMethode(Long id, boolean showExcelMethode) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        entreprise.setShowExcelMethode(showExcelMethode);
        Entreprise updatedEntreprise = entrepriseRepository.save(entreprise);
        return entrepriseMapper.toDto(updatedEntreprise);
    }

    public EntrepriseDto updateDownloadVideo(Long id, boolean downloadVideo) {
        Entreprise entreprise = entrepriseRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Entreprise not found"));
        entreprise.setDownloadVideo(downloadVideo);
        Entreprise updatedEntreprise = entrepriseRepository.save(entreprise);
        return entrepriseMapper.toDto(updatedEntreprise);
    }

    public boolean isDownloadVideoResponsable(Long reponsableId) {
        return entrepriseRepository.findByResponsableFormationsUserId(reponsableId).isDownloadVideo();
    }


    public boolean isDownloadVideoForamteur(Long formateurId) {
        Optional<Entreprise> optional =  entrepriseRepository.findByFormateursUserId(formateurId);
        return optional.map(Entreprise::isDownloadVideo).orElse(false);
    }


    public boolean isDownloadVideoChargeFormation(Long chargeFormationId) {
        Optional<Entreprise> optional =  entrepriseRepository.findByChargeFormationsUserId(chargeFormationId);
        return optional.map(Entreprise::isDownloadVideo).orElse(false);
    }
}
