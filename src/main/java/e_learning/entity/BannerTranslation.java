package e_learning.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "banner_translations")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerTranslation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String language; // e.g., 'en', 'fr', 'es'

    private String title;
    private String subtitle;
    private String link1Text;
    private String link2Text;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "banner_id")
    @JsonIgnore
    private Banner banner;
}
