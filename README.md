# Camunda 8 Practical Assignment — Labs 1–3

Educational BPMN/DMN models, Java Spring Boot job workers, mock REST services, and synthetic test inputs for three Camunda 8 workflows.

## Target environment
- Camunda 8.10.7 local Camunda Run
- Java 21
- Maven 3.9+
- Camunda Spring Boot 3 Starter 8.10.7
- Spring Boot 3.5.x

Camunda's Orchestration Cluster REST API uses port 8080, the Zeebe gRPC gateway uses port 26500, and the worker plus mock REST API use port 8081.

## Start Camunda Run
Open Command Prompt and run:

~~~cmd
cd /d C:\Camunda8
c8run.exe start
~~~

Wait until Camunda Run reports that all processes are healthy.

Local demo login for Tasklist and Operate: username `demo`, password `demo`.

## Prepare and run the Java worker
From the repository root, copy the sample config:

~~~cmd
if not exist worker\src\main\resources mkdir worker\src\main\resources
copy config\application.yml.example worker\src\main\resources\application.yml
~~~

Then run the worker:

~~~cmd
cd worker
mvn clean package
mvn spring-boot:run
~~~

The worker app listens on `http://localhost:8081` and registers the job types used in the BPMN models. Keep its terminal open. Mock REST endpoints are under `/mock`.

## Connect Camunda Modeler
In Camunda Modeler, use the local Self-Managed cluster:
- Cluster endpoint: `http://localhost:8080/v2/`
- Authentication: None

Deploy each DMN file first, then its matching BPMN file:
1. `models/lab1-loan.dmn` then `models/lab1-loan.bpmn`
2. `models/lab2-priority.dmn` then `models/lab2-ecommerce.bpmn`
3. `models/lab3-triage.dmn` then `models/lab3-hospital.bpmn`

The process IDs are `loanApprovalProcess`, `ecommerceOrderProcess`, and `hospitalAppointmentProcess`. The DMN IDs are `loanDecision`, `orderPriorityDecision`, and `triageDecision`.

The BPMN models use Camunda 8 `zeebe:calledDecision` business-rule tasks and Camunda user tasks.

## Start process instances
Use the Orchestration Cluster REST API endpoint `POST http://localhost:8080/v2/process-instances`. Example PowerShell request for an approved loan:

~~~powershell
$body = @{
  processDefinitionId = "loanApprovalProcess"
  variables = @{
    applicantId = "SYN-LOAN-001"
    requestedAmount = 10000
    monthlyIncome = 5000
    employmentYears = 4
    creditScore = 740
  }
} | ConvertTo-Json -Depth 10

Invoke-RestMethod -Method Post -Uri "http://localhost:8080/v2/process-instances" -ContentType "application/json" -Body $body
~~~

Use `ecommerceOrderProcess` for Lab 2 and `hospitalAppointmentProcess` for Lab 3. More test inputs are in `tests/test-cases.json`.

## Show the practical
- Operate: `http://localhost:8080/operate` — show process instances, process path, variables, and incidents.
- Tasklist: `http://localhost:8080/tasklist` — complete manual-review and staff-review tasks.
- Worker terminal: show the worker startup and job-worker registration logs.
- Mock API: use the examples in `docs/api-examples.md`.

For a Loan manual-review demonstration use a valid synthetic application with requested amount greater than ten times monthly income and credit score at least 650, e.g. amount 40000, monthly income 3000, credit score 700.

For E-Commerce, use payment token `TEST-OK` for success or `DECLINE` for a declined payment. A high-value example uses quantity 5 and unit price 500.

For Hospital, use urgency indicator 0 for routine, 1 for priority review, and 2 for urgent human assessment. These are educational routing rules only, not clinical guidance. Use synthetic patient references only.

## Failure and recovery
Set `simulateFailure: true` in a test instance to make a mock REST endpoint return HTTP 503. Inspect retries or incidents in Operate. Record the actual behavior you observe.

## Submission evidence
See `docs/submission-checklist.md`. Validate the models, deploy them, run test instances, record actual outcomes, and capture your own Modeler, Tasklist, and Operate screenshots. No execution evidence is fabricated by this starter bundle.
