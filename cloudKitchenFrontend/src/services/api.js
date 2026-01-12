import axios from 'axios'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:5000'

// Helper function to safely parse stored tokens
const getStoredToken = () => {
  const raw = localStorage.getItem('jwt')
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw)
    if (typeof parsed === 'string') return parsed
    return parsed
  } catch {
    return raw
  }
}

const getStoredRefreshToken = () => {
  const raw = localStorage.getItem('refresh')
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw)
    if (typeof parsed === 'string') return parsed
    return parsed
  } catch {
    return raw
  }
}

// Create axios instance
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
})

// Flag to prevent infinite refresh loops
let isRefreshing = false
let failedQueue = []

const processQueue = (error, token = null) => {
  failedQueue.forEach(prom => {
    if (error) {
      prom.reject(error)
    } else {
      prom.resolve(token)
    }
  })
  failedQueue = []
}

// Request interceptor to add token to headers
api.interceptors.request.use(
  (config) => {
    const token = getStoredToken()
    if (token) {
      config.headers.Authorization = `Bearer ${token}`
    }
    return config
  },
  (error) => Promise.reject(error)
)

// Response interceptor to handle token refresh
api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config

    // Check if error is 401 (Unauthorized) or 403 (Forbidden)
    if ((error.response?.status === 401 || error.response?.status === 403) && originalRequest && !originalRequest._retry) {
      if (isRefreshing) {
        // Queue the request if already refreshing
        return new Promise((resolve, reject) => {
          failedQueue.push({ resolve, reject })
        })
          .then((token) => {
            originalRequest.headers.Authorization = `Bearer ${token}`
            return api(originalRequest)
          })
          .catch((err) => Promise.reject(err))
      }

      originalRequest._retry = true
      isRefreshing = true

      try {
        const refreshToken = getStoredRefreshToken()
        if (!refreshToken) {
          // No refresh token available, redirect to login
          processQueue(new Error('No refresh token'), null)
          localStorage.removeItem('jwt')
          localStorage.removeItem('refresh')
          localStorage.removeItem('username')
          window.location.href = '/login'
          return Promise.reject(error)
        }

        // Call refresh endpoint with a plain axios instance to avoid interceptor loop
        const refreshResponse = await axios.post(`${API_BASE_URL}/refresh`, {
          refreshToken,
        })

        if (refreshResponse.data.accessToken && refreshResponse.data.refreshToken) {
          // Store new tokens
          localStorage.setItem('jwt', typeof refreshResponse.data.accessToken === 'string' 
            ? refreshResponse.data.accessToken 
            : JSON.stringify(refreshResponse.data.accessToken))
          localStorage.setItem('refresh', typeof refreshResponse.data.refreshToken === 'string' 
            ? refreshResponse.data.refreshToken 
            : JSON.stringify(refreshResponse.data.refreshToken))

          // Update the failed request with new token
          const newToken = refreshResponse.data.accessToken
          originalRequest.headers.Authorization = `Bearer ${newToken}`

          // Process queued requests with new token
          processQueue(null, newToken)

          // Retry the original request with new token
          return api(originalRequest)
        } else {
          // Refresh failed, redirect to login
          processQueue(new Error('Refresh token invalid'), null)
          localStorage.removeItem('jwt')
          localStorage.removeItem('refresh')
          localStorage.removeItem('username')
          window.location.href = '/login'
          return Promise.reject(new Error('Failed to refresh token'))
        }
      } catch (refreshError) {
        // Refresh endpoint failed, redirect to login
        processQueue(refreshError, null)
        localStorage.removeItem('jwt')
        localStorage.removeItem('refresh')
        localStorage.removeItem('username')
        window.location.href = '/login'
        return Promise.reject(refreshError)
      } finally {
        isRefreshing = false
      }
    }

    return Promise.reject(error)
  }
)

export default api
