package e_learning.DTO;

import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserUpdateDto {
    private String email;
    private String firstName;
    private String lastName;
    private String num;
    private String gender;
    private String CIN;
    private int age;
}