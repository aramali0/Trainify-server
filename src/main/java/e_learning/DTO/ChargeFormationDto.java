package e_learning.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.enums.Gender;
import lombok.Builder;

import java.util.Date;

@Builder
public record ChargeFormationDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String num,
        Gender gender,
        int age,
        Date createdAt,
        Long entrepriseId
) {}