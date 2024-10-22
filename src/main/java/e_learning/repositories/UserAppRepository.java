package e_learning.repositories;

import e_learning.entity.UserApp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAppRepository extends JpaRepository<UserApp,Long> {
    public UserApp findUserAppByEmail(String email);

    List<UserApp> findByLastActiveTimeAfter(LocalDateTime activeThreshold);

    List<UserApp> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String name, String name1);
}
