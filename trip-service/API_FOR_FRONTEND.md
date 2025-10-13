# Car Sharing Backend API Documentation

## Authentication
All endpoints (except login/signup/health) require a JWT token in the `Authorization` header:
```
Authorization: Bearer <JWT_TOKEN>
```

## Endpoints

### 1. Offer a Ride
- **POST** `/api/trips/offer`
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Body:**
```json
{
  "vehicleNumber": "string",
  "sourceAddress": { "latitude": float, "longitude": float, "placeAddress": "string" },
  "destinationAddress": { "latitude": float, "longitude": float, "placeAddress": "string" },
  "tripStartDateTime": "ISO8601 string",
  "tripTimezone": "string",
  "routeGeometry": { "type": "LineString", "coordinates": [[lon, lat], ...] },
  ...other fields...
}
```
- **Response:**
```json
{
  "success": true,
  "responseContent": { "tripId": "string", ... }
}
```

### 2. Find a Ride
- **POST** `/api/rides/find-ride`
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`
- **Body:**
```json
{
  "userId": "user@example.com",
  "requestContent": {
    "pickupLatitude": -6.7924,
    "pickupLongitude": 39.2083,
    "pickupLocation": "Dar es Salaam",
    "pickupRadiusKm": 5.0,
    "dropoffLatitude": -6.1630,
    "dropoffLongitude": 35.7516,
    "dropoffLocation": "Dodoma",
    "dropoffRadiusKm": 5.0,
    "departureTime": "2025-10-15T10:00:00",
    "numberOfPassengers": 2
  }
}
```
- **Response:**
```json
{
  "success": true,
  "responseContent": [
    {
      "tripId": "string",
      "driverEmail": "string",
      "vehicleNumber": "string",
      "sourceAddress": { "latitude": -6.7924, "longitude": 39.2083, "placeAddress": "Dar es Salaam" },
      "destinationAddress": { "latitude": -6.1630, "longitude": 35.7516, "placeAddress": "Dodoma" },
      "tripStartDateTime": "2025-10-15T10:00:00",
      "availableSeats": 3,
      ...
    }
  ]
}
```

### 3. Upcoming Trips (Driver)
- **GET** `/api/trips/my-trips/upcoming`
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `X-User-Id: <userId>`
- **Response:**
```json
{
  "success": true,
  "responseContent": [ { "tripId": "string", ... }, ... ]
}
```

### 4. Upcoming Rides (Passenger)
- **GET** `/api/trips/my-rides/upcoming`
- **Headers:** `Authorization: Bearer <JWT_TOKEN>`, `X-User-Id: <userId>`
- **Response:**
```json
{
  "success": true,
  "responseContent": [ { "rideId": "string", ... }, ... ]
}
```

## DTOs
- See backend code for full DTOs: `DriverUpcomingTripDTO`, `PassengerUpcomingRideDTO`, etc.
- All location fields for geospatial queries use GeoJSON format:
```json
{ "type": "Point", "coordinates": [longitude, latitude] }
```

## Notes
- Always send JWT in `Authorization` header.
- For upcoming rides/trips, the backend expects `X-User-Id` (can be extracted from JWT by gateway).
- CORS is enabled for all origins.

---
For questions, contact backend team.
