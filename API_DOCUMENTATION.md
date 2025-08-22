# Ensek API Documentation

This document provides comprehensive information about the Ensek Energy Trading Platform API endpoints tested in this automation suite.

## Base URL

```
Production: https://ensekautomationcandidatetest.azurewebsites.net
Base Path: /
```

## Authentication

All API endpoints require Bearer token authentication:

```http
Authorization: Bearer YOUR_ACCESS_TOKEN
Content-Type: application/json
Accept: application/json
```

## API Endpoints

### 1. Reset Test Data

**Endpoint:** `POST /ENSEK/reset`

**Purpose:** Resets the test environment to a clean state

**Headers:**
```http
Authorization: Bearer {token}
```

**Response:**
- **200 OK**: Test data successfully reset
- **401 Unauthorized**: Invalid or missing authentication token
- **500 Internal Server Error**: Server-side error during reset

**Example Request:**
```bash
curl -X POST "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/reset" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 2. Buy Energy/Fuel

**Endpoint:** `PUT /ENSEK/buy/{id}/{quantity}`

**Purpose:** Purchase energy of a specific type and quantity

**Path Parameters:**
- `id` (integer): Energy type identifier
- `quantity` (integer): Quantity to purchase

**Headers:**
```http
Authorization: Bearer {token}
Content-Type: application/json
```

**Response Codes:**
- **200 OK**: Purchase successful
- **400 Bad Request**: Invalid fuel ID or quantity
- **401 Unauthorized**: Authentication failed
- **409 Conflict**: Insufficient stock available
- **422 Unprocessable Entity**: Validation error

**Example Requests:**

✅ **Successful Purchase:**
```bash
curl -X PUT "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/buy/1/10" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

❌ **Invalid Purchase (Negative Quantity):**
```bash
curl -X PUT "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/buy/1/-5" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

**Test Scenarios:**
- Valid fuel ID with valid quantity
- Invalid fuel ID (negative, zero, non-existent)
- Invalid quantity (negative, zero, exceeding available stock)
- Purchasing when fuel is out of stock

---

### 3. Get All Orders

**Endpoint:** `GET /ENSEK/orders`

**Purpose:** Retrieve list of all orders

**Headers:**
```http
Authorization: Bearer {token}
Accept: application/json
```

**Response:**
- **200 OK**: Orders retrieved successfully
- **401 Unauthorized**: Authentication failed
- **500 Internal Server Error**: Server error

**Response Structure:**
```json
[
  {
    "orderId": "string",
    "fuelId": integer,
    "quantity": integer,
    "timestamp": "datetime",
    "status": "string"
  }
]
```

**Example Request:**
```bash
curl -X GET "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/orders" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -H "Accept: application/json"
```

---

### 4. Get Specific Order

**Endpoint:** `GET /ENSEK/orders/{orderId}`

**Purpose:** Retrieve details of a specific order

**Path Parameters:**
- `orderId` (string): Unique order identifier

**Headers:**
```http
Authorization: Bearer {token}
Accept: application/json
```

**Response Codes:**
- **200 OK**: Order found and returned
- **404 Not Found**: Order does not exist
- **401 Unauthorized**: Authentication failed
- **400 Bad Request**: Invalid order ID format

**Response Structure:**
```json
{
  "orderId": "string",
  "fuelId": integer,
  "quantity": integer,
  "timestamp": "datetime",
  "status": "string",
  "totalCost": number
}
```

**Example Requests:**

✅ **Valid Order ID:**
```bash
curl -X GET "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/orders/ORDER123" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

❌ **Non-existent Order:**
```bash
curl -X GET "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/orders/INVALID" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 5. Get Energy Types

**Endpoint:** `GET /ENSEK/energy`

**Purpose:** Retrieve available energy types and their details

**Headers:**
```http
Authorization: Bearer {token}
Accept: application/json
```

**Response:**
- **200 OK**: Energy types retrieved successfully
- **401 Unauthorized**: Authentication failed
- **500 Internal Server Error**: Server error

**Response Structure:**
```json
[
  {
    "id": integer,
    "name": "string",
    "type": "string",
    "unit_cost": number,
    "quantity_available": integer,
    "unit": "string"
  }
]
```

**Example Energy Types:**
```json
[
  {
    "id": 1,
    "name": "Gas",
    "type": "fossil",
    "unit_cost": 10.50,
    "quantity_available": 1000,
    "unit": "kWh"
  },
  {
    "id": 2,
    "name": "Nuclear",
    "type": "nuclear",
    "unit_cost": 15.75,
    "quantity_available": 0,
    "unit": "kWh"
  },
  {
    "id": 3,
    "name": "Solar",
    "type": "renewable",
    "unit_cost": 8.25,
    "quantity_available": 500,
    "unit": "kWh"
  }
]
```

**Example Request:**
```bash
curl -X GET "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/energy" \
  -H "Authorization: Bearer YOUR_TOKEN"
```

---

### 6. User Login

**Endpoint:** `POST /ENSEK/login`

**Purpose:** Authenticate user and obtain access token

**Headers:**
```http
Content-Type: application/json
```

**Request Body:**
```json
{
  "username": "string",
  "password": "string"
}
```

**Response Codes:**
- **200 OK**: Login successful
- **401 Unauthorized**: Invalid credentials
- **400 Bad Request**: Missing or invalid request format

**Response Structure:**
```json
{
  "access_token": "string",
  "token_type": "Bearer",
  "expires_in": integer,
  "user_id": "string"
}
```

**Example Requests:**

✅ **Valid Login:**
```bash
curl -X POST "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "test",
    "password": "testing"
  }'
```

❌ **Invalid Credentials:**
```bash
curl -X POST "https://ensekautomationcandidatetest.azurewebsites.net/ENSEK/login" \
  -H "Content-Type: application/json" \
  -d '{
    "username": "invalid",
    "password": "wrong"
  }'
```

## Error Handling

### Common Error Response Structure

```json
{
  "error": "string",
  "message": "string",
  "details": "string",
  "timestamp": "datetime",
  "status": integer
}
```

### HTTP Status Codes

| Code | Meaning | Description |
|------|---------|-------------|
| 200 | OK | Request successful |
| 201 | Created | Resource created successfully |
| 400 | Bad Request | Invalid request format or parameters |
| 401 | Unauthorized | Authentication required or failed |
| 403 | Forbidden | Access denied |
| 404 | Not Found | Resource not found |
| 409 | Conflict | Resource conflict (e.g., insufficient stock) |
| 422 | Unprocessable Entity | Validation error |
| 500 | Internal Server Error | Server-side error |

## Rate Limiting

The API may implement rate limiting. If you encounter 429 Too Many Requests, implement exponential backoff:

```
Initial delay: 1 second
Maximum delay: 60 seconds
Backoff factor: 2
```

## Security Considerations

### Authentication Token
- Tokens may expire and need renewal
- Store tokens securely
- Use HTTPS for all requests
- Include User-Agent header for tracking

### Input Validation
- Validate all input parameters
- Sanitize string inputs
- Check numeric ranges
- Verify required fields

## Testing Strategies

### Positive Testing
- Valid inputs within expected ranges
- Successful authentication
- Available resources access

### Negative Testing
- Invalid authentication tokens
- Out-of-range parameters
- Non-existent resources
- Invalid input formats

### Edge Cases
- Boundary value testing (0, negative numbers)
- Maximum/minimum quantities
- Empty or null values
- Special characters in strings

### Performance Testing
- Response time validation (< 5 seconds recommended)
- Concurrent request handling
- Large data set processing

## API Versioning

Currently testing against version 1.0 of the Ensek API. Future versions may include:
- `/v2/ENSEK/` endpoint prefix
- Additional response fields
- Enhanced error messaging
- New authentication methods

## Support and Issues

For API-related issues during testing:

1. **Authentication Problems**: Verify token validity and format
2. **Network Issues**: Check connectivity and DNS resolution
3. **Data Issues**: Reset test data using `/ENSEK/reset`
4. **Validation Errors**: Review request format and required fields

## Changelog

### Version 1.0
- Initial API implementation
- Basic CRUD operations for energy trading
- Bearer token authentication
- JSON request/response format

---

**Note:** This documentation is based on reverse engineering the API behavior during test development. Actual API specifications may differ and should be confirmed with Ensek's official documentation.