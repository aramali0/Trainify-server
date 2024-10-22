package e_learning.services.ServiceImpl;
import e_learning.entity.AboutUs;
import e_learning.repositories.AboutUsRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AboutUsService {

    private final AboutUsRepository aboutUsRepository;

    public AboutUs getAboutUsContent() {
        return aboutUsRepository.findById(1L).orElse(new AboutUs());
    }

    public AboutUs updateAboutUsSection(AboutUs aboutUsSection) {
        aboutUsSection.setId(1L);
        return aboutUsRepository.save(aboutUsSection);
    }
}
