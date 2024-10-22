package e_learning.mappers.mappersImpl;

import e_learning.DTO.CourDto;
import e_learning.DTO.FormateurDto;
import e_learning.entity.*;
import e_learning.repositories.*;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FormateurMapper {

    private final CourRepository courRepository;

    public FormateurDto toDto(Formateur formateur) {
        return new FormateurDto(
                formateur.getUserId() != null ? formateur.getUserId() : null,
                formateur.getFirstName(),
                formateur.getLastName(),
                formateur.getEmail(),
                formateur.getPassword(),
                formateur.getNum(),
                formateur.getGender(),
                formateur.getAge(),
                formateur.getCIN(),
                formateur.getCabinetName() != null ? formateur.getCabinetName() : null,
                formateur.getCabinetNum() != null ? formateur.getCabinetNum() : null,
                formateur.getTypeFormateur() != null ? formateur.getTypeFormateur() : null,
                formateur.getCreatedAt(),
                formateur.getCours() != null ? formateur.getCours().stream().map(Cour::getId).collect(Collectors.toList()) : null,
                formateur.getProfileImagePath() != null ? formateur.getProfileImagePath() : null
        );
    }

    public Formateur toEntity(FormateurDto formateurDto) {
        Formateur formateur = new Formateur();
       if (formateurDto.id() != null) formateur.setUserId(formateurDto.id());
        formateur.setFirstName(formateurDto.firstName());
        formateur.setLastName(formateurDto.lastName());
        formateur.setEmail(formateurDto.email());
        formateur.setPassword(formateurDto.password());
        formateur.setNum(formateurDto.num());
        formateur.setGender(formateurDto.gender());
        formateur.setAge(formateurDto.age());
        formateur.setCIN(formateurDto.cin());
        formateur.setCreatedAt(formateurDto.createdAt());
        if (formateurDto.cabinetName() != null)
        formateur.setCabinetName(formateurDto.cabinetName());
        if (formateurDto.cabinetNum() != null)
        formateur.setCabinetNum(formateurDto.cabinetNum());
        if (formateurDto.typeFormateur() != null)
        formateur.setTypeFormateur(formateurDto.typeFormateur());
        if (formateurDto.profileImagePath() != null)
        formateur.setProfileImagePath(formateurDto.profileImagePath());
        if (formateurDto.courIds() != null) {
            List<Cour> cours = courRepository.findAllById(formateurDto.courIds());
            formateur.setCours(cours);
        }
        else {
            formateur.setCours(null);
        }
        return formateur;
    }
}
