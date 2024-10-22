package e_learning.mappers;

import e_learning.DTO.UserDTO;
import e_learning.entity.UserApp;

public interface UserMapper {
    public UserApp fromDTO(UserDTO userDTO);
    public UserDTO fromEntity( UserApp userApp);
}
