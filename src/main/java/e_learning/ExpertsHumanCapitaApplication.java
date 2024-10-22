package e_learning;

import e_learning.entity.ClassEntity;
import e_learning.entity.Participant;
import e_learning.repositories.ClassEntityRepository;
import e_learning.repositories.ParticipantRepository;
import e_learning.security.RsaKeyConfig;
import jakarta.mail.Part;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@SpringBootApplication
@EnableConfigurationProperties(RsaKeyConfig.class)
public class ExpertsHumanCapitaApplication  {
	public static void main(String[] args) {
		SpringApplication.run(ExpertsHumanCapitaApplication.class, args);
	}
}