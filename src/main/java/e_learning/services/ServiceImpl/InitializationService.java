package e_learning.services.ServiceImpl;

import e_learning.entity.Admin;
import e_learning.entity.AboutUs;
import e_learning.entity.ContactInfo;
import e_learning.enums.Gender;
import e_learning.repositories.AboutUsRepository;
import e_learning.repositories.ContactInfoRepository;
import e_learning.repositories.UserAppRepository;
import e_learning.services.UserService;
import jakarta.annotation.PostConstruct;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Date;

@Service
@AllArgsConstructor
public class InitializationService {

    private final UserAppRepository userAppRepository;
    private final AboutUsRepository aboutUsRepository;
    private final UserService userService;
    private final ContactInfoRepository contactInfoRepository;

    @PostConstruct
    public void init() {
        if (userAppRepository.findUserAppByEmail("admin@gmail.com") == null) {
            createAdmin();
        }
            createAboutUs();
        if (contactInfoRepository.findAll().isEmpty()) {
            createContactInfo();
        }
    }

    private void createAdmin() {
        Admin admin = new Admin();
        admin.setMatriculeId("000");
        admin.setFirstName("Admin");
        admin.setLastName("Admin");
        admin.setEmail("admin@gmail.com");
        admin.setPassword("admin123"); // Use hashed password in production
        admin.setNum("123456789");
        admin.setGender(Gender.MALE);
        admin.setVerified(true);
        admin.setEnabled(true);
        admin.setAge(30);
        admin.setCreatedAt(new Date());
        userService.registerAdmin(admin);
    }

    public void createAboutUs() {
        AboutUs aboutUs = new AboutUs();
        aboutUs.setTitle_en("Welcome to the Online Learning Center");
        aboutUs.setDescription_en("This is the description of the learning center.");
        aboutUs.setTitle_fr("Bienvenue au Centre d'apprentissage en ligne");
        aboutUs.setDescription_fr("Ceci est la description du centre d'apprentissage.");
        aboutUs.setTitle_ar("مرحبًا بكم في مركز التعلم عبر الإنترنت");
        aboutUs.setDescription_ar("هذه هي وصف مركز التعلم.");
        aboutUs.setId(1L);
        aboutUsRepository.save(aboutUs);
    }

    private void createContactInfo() {
        ContactInfo contactInfo = new ContactInfo();
        contactInfo.setLocation("CasaBlanca, Morocco");
        contactInfo.setEmail("mouhammedaramali@gmail.com");
        contactInfo.setPhone("123456789");
        contactInfoRepository.save(contactInfo);
    }
}
