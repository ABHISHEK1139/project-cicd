// Single source of truth for the backend API base URL.
// Driven by the build-time env var REACT_APP_API_BASE_URL (wired in
// docker-compose.yml); falls back to the local dev default.
export const API_BASE_URL =
  process.env.REACT_APP_API_BASE_URL || "http://localhost:8080";
