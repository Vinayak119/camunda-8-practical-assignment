# Submission checklist

- [ ] Open all three BPMN files in Camunda 8 Modeler and validate them.
- [ ] Open each matching DMN file and evaluate sample inputs.
- [ ] Deploy each BPMN and DMN together.
- [ ] Start each process with the variables in `tests/test-cases.json`.
- [ ] Complete the manual review tasks in Tasklist.
- [ ] Inspect variables and incidents in Operate.
- [ ] Trigger a mock API failure, capture the incident, remove `simulateFailure`, and retry where supported.
- [ ] Record expected vs actual outcomes in your report.
- [ ] Capture your own screenshots; this bundle intentionally contains no fabricated execution evidence.