package e_learning.mappers.mappersImpl;

import e_learning.DTO.EntrepriseDto;
import e_learning.entity.Cour;
import e_learning.entity.Entreprise;
import e_learning.entity.HierarchicalUnit;
import e_learning.entity.UserApp;
import e_learning.repositories.*;
import e_learning.services.ServiceImpl.EntrepriseService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class EntrepriseMapper {

    private final ResponsableFormationRepository responsableFormationRepository;
    private final CourRepository courRepository;
    private final ParticipantRepository participantRepository;
    private final FormateurRepository formateurRepository;
    private final HierarchicalUnitRepository hierarchicalUnitRepository;


    public EntrepriseDto toDto(Entreprise entreprise) {
        EntrepriseDto dto = new EntrepriseDto();
        if(entreprise.getId() != null) dto.setId(entreprise.getId());
        dto.setNomCommercial(entreprise.getNomCommercial());
        dto.setNumeroRC(entreprise.getNumeroRC());
        dto.setNumeroCNSS(entreprise.getNumeroCNSS());
        dto.setNumeroIF(entreprise.getNumeroIF());
        dto.setNumeroTP(entreprise.getNumeroTP());
        dto.setNombreSalaries(entreprise.getNombreSalaries());
        dto.setShowExcelMethode(entreprise.isShowExcelMethode());
        dto.setShowQuizResult(entreprise.isShowQuizResult());
        dto.setShowQuizCorrection(entreprise.isShowQuizCorrection());
        if(entreprise.getResponsableFormations() != null) {
            dto.setResponsableFormationIds(entreprise.getResponsableFormations().stream().map(UserApp::getUserId).collect(Collectors.toList()));
        }
        if(entreprise.getCours() != null) {
            dto.setCoursIds(entreprise.getCours().stream().map(Cour::getId).collect(Collectors.toList()));
        }

        if(entreprise.getAppendingParticipants() != null) {
            dto.setAppendingParticipantsIds(entreprise.getAppendingParticipants().stream().map(UserApp::getUserId).collect(Collectors.toList()));
        }
        if(entreprise.getParticipants() != null) {
            dto.setParticipantsIds(entreprise.getParticipants().stream().map(UserApp::getUserId).collect(Collectors.toList()));
        }
        if(entreprise.getFormateurs() != null) {
            dto.setFormateursIds(entreprise.getFormateurs().stream().map(UserApp::getUserId).collect(Collectors.toList()));
        }
       if (entreprise.getHierarchicalUnits() != null) {
           dto.setHierarchicalUnitIds(entreprise.getHierarchicalUnits().stream().map(HierarchicalUnit::getId).collect(Collectors.toList()));
        }
       if (entreprise.getChargeFormations() != null) {
           dto.setChargeFormationsIds(entreprise.getChargeFormations().stream().map(UserApp::getUserId).collect(Collectors.toList()));
       }

        if(entreprise.getLogo() != null)
        {
            dto.setLogo(entreprise.getLogo());
        }

        if(entreprise.getMaxSize() != null)
        {
            dto.setMaxSize(entreprise.getMaxSize());
        }


        return dto;
    }

    public Entreprise toEntity(EntrepriseDto dto) {
        Entreprise entreprise = new Entreprise();
        if(dto.getId() != null) entreprise.setId(dto.getId());
        entreprise.setNomCommercial(dto.getNomCommercial());
        entreprise.setNumeroRC(dto.getNumeroRC());
        entreprise.setNumeroCNSS(dto.getNumeroCNSS());
        entreprise.setNumeroIF(dto.getNumeroIF());
        entreprise.setNumeroTP(dto.getNumeroTP());
        entreprise.setNombreSalaries(dto.getNombreSalaries());

        if(dto.getResponsableFormationIds() != null) {
            entreprise.setResponsableFormations(responsableFormationRepository.findAllById(dto.getResponsableFormationIds()));
        }

        if(dto.getCoursIds() != null) {
            entreprise.setCours(courRepository.findAllById(dto.getCoursIds()));
        }

        if(dto.getAppendingParticipantsIds() != null) {
            entreprise.setAppendingParticipants(participantRepository.findAllById(dto.getAppendingParticipantsIds()));
        }

        if(dto.getParticipantsIds() != null) {
            entreprise.setParticipants(participantRepository.findAllById(dto.getParticipantsIds()));
        }

        if(dto.getFormateursIds() != null) {
            entreprise.setFormateurs(formateurRepository.findAllById(dto.getFormateursIds()));
        }

        if (dto.getHierarchicalUnitIds() != null)
        {
            entreprise.setHierarchicalUnits(hierarchicalUnitRepository.findAllById(dto.getHierarchicalUnitIds()));
        }

        if(dto.getLogo() != null)
        {
            entreprise.setLogo(dto.getLogo());
        }

        return entreprise;
    }
}
