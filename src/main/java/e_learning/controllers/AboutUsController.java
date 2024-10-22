package e_learning.controllers;

import e_learning.entity.AboutUs;
import e_learning.exceptions.FileStorageException;
import e_learning.services.ServiceImpl.AboutUsService;
import e_learning.services.ServiceImpl.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/about-us")
@AllArgsConstructor
public class AboutUsController {
    private final AboutUsService aboutUsService;
    private final FileStorageService fileStorageService;

    @GetMapping
    public AboutUs getAboutUs() {
        return aboutUsService.getAboutUsContent();
    }

    @PutMapping
    public ResponseEntity<AboutUs> updateAboutUsSection(
            @RequestPart("aboutUsSection") AboutUs aboutUsSection,
            @RequestPart(value = "image", required = false) MultipartFile image) throws IOException, FileStorageException {

        if (image != null && !image.isEmpty()) {
            String imagePath = fileStorageService.storeFile(image);
            imagePath = imagePath.replace("app\\uploads\\", "uploads/");
            aboutUsSection.setImagePath(imagePath);
        }

        AboutUs updatedSection = aboutUsService.updateAboutUsSection(aboutUsSection);
        return ResponseEntity.ok(updatedSection);
    }
}
