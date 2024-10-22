package e_learning.entity;


import jakarta.persistence.*;
import lombok.NoArgsConstructor;



import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "banners")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String backgroundImage;
    private String link1Href;
    private String link2Href;

    @OneToMany(mappedBy = "banner", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BannerTranslation> translations = new ArrayList<>();

    public void addTranslation(BannerTranslation translation) {
        translations.add(translation);
        translation.setBanner(this);
    }

    public void removeTranslation(BannerTranslation translation) {
        translations.remove(translation);
        translation.setBanner(null);
    }
}
