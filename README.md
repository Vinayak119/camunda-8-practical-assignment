# Camunda 8 Practical Assignment — Labs 1–3

Educational BPMN/DMN models, Java Spring Boot workers, mock REST services, and synthetic tests for three Camunda 8 workflows.

## Target stack
- Camunda 8.7.x (the worker dependency is pinned to 8.7.5)
- Java 21
- Spring Boot 3.4.x
- Maven 3.9+

The local worker app listens on port **8081**. The local Camunda Orchestration Cluster REST API remains on **8080**, and the Zeebe gRPC gateway remains on **26500**.

## Repository layout
- `models/`: three BPMN processes and their matching DMN decision tables
- `worker/`: Spring Boot application, mock REST endpoints, and Zeebe job workers
- `config/application.yml.example`: local development configuration
- `tests/test-cases.json`: synthetic test variables
- `docs/`: mock API examples and submission checklist

## Run the worker
1. Start your Camunda 8.7.x local cluster first. Confirm the Orchestration Cluster REST API at `http://localhost:8080` and Zeebe gateway at `http://localhost:26500` are reachable.
2. From the repository root, copy `config/application.yml.example` to `worker/src/main/resources/application.yml`. Keep this local configuration out of Git; `.gitignore` excludes it.
3. Open a terminal in the `worker` directory and run:

```bash
mvn clean package
mvn spring-boot:run
```

The worker application runs at `http://localhost:8081`. It registers the task types defined in the BPMN models and hosts mock endpoints under `/mock`.

## Deploy the models
Open each DMN file in Camunda Modeler and deploy it to the same cluster. Then open and deploy its matching BPMN file:
- `lab1-loan.dmn` then `lab1-loan.bpmn`
- `lab2-priority.dmn` then `lab2-ecommerce.bpmn`
- `lab3-triage.dmn` then `lab3-hospital.bpmn`

The BPMN business rule tasks use Camunda 8 `zeebe:calledDecision` extensions. Their decision IDs are `loanDecision`, `orderPriorityDecision`, and `triageDecision`.

## Start process instances
Use the worker's process-start endpoints after deploying the process models:
- `POST http://localhost:8081/api/process-instances/loan`
- `POST http://localhost:8081/api/process-instances/ecommerce`
- `POST http://localhost:8081/api/process-instances/hospital`

Each endpoint accepts the process variables as a JSON object. Example request bodies and scenarios are documented in `tests/test-cases.json`.

## Observe and complete
- Open Tasklist at `http://localhost:8080/tasklist` to see and complete manual review / staff review tasks.
- Open Operate at `http://localhost:8080/operate` to inspect process instances, variables, incidents, and completed paths.
- For controlled failure tests, set `simulateFailure: true` in the process variables. The mock REST API returns HTTP 503 and the worker fails the job, producing a retry/incident path depending on job retry configuration.

## Process variables
| Lab | Core variables |
|---|---|
| Loan | `applicantId`, `requestedAmount`, `monthlyIncome`, `employmentYears`, `validationPassed`, `creditScore`, `creditStatus`, `loanDecisionResult`, `reviewOutcome`, `finalStatus` |
| E-commerce | `customerRef`, `productId`, `quantity`, `unitPrice`, `orderValue`, `stockAvailable`, `paymentStatus`, `deliveryPreference`, `customerCategory`, `orderPriorityResult`, `reviewOutcome`, `orderStatus` |
| Hospital | `patientRef`, `appointmentPreference`, `symptomsSummary`, `urgencyIndicator`, `registrationValid`, `availableSlots`, `appointmentBooked`, `triageResult`, `staffReviewOutcome`, `appointmentStatus` |

## Submission checklist
See `docs/submission-checklist.md`. Validate, deploy, execute sample instances, record actual outcomes, and capture your own Modeler, Tasklist, and Operate screenshots. This starter does not fabricate execution evidence.

Hospital triage decisions are educational routing examples only and are not clinical guidance. Use synthetic patient references only.