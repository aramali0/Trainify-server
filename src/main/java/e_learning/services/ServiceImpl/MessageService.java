package e_learning.services.ServiceImpl;

import e_learning.DTO.MessageDto;
import e_learning.entity.MessageEntity;
import e_learning.entity.UserApp;
import e_learning.mappers.mappersImpl.MessageMapper;
import e_learning.repositories.MessageEntityRepository;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MessageService {

    private final MessageEntityRepository messageRepository;
    private final UserAppRepository userAppRepository;

    public List<MessageDto> getMessagesBySenderAndReceiver(Long senderId, Long receiverId) {
        List<MessageEntity> messages = messageRepository.findBySenderUserIdAndReceiverUserIdOrReceiverUserIdAndSenderUserId(senderId, receiverId);
        messages.forEach(message -> {
        message.setRead(true); // Mark as read
        messageRepository.save(message);
    });
        return messages.stream().map(MessageMapper::toDTO).collect(Collectors.toList());
    }
    public List<MessageDto> getMessagesByUser(String userId) {
        List<MessageEntity> messages = messageRepository.findByReceiverEmail(userId);
        return messages.stream().map(MessageMapper::toDTO).collect(Collectors.toList());
    }

    public MessageDto sendMessage(MessageDto messageDTO) {
        MessageMapper messageMapper = new MessageMapper();
        MessageEntity messageEntity = messageMapper.toEntity(messageDTO,userAppRepository);
        MessageEntity savedMessage = messageRepository.save(messageEntity);
        return MessageMapper.toDTO(savedMessage);
    }

    public long getUnreadMessagesCount(Long userId) {
        return messageRepository.countByReceiverUserIdAndReadIsFalse(userId);
    }

    public Map<Long, Long> getUnreadMessagesCountBySender(Long userId) {
        List<Object[]> results = messageRepository.countUnreadMessagesGroupedBySender(userId);
        return results.stream().collect(Collectors.toMap(
                result -> (Long) result[0],
                result -> (Long) result[1]
        ));
    }

}
