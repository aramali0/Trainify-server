package e_learning.services;

import e_learning.DTO.AuthenticationDTO;
import e_learning.entity.*;
import e_learning.exceptions.ActivationException;
import e_learning.exceptions.RefreshTokenExpiredException;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    Participant saveUser(Participant userApp);

    public String generatePasswordResetToken(UserApp user);

    public boolean resetPassword(UserApp user, String code, String newPassword) ;
    public UserApp findByEmail(String email);
    public boolean changePassword(UserApp user, String currentPassword, String newPassword);
    UserApp registerParticipant(Participant userApp);
    UserApp registerUser(UserApp userApp , String role);
    public UserApp updateUser(UserApp userApp);
    public List<UserApp> getAllUsers();
    List<UserApp> searchUsersByName(String name);
    public void activationAccount(Map<String, String> code) throws ActivationException;
    public Map <String,String> connexion(AuthenticationDTO authenticationDTO) throws RefreshTokenExpiredException;
    public Optional<UserApp> getUserById(Long userId);
    Admin registerAdmin(Admin userApp);
    List<UserApp> getActiveUsers();
    void blockUser(Long userId);
    void deleteUser(Long userId);
}
