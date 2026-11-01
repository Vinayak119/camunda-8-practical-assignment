package edu.camunda.labs;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import java.util.*;

@RestController
@RequestMapping("/mock")
public class MockApiController {
    private void maybeFail(Map<String, Object> body) {
        if (Boolean.TRUE.equals(body.get("simulateFailure"))) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Simulated service outage");
        }
    }

    @PostMapping("/credit-assessment")
    public Map<String, Object> credit(@RequestBody Map<String, Object> body) {
        maybeFail(body);
        int score = number(body.getOrDefault("creditScore", 700), 700);
        return Map.of("creditScore", score, "creditStatus", score >= 650 ? "PASS" : "FAIL");
    }

    @PostMapping("/payment")
    public Map<String, Object> payment(@RequestBody Map<String, Object> body) {
        maybeFail(body);
        boolean approved = !"DECLINE".equalsIgnoreCase(String.valueOf(body.getOrDefault("paymentToken", "TEST-OK")));
        return Map.of("paymentStatus", approved ? "APPROVED" : "DECLINED",
                      "transactionRef", "MOCK-" + UUID.randomUUID().toString().substring(0, 8));
    }

    @PostMapping("/inventory")
    public Map<String, Object> inventory(@RequestBody Map<String, Object> body) {
        maybeFail(body);
        int qty = number(body.getOrDefault("quantity", 1), 1);
        boolean available = !Boolean.FALSE.equals(body.get("forceOutOfStock")) && qty <= 10;
        return Map.of("stockAvailable", available, "availableQuantity", available ? 10 : 0);
    }

    @PostMapping("/appointments")
    public Map<String, Object> appointments(@RequestBody Map<String, Object> body) {
        maybeFail(body);
        boolean noSlots = Boolean.TRUE.equals(body.get("forceNoSlots"));
        List<String> slots = noSlots ? List.of() : List.of("2026-11-02T09:00:00", "2026-11-02T11:30:00");
        return Map.of("availableSlots", slots, "hasAvailability", !slots.isEmpty());
    }

    private int number(Object value, int fallback) {
        try { return Integer.parseInt(String.valueOf(value)); }
        catch (Exception e) { return fallback; }
    }
}