package e_learning.services.ServiceImpl;

import e_learning.entity.ContactMessage;
import e_learning.repositories.ContactMassageRepository;
import e_learning.repositories.MessageEntityRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ContactMessageService {
    private ContactMassageRepository messageRepository;

    public ContactMessage saveMessage(ContactMessage message) {
        return messageRepository.save(message);
    }

    public List<ContactMessage> getAllMessages() {
        return messageRepository.findAll();
    }

    public ContactMessage updateMessageResponse(Long id, String response) {
        ContactMessage message = messageRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setResponse(response);
        return messageRepository.save(message);
    }
     public List<ContactMessage> getMessagesWithResponses() {
        return messageRepository.findByResponseIsNotNull();
    }
}
