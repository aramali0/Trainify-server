package e_learning.repositories;

import e_learning.entity.Cour;
import e_learning.entity.Library;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LibraryRepository extends JpaRepository<Library,Long> {
    Library findByName(String name);
    Library findByCour(Cour cour);
    List<Library> findByCourId(Long courId);

    List<Library> findByCourIn(List<Cour> cours);
}
