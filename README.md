# AI Research Agent

Full-stack AI research assistant built with:

- Spring Boot 3
- Spring Security + JWT
- Spring Data JPA + Hibernate
- MySQL
- Gemini API
- HTML, CSS, JavaScript, Bootstrap

## Structure

```text
backend/
frontend/
```

## Backend setup

1. Create a MySQL database or let MySQL auto-create `ai_research_agent`.
2. Set environment variables:
   - `DB_URL`
   - `DB_USERNAME`
   - `DB_PASSWORD`
   - `JWT_SECRET`
   - `GEMINI_API_KEY`
3. Run:

```bash
cd backend
mvn spring-boot:run
```

Backend runs on `http://localhost:8080`.

## Frontend setup

Serve the `frontend/` folder with any static server, for example:

```bash
cd frontend
python -m http.server 5500
```

Then open `http://localhost:5500`.

## Available APIs

- `POST /api/auth/signup`
- `POST /api/auth/login`
- `GET /api/dashboard`
- `POST /api/research`
- `GET /api/research/history`
- `POST /api/chat`
- `GET /api/chat/history`
- `POST /api/files/upload`
- `POST /api/citations`
- `POST /api/export/txt`

## Notes

- PDF summarization uses Apache PDFBox.
- AI text generation is wired to Gemini by default.
- The frontend stores the JWT token in `localStorage`.
