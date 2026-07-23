# Portfolio — Masapaka Raju

A full-stack developer portfolio with a **Spring Boot REST API** backend, **React + TypeScript** frontend, PostgreSQL persistence, real-time Slack notifications, Docker containerization, and a fully automated CI/CD pipeline deploying to Render and GitHub Pages.

**Live:** [masapakaraju.github.io/portfolio](https://masapakaraju.github.io/portfolio/)

---

## Architecture

```
┌─────────────────────────────────────────────────────────────────────┐
│                         GitHub Actions                              │
│                                                                     │
│  ┌───────────┐   ┌───────────┐   ┌──────────┐   ┌──────────────┐  │
│  │  Frontend  │   │  Backend  │   │ Security │   │   Deploy     │  │
│  │  Validate  │──▶│  Validate │──▶│  Scan    │──▶│  GH Pages +  │  │
│  │  TS+ESLint │   │  Compile+ │   │  OWASP + │   │  Render      │  │
│  │  +Tests    │   │  AllTests │   │  Trivy   │   │  + Docker    │  │
│  └───────────┘   └───────────┘   └──────────┘   └──────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
         │                                              │
         ▼                                              ▼
   GitHub Pages                                    Render
   (React SPA)                              (Spring Boot API)
                                                   │
                                                   ▼
                                               PostgreSQL
```

| Layer | Technology | Deployment |
|-------|-----------|------------|
| Frontend | React 18, TypeScript, Vite 8 | GitHub Pages |
| Backend | Spring Boot 3.3, Java 17 | Render (Docker) |
| Database | PostgreSQL 16 | Render |
| Notifications | Slack Incoming Webhooks | Slack |
| CI/CD | GitHub Actions | GHCR (Docker images) |

---

## Features

- **Dynamic portfolio** — Personal info, skills, experience, and projects served from a REST API and rendered as a responsive SPA
- **Contact form with Slack notifications** — Validated form submissions trigger real-time Slack Block Kit messages with full contact details
- **Country code selector** — 29 countries with default India (+91), combined on submit as `+CODE NUMBER`
- **Client-side caching** — Portfolio data cached in localStorage; stale-while-revalidate pattern loads instantly then refreshes silently
- **Retry with backoff** — Frontend retries failed API calls up to 6 times with exponential backoff (3s×attempt, ~63s total to survive Render cold starts)
- **Scroll progress indicator** — Visual progress bar at the top of the page
- **Responsive design** — Mobile-first layout with hamburger nav, smooth scroll navigation
- **Security scanning** — OWASP Dependency Check and Trivy vulnerability scanning in CI
- **55 backend unit & integration tests** — Controller slice tests, service mock tests, exception handler tests, and a full Spring Boot integration test that validates DB schema against a real database

---

## Tech Stack

### Frontend

| Library | Purpose |
|---------|---------|
| React 18 | UI framework |
| TypeScript 5.5 | Type safety |
| Vite 8 | Build tool & dev server |
| Vitest + Testing Library | Unit testing |
| jsdom | Browser environment simulation |

### Backend

| Library | Purpose |
|---------|---------|
| Spring Boot 3.3 | REST API framework |
| Spring Data JPA | Database ORM |
| Hibernate | SQL dialect & schema management |
| PostgreSQL 16 | Persistent storage |
| Lombok | Boilerplate reduction |
| Jakarta Validation | Request validation |
| OWASP Dependency Check | Security scanning |

### DevOps

| Tool | Purpose |
|------|---------|
| Docker | Multi-stage builds for backend & frontend |
| GitHub Actions | CI/CD pipeline |
| GitHub Container Registry | Docker image hosting |
| Render | Backend hosting |
| GitHub Pages | Frontend hosting |

---

## Project Structure

```
my-portfolio/
├── backend/
│   ├── src/main/java/com/portfolio/
│   │   ├── config/           # CORS, exception handling, data seeding
│   │   ├── controller/       # REST controllers (Portfolio, Contact, Health)
│   │   ├── dto/              # Request/Response DTOs
│   │   ├── model/            # JPA entities
│   │   ├── repository/       # Spring Data repositories
│   │   └── service/          # Business logic (Contact, Portfolio, Slack)
│   ├── src/test/java/        # 55 unit & integration tests
│   ├── Dockerfile            # Multi-stage Maven build
│   └── pom.xml
├── frontend/
│   ├── src/
│   │   ├── api/              # API client with retry, caching, timeout
│   │   ├── components/       # 9 React components (Hero, About, Skills, etc.)
│   │   ├── styles/           # Global CSS
│   │   ├── test/             # Vitest + Testing Library tests
│   │   └── types/            # TypeScript interfaces
│   ├── Dockerfile            # Multi-stage Node + Nginx build
│   └── package.json
├── .github/workflows/
│   └── ci-cd-pipeline.yml    # Full CI/CD: build, test, scan, deploy
├── docker-compose.yml        # Local development stack
└── .gitignore
```

---

## API Endpoints

| Method | Path | Description |
|--------|------|-------------|
| `GET` | `/api/portfolio` | Returns complete portfolio (personal info, skills, experiences, projects) |
| `POST` | `/api/contact` | Submits contact form — persists to DB + sends Slack notification |
| `GET` | `/health` | Health check — returns `{"status": "ok"}` |

### POST /api/contact

**Request body:**

```json
{
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "+919876543210",
  "subject": "Project Inquiry",
  "message": "I'd like to discuss a project."
}
```

**Validation rules:**

| Field | Rules |
|-------|-------|
| name | Required, max 100 characters |
| email | Required, valid email format, max 254 characters |
| phone | Required, matches `+?[1-9]\d{7,14}` (8-15 digits, optional + prefix) |
| subject | Required, max 180 characters |
| message | Required, max 5000 characters |

**Response (200):**

```json
{ "message": "Your message has been sent successfully. I'll get back to you soon!" }
```

---

## Local Development

### Prerequisites

- Java 17+
- Maven 3.9+
- Node.js 20+
- Docker & Docker Compose

### With Docker Compose (recommended)

```bash
docker compose up
```

| Service | URL |
|---------|-----|
| Frontend | http://localhost |
| Backend API | http://localhost:8080/api |
| PostgreSQL | localhost:5432 |

### Without Docker

**Backend:**

```bash
cd backend
mvn spring-boot:run
```

Requires PostgreSQL running locally on port 5432 (database: `portfolio_db`, user: `postgres`, password: `1234`). The app auto-seeds portfolio data on first startup.

**Frontend:**

```bash
cd frontend
npm install
npm run dev
```

The Vite dev server proxies `/api` requests to `http://localhost:8080`.

---

## Environment Variables

### Backend

| Variable | Default | Description |
|----------|---------|-------------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://localhost:5432/portfolio_db` | PostgreSQL connection URL |
| `SPRING_DATASOURCE_USERNAME` | `postgres` | Database username |
| `SPRING_DATASOURCE_PASSWORD` | `1234` | Database password |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | `update` | Hibernate DDL strategy |
| `PORT` | `8080` | Server port |
| `CORS_ALLOWED_ORIGINS` | `http://localhost:5173,...` | Comma-separated allowed origins |
| `SLACK_WEBHOOK_URL` | _(empty)_ | Slack incoming webhook URL for contact notifications |

### Frontend

| Variable | Description |
|----------|-------------|
| `VITE_API_URL` | Backend API base URL (empty for local dev proxy) |

### CI/CD Secrets

| Secret | Description |
|--------|-------------|
| `VITE_API_URL` | Production API URL for frontend build |
| `SLACK_WEBHOOK_URL` | Slack webhook for pipeline notifications |
| `RENDER_DEPLOY_HOOK` | Render auto-deploy trigger URL |
| `NVD_API_KEY` | NIST NVD API key for OWASP scanning |

---

## Testing

### Backend (55 tests)

```bash
cd backend
mvn test
```

| Test Class | Tests | Type |
|------------|-------|------|
| `ContactControllerTest` | 21 | `@WebMvcTest` — validation, happy path, edge cases |
| `PortfolioControllerTest` | 6 | `@WebMvcTest` — portfolio retrieval, null data, multiple records |
| `HealthControllerTest` | 1 | `@WebMvcTest` — health endpoint |
| `ApiExceptionHandlerTest` | 3 | `@WebMvcTest` — generic exception handling |
| `ContactServiceTest` | 4 | Mockito — DB persistence, Slack notification, error resilience |
| `PortfolioServiceTest` | 8 | Mockito — data aggregation, empty repos, ordering |
| `SlackServiceTest` | 10 | Unit — webhook config, JSON escaping, payload structure |
| `DataSeederIntegrationTest` | 2 | `@SpringBootTest` — full context boot + DB schema validation via DataSeeder |

### Frontend (12 tests)

```bash
cd frontend
npm test
```

| Test File | Tests | Coverage |
|-----------|-------|----------|
| `Contact.test.tsx` | 12 | Form rendering, validation, submission, country code selector, error/success states |

---

## CI/CD Pipeline

The GitHub Actions workflow (`ci-cd-pipeline.yml`) runs on every push/PR to `master`, `main`, or `develop`:

```
frontend-validate ──┐
                    ├─▶ security-scan ──▶ docker-build
backend-validate ──┘         │                  │
                            │                  ▼
                            │           deploy-render
                            │                  │
                            │                  ▼
                            │      deploy-to-github-pages
                            │                  │
                            └──────────────────┘
                                       │
                                  notify (Slack)
```

### Frontend Validate
1. **Type check** — `tsc --noEmit` catches type errors
2. **ESLint** — enforces code quality rules (no warnings allowed)
3. **Unit tests** — `vitest run` runs all 12 frontend tests
4. **Build** — `npm run build` produces production bundle
5. **Build verification** — confirms `dist/index.html` exists

### Backend Validate
1. **Compile check** — `mvn compile` catches compilation errors early
2. **All tests** — `mvn test` runs 55 tests against a PostgreSQL 16 service container, including `DataSeederIntegrationTest` which validates DB schema (entity column lengths vs actual data)
3. **JAR verification** — `mvn package -DskipTests` confirms the final artifact builds
4. **Upload artifacts** — test results and JAR for downstream jobs

### Security Scan
- **OWASP Dependency Check** — scans backend dependencies for known CVEs
- **Trivy** — filesystem vulnerability scanner, results uploaded to GitHub Security tab
- **npm audit** — scans frontend dependencies

### Deploy
- **Docker Build** — multi-stage backend image pushed to GHCR
- **Render** — backend deployed via deploy hook + 5-minute wait for cold start
- **GitHub Pages** — frontend built with `VITE_API_URL` baked in and deployed
- **Notify** — Slack message with status and action buttons

---

## Database Schema

The application uses Spring Data JPA with `ddl-auto: update` to auto-create tables:

| Table | Description |
|-------|-------------|
| `personal_info` | Name, title, tagline, location, contact info, social links, about section |
| `skills` | Skill name, category, proficiency (0-100) |
| `experience` | Company, role, dates, description, highlights, display order |
| `experience_highlights` | One-to-many collection of highlight strings per experience |
| `projects` | Name, description, tech stack, URLs, featured flag, display order |
| `contact_messages` | Submitted contact form entries with timestamps and read status |

Data is seeded on application startup via `DataSeeder.java`.

---

## License

This project is not currently licensed under a specific open-source license.
