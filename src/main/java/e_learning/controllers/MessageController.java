package e_learning.controllers;

import e_learning.DTO.MessageDto;
import e_learning.DTO.NotificationDto;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import e_learning.services.ServiceImpl.MessageService;
import lombok.AllArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@AllArgsConstructor
@CrossOrigin(origins = "http://localhost:5137")
public class MessageController {
    private final MessageService messageService;
    private final UserAppRepository userAppRepository;
    private final SimpMessagingTemplate messagingTemplate;


    @GetMapping
    public List<MessageDto> getMessagesByUser(Principal principal) {
        String email = principal.getName();
        return messageService.getMessagesByUser(email);
    }

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public MessageDto sendMessage(@RequestBody MessageDto messageDTO, Principal principal) {
        String email = principal.getName();
        UserApp userApp = userAppRepository.findUserAppByEmail(email);

        MessageDto messageDto1 = new MessageDto(
                messageDTO.content(),
                new Date(),
                false,
                messageDTO.senderId(),
                messageDTO.receiverId()
        );

        // Save the message
        MessageDto savedMessage = messageService.sendMessage(messageDto1);

        // Send the message to the receiver
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDTO.receiverId()),
                "/queue/messages",
                savedMessage
        );

        // Send notification to the receiver (if not viewing the conversation)
        messagingTemplate.convertAndSendToUser(
                String.valueOf(messageDTO.receiverId()),
                "/queue/notifications",
                new NotificationDto(savedMessage.senderId(), savedMessage.receiverId(), "New message received")
        );

        return savedMessage;
    }

    @GetMapping("/conversation")
    @PreAuthorize("isAuthenticated()")
    public List<MessageDto> getMessagesBetweenUsers(@RequestParam Long senderId, @RequestParam Long receiverId) {
        return messageService.getMessagesBySenderAndReceiver(senderId, receiverId);
    }

    @GetMapping("/unread/{userId}")
    public long getUnreadMessagesCount(@PathVariable Long userId) {
        return messageService.getUnreadMessagesCount(userId);
    }

    @GetMapping("/unread")
    public Map<Long, Long> getUnreadMessagesCountBySender(Principal principal) {
        UserApp user = userAppRepository.findUserAppByEmail(principal.getName());
        return messageService.getUnreadMessagesCountBySender(user.getUserId());
    }

}
