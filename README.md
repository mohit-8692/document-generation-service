# pdf-service (classpath-backed edition)

Generic, domain-agnostic PDF generation microservice. Templates live entirely
on the classpath under `src/main/resources/templates/{templateId}/{version}/` —
no S3, no Git-as-a-service, no database. Adding or updating a template means
adding files under `resources/templates` and shipping a new build.

## Pipeline

```
Controller -> Orchestrator -> Services
```

- `controller/PdfGenerationController` — thin HTTP boundary only.
- `orchestrator/PdfGenerationOrchestrator` — owns the pipeline order:
  resolve template -> validate data -> render HTML -> render PDF. No HTTP
  concerns, no rendering logic of its own.
- `service/` — one focused service per pipeline step:
  - `TemplateResolverService` — templateId+version -> `CompiledTemplate`, cached.
  - `DataValidationService` — validates request `data` against the template's `schema.json`.
  - `TemplateEngineService` — merges `data` into the Freemarker template -> HTML.
  - `PdfRenderService` — HTML -> PDF bytes via OpenHTMLToPDF.
- `repository/ClasspathTemplateRepository` — the only class that knows templates
  live on the classpath. Everything else depends on the `TemplateRepository`
  interface, so swapping this for a database- or S3-backed implementation later
  is a one-class change.

## Template layout

```
src/main/resources/templates/
├── invoice-template/
│   └── 1.0.0/
│       ├── template.ftl
│       └── schema.json
├── order-template/
│   └── 1.0.0/
│       ├── template.ftl
│       └── schema.json
└── esim-template/
    └── 1.0.0/
        ├── template.ftl
        └── schema.json
```

To add a new template: create `templates/{new-id}/1.0.0/template.ftl` and a
matching `schema.json`, rebuild, redeploy. To publish a new version of an
existing template: add a new version folder (e.g. `1.1.0/`) alongside the old
one — the resolver picks the highest version automatically when a request
doesn't pin a specific `templateVersion`, and old versions keep working for
callers that do pin them.

## Running it

```bash
mvn spring-boot:run
```

## Example request

```bash
curl -X POST http://localhost:8080/pdf/generate \
  -H "Content-Type: application/json" \
  -o invoice.pdf \
  -d '{
    "templateId": "invoice-template",
    "data": {
      "invoiceNumber": "INV001",
      "customerName": "John",
      "amount": 1050,
      "isPaid": true,
      "items": [
        { "name": "Laptop", "price": 1000 },
        { "name": "Mouse", "price": 50 }
      ]
    }
  }'
```

Omitting `templateVersion` resolves to the latest version found on the
classpath for that `templateId`. A request with a `data` payload missing a
required field (per `schema.json`) gets a `422` with a `TEMPLATE_DATA_MISMATCH`
error body listing which fields failed, instead of a broken PDF.

## What this iteration intentionally does not have

- No external template store (S3/Git/DB) — adding a template requires a
  redeploy. See the architecture doc's storage comparison if/when that
  trade-off needs revisiting.
- No async job endpoint — generation is synchronous only.
- `PdfGenerationOptions` (locale/pageSize/orientation) is wired through but not
  yet applied to rendering — templates control page size via their own
  `@page` CSS today.
