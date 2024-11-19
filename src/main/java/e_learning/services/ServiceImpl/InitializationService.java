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
        aboutUs.setTitle_en("Welcome to EHC Learning and coaching");
        aboutUs.setDescription_en("Our online coaching and training services management platform, which offers complete management of online learning/coaching features adapted to different types of users, such as administrators , training/project managers, coaches, trainers, participants");
        aboutUs.setTitle_fr("Bienvenue chez EHC Learning & coaching");
        aboutUs.setDescription_fr("Notre plateforme de gestion de prestations coaching et formation en ligne, qui offre une gestion complète de fonctionnalités d'apprentissage/coaching en ligne adaptées à différents types d'utilisateurs ,tels que les administrateurs ,les responsables de formation/projet ,coachs ,formateurs ,participants");
        aboutUs.setTitle_ar("منصة التدريب وإدارة الخدمات لدينا");
        aboutUs.setDescription_ar("منصة التدريب وإدارة الخدمات لدينا التدريب عبر الإنترنت،تقدم إدارة كاملة للتعلم/التدريب عبر الإنترنت تتكيف مع أنواع مختلفة من المستخدمين، مثل المسؤولين، ومديري التدريب/المشاريع، والمدربين، والمشاركين");
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
