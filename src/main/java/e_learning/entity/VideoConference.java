package e_learning.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class VideoConference {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String platform;
    private String url;
    private Date startTime;

    @ManyToOne
    @JoinColumn(name = "session_id", nullable = false)
    private Session session;
}
