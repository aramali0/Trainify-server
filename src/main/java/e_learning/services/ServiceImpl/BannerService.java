package e_learning.services.ServiceImpl;

import e_learning.DTO.BannerCreateDTO;
import e_learning.DTO.BannerDTO;
import e_learning.entity.Banner;
import e_learning.entity.BannerTranslation;
import e_learning.repositories.BannerRepository;
import e_learning.repositories.BannerTranslationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;


import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class BannerService {

    private final BannerRepository bannerRepository;
    private final BannerTranslationRepository translationRepository;

    /**
     * Fetches all banners with translations in the specified language.
     * If a translation doesn't exist for a banner, it can fallback to a default language.
     *
     * @param language The language code (e.g., 'en', 'fr').
     * @return List of BannerDTO with translated content.
     */
    public List<BannerDTO> getAllBanners(String language) {
        List<Banner> banners = bannerRepository.findAll();
        return banners.stream().map(banner -> {
            BannerDTO dto = BannerDTO.builder()
                    .id(banner.getId())
                    .backgroundImage(banner.getBackgroundImage())
                    .link1Href(banner.getLink1Href())
                    .link2Href(banner.getLink2Href())
                    .build();

            // Fetch translation for the specified language
            BannerTranslation translation = translationRepository.findByBannerIdAndLanguage(banner.getId(), language);

            if (translation != null) {
                dto.setTitle(translation.getTitle());
                dto.setSubtitle(translation.getSubtitle());
                dto.setLink1Text(translation.getLink1Text());
                dto.setLink2Text(translation.getLink2Text());
            } else {
                // Optionally, fallback to default language (e.g., 'en')
                BannerTranslation defaultTranslation = translationRepository.findByBannerIdAndLanguage(banner.getId(), "en");

                if (defaultTranslation != null) {
                    dto.setTitle(defaultTranslation.getTitle());
                    dto.setSubtitle(defaultTranslation.getSubtitle());
                    dto.setLink1Text(defaultTranslation.getLink1Text());
                    dto.setLink2Text(defaultTranslation.getLink2Text());
                } else {
                    // Set default empty strings or handle as needed
                    dto.setTitle("");
                    dto.setSubtitle("");
                    dto.setLink1Text("");
                    dto.setLink2Text("");
                }
            }

            return dto;
        }).collect(Collectors.toList());
    }

    /**
     * Creates a new banner with translations.
     *
     * @param bannerCreateDTO The DTO containing banner data and translations.
     * @return The saved Banner entity.
     */
    public Banner createBanner(BannerCreateDTO bannerCreateDTO) {
        Banner banner = Banner.builder()
                .backgroundImage(bannerCreateDTO.getBackgroundImage())
                .link1Href(bannerCreateDTO.getLink1Href())
                .link2Href(bannerCreateDTO.getLink2Href())
                .build();

        // Add translations
        List<BannerTranslation> translations = bannerCreateDTO.getTranslations().stream().map(translationDTO ->
                BannerTranslation.builder()
                        .language(translationDTO.getLanguage())
                        .title(translationDTO.getTitle())
                        .subtitle(translationDTO.getSubtitle())
                        .link1Text(translationDTO.getLink1Text())
                        .link2Text(translationDTO.getLink2Text())
                        .banner(banner)
                        .build()
        ).collect(Collectors.toList());

        banner.setTranslations(translations);

        return bannerRepository.save(banner);
    }

    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }
}
