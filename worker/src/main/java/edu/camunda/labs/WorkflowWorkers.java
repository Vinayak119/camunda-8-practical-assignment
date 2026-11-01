package edu.camunda.labs;

import io.camunda.client.annotation.JobWorker;
import io.camunda.client.api.response.ActivatedJob;
import io.camunda.client.api.worker.JobClient;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.util.*;

@Component
public class WorkflowWorkers {
    private final RestClient rest = RestClient.builder().baseUrl(
        System.getenv().getOrDefault("MOCK_API_BASE_URL", "http://localhost:8080/mock")).build();

    @JobWorker(type = "loan-validate")
    public Map<String, Object> validateLoan(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        boolean valid = nonBlank(v.get("applicantId")) && number(v.get("requestedAmount")) > 0
            && number(v.get("monthlyIncome")) > 0;
        return Map.of("validationPassed", valid);
    }

    @JobWorker(type = "loan-credit-check")
    public Map<String, Object> creditCheck(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        Map<String, Object> req = new HashMap<>();
        req.put("creditScore", v.getOrDefault("creditScore", 700));
        req.put("simulateFailure", v.getOrDefault("simulateFailure", false));
        return rest.post().uri("/credit-assessment").body(req).retrieve().body(Map.class);
    }

    @JobWorker(type = "order-validate-inventory")
    public Map<String, Object> validateOrder(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        int qty = number(v.get("quantity"));
        double unitPrice = decimal(v.get("unitPrice"));
        boolean valid = nonBlank(v.get("productId")) && qty > 0 && unitPrice >= 0;
        Map<String, Object> req = new HashMap<>(v);
        req.put("simulateFailure", v.getOrDefault("simulateFailure", false));
        Map<String, Object> stock = rest.post().uri("/inventory").body(req).retrieve().body(Map.class);
        Map<String, Object> result = new HashMap<>(stock == null ? Map.of() : stock);
        result.put("orderValid", valid);
        result.put("orderValue", qty * unitPrice);
        return result;
    }

    @JobWorker(type = "order-payment")
    public Map<String, Object> processPayment(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        Map<String, Object> req = new HashMap<>(v);
        req.put("simulateFailure", v.getOrDefault("simulateFailure", false));
        return rest.post().uri("/payment").body(req).retrieve().body(Map.class);
    }

    @JobWorker(type = "hospital-register-and-slots")
    public Map<String, Object> registerAndSlots(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        boolean valid = nonBlank(v.get("patientRef")) && nonBlank(v.get("appointmentPreference"));
        Map<String, Object> req = new HashMap<>(v);
        req.put("simulateFailure", v.getOrDefault("simulateFailure", false));
        Map<String, Object> response = rest.post().uri("/appointments").body(req).retrieve().body(Map.class);
        Map<String, Object> result = new HashMap<>(response == null ? Map.of() : response);
        result.put("registrationValid", valid);
        result.put("appointmentBooked", valid && Boolean.TRUE.equals(result.get("hasAvailability")));
        return result;
    }

    @JobWorker(type = "loan-finalize")
    public Map<String, Object> finalizeLoan(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        String decision = String.valueOf(v.getOrDefault("loanDecisionResult", "Manual Review"));
        String review = String.valueOf(v.getOrDefault("reviewOutcome", "PENDING"));
        String status = decision.equals("Manual Review") ? review : decision;
        return Map.of("finalStatus", status);
    }

    @JobWorker(type = "order-finalize")
    public Map<String, Object> finalizeOrder(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        String priority = String.valueOf(v.getOrDefault("orderPriorityResult", "Standard"));
        String payment = String.valueOf(v.getOrDefault("paymentStatus", "UNKNOWN"));
        String status = "DECLINED".equals(payment) ? "PAYMENT_DECLINED" :
            Boolean.FALSE.equals(v.get("stockAvailable")) ? "OUT_OF_STOCK" :
            ("High".equals(priority) && "PENDING".equals(v.getOrDefault("reviewOutcome", "PENDING")) ? "MANUAL_REVIEW" : "READY_FOR_FULFILMENT");
        return Map.of("orderStatus", status);
    }

    @JobWorker(type = "hospital-finalize")
    public Map<String, Object> finalizeHospital(final ActivatedJob job) {
        Map<String, Object> v = job.getVariablesAsMap();
        String triage = String.valueOf(v.getOrDefault("triageResult", "Routine"));
        String status = "Urgent Assessment".equals(triage) ? "URGENT_HUMAN_ASSESSMENT" :
            Boolean.FALSE.equals(v.get("appointmentBooked")) ? "STAFF_FOLLOW_UP" : "BOOKED";
        return Map.of("appointmentStatus", status);
    }

    private boolean nonBlank(Object o) { return o != null && !String.valueOf(o).isBlank(); }
    private int number(Object o) { try { return Integer.parseInt(String.valueOf(o)); } catch (Exception e) { return 0; } }
    private double decimal(Object o) { try { return Double.parseDouble(String.valueOf(o)); } catch (Exception e) { return 0; } }
}