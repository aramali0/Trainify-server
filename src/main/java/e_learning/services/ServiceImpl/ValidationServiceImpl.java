package e_learning.services.ServiceImpl;

import e_learning.entity.UserApp;
import e_learning.entity.Validation;
import e_learning.repositories.ValidationRepository;
import e_learning.services.NotificationService;
import e_learning.services.ValidationService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Random;

@AllArgsConstructor
@Service
public class ValidationServiceImpl implements ValidationService {
    private final ValidationRepository validationRepository;
    private final NotificationService notificationService;
    @Override
    public Validation addNewValidation(UserApp userApp) {
        if(validationRepository.findValidationByUserApp(userApp)!=null){
            validationRepository.delete(validationRepository.findValidationByUserApp(userApp));
        }
        Validation validation = new Validation();
        validation.setUserApp(userApp);
        validation.setCreatedAt(Instant.now());
        validation.setExpireAt(validation.getCreatedAt().plus(60, ChronoUnit.MINUTES));
        Random random = new Random();
        int r=random.nextInt(999999);
        String code = String.format("%06d",r);
        validation.setCode(code);
        Validation v = validationRepository.save(validation);
        notificationService.envoyerEmailVerificationUser(validation);
        return v;
    }

    @Override
    public Validation getValidationBuCode(String code) {
        return validationRepository.findValidationByCode(code);
    }
}
