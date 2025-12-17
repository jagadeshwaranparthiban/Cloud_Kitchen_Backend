import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import axios from 'axios'
import AnimatedButton from './AnimatedButton'
import bg from '../assets/CKitchen_Background.jpg'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:5000'

const getStoredToken = () => {
  const raw = localStorage.getItem('jwt')
  if (!raw) return null
  try {
    const parsed = JSON.parse(raw)
    if (typeof parsed === 'string') return parsed
    if (parsed?.token) return parsed.token
    return parsed
  } catch {
    return raw
  }
}

const Login = () => {
  const [username, setUsername] = useState('')
  const [emailId, setEmailId] = useState('')
  const [password, setPassword] = useState('')
  const [confirmedPassword, setConfirmedPassword] = useState('')
  const [error, setError] = useState('')
  const [role, setRole] = useState('')
  const [loading, setLoading] = useState(false)
  const [toggle, setToggle] = useState(false)

  const navigate = useNavigate()

  const handleLogin = async (e) => {
    e.preventDefault()
    if (!username || !password || !role) {
      setError('Please fill all the fields')
      return
    }
    setError('')
    setLoading(true)
    try {
      const res = await axios.post(`${API_BASE_URL}/login`, {
        userName: username,
        password,
        role: `ROLE_${role}`,
      })
      if (res.data.success) {
        localStorage.setItem('jwt', typeof res.data.success === 'string' ? res.data.success : JSON.stringify(res.data.success))
        localStorage.setItem('username', username)
        if (role === 'ADMIN') {
          navigate('/admin')
        } else {
          navigate('/home', { state: { username } })
        }
      } else if (res.data.exception) {
        setError(res.data.exception)
      } else {
        setError('Unable to login. Please try again.')
      }
    } catch (err) {
      setError(err.response?.data?.exception || 'Unable to login. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleRegister = async (e) => {
    e.preventDefault()
    if (!username || !emailId || !password || !confirmedPassword) {
      setError('Please fill all the fields')
      return
    }
    if (password !== confirmedPassword) {
      setError('Passwords do not match')
      return
    }
    setError('')
    setLoading(true)
    try {
      const res = await axios.post(`${API_BASE_URL}/register`, {
        userName: username,
        emailId,
        password,
        role: `ROLE_USER`,
      })
      if (res.data.success) {
        localStorage.setItem('jwt', typeof res.data.success === 'string' ? res.data.success : JSON.stringify(res.data.success))
        localStorage.setItem('username', username)
        handleToggle()
        alert('Registration successful. Please login to continue.')
      } else {
        setError(res.data.exception || 'Registration failed. Please try again.')
      }
    } catch (err) {
      setError(err.response?.data?.exception || 'Registration failed. Please try again.')
    } finally {
      setLoading(false)
    }
  }

  const handleToggle = () => {
    setToggle(!toggle)
    setError('')
    setUsername('')
    setEmailId('')
    setPassword('')
    setConfirmedPassword('')
    setRole('')
  }

  return (
    <div className="relative min-h-screen">
      <div className="absolute inset-0 -z-10">
        <img src={bg} alt="CKitchen background" className="w-full h-full object-cover filter blur-sm brightness-75" />
        <div className="absolute inset-0 bg-black opacity-20" />
      </div>

      <div className="flex items-center justify-center min-h-screen">
        <div className="w-1/2 flex flex-col rounded-lg shadow-2xl p-8 sm:flex-row bg-white/80 backdrop-blur-sm">
        <div
          className={`w-1/2 flex flex-col gap-3 items-center justify-center rounded-lg transition duration-200 ${
            toggle ? 'px-5 mt-5' : 'bg-orange-500'
          }`}
        >
          {toggle ? (
            <>
              <h2 className="text-3xl font-medium">Register</h2>
              <form className="w-full mt-3 flex flex-col">
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Username: </label>
                  <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Enter your username"
                    required
                    className="input_custom"
                  />
                  <span className="text-sm text-orange-500 mb-2">Your role will be USER by default.</span>
                </div>
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Email ID: </label>
                  <input
                    type="email"
                    value={emailId}
                    onChange={(e) => setEmailId(e.target.value)}
                    placeholder="Enter your email id"
                    required
                    className="input_custom"
                  />
                </div>
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Password: </label>
                  <input
                    type="text"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    required
                    className="input_custom"
                  />
                </div>
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Confirm Password: </label>
                  <input
                    type="password"
                    value={confirmedPassword}
                    onChange={(e) => setConfirmedPassword(e.target.value)}
                    required
                    className="input_custom"
                  />
                </div>
                <AnimatedButton 
                  variant="primary" 
                  onClick={handleRegister} 
                  disabled={loading}
                  className="w-full rounded-full mt-3"
                >
                  {loading ? 'Registering...' : 'Register'}
                </AnimatedButton>
                {error && <span className="text-sm text-red-500 mt-2 self-center">{error}</span>}
              </form>
            </>
          ) : (
            <button
              className="px-4 py-2 bg-transparent text-white text-2xl font-medium rounded-full hover:bg-white hover:text-orange-500 transition duration-300 ease-in-out cursor-pointer"
              onClick={handleToggle}
            >
              New user? register now!
            </button>
          )}
        </div>
        <div
          className={`w-1/2 flex flex-col gap-3 items-center justify-center rounded-lg transition duration-200 ${
            toggle ? 'bg-orange-500' : 'px-5 mt-5'
          }`}
        >
          {toggle ? (
            <button
              className="px-4 py-2 bg-transparent text-white text-2xl font-medium rounded-full hover:bg-white hover:text-orange-500 transition duration-300 ease-in-out cursor-pointer"
              onClick={handleToggle}
            >
              Already our user? Login
            </button>
          ) : (
            <>
              <h2 className="text-3xl font-medium">Login</h2>
              <form className="w-full mt-3 flex flex-col">
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Username: </label>
                  <input
                    type="text"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    placeholder="Enter your username"
                    required
                    className="input_custom"
                  />
                </div>
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Password: </label>
                  <input
                    type="password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    placeholder="Enter your password"
                    required
                    className="input_custom"
                  />
                </div>
                <div className="mb-4 flex flex-col gap-2">
                  <label className="block mb-2 text-md font-medium text-gray-900">Role: </label>
                  <select className="input_custom" value={role} onChange={(e) => setRole(e.target.value)} required>
                    <option value="">Select your role</option>
                    <option value="USER" className="option_custom">
                      USER
                    </option>
                    <option value="ADMIN" className="option_custom">
                      ADMIN
                    </option>
                  </select>
                </div>
                <AnimatedButton 
                  variant="primary" 
                  onClick={handleLogin} 
                  disabled={loading}
                  className="w-full rounded-full mt-3"
                >
                  {loading ? 'Signing in...' : 'Login'}
                </AnimatedButton>
                {error && <span className="text-sm text-red-500 mt-2 self-center">{error}</span>}
              </form>
            </>
          )}
        </div>
      </div>
    </div>
    </div>
  )
}

export default Login