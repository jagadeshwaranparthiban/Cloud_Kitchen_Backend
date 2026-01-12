# Token Refresh Implementation Summary

## Overview

Successfully implemented access token + refresh token logic in the React frontend with automatic token refresh on 401/403 responses.

## Files Created

### `src/services/api.js` (New)

A centralized axios instance with interceptors that handles:

**Request Interceptor:**

- Automatically adds the access token to all outgoing requests via `Authorization: Bearer {token}` header

**Response Interceptor:**

- Detects 401 (Unauthorized) and 403 (Forbidden) responses
- Prevents infinite loops using `isRefreshing` flag
- Queues failed requests while token refresh is in progress
- Calls `/refresh` endpoint with the refresh token
- Stores new `accessToken` and `refreshToken` from response
- Automatically retries the failed request with the new token
- Silently handles the refresh and retry without user interruption
- Redirects to `/login` if refresh token is invalid or missing

## Files Modified

### `src/components/Login.jsx`

- Replaced `axios` with `api` service
- Now correctly stores both `accessToken` and `refreshToken` from the login response
- Removed console.log statements for cleaner code

### `src/components/Header.jsx`

- Replaced `axios` with `api` service
- Updated logout to use the new `api` instance
- Removed unnecessary console.log

### `src/components/HomePage.jsx`

- Replaced `axios` with `api` service
- Removed manual `Authorization` header (now handled by interceptor)
- Simplified API call: `api.get('/menu')` instead of `axios.get(${API_BASE_URL}/menu, {...})`

### `src/components/OrderAnalytics.jsx`

- Replaced `axios` with `api` service
- Removed manual header passing
- Simplified API call

### `src/components/MonthlyOrdersLine.jsx`

- Replaced `axios` with `api` service
- Removed manual header passing
- Simplified API call

### `src/components/Checkout.jsx`

- Replaced `axios` with `api` service
- Updated discount fetch and order placement endpoints
- Removed manual header passing

## How It Works

### Login Flow

1. User logs in with credentials
2. Backend returns `{ accessToken, refreshToken }`
3. Frontend stores both tokens in localStorage
4. User is authenticated for subsequent requests

### Request Flow

1. Every API request automatically includes the `Authorization: Bearer {accessToken}` header
2. If the request succeeds, response is returned normally

### Token Refresh Flow

1. Request fails with 401/403
2. If already refreshing, request is queued
3. If not refreshing, the `/refresh` endpoint is called with the refresh token
4. Backend returns new `{ accessToken, refreshToken }`
5. New tokens are stored in localStorage
6. All queued requests are retried with the new token
7. The original failed request is silently retried
8. If refresh fails, user is redirected to login

### Logout Flow

1. User clicks "Sign out"
2. Frontend calls `/logout` endpoint with refresh token
3. Backend deletes the refresh token from database
4. Frontend clears localStorage tokens
5. User is redirected to landing page

## Key Features

✅ **Automatic Token Refresh** - No manual token management needed  
✅ **Silent Retry** - Users don't see failed requests that are fixed by refresh  
✅ **Queue Management** - Multiple simultaneous requests don't cause multiple refresh calls  
✅ **Error Handling** - Invalid refresh tokens redirect to login  
✅ **Centralized Configuration** - Single source of truth for API base URL  
✅ **Clean Code** - No redundant Authorization headers in components

## Testing Checklist

- [ ] Login works and stores both tokens
- [ ] Requests include Authorization header automatically
- [ ] 401 response triggers token refresh
- [ ] Token refresh returns new tokens and retries request
- [ ] Invalid refresh token redirects to login
- [ ] Multiple simultaneous requests don't cause multiple refreshes
- [ ] Logout clears tokens and redirects to login
- [ ] Logout calls backend with refresh token

## Environment Variables

Make sure your `.env` file includes:

```
VITE_API_URL=http://localhost:5000
```

Or it will default to `http://localhost:5000`
