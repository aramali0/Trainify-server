package e_learning.repositories;

import e_learning.entity.ResponsableFormation;
import e_learning.entity.Response;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ResponseRepository extends JpaRepository<Response,Long> {
}
