package e_learning.controllers;

import e_learning.DTO.ContactDto;
import e_learning.DTO.UserDTO;
import e_learning.DTO.UserUpdateDto;
import e_learning.entity.*;
import e_learning.enums.Gender;
import e_learning.enums.UserRole;
import e_learning.exceptions.FileStorageException;
import e_learning.mappers.UserMapper;
import e_learning.mappers.mappersImpl.UserMapperImpl;
import e_learning.repositories.*;
import e_learning.services.ServiceImpl.FileStorageService;
import e_learning.services.ServiceImpl.RoleServiceImpl;
import e_learning.services.ServiceImpl.UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/users")
@AllArgsConstructor
public class UserController {
    private final UserServiceImpl userService;
    private final UserMapperImpl userMapperImpl;
    private final UserAppRepository userAppRepository;
    private final MessageEntityRepository messageEntityRepository;
    private final FileStorageService fileStorageService;
    private final SectionRepository sectionRepository;
    private final SessionRepository sessionRepository;
    private final CourRepository courRepository;
    private final RoleServiceImpl roleServiceImpl;

    @GetMapping("/active")
    public ResponseEntity<List<UserDTO>> getActiveUsers() {
        List<UserApp> activeUsers = userService.getActiveUsers();
        List<UserDTO> activeUsersDTO = activeUsers.stream().map(userMapperImpl::fromEntity).collect(Collectors.toList());
        return new ResponseEntity<>(activeUsersDTO, HttpStatus.OK);
    }


    @GetMapping("/all")
    public List<UserDTO> getAllUsers()
    {
        List<UserApp> userApps = userAppRepository.findAll();
        return  userApps.stream().map(userMapperImpl::fromEntity).collect(Collectors.toList());
    }



    @GetMapping
    public List<ContactDto> getAllUsersWithUnreadMessageCount(Principal principal) {
        String email = principal.getName();
        UserApp currentUser = userAppRepository.findUserAppByEmail(email);

        return userAppRepository.findAll().stream()
                .filter(user -> !user.getEmail().equals(email))
                .map(user -> {
                    int unreadMessageCount = messageEntityRepository.countUnreadMessagesBetweenUsers(currentUser.getUserId(), user.getUserId());
                    return new ContactDto(user.getUserId(), user.getFirstName(), user.getLastName(), user.getLastActiveTime(), unreadMessageCount);
                })
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        UserApp user = userService.getUserById(id).orElse(null);
        UserDTO userDTO = userMapperImpl.fromEntity(user);
        return new ResponseEntity<>(userDTO, HttpStatus.OK);
    }

    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(@RequestParam String name) {
        List<UserApp> users = userService.searchUsersByName(name);
        List<UserDTO> usersDTO = users.stream().map(userMapperImpl::fromEntity).collect(Collectors.toList());
        return new ResponseEntity<>(usersDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/update-profile", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> updateProfile(
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("CIN") String CIN,
            @RequestParam("age") int age,
            @RequestParam(value = "roles" , required = false) List<String> roles,  // Accepting a list of roles
            @RequestPart(value = "image", required = false) MultipartFile image) throws FileStorageException {

        UserApp existingUser = userService.findByEmail(email);
        if (existingUser == null) {
            return ResponseEntity.badRequest().body("User not found");
        }

        existingUser.setFirstName(firstName);
        existingUser.setLastName(lastName);
        existingUser.setNum(num);
        existingUser.setGender(Gender.valueOf(gender.toUpperCase()));
        existingUser.setCIN(CIN);
        existingUser.setAge(age);

        // Clear and update the user's roles

        List<RoleApp> newRoles = new ArrayList<>();

        if(roles != null) {
            for (String roleName : roles) {
                RoleApp role = roleServiceImpl.findByRole(UserRole.valueOf(roleName.toUpperCase()));
                if (role != null) {
                    newRoles.add(role);
                } else {
                    return ResponseEntity.badRequest().body("Invalid role: " + roleName);
                }
            }
            existingUser.setRoles(newRoles); // Set the new list of roles
        }
        // Handle profile image if provided
        if (image != null) {
//            String imagePath = fileStorageService.storeFile(image);
//            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            String imagePath = fileStorageService.saveImage(image);
            existingUser.setProfileImagePath(imagePath);
        }

        UserApp updatedUser = userService.updateUser(existingUser);

        if (updatedUser == null) {
            return ResponseEntity.status(500).body("Failed to update profile");
        }

        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        UserApp user = userAppRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        try {
                List<Section> sections = sectionRepository.findByCreatedByUserId(user.getUserId());
                for (Section section : sections) {
                    section.setCreatedBy(null);
                    sectionRepository.save(section);
                }

                List<Session> sessions = sessionRepository.findByCreatedByUserId(user.getUserId());
                for (Session session : sessions) {
                    session.setCreatedBy(null); ;
                    sessionRepository.save(session);
                }

                List<Cour> cours = (List<Cour>) courRepository.findByFormateursUserId(user.getUserId());
                for (Cour cour : cours) {
                    cour.getFormateurs().remove(user);
                    courRepository.save(cour);
                }

                List<Cour> coursResponsable = (List<Cour>) courRepository.findByEntrepriseResponsableFormationsUserId(user.getUserId());
            for (Cour cour : coursResponsable) {
                cour.setEntreprise(null);
                courRepository.save(cour);
            }

                userAppRepository.delete(user);
            return ResponseEntity.ok("User deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to delete user");
        }
    }

    @PutMapping("/{id}/block")
    public ResponseEntity<?> blockUser(@PathVariable Long id) {
        try {
            userService.blockUser(id); // Implement this method in your service layer
            return ResponseEntity.ok("User blocked successfully!");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to block user");
        }
    }

}