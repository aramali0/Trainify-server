package e_learning.controllers;

import e_learning.DTO.*;
import e_learning.entity.*;
import e_learning.enums.Gender;
import e_learning.enums.TypeFormateur;
import e_learning.exceptions.ActivationException;
import e_learning.exceptions.FileStorageException;
import e_learning.exceptions.RefreshTokenExpiredException;
import e_learning.mappers.mappersImpl.FormateurMapper;
import e_learning.mappers.mappersImpl.ParticipantMapper;
import e_learning.mappers.mappersImpl.ResponsableFormationMapper;
import e_learning.mappers.mappersImpl.UserMapperImpl;
import e_learning.repositories.*;
import e_learning.services.ServiceImpl.EmailService;
import e_learning.services.ServiceImpl.FileStorageService;
import e_learning.services.ServiceImpl.ValidationServiceImpl;
import e_learning.services.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.task.DelegatingSecurityContextAsyncTaskExecutor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.management.relation.Role;
import java.security.Principal;
import java.util.Collections;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/auth")
@AllArgsConstructor
public class AuthenticationController {
    private final UserAppRepository userAppRepository;
    private final EmailService emailService;
    private final FileStorageService fileStorageService;
    private final ValidationServiceImpl validationServiceImpl;
    private final EntrepriseRepository entrepriseRepository;
    private final HierarchicalUnitRepository hierarchicalUnitRepository;
    private UserService userService;



    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerUser(
            @RequestParam("matriculeId") String matriculeId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("cin") String cin,
            @RequestParam("entrepriseName") String entrepriseName,
            @RequestParam(value = "unitName", required = false) String unitName,
            @RequestParam("role") String role,
            @RequestParam(value = "typeFormateur",required = false) String typeFormateur,
            @RequestParam(value = "cabinetName",required = false) String cabinetName,
            @RequestParam(value = "cabinetNum",required = false) String cabinetNum,
            @RequestPart( value = "image",required = false)  MultipartFile image) throws FileStorageException {

        UserApp userApp;

        Optional<Entreprise> entreprise = entrepriseRepository.findByNomCommercial(entrepriseName);
        if(entreprise.isEmpty()) {
            return ResponseEntity.badRequest().body("Entreprise with id " + entrepriseName + " not found");
        }
        Entreprise en = entreprise.get();

        if(role.equals("FORMATEUR")) {
             userApp = (Formateur) new Formateur();
            ((Formateur) userApp).setCabinetName(cabinetName);
            ((Formateur) userApp).setEntreprise(en);
            ((Formateur) userApp).setCabinetNum(cabinetNum);
            ((Formateur) userApp).setTypeFormateur(TypeFormateur.valueOf(typeFormateur.toUpperCase()));
        }
        else if(role.equals("PARTICIPANT")){
            userApp =  new Participant();

            HierarchicalUnit hu = hierarchicalUnitRepository.findByName(unitName).orElse(null);

            ((Participant) userApp).setEntreprise(en);
            ((Participant) userApp).setHierarchicalUnit(hu);
        }
        else if(role.equals("RESPONSABLE")) {
            userApp = new ResponsableFormation();
            ((ResponsableFormation) userApp).setEntreprise(en);
        }
        else if (role.equals("CHARGE")) {
            userApp = new ChargeFormation();
            ((ChargeFormation) userApp).setEntreprise(en);
        }
        else {
            userApp = new ResponsableFormation();
//            ((ResponsableFormation) userApp).getEntreprise().setMaxSize(0);
            ((ResponsableFormation) userApp).setEntreprise(en);
        }

        if(image != null) {
            String imagePath = fileStorageService.storeFile(image); // Save the image and get the path
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            userApp.setProfileImagePath(imagePath);
        }

        userApp.setMatriculeId(matriculeId);
        userApp.setFirstName(firstName);
        userApp.setLastName(lastName);
        userApp.setEmail(email);
        userApp.setPassword(password);
        userApp.setNum(num);
        userApp.setGender(Gender.valueOf(gender.toUpperCase()));
        userApp.setAge(age);
        userApp.setCIN(cin);
        userApp.setVerified(true);
        userApp.setCreatedAt(new Date());

        UserApp savedUser = userService.registerUser(userApp,role);

        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Email " + email+ "already exists");
        }

        return ResponseEntity.ok(savedUser);
    }


    @PostMapping(value = "/register-admin", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerAdmin(
            @RequestParam("matriculeId") String matriculeId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("cin") String cin,
            @RequestParam("age") int age,
            @RequestPart( value = "image",required = false)  MultipartFile image) throws FileStorageException {

        Admin userApp = new Admin();

        if(image != null) {
            String imagePath = fileStorageService.storeFile(image); // Save the image and get the path
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            userApp.setProfileImagePath(imagePath);
        }

        userApp.setMatriculeId(matriculeId);
        userApp.setFirstName(firstName);
        userApp.setLastName(lastName);
        userApp.setEmail(email);
        userApp.setPassword(password);
        userApp.setNum(num);
        userApp.setGender(Gender.valueOf(gender.toUpperCase()));
        userApp.setAge(age);
        userApp.setCIN(cin);
        userApp.setVerified(true);
        userApp.setCreatedAt(new Date());

        UserApp savedUser = userService.registerAdmin(userApp);

        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Email " + email+ "already exists");
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping(value = "/participant", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerParticipant(
            @RequestParam("matriculeId") String matriculeId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("cin") String cin,
            @RequestParam("entrepriseId") String entrepriseId,
            @RequestParam(value = "unitName", required = false) String unitName,

            @RequestPart( value = "image",required = false)  MultipartFile image) throws FileStorageException {

        Participant userApp = new Participant();

        if(entrepriseRepository.findByNomCommercial(entrepriseId).isEmpty()) {

            return ResponseEntity.badRequest().body("Entreprise with name " + entrepriseId + " not found");
        }
        Entreprise en = entrepriseRepository.findByNomCommercial(entrepriseId).get();

        if(image != null) {
            String imagePath = fileStorageService.storeFile(image); // Save the image and get the path
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            userApp.setProfileImagePath(imagePath);
        }

        userApp.setMatriculeId(matriculeId);
        userApp.setFirstName(firstName);
        userApp.setLastName(lastName);
        userApp.setEmail(email);
        userApp.setPassword(password);
        userApp.setNum(num);
        userApp.setGender(Gender.valueOf(gender.toUpperCase()));
        userApp.setAge(age);
        userApp.setCIN(cin);
        userApp.setEntreprise(en);
        userApp.setHierarchicalUnit(hierarchicalUnitRepository.findByNameAndEntrepriseId(unitName,en.getId()).orElse(null));
        userApp.setCreatedAt(new Date());
        UserApp savedUser = userService.registerParticipant(userApp);
        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Email " + email+ "already exists");
        }
        en.getAppendingParticipants().add(userApp);
        entrepriseRepository.save(en);
        return ResponseEntity.ok(savedUser);
    }

    @PostMapping(value = "/responsable", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> registerResponsable(
            @RequestParam("matriculeId") String matriculeId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("cin") String cin,
            @RequestPart( value = "image",required = false)  MultipartFile image) throws FileStorageException {

        ResponsableFormation userApp = new ResponsableFormation();

        if(image != null) {
            String imagePath = fileStorageService.storeFile(image); // Save the image and get the path
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            userApp.setProfileImagePath(imagePath);
        }

        userApp.setMatriculeId(matriculeId);
        userApp.setFirstName(firstName);
        userApp.setLastName(lastName);
        userApp.setEmail(email);
        userApp.setPassword(password);
        userApp.setNum(num);
        userApp.setGender(Gender.valueOf(gender.toUpperCase()));
        userApp.setAge(age);
        userApp.setCIN(cin);
        userApp.setVerified(true);
        userApp.setCreatedAt(new Date());


        UserApp savedUser = userService.registerUser(userApp,"RESPONSABLE");

        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Email " + email+ "already exists");
        }

        return ResponseEntity.ok(savedUser);
    }

    @PostMapping(value = "/register-responsable", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> registerUserResponsable(
            @RequestParam("matriculeId") String matriculeId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("num") String num,
            @RequestParam("gender") String gender,
            @RequestParam("age") int age,
            @RequestParam("cin") String cin,
            @RequestParam(value = "unitName", required = false) String unitName,
            @RequestParam("role") String role,
            @RequestParam(value = "typeFormateur",required = false) String typeFormateur,
            @RequestParam(value = "cabinetName",required = false) String cabinetName,
            @RequestParam(value = "cabinetNum",required = false) String cabinetNum,
            @RequestPart( value = "image",required = false)  MultipartFile image,
            Principal principal
    ) throws FileStorageException {


        UserApp userApp;
        UserApp responsable =  userAppRepository.findUserAppByEmail(principal.getName());

        if(role.equals("FORMATEUR")) {
            Entreprise en = ((ResponsableFormation) responsable).getEntreprise();
            userApp = (Formateur) new Formateur();
            ((Formateur) userApp).setCabinetName(cabinetName);
            ((Formateur) userApp).setCabinetNum(cabinetNum);
            ((Formateur) userApp).setTypeFormateur(TypeFormateur.valueOf(typeFormateur.toUpperCase()));
            ((Formateur) userApp).setEntreprise(en);
        }
        else if(role.equals("PARTICIPANT")){
            userApp = new Participant();
            Entreprise en = ((ResponsableFormation) responsable).getEntreprise();
            HierarchicalUnit hu = hierarchicalUnitRepository.findByName(unitName).orElse(null);
            ((Participant) userApp).setEntreprise(en);
            ((Participant) userApp).setHierarchicalUnit(hu);
        }
        else if(role.equals("CHARGE")) {
            userApp = new ChargeFormation();
            Entreprise en = ((ResponsableFormation) responsable).getEntreprise();
            ((ChargeFormation) userApp).setEntreprise(en);
        }
        else {
            userApp = new ResponsableFormation();
            Entreprise en = ((ResponsableFormation) responsable).getEntreprise();
            ((ResponsableFormation) userApp).setEntreprise(en);
        }

        if(image != null) {
            String imagePath = fileStorageService.storeFile(image); // Save the image and get the path
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            userApp.setProfileImagePath(imagePath);
        }

        userApp.setMatriculeId(matriculeId);
        userApp.setFirstName(firstName);
        userApp.setLastName(lastName);
        userApp.setEmail(email);
        userApp.setPassword(password);
        userApp.setNum(num);
        userApp.setGender(Gender.valueOf(gender.toUpperCase()));
        userApp.setAge(age);
        userApp.setCIN(cin);
        userApp.setVerified(true);
        userApp.setCreatedAt(new Date());

        UserApp savedUser = userService.registerUser(userApp,role);

        if (savedUser == null) {
            return ResponseEntity.badRequest().body("Email " + email+ "already exists");
        }

        return ResponseEntity.ok(savedUser);
    }

 @PostMapping("/resend-code/{email}")
    public ResponseEntity<String> resendActivationCode(@PathVariable String email) {
        try {
            System.out.println("email : "+ email);
            UserApp user = userService.findByEmail(email);
            if (user == null) {
                return new ResponseEntity<>("User not found", HttpStatus.NOT_FOUND);
            }
            validationServiceImpl.addNewValidation(user);
            return new ResponseEntity<>("Activation code resent successfully", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody ForgetPasswordModel model) {


        UserApp user = userService.findByEmail(model.email());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }

        String code = userService.generatePasswordResetToken(user);

        try {
            emailService.sendEmail(user.getEmail(), "Reset your password", "Your password reset token is: " + code);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error sending email.");
        }

        return ResponseEntity.ok(Collections.singletonMap("message", "Password reset token sent."));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody ResetPasswordModel model) {


        UserApp user = userService.findByEmail(model.email());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }

        boolean success = userService.resetPassword(user, model.code(), model.newPassword());
        if (success) {
            return ResponseEntity.ok("Password reset successfully!");
        }

        return ResponseEntity.badRequest().body("Password reset failed.");
    }

    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePassword model) {
        System.out.println("model: " + model);
        UserApp user = userService.findByEmail(model.email());
        if (user == null) {
            return ResponseEntity.badRequest().body("Invalid email.");
        }

        boolean success = userService.changePassword(user, model.currentPassword(), model.newPassword());
        if (success) {
            return ResponseEntity.ok(Collections.singletonMap("message", "Password changed successfully."));
        }

        return ResponseEntity.badRequest().body("Password change failed.");
    }

    @PostMapping("/activation")
    public ResponseEntity<String> activeAccount(@RequestBody Map<String , String> code){
        try{
            userService.activationAccount(code);
            return new ResponseEntity<String>("Activation a passed avec succes", HttpStatus.valueOf(200));
        }catch(ActivationException e){
            return new ResponseEntity<>(e.getMessage(), HttpStatusCode.valueOf(406));
        }catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }


    @PostMapping("/login")
    public Map<String, String> login(@RequestBody AuthenticationDTO authenticationInfo) throws RefreshTokenExpiredException {
        return userService.connexion(authenticationInfo);
    }

    @ExceptionHandler(RefreshTokenExpiredException.class)
    public ResponseEntity<Map<String, String>> handleRefreshTokenExpiredExcception (RefreshTokenExpiredException exception){
        return ResponseEntity.status(414).body(Map.of("error", exception.getMessage()));
    }

}