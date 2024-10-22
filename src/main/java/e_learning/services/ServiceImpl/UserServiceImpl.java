package e_learning.services.ServiceImpl;

import e_learning.DTO.AuthenticationDTO;
import e_learning.entity.*;
import e_learning.enums.UserRole;
import e_learning.exceptions.ActivationException;
import e_learning.exceptions.RefreshTokenExpiredException;
import e_learning.repositories.*;
import e_learning.services.JwtService;
import e_learning.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.*;

@AllArgsConstructor
@Service
public class UserServiceImpl implements UserService, UserDetailsService {
    private final UserAppRepository userAppRepository;
    private final ValidationServiceImpl validationService;
    private final JwtService jwtService;
    private final RoleAppRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ResponsableFormationRepository responsableFormationRepository;
    private final ParticipantRepository participantRepository;
    private final ResponseRepository responseRepository;
    private final FormateurRepository formateurRepository;
    private final AdminRepository adminRepository;
    private final ChargeFormationRepository chargeFormationRepository;

    @Override
    public Participant saveUser(Participant userApp) {
        return participantRepository.save(userApp);
    }

    @Override
    public String generatePasswordResetToken(UserApp user) {
        String token = UUID.randomUUID().toString();
        user.setPasswordResetCode(token);
        user.setCodeExpirationTime(LocalDateTime.now().plusHours(1)); // Token valid for 1 hour
        userAppRepository.save(user);
        return token;
    }

    @Override
    public boolean resetPassword(UserApp user, String code, String newPassword) {
        if (!code.equals(user.getPasswordResetCode()) || user.getCodeExpirationTime().isBefore(LocalDateTime.now())) {
            return false;
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetCode(null);
        user.setCodeExpirationTime(null);
        userAppRepository.save(user);
        return true;
    }

    @Override
    public UserApp findByEmail(String email) {
        return userAppRepository.findUserAppByEmail(email);
    }

    @Override
    public boolean changePassword(UserApp user, String currentPassword, String newPassword) {
        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            return false;
        }
        user.setPassword(passwordEncoder.encode(newPassword));
        userAppRepository.save(user);
        return true;
    }
//
//    @Override
//    public ResponsableFormation saveUser(ResponsableFormation userApp) {
//        return null;
//    }
//
//    @Override
//    public ResponsableFormation registerUser(ResponsableFormation userApp) {
//        return null
//    }


    @Override
    public UserApp registerParticipant(Participant userApp){
        //check if the user with this email already exists
        if (userAppRepository.findUserAppByEmail(userApp.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        RoleApp roleApp1 = new RoleApp();
        roleApp1.setRole(UserRole.PARTICIPANT);
        roleApp1 = roleRepository.save(roleApp1);

        List<RoleApp> roles = new ArrayList<>();
        roles.add(roleApp1);
        userApp.setRoles(roles);

        String encodedPassword = passwordEncoder.encode(userApp.getPassword());
        userApp.setPassword(encodedPassword);
        userApp.setEnabled(false);
        Participant userApp1 =  participantRepository.save(userApp);
        validationService.addNewValidation(userApp);
        return userApp1;

    }


    @Override
    public UserApp registerUser(UserApp userApp,String role) {

        //check if the user with this email already exists
        if (userAppRepository.findUserAppByEmail(userApp.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        RoleApp roleApp1 = new RoleApp();
        roleApp1.setRole(UserRole.valueOf(role.toUpperCase()));
        roleApp1 = roleRepository.save(roleApp1);

        List<RoleApp> roles = new ArrayList<>();
        roles.add(roleApp1);
        userApp.setRoles(roles);

        String encodedPassword = passwordEncoder.encode(userApp.getPassword());
        userApp.setPassword(encodedPassword);
        userApp.setEnabled(true);
        if(role.equals("FORMATEUR")){
            Formateur userApp1 =  formateurRepository.save((Formateur) userApp);
            return userApp1;
        }
        else if(role.equals("RESPONSABLE")){
            ResponsableFormation userApp1 = responsableFormationRepository.save((ResponsableFormation) userApp);
            return userApp1;
        }
        else if (role.equals("CHARGE"))
        {
            ChargeFormation userApp1 =  chargeFormationRepository.save((ChargeFormation) userApp);
            return userApp1;
        }

        Participant userApp1 =  participantRepository.save((Participant) userApp);
        return userApp1;

    }

    @Override
    public Admin registerAdmin(Admin userApp) {

        //check if the user with this email already exists
        if (userAppRepository.findUserAppByEmail(userApp.getEmail()) != null) {
            throw new RuntimeException("User with this email already exists");
        }

        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();


        RoleApp roleApp1 = new RoleApp();
        roleApp1.setRole(UserRole.RESPONSABLE);
        roleApp1 = roleRepository.save(roleApp1);

        RoleApp roleApp2 = new RoleApp();
        roleApp2.setRole(UserRole.FORMATEUR);
        roleApp2 = roleRepository.save(roleApp2);

        RoleApp roleApp3 = new RoleApp();
        roleApp3.setRole(UserRole.PARTICIPANT);
        roleApp3 = roleRepository.save(roleApp3);

        RoleApp roleApp4 = new RoleApp();
        roleApp4.setRole(UserRole.ADMIN);
        roleApp4 = roleRepository.save(roleApp4);

        List<RoleApp> roles = new ArrayList<>();
        roles.add(roleApp1);
        roles.add(roleApp2);
        roles.add(roleApp3);
        roles.add(roleApp4);
        userApp.setRoles(roles);

        String encodedPassword = passwordEncoder.encode(userApp.getPassword());
        userApp.setPassword(encodedPassword);
        userApp.setEnabled(true);
        return adminRepository.save(userApp);

    }

    @Override
    public UserApp updateUser(UserApp userApp) {
        return userAppRepository.save(userApp);
    }

    @Override
    public List<UserApp> getAllUsers() {
        return userAppRepository.findAll();
    }

    @Override
     public List<UserApp> searchUsersByName(String name) {
        return userAppRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name);
    }

    @Override
    public void activationAccount(Map<String, String> activation) throws ActivationException {
        String code = activation.get("code");
        Validation validation = validationService.getValidationBuCode(code);
        if(Instant.now().isAfter(validation.getExpireAt())){
            throw new ActivationException("Code already expired!!");
        }
        UserApp user = validation.getUserApp();
        user.setEnabled(true);
        userAppRepository.save(user);
    }

    @Override
    public Map<String, String> connexion(AuthenticationDTO authenticationDTO) throws RefreshTokenExpiredException {
        return jwtService.generateToken(authenticationDTO);
    }

    @Override
    public Optional<UserApp> getUserById(Long userId) {
        return userAppRepository.findById(userId);
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserApp user = userAppRepository.findUserAppByEmail(username);
        return user;
    }

    @Override
    public List<UserApp> getActiveUsers() {
        LocalDateTime activeThreshold = LocalDateTime.now().minusMinutes(5);
        return userAppRepository.findByLastActiveTimeAfter(activeThreshold);
    }


    @Override
    public void blockUser(Long userId) {
        UserApp user = userAppRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        user.setEnabled(!user.isEnabled()); // Set the user as blocked
        userAppRepository.save(user);
    }


    @Override
    public void deleteUser(Long userId) {
        if (!userAppRepository.existsById(userId)) {
            throw new RuntimeException("User not found");
        }
        userAppRepository.deleteById(userId);
    }
}
