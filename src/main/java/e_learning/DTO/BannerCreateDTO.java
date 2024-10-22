package e_learning.DTO;


import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerCreateDTO {
    private String backgroundImage; // URL/path to the stored image
    private String link1Href;
    private String link2Href;
    private List<BannerTranslationDTO> translations;
}
