package e_learning.DTO;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerTranslationDTO {
    private String language;
    private String title;
    private String subtitle;
    private String link1Text;
    private String link2Text;
}
