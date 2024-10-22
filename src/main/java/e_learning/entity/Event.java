package e_learning.entity;

import lombok.*;
import jakarta.persistence.*;

import java.util.Date;

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title_en;
    private String description_en;

    private String title_fr;
    private String description_fr;

    private String title_ar;
    private String description_ar;

    private Date date;
    private String location;
    private String imagePath;
}
