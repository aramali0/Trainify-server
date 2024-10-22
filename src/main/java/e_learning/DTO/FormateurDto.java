package e_learning.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;
import e_learning.enums.Gender;
import e_learning.enums.TypeFormateur;
import e_learning.enums.UserRole;
import lombok.Builder;

import java.util.Date;
import java.util.List;

@Builder
public record FormateurDto(
        Long id,
        String firstName,
        String lastName,
        String email,
        @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
        String password,
        String num,
        Gender gender,
        int age,
        String cin,
        String cabinetName,
        String cabinetNum,
        TypeFormateur typeFormateur,
        Date createdAt,
        List<Long> courIds,
        String profileImagePath // New field
) {}