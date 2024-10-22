package e_learning.services.ServiceImpl;

import e_learning.entity.ContactInfo;
import e_learning.exceptions.ResourceNotFoundException;
import e_learning.repositories.ContactInfoRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ContactInfoService {
    private final ContactInfoRepository contactInfoRepository;

    public ContactInfo getContactInfo() {
        return contactInfoRepository.findById(1L).orElseThrow(() -> new ResourceNotFoundException("ContactInfo not found"));
    }

    public ContactInfo updateContactInfo(ContactInfo contactInfo) {
        return contactInfoRepository.save(contactInfo);
    }
}
