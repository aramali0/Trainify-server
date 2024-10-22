package e_learning.repositories;

import e_learning.entity.Banner;
import e_learning.entity.BannerTranslation;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BannerTranslationRepository extends JpaRepository<BannerTranslation, Long> {
    BannerTranslation findByBannerIdAndLanguage(Long id, String en);
}
