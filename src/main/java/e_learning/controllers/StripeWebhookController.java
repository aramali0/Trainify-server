package e_learning.controllers;

import com.stripe.model.Event;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import e_learning.entity.Payment;
import e_learning.repositories.PaymentRepository;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/webhook")
public class StripeWebhookController {

    private final PaymentRepository paymentRepository;

    public StripeWebhookController(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    @PostMapping("/stripe")
    public String handleStripeWebhook(@RequestBody String payload,
                                      @RequestHeader("Stripe-Signature") String sigHeader) {
        String endpointSecret = "whsec_ced7d074176170b5e549ed5ba8a9ae6d0e47f42bb89c22c3f2f64f1b3f61fdc4";

        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, endpointSecret);
        } catch (Exception e) {
            return "Webhook error: " + e.getMessage();
        }

        if ("checkout.session.completed".equals(event.getType())) {
            try {

                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(payload);
                JsonNode sessionNode = jsonNode.get("data").get("object");
                System.out.println(sessionNode);

                String sessionId = sessionNode.get("id").asText();
                String paymentStatus = sessionNode.get("payment_status").asText();
                String customerName = sessionNode.get("customer_details").get("name").asText();
                String paymentIntentId = sessionNode.get("payment_intent").asText();


                // Fetch payment intent to get the receipt URL
                PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);
                String receiptUrl = paymentIntent.getCharges().getData().get(0).getReceiptUrl();

                // Process the session data
                Payment payment = paymentRepository.findBySessionId(sessionId);
                if (payment != null && "paid".equals(paymentStatus)) {
                    payment.setStatus("paid");
                    payment.setName(customerName);
                    payment.setReceiptUrl(receiptUrl);
                    paymentRepository.save(payment);
                } else {
                    System.out.println("Payment not found or status is not 'paid' for session id: " + sessionId);
                }
            } catch (Exception e) {
                System.out.println("Failed to parse JSON: " + e.getMessage());
            }
        }

        return "Success";
    }
}
