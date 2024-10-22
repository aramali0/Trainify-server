package e_learning.mappers.mappersImpl;


import e_learning.DTO.MessageDto;
import e_learning.entity.MessageEntity;
import e_learning.entity.UserApp;
import e_learning.repositories.UserAppRepository;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.info.ProjectInfoProperties;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public static MessageDto toDTO(MessageEntity entity) {
        return new MessageDto(
                entity.getContent(),
                entity.getTimestamp(),
                entity.isRead(),
                entity.getSender().getUserId(),
                entity.getReceiver().getUserId()
        );
    }

    public MessageEntity toEntity(MessageDto dto , UserAppRepository userAppRepository) {

        UserApp sender = userAppRepository.findById(dto.senderId()).orElseThrow(() -> new IllegalArgumentException("Invalid sender ID"));
        UserApp receiver = userAppRepository.findById(dto.receiverId()).orElseThrow(() -> new IllegalArgumentException("Invalid receiver ID"));
        MessageEntity messageEntity
                = MessageEntity.builder()
                .content(dto.content())
                        .timestamp(dto.timestamp())
                        .isRead(dto.isRead())
                        .sender(sender)
                        .receiver(receiver).build();
        return messageEntity;
    }
}
