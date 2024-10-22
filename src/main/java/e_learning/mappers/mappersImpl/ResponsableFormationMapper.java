package e_learning.mappers.mappersImpl;

import e_learning.DTO.ClassEntityDto;
import e_learning.DTO.ResponsableFormationDto;
import e_learning.entity.ClassEntity;
import e_learning.entity.Cour;
import e_learning.entity.Participant;
import e_learning.entity.ResponsableFormation;
import e_learning.repositories.CourRepository;
import e_learning.repositories.ParticipantRepository;
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
public class ResponsableFormationMapper {

    private final CourRepository courRepository;

    public ResponsableFormationDto toDto(ResponsableFormation responsableFormation) {
        return new ResponsableFormationDto(
                responsableFormation.getUserId(),
                responsableFormation.getFirstName(),
                responsableFormation.getLastName(),
                responsableFormation.getEmail(),
                responsableFormation.getPassword(),
                responsableFormation.getNum(),
                responsableFormation.getGender(),
                responsableFormation.getAge(),
                responsableFormation.getCreatedAt(),
                responsableFormation.getEntreprise().getId()

        );
    }

    public ResponsableFormation toEntity(ResponsableFormationDto responsableFormationDto) {
        ResponsableFormation responsableFormation = new ResponsableFormation();
        if(responsableFormationDto.id() != null) responsableFormation.setUserId(responsableFormationDto.id());
        responsableFormation.setFirstName(responsableFormationDto.firstName());
        responsableFormation.setLastName(responsableFormationDto.lastName());
        responsableFormation.setEmail(responsableFormationDto.email());
        responsableFormation.setPassword(responsableFormationDto.password());
        responsableFormation.setNum(responsableFormationDto.num());
        responsableFormation.setGender(responsableFormationDto.gender());
        responsableFormation.setAge(responsableFormationDto.age());
        responsableFormation.setCreatedAt(responsableFormationDto.createdAt());

        if(responsableFormationDto.entrepriseId() != null) {
            responsableFormation.setEntreprise(responsableFormation.getEntreprise());
        }

        return responsableFormation;
    }
}
