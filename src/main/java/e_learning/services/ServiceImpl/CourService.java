package e_learning.services.ServiceImpl;

import e_learning.DTO.CourDto;
import e_learning.DTO.SessionDto;
import e_learning.entity.*;
import e_learning.mappers.mappersImpl.CourMapper;
import e_learning.mappers.mappersImpl.SessionMapper;
import e_learning.repositories.ActionApprovalRepository;
import e_learning.repositories.CourRepository;
import e_learning.security.SecurityService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CourService {
    private final CourRepository courRepository;
    private final CourMapper courMapper;
    private final SecurityService securityService;
    private final SessionMapper sessionMapper;
    private final ActionApprovalRepository actionApprovalRepository;

    public List<CourDto> getAllCours() {
        return courRepository.findAll().stream().filter(Cour::isApproved).map(courMapper::toDto).collect(Collectors.toList());
    }

    public List<CourDto> getAllFavoriteCours() {
        return courRepository.findAll().stream().filter(Cour::isFavorite).map(courMapper::toDto).collect(Collectors.toList());
    }

    public void handleFavorite(Long courseId) {
        Optional<Cour> courOptional = courRepository.findById(courseId);
        if (courOptional.isPresent()) {
            Cour cour = courOptional.get();
            cour.setFavorite(!cour.isFavorite());
            courRepository.save(cour);
        } else {
            throw new RuntimeException("Course not found");
        }
    }

    public Optional<CourDto> getCourById(Long id) {
        return courRepository.findById(id).map(courMapper::toDto);
    }

    @Transactional
    public CourDto createOrUpdateCour(CourDto courDto, UserApp responsableFormation) {


        if (responsableFormation.getRoles().stream()
                .noneMatch(role -> role != null && ("RESPONSABLE".equalsIgnoreCase(String.valueOf(role.getRole())) || "CHARGE".equalsIgnoreCase(String.valueOf(role.getRole()))))) {
            throw new RuntimeException("You are not allowed to create a course");
        }

        Cour cour = courMapper.toEntity(courDto);

        if (cour.getId() == null) { // New course
            if(responsableFormation instanceof ResponsableFormation) {
                cour.setEntreprise(((ResponsableFormation) responsableFormation).getEntreprise());
            } else {
                cour.setEntreprise(((ChargeFormation) responsableFormation).getEntreprise());
            }
            cour.setCreatedBy(responsableFormation);
        }
        cour = courRepository.save(cour);

        if(responsableFormation.getRoles().stream().allMatch((role) -> role != null && "CHARGE".equalsIgnoreCase(String.valueOf(role.getRole())))) {
            cour.setApproved(false);
            courRepository.save(cour);

        }

        return courMapper.toDto(cour);
    }

    @Transactional
    public void deleteCour(Long id) {
        courRepository.deleteById(id);
    }

    public List<CourDto> getCoursByResponsableFormationId(Long responsableFormationId) {
        return courRepository.findByEntrepriseResponsableFormationsUserId(responsableFormationId).stream().filter(Cour::isApproved).map(courMapper::toDto).collect(Collectors.toList());
    }

    public List<CourDto> getCoursByFormateurId(Long formateurId) {
        List<CourDto> courDtos = courRepository.findByFormateursUserId(formateurId).stream().filter(Cour::isApproved).map(courMapper::toDto).collect(Collectors.toList());
        return courDtos;
    }

    public List<CourDto> getCoursByParticipant(Long formateurId) {
        List<CourDto> courDtos = courRepository.findByClassesParticipantsUserId(formateurId).stream().filter(Cour::isApproved).map(courMapper::toDto).collect(Collectors.toList());
        return courDtos;
    }

    public List<CourDto> getCoursByResponsableFormationEmail(String email) {
        return courRepository.findByEntrepriseResponsableFormationsEmail(email).stream().filter(Cour::isApproved).map(courMapper::toDto).collect(Collectors.toList());
    }

    public List<CourDto> getCoursByClasses(ClassEntity classEntity) {
        return courRepository.findByClassesId(classEntity.getId()).stream().map(courMapper::toDto).collect(Collectors.toList());
    }

    public List<SessionDto> getSessionsByCourId(Long courId, UserApp userApp) {

        Optional<Cour> cour = courRepository.findById(courId);
        List<SessionDto> session = cour.map(value -> value.getSessions().stream().filter(sesion -> sesion.isApproved() || (Objects.equals(sesion.getCreatedBy().getUserId(), userApp.getUserId()) && !sesion.isSent()) || (sesion.isSent() && sesion.getCour().getEntreprise().getResponsableFormations().stream().anyMatch(responsableFormation -> Objects.equals(responsableFormation.getUserId(), userApp.getUserId())))).map(sessionMapper::toDto).collect(Collectors.toList())).orElse(Collections.emptyList());
        System.out.println("sectionDtos = " + session);
        return session;
    }

    @Transactional
    public void blockCour(Long id) {
        courRepository.blockCour(id);
    }

    @Transactional
    public void unblockCour(Long id) {
        courRepository.unblockCour(id);
    }

    public List<CourDto> getCoursByChargeFormation(Long id) {

        List<CourDto> chargeFormationCourses = new ArrayList<>(courRepository.findByEntrepriseChargeFormationsUserId(id).stream().filter(cour -> Objects.equals(cour.getCreatedBy().getUserId() , id)).filter(cour -> !cour.isSent()).map(courMapper::toDto).distinct().toList());
        chargeFormationCourses.addAll(courRepository.findByEntrepriseChargeFormationsUserId(id).stream().filter(Cour::isApproved).distinct().map(courMapper::toDto).toList());
        return chargeFormationCourses.stream().distinct().toList();
    }


}
