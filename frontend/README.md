# Cloud Kitchen Frontend (React + Vite)

React 19 + Vite app for a cloud-kitchen experience with customer and admin flows, backed by protected APIs.

## Features
- Auth: Login/Register with roles (USER/ADMIN), JWT stored in localStorage.
- Routing: Landing, Login, Home (user), Admin dashboard; protected routes redirect to login if no token.
- Menu browsing: Home fetches menu via `/menu` with Bearer token and displays items.
- Admin dashboard:
  - Weekly orders bar chart (`/analytics/orders/weekly`, start/end params for current week).
  - Monthly orders line chart (`/analytics/orders/monthly`, current year, no params).
  - Action shortcuts (Manage Menu/Orders/Users/Payments/Discounts).
- UI: Tailwind classes, Recharts charts, motion animations, Loading states, empty/error states.

## API Expectations
- Auth: `POST /login { userName, password, role }` returns `success` token; `POST /register`.
- Menu: `GET /menu` with `Authorization: Bearer <token>`.
- Weekly analytics: `GET /analytics/orders/weekly?startDate=YYYY-MM-DD&endDate=YYYY-MM-DD`
  - Returns either `{ data: [{ date|day|weekday, orderCount|count|totalRevenue }] }` or an array.
- Monthly analytics: `GET /analytics/orders/monthly` (current year inferred)
  - Returns `{ data: [{ month: 'JANUARY', orderCount }] }` or direct array.
- Missing days/months are auto-filled with 0 on the frontend.

## Tech Stack
- React 19, Vite, React Router, React Query, Axios, Recharts, motion.
- Styling via Tailwind (through `@tailwindcss/vite`).

## Getting Started
1) Install: `npm install`
2) Run dev: `npm run dev`
3) Set env: `VITE_API_URL` (defaults to `http://localhost:5000`)
4) Login/Register via UI to obtain JWT; navigate to Admin to see analytics.
