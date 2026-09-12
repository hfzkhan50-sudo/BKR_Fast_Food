# BKR Fast Food Backend

This service provides the HTTP API used by the packaged Java desktop client.

## Render deployment

1. Push this repository to GitHub.
2. In Render, choose **New > Blueprint** and select the repository.
3. Render will create the Node web service and PostgreSQL database from `render.yaml`.
4. Copy the web service URL into the desktop client's `config.properties` as `server.url=https://<service>.onrender.com/api`.

The server applies `schema.sql` automatically when `DATABASE_URL` is present. Render supplies that variable through the Blueprint database connection.

## Local development

Set `DATABASE_URL` to a PostgreSQL connection string, then run:

```text
npm install
npm start
```

The health endpoint is `GET /health`.
