package e_learning.services.ServiceImpl;

import e_learning.exceptions.FileStorageException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import java.util.Map;

@Service
public class FileStorageService {

    private final Cloudinary cloudinary;

    @Autowired
    public FileStorageService(@Value("${cloudinary.cloud_name}") String cloudName,
                              @Value("${cloudinary.api_key}") String apiKey,
                              @Value("${cloudinary.api_secret}") String apiSecret) {
        this.cloudinary = new Cloudinary(ObjectUtils.asMap(
                "cloud_name", cloudName,
                "api_key", apiKey,
                "api_secret", apiSecret
        ));
    }

    public String storeFile(MultipartFile file)  throws FileStorageException {
        try {
            // Upload file to Cloudinary
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), ObjectUtils.emptyMap());

            // Get the secure URL for accessing the file
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new FileStorageException("Could not upload file to Cloudinary. " + e.getMessage());
        }
    }

    public String saveImage(MultipartFile image) throws FileStorageException {
        return storeFile(image);
    }

    public String saveVideo(MultipartFile video) throws FileStorageException {
        return storeFile(video);
    }

    public String saveDocument(MultipartFile document) throws FileStorageException {
        return storeFile(document);
    }
}
