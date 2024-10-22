package e_learning.DTO;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BannerDTO {
    private Long id;
    private String backgroundImage;
    private String link1Href;
    private String link2Href;
    private String title;
    private String subtitle;
    private String link1Text;
    private String link2Text;
}
