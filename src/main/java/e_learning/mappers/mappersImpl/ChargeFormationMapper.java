package e_learning.mappers.mappersImpl;

import e_learning.DTO.ChargeFormationDto;
import e_learning.DTO.ResponsableFormationDto;
import e_learning.entity.ChargeFormation;
import e_learning.entity.ResponsableFormation;
import e_learning.repositories.CourRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class ChargeFormationMapper {

    private final CourRepository courRepository;

    public ChargeFormationDto toDto(ChargeFormation responsableFormation) {
        return new ChargeFormationDto(
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

    public ChargeFormation toEntity(ChargeFormationDto responsableFormationDto) {
        ChargeFormation responsableFormation = new ChargeFormation();
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
