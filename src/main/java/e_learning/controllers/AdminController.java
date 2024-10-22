package e_learning.controllers;

import e_learning.entity.Payment;
import e_learning.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {

    private final PaymentRepository paymentRepository;

    @GetMapping("/payments")
    public List<Payment> getAllPayments() {
        return paymentRepository.findByStatus("paid");
    }
}
