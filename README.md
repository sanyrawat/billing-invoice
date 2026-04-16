# invoice-pdf-generator

Full-stack invoice generation app:
- Spring Boot backend for PDF generation
- React UI for entering invoice data and downloading PDF

## Tech Stack

- Java 17
- Spring Boot 3
- Maven
- OpenPDF
- Lombok
- springdoc-openapi (Swagger UI)
- React + Vite (frontend)

## Project Structure

```text
src/main/java/com/example/invoice
    controller
    service
    service/impl
    model
    dto
    util
    config
    exception
```

## Run Backend

```bash
mvn clean install
mvn spring-boot:run
```

Application starts on:

- `http://localhost:8080`

Swagger UI:

- `http://localhost:8080/swagger-ui.html`

## Run Frontend UI

```bash
cd frontend
npm install
npm run dev
```

Frontend URL:

- `http://localhost:5173`

From the UI, fill invoice fields and click **Generate Invoice PDF**.

## API

### Generate Invoice PDF

- **Method:** `POST`
- **Endpoint:** `http://localhost:8080/api/invoices/generate`
- **Content-Type:** `application/json`
- **Response:** `application/pdf` (downloadable attachment)

Response header:

- `Content-Disposition: attachment; filename=<invoiceNumber>.pdf`

### Sample Request (Postman)

Use the JSON from:

- `postman-sample-request.json`

Or copy:

```json
{
  "invoiceNumber": "INV-2026-1001",
  "invoiceDate": "2026-04-14",
  "customerName": "John Doe",
  "customerAddress": "Lucknow, Uttar Pradesh, India",
  "items": [
    {
      "description": "Laptop",
      "quantity": 1,
      "unitPrice": 75000
    },
    {
      "description": "Mouse",
      "quantity": 2,
      "unitPrice": 1200
    }
  ],
  "taxPercentage": 18,
  "companyName": "ABC Technologies Pvt Ltd",
  "companyAddress": "Noida, India",
  "companyPhone": "9876543210",
  "companyEmail": "accounts@abctech.com",
  "companyGstin": "09ABCDE1234F1Z1",
  "companyState": "09-Uttar Pradesh",
  "customerContactNo": "9876500000",
  "poDate": "2026-04-14",
  "poNumber": "511687794906159",
  "ewayBillNumber": "411704007408",
  "bankName": "Union Bank Of India, Faizabad Road",
  "accountNumber": "555001010050732",
  "ifscCode": "UBIN0555509",
  "accountHolderName": "ABC Technologies Pvt Ltd"
}
```

## Validation Rules

- `invoiceNumber` is mandatory
- `customerName` is mandatory
- at least one item is mandatory
- `quantity > 0`
- `unitPrice > 0`

Validation errors are returned as structured JSON with field-level messages.

## Calculation Logic

- `subtotal = sum(quantity * unitPrice)`
- `taxAmount = subtotal * taxPercentage / 100`
- `grandTotal = subtotal + taxAmount`

All money operations use `BigDecimal`.
