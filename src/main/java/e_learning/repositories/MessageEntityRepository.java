package e_learning.repositories;

import e_learning.entity.ClassEntity;
import e_learning.entity.MessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface MessageEntityRepository extends JpaRepository<MessageEntity,Long> {
    List<MessageEntity> findByReceiverUserId(Long receiverId);
    List<MessageEntity> findByReceiverEmail(String email);
    List<MessageEntity> findBySenderEmail(String email);
    List<MessageEntity> findBySenderUserIdAndReceiverUserId(Long senderId, Long receiverId);
    List<MessageEntity> findBySenderUserId(Long senderId);
    @Query("SELECT m FROM MessageEntity m WHERE (m.sender.userId = :senderId AND m.receiver.userId = :receiverId) OR (m.sender.userId = :receiverId AND m.receiver.userId = :senderId) ORDER BY m.timestamp ASC")
    List<MessageEntity> findBySenderUserIdAndReceiverUserIdOrReceiverUserIdAndSenderUserId(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.sender.userId = :senderId AND m.receiver.userId = :receiverId AND m.isRead = false")
    int countUnreadMessagesBetweenUsers(@Param("senderId") Long senderId, @Param("receiverId") Long receiverId);

    @Query("SELECT COUNT(m) FROM MessageEntity m WHERE m.receiver.userId = :userId AND  m.isRead = false")
    long countByReceiverUserIdAndReadIsFalse(@Param("userId") Long userId);

    @Query("SELECT m.sender.userId, COUNT(m) FROM MessageEntity m WHERE m.receiver.userId = :userId AND m.isRead = false GROUP BY m.sender.userId")
    List<Object[]> countUnreadMessagesGroupedBySender(@Param("userId") Long userId);
}
