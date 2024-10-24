package e_learning.services.ServiceImpl;

import ch.qos.logback.core.util.StringUtil;
import e_learning.exceptions.FileStorageException;
import e_learning.security.FileStorageProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileStorageService {

    private final Path fileStorageLocation;

    @Value("${file.replaceUrl}")
    private String replaceUrl;

    @Autowired
    public FileStorageService(FileStorageProperties fileStorageProperties) throws FileStorageException {
        this.fileStorageLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create the directory where the uploaded files will be stored." + ex.toString());
        }
    }

    public String storeFile(MultipartFile file) throws FileStorageException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        try {
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);

            return targetLocation.toString();
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + fileName + ". Please try again!"+ ex);
        }
    }


    public String saveImage(MultipartFile image) throws FileStorageException {
//        String replaceUrl = "C:\\Users\\PC\\Desktop\\Trainify-server\\backend\\uploads\\";

        // Handle image upload
        String imagePath = null;
        if (image != null) {
            imagePath = this.storeFile(image);
            imagePath = imagePath.replace(replaceUrl, "uploads/");
        }
        return imagePath;
    }
}
