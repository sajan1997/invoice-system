# Invoice System

Invoice System project is built with Spring Boot. It provides APIs to create invoices,fetch invoices, pay invoices, and process overdue invoices.

## Endpoints

### Create Invoice
Creates a new invoice with the specified amount and due date.
```http
POST /invoices
{
  "amount": 150.50,
  "dueDate": "2024-06-29"
}
-> 201 Created
{
    "message": "Create Invoice",
    "data": {
        "id": 1
    }
}
```

### Get All Invoices
Retrieves all invoices with their current status and details.
```http
GET /invoices
-> 200 OK
{
    "message": "Invoices",
    "data": [
        {
            "id": 1,
            "amount": 500.55,
            "paidAmount": 0.0,
            "dueDate": "2024-06-05",
            "status": "pending"
        }
    ]
}
```
### Pay Invoice
Updates a paymentAmount for the specified invoice. If the invoice is fully paid, its status is updated to "paid".
```http
POST /invoices/{invoiceId}/payments
{
  "amount": 100.00
}
-> 200 OK
{
    "message": "Invoice payment",
    "data": {
        "id": 1,
        "amount": 500.55,
        "paidAmount": 100.0,
        "dueDate": "2024-06-05",
        "status": "pending"
    }
}
```

### Process Overdue Invoices
Processes all pending invoices that are overdue.
```http
POST /invoices/process-overdue
{
    "lateFee": 100.00,
    "overdueDays": 10
}
-> 200 OK
{
    "message": "Invoices payment overdue",
    "data": "Overdue invoices processed"
}
```
## Assumptions

* Database
  * For simplicity and ease of testing, an in-memory H2 database is used.
  
## Additional Functionality

* Validation 
    * Basic validation is added to all api to ensure that the amount, lateFee and dueDate etc fields are provided.
    * Ensures valid invoice is processed.
    * Ensures payments do not exceed the invoice amount.
* Unit Tests
  * Unit tests are provided for the controller, service and dao layer to ensure the core functionalities work as expected. 