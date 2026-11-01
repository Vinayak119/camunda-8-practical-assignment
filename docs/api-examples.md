# Mock REST API examples

Start the Spring Boot application, then call the mock endpoints.

## Credit assessment
```bash
curl -X POST http://localhost:8080/mock/credit-assessment \
  -H 'Content-Type: application/json' \
  -d '{"creditScore":740}'
```
Example response: `{"creditScore":740,"creditStatus":"PASS"}`

## Inventory
```bash
curl -X POST http://localhost:8080/mock/inventory \
  -H 'Content-Type: application/json' \
  -d '{"productId":"SKU-101","quantity":2}'
```

## Payment
```bash
curl -X POST http://localhost:8080/mock/payment \
  -H 'Content-Type: application/json' \
  -d '{"paymentToken":"TEST-OK"}'
```

## Scheduling
```bash
curl -X POST http://localhost:8080/mock/appointments \
  -H 'Content-Type: application/json' \
  -d '{"patientRef":"SYN-PAT-001","forceNoSlots":false}'
```

## Simulate an outage
Add `"simulateFailure": true` to any request body. The API returns HTTP 503. Never use real credentials or patient data.