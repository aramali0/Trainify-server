package e_learning.services.ServiceImpl;

import e_learning.entity.Validation;
import e_learning.services.NotificationService;
import lombok.AllArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
//    private final NotificationRepository notificationRepository;
    private final JavaMailSender javaMailSender;
    @Override
    public void envoyerEmailVerificationUser(Validation validation) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setFrom("mouhammedaramali@gmail.com");
        simpleMailMessage.setTo(validation.getUserApp().getEmail());
        simpleMailMessage.setSubject("Votre code d'activation");
        String txt = String.format("Bonjour %s \nVotre code d'activation %s", validation.getUserApp().getUsername(), validation.getCode());
        simpleMailMessage.setText(txt);
        javaMailSender.send(simpleMailMessage);
    }
}
