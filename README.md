# Camunda 8 Practical Assignment — Labs 1–3 Starter Bundle

This bundle provides starter BPMN/DMN models, a Java Spring Boot worker, mock REST endpoints, synthetic test inputs, and deployment/testing instructions for all three labs.

## Important scope and assumptions
- Designed as an educational starter for **Camunda 8** with a reachable Zeebe gateway (Camunda SaaS or a local/self-managed cluster).
- The Spring Boot app uses the Camunda 8 Java client through `spring-boot-starter-camunda`/Zeebe client dependencies. Configure your cluster endpoint and credentials in environment variables.
- REST integration is demonstrated by Java workers calling the included mock REST endpoints. If your course specifically requires a native Camunda REST Connector template, add/configure that template in your target cluster and replace the corresponding worker task.
- Hospital triage rules are **educational routing rules only**, not clinical guidance. Use synthetic patient references only.
- This is a starter bundle, not evidence of deployment. Capture your own screenshots from your running environment.

## Contents
- `models/lab1-loan.bpmn`, `models/lab1-loan.dmn`
- `models/lab2-ecommerce.bpmn`, `models/lab2-priority.dmn`
- `models/lab3-hospital.bpmn`, `models/lab3-triage.dmn`
- `worker/` Spring Boot worker + mock REST API
- `tests/test-cases.json` sample test cases
- `config/application.yml.example` configuration example

## Prerequisites
1. Java 21 (or a version supported by your chosen Camunda 8 client/runtime).
2. Maven 3.9+.
3. Access to a Camunda 8 cluster and its Zeebe gateway.
4. Camunda 8 Modeler to inspect/edit BPMN and DMN files.

## Configure
Copy `config/application.yml.example` to `worker/src/main/resources/application.yml`, then fill in your own endpoint and credentials. Do not commit secrets.

Environment variables supported:
- `ZEEBE_CLIENT_CLOUD_CLUSTER_ID`
- `ZEEBE_CLIENT_CLOUD_CLIENT_ID`
- `ZEEBE_CLIENT_CLOUD_CLIENT_SECRET`
- `ZEEBE_CLIENT_BROKER_GATEWAY_ADDRESS` (for self-managed)
- `ZEEBE_CLIENT_SECURITY_PLAINTEXT` (set `true` only for local non-TLS development)
- `MOCK_API_BASE_URL` (defaults to `http://localhost:8080/mock`)

The exact client properties vary by Camunda client version and deployment type; use the application configuration appropriate to your installed Camunda 8 release.

## Build and run
From `worker/`:
```bash
mvn clean package
mvn spring-boot:run
```
The application exposes mock endpoints under `/mock`. It also registers workers for the task types used in the BPMN files.

## Deploy models
Use Camunda 8 Modeler or your cluster's deployment workflow to deploy each `.bpmn` and its matching `.dmn` file together. Ensure each DMN decision ID matches the `camunda:decisionRef` in the corresponding BPMN business-rule task:
- `loanDecision`
- `orderPriorityDecision`
- `triageDecision`

After deployment, start instances with the sample variables in `tests/test-cases.json`. Inspect user tasks in Tasklist and process state/incidents in Operate.

## Process variables (core)
| Lab | Variables |
|---|---|
| Loan | `applicantId`, `requestedAmount`, `monthlyIncome`, `employmentYears`, `validationPassed`, `creditScore`, `creditStatus`, `loanDecisionResult`, `reviewOutcome`, `finalStatus` |
| E-commerce | `customerRef`, `productId`, `quantity`, `unitPrice`, `orderValue`, `stockAvailable`, `paymentStatus`, `deliveryPreference`, `customerCategory`, `orderPriorityResult`, `reviewOutcome`, `orderStatus` |
| Hospital | `patientRef`, `appointmentPreference`, `symptomsSummary`, `urgencyIndicator`, `registrationValid`, `availableSlots`, `appointmentBooked`, `triageResult`, `staffReviewOutcome`, `appointmentStatus` |

## Failure/recovery exercise
For a controlled demo, send `X-Demo-Fail: true` to one of the mock endpoints or set `simulateFailure: true` in a request. The worker will throw an error when it receives that flag, creating a retry/incident path depending on the cluster retry configuration. Remove the flag and retry the job in Operate where supported.

## Before submission
- Open and validate each model in Camunda 8 Modeler.
- Deploy and run multiple instances.
- Record actual results, not just expected results.
- Take your own screenshots from Modeler, Tasklist, and Operate.
- Document any version-specific changes made to connectors, worker configuration, and deployment.