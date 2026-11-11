# Mock REST API examples

The Java worker and mock REST endpoints run on port 8081. Start Camunda Run and the worker before using these requests.

## Credit assessment
~~~powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/mock/credit-assessment" -ContentType "application/json" -Body '{"creditScore":740}'
~~~

## Inventory
~~~powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/mock/inventory" -ContentType "application/json" -Body '{"productId":"SKU-101","quantity":2}'
~~~

## Payment
~~~powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/mock/payment" -ContentType "application/json" -Body '{"paymentToken":"TEST-OK"}'
~~~

## Scheduling
~~~powershell
Invoke-RestMethod -Method Post -Uri "http://localhost:8081/mock/appointments" -ContentType "application/json" -Body '{"patientRef":"SYN-PAT-001","forceNoSlots":false}'
~~~

## Simulated outage
Add `"simulateFailure": true` to a JSON body. The endpoint returns HTTP 503. Never use real payment credentials or patient data.
