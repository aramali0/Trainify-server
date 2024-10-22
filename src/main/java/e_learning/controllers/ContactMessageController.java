package e_learning.controllers;


import e_learning.entity.ContactMessage;
import e_learning.services.ServiceImpl.ContactMessageService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contact-messages")
@AllArgsConstructor
public class ContactMessageController {
    private ContactMessageService messageService;

    @PostMapping
    public ResponseEntity<String> createMessage(@RequestBody ContactMessage message) {
        if (message.getName() == null || message.getEmail() == null || message.getMessage() == null) {
            return new ResponseEntity<>("Name, email, and message are required", HttpStatus.BAD_REQUEST);
        }
        messageService.saveMessage(message);
        return new ResponseEntity<>("Message received", HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getMessages() {
        List<ContactMessage> messages = messageService.getAllMessages();
        return new ResponseEntity<>(messages, HttpStatus.OK);
    }

    @PutMapping("/{id}/response")
    public ResponseEntity<ContactMessage> updateMessageResponse(
            @PathVariable Long id, @RequestBody String response) {
        System.out.println("id: " + id + " response: " + response);
        // Remove extra quotes if necessary
        response = response.replaceAll("^\"|\"$", "");
        ContactMessage updatedMessage = messageService.updateMessageResponse(id, response);
        return new ResponseEntity<>(updatedMessage, HttpStatus.OK);
    }

    @GetMapping("/faqs")
    public ResponseEntity<List<ContactMessage>> getFAQs() {
        List<ContactMessage> faqs = messageService.getMessagesWithResponses();
        return new ResponseEntity<>(faqs, HttpStatus.OK);
    }
}
