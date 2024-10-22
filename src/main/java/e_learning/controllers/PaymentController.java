package e_learning.controllers;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;
import e_learning.entity.Payment;
import e_learning.repositories.PaymentRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/payment")
@AllArgsConstructor
public class PaymentController {

    private final PaymentRepository paymentRepository;

    @PostMapping("/create-checkout-session")
    public Map<String, String> createCheckoutSession(@RequestBody Map<String, Object> data) throws Exception {
        String domainUrl = "http://localhost:5173";  // Your frontend URL

        Long userId = null;
        if (data.get("userId") instanceof Integer) {
            userId = ((Integer) data.get("userId")).longValue();
        } else if (data.get("userId") instanceof String) {
            userId = Long.parseLong((String) data.get("userId"));
        }

        String plan = (String) data.get("plan");

        Long amount = null;
        if (data.get("amount") instanceof Integer) {
            amount = ((Integer) data.get("amount")).longValue();
        } else if (data.get("amount") instanceof String) {
            amount = Long.parseLong((String) data.get("amount"));
        }

        SessionCreateParams params =
                SessionCreateParams.builder()
                        .setMode(SessionCreateParams.Mode.PAYMENT)
                        .setSuccessUrl(domainUrl + "/success?session_id={CHECKOUT_SESSION_ID}") // Ensure this is correct
                        .setCancelUrl(domainUrl + "/cancel")
                        .addLineItem(
                                SessionCreateParams.LineItem.builder()
                                        .setPriceData(
                                                SessionCreateParams.LineItem.PriceData.builder()
                                                        .setCurrency("usd")
                                                        .setUnitAmount(amount)
                                                        .setProductData(
                                                                SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                        .setName(plan)
                                                                        .build())
                                                        .build())
                                        .setQuantity(1L)
                                        .build())
                        .build();

        Session session = Session.create(params);

        // Save initial payment info in database
        Payment payment = new Payment();
        payment.setUserId(userId);
        payment.setSessionId(session.getId());
        payment.setPlan(plan);
        payment.setAmount(amount);
        payment.setStatus("pending");
        payment.setCreatedAt(LocalDateTime.now());

        paymentRepository.save(payment);

        Map<String, String> responseData = new HashMap<>();
        responseData.put("id", session.getId());
        return responseData;
    }

     @GetMapping("/details")
    public ResponseEntity<Map<String, String>> getPaymentDetails(@RequestParam String session_id) {
         System.out.println("session_id: " + session_id);
        Payment payment = paymentRepository.findBySessionId(session_id);

        if (payment == null || !"paid".equals(payment.getStatus())) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }

        Map<String, String> paymentDetails = new HashMap<>();
        paymentDetails.put("name", payment.getName());
        paymentDetails.put("receiptUrl", payment.getReceiptUrl());

        return ResponseEntity.ok(paymentDetails);
    }
}
