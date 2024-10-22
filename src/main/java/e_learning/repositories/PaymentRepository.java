package e_learning.repositories;

import e_learning.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentRepository  extends JpaRepository<Payment, Long> {
    Payment findBySessionId(String sessionId);

    List<Payment> findByStatus(String paid);
}
