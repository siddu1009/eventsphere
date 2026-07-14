# EventSphere

EventSphere is a modern Indian event-management platform for discovering, booking, and managing local experiences. The interface uses Indian event examples, cities, and pricing while retaining role-based dashboards for users, organizers, and administrators. The project includes:

- A Spring Boot 3 backend with REST APIs for health checks, events, and auth
- A React + Vite frontend with a polished landing experience and demo auth flow
- H2 in-memory persistence for local development and demos

## Prerequisites

- Java 17+
- Node.js 18+
- Maven 3.9+

## Run locally

### Backend

```bash
cd backend
mvn spring-boot:run
```

The API runs internally at `http://localhost:8080/api`. End users do not need to open it.

### Frontend

```bash
cd frontend
npm install
npm run dev -- --host 0.0.0.0
```

Open only http://localhost:5173 in the browser. The Vite development server proxies all `/api` requests to the backend automatically.

## Single-domain production deployment

Serve the built frontend and forward `/api/*` to the Spring Boot service using your reverse proxy. The frontend uses the relative `/api` path, so the public URL remains a single domain such as `https://eventsphere.com`; users never need the backend address.

If the API is hosted separately intentionally, set `VITE_API_BASE_URL` at build time to its API origin.

## Demo endpoints

- Health: GET /api/health
- Events: GET /api/events
- Register: POST /api/auth/register
- Login: POST /api/auth/login
- My bookings: GET /api/bookings (Bearer token required)
- Create booking: POST /api/bookings (Bearer token required)

## Demo role accounts

The backend seeds these accounts on startup (password: `Demo1234!`):

- Organizer: `organizer@eventsphere.local`
- Admin: `admin@eventsphere.local`

New registrations create standard user accounts and can book tickets from the event detail panel.
