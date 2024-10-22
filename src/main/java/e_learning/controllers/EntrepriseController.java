package e_learning.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import e_learning.DTO.*;
import e_learning.entity.Entreprise;
import e_learning.exceptions.FileStorageException;
import e_learning.mappers.mappersImpl.EntrepriseMapper;
import e_learning.repositories.EntrepriseRepository;
import e_learning.services.ServiceImpl.EntrepriseService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/entreprises")
@AllArgsConstructor
public class EntrepriseController {
    private final EntrepriseService entrepriseService;
    private final EntrepriseRepository entrepriseRepository;
    private final EntrepriseMapper entrepriseMapper;

    @GetMapping
    public List<EntrepriseDto> getAllEntreprises() {
        return entrepriseService.getAllEntreprises();
    }

    @GetMapping("/{id}")
    public EntrepriseDto getEntrepriseById(@PathVariable Long id) {
        return entrepriseService.getEntrepriseById(id);
    }

    @GetMapping("nomCommercial/{nomCommercial}")
    public EntrepriseDto getEntrepriseByNomCommercial(@PathVariable String nomCommercial) {
        Optional<Entreprise> entrepriseOptional = entrepriseRepository.findByNomCommercial(nomCommercial);
        if (entrepriseOptional.isEmpty()) {
            return null;
        }
        return entrepriseMapper.toDto(entrepriseOptional.get());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createEntreprise(
            @RequestPart("entreprise") EntrepriseDto entrepriseDto,
            @RequestPart(value = "logo", required = false) MultipartFile logo
    ) throws FileStorageException {
//        if(entrepriseDto.getParticipantsIds() != null && entrepriseDto.getResponsableFormationIds().isEmpty()){
//           return new ResponseEntity<>("Responsable formation is required", HttpStatus.BAD_REQUEST);
//        }
        EntrepriseDto newEntreprise = entrepriseService.createEntreprise(entrepriseDto, logo);
        return new ResponseEntity<>(newEntreprise, HttpStatus.CREATED);
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public EntrepriseDto updateEntreprise(
            @PathVariable Long id,
            @RequestPart("entreprise") String entrepriseDtoJson, @RequestPart(value = "logo", required = false) MultipartFile logo
    ) throws FileStorageException, JsonProcessingException {
        // Convert JSON string to EntrepriseDto
        ObjectMapper objectMapper = new ObjectMapper();
        EntrepriseDto entrepriseDto = objectMapper.readValue(entrepriseDtoJson, EntrepriseDto.class);

        return entrepriseService.updateEntreprise(id, entrepriseDto, logo);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEntreprise(@PathVariable Long id) {
        entrepriseService.deleteEntreprise(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @GetMapping("/{id}/users")
//    public List<UserDTO> getUserByEntrepriseId(@PathVariable Long id, @RequestParam(required = false) String role ) {
//        List<UserDTO> users = new ArrayList<>();
//        if(role != null){
//            switch (role) {
//                case "participants" -> {
//                    return entrepriseService.getParticipantsByEntrepriseId(id);
//                }
//                case "formateurs" -> {
//                    return entrepriseService.getFormateursByEntrepriseId(id);
//                }
//                case "responsableFormation" -> {
//
//                    return List.of(entrepriseService.getResponsableFormationByEntrepriseId(id));
//                }
//            }
//        }
//        users.addAll(entrepriseService.getParticipantsByEntrepriseId(id));
//        users.addAll(entrepriseService.getFormateursByEntrepriseId(id));
//        users.add(entrepriseService.getResponsableFormationByEntrepriseId(id));
//        return users;
//    }

    @GetMapping("/{id}/participants")
    public List<ParticipantDto> getParticipantsByEntrepriseId(@PathVariable Long id) {
        return entrepriseService.getParticipantsByEntrepriseId(id);
    }

    @GetMapping("/{id}/formateurs")
    public List<FormateurDto> getFormateursByEntrepriseId(@PathVariable Long id) {
        return entrepriseService.getFormateursByEntrepriseId(id);
    }

    @GetMapping("/{id}/responsable-formation")
    public List<ResponsableFormationDto> getResponsableFormationByEntrepriseId(@PathVariable Long id) {

        return entrepriseService.getResponsableFormationByEntrepriseId(id);
    }

    @GetMapping("/responsable/{id}")
    public EntrepriseDto getEntrepriseByResponsableFormationId(@PathVariable Long id) {
        return entrepriseService.getEntrepriseByResponsableFormationId(id);
    }

    @PatchMapping("/{id}/show-quiz-result")
    public ResponseEntity<EntrepriseDto> updateShowQuizResult(@PathVariable Long id, @RequestParam boolean showQuizResult) {
        EntrepriseDto updatedEntreprise = entrepriseService.updateShowQuizResult(id, showQuizResult);
        return new ResponseEntity<>(updatedEntreprise, HttpStatus.OK);
    }

    @PatchMapping("/{id}/show-quiz-correction")
    public ResponseEntity<EntrepriseDto> updateShowQuizCorrection(@PathVariable Long id, @RequestParam boolean showQuizCorrection) {
        EntrepriseDto updatedEntreprise = entrepriseService.updateShowQuizCorrection(id, showQuizCorrection);
        return new ResponseEntity<>(updatedEntreprise, HttpStatus.OK);
    }

    @PatchMapping("/{id}/show-excel-methode")
    public ResponseEntity<EntrepriseDto> updateShowExcelMethode(@PathVariable Long id, @RequestParam boolean showExcelMethode) {
        EntrepriseDto updatedEntreprise = entrepriseService.updateShowExcelMethode(id, showExcelMethode);
        return new ResponseEntity<>(updatedEntreprise, HttpStatus.OK);
    }
}