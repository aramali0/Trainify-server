package e_learning.repositories;

import e_learning.entity.Admin;
import e_learning.entity.Banner;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerRepository extends JpaRepository<Banner, Long> {


}
