package e_learning.mappers.mappersImpl;

import e_learning.DTO.UserDTO;
import e_learning.entity.UserApp;
import e_learning.mappers.UserMapper;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class UserMapperImpl implements UserMapper {
    @Override
    public UserApp fromDTO(UserDTO userDTO){
        UserApp userApp = new UserApp();
        if (userDTO.userId() != null) userApp.setUserId(userDTO.userId());
        userApp.setFirstName(userDTO.firstName());
        userApp.setLastName(userDTO.lastName());
        userApp.setNum(userDTO.num());
        userApp.setGender(userDTO.gender());
        userApp.setEmail(userDTO.email());
        userApp.setPassword(userDTO.password());
        userApp.setCIN(userDTO.CIN());
        userApp.setVerified(userDTO.isVerified());
        userApp.setAge(userDTO.age());
        userApp.setEnabled(userDTO.isEnabled());
        userApp.setLastActiveTime(userDTO.lastActiveTime());
        userApp.setProfileImagePath(userDTO.profileImagePath()); // New mapping
        userApp.setCreatedAt(userDTO.createdAt());
        userApp.setRoles(userDTO.roles() == null ? userApp.getRoles() : userDTO.roles());
        return userApp;
    }

    @Override
    public UserDTO fromEntity(UserApp userApp){
        return UserDTO.builder()
                .userId(userApp.getUserId())
                .firstName(userApp.getFirstName())
                .lastName(userApp.getLastName())
                .num(userApp.getNum())
                .gender(userApp.getGender())
                .age(userApp.getAge())
                .CIN(userApp.getCIN())
                .email(userApp.getEmail())
                .isVerified(userApp.isVerified())
                .lastActiveTime(userApp.getLastActiveTime())
                .profileImagePath(userApp.getProfileImagePath()) // New mapping
                .createdAt(userApp.getCreatedAt())
                .roles(userApp.getRoles() == null ? new ArrayList<>() : userApp.getRoles())
                .build();
    }
}
