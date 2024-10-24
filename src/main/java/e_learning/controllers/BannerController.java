package e_learning.controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import e_learning.DTO.BannerCreateDTO;
import e_learning.DTO.BannerDTO;
import e_learning.DTO.BannerTranslationDTO;
import e_learning.entity.*;
import e_learning.exceptions.FileStorageException;
import e_learning.services.ServiceImpl.BannerService;
import e_learning.services.ServiceImpl.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;

@RestController
@RequestMapping("/banners")
@AllArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final FileStorageService fileStorageService;

    /**
     * Fetches all banners with translations based on the 'Accept-Language' header.
     *
     * @param language The language code from the 'Accept-Language' header.
     * @return List of BannerDTO with translated content.
     */
    @GetMapping
    public ResponseEntity<List<BannerDTO>> getAllBanners(
            @RequestHeader(name = "Accept-Language", defaultValue = "en") String language) {
        List<BannerDTO> banners = bannerService.getAllBanners(language);
        return ResponseEntity.ok(banners);
    }

    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Banner> createBanner(
            @RequestParam("backgroundImage") MultipartFile backgroundImage,
            @RequestParam("link1Href") String link1Href,
            @RequestParam("link2Href") String link2Href,
            @RequestParam("translations") String translationsJson
    ) throws FileStorageException, JsonProcessingException {

        // Parse translations JSON
        ObjectMapper objectMapper = new ObjectMapper();
        List<BannerTranslationDTO> translations = objectMapper.readValue(
                translationsJson, new TypeReference<List<BannerTranslationDTO>>() {}
        );

        // Store the image
        String imagePath = fileStorageService.saveImage(backgroundImage);
        System.out.println(imagePath);

        // Create BannerCreateDTO
        BannerCreateDTO bannerCreateDTO = BannerCreateDTO.builder()
                .backgroundImage(imagePath)
                .link1Href(link1Href)
                .link2Href(link2Href)
                .translations(translations)
                .build();

        // Create banner
        Banner savedBanner = bannerService.createBanner(bannerCreateDTO);
        return ResponseEntity.ok(savedBanner);
    }

    @DeleteMapping("/{id}")
    public void deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
    }
}
