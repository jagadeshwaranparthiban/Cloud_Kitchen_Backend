import React from 'react'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
import Loading from './Loading'
import { motion } from 'motion/react'

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

// Calculate current week (Monday to Sunday)
const getCurrentWeek = () => {
  const today = new Date()
  const dayOfWeek = today.getDay() // 0 = Sunday, 1 = Monday, etc.
  const diff = today.getDate() - dayOfWeek + (dayOfWeek === 0 ? -6 : 1) // Adjust to Monday
  
  const monday = new Date(today.setDate(diff))
  const week = []
  const dayNames = ['Monday', 'Tuesday', 'Wednesday', 'Thursday', 'Friday', 'Saturday', 'Sunday']
  const dayAbbr = ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun']
  
  for (let i = 0; i < 7; i++) {
    const date = new Date(monday)
    date.setDate(monday.getDate() + i)
    week.push({
      dayName: dayNames[i],
      dayAbbr: dayAbbr[i],
      date: date.toISOString().split('T')[0],
      orderCount: 0,
    })
  }
  
  return week
}

// Transform API data to chart format
const transformData = (apiData, weekData) => {
  if (!apiData || !Array.isArray(apiData)) {
    return weekData
  }

  // Create a map keyed by uppercase weekday or date for flexibility
  const dataMap = new Map()
  apiData.forEach((item) => {
    const weekday = (item.weekday || item.day || item.date || '').toString().toUpperCase()
    const dateKey = item.date || item.day
    const value = item.orderCount ?? item.count ?? item.totalRevenue ?? 0
    if (weekday) dataMap.set(weekday, value)
    if (dateKey) dataMap.set(dateKey, value)
  })

  // Merge API data with week structure; fall back to date map if provided
  return weekData.map((day) => {
    const weekdayKey = day.dayName.toUpperCase()
    const fromWeekday = dataMap.get(weekdayKey)
    const fromDate = dataMap.get(day.date)
    return {
      ...day,
      orderCount: fromWeekday ?? fromDate ?? 0,
    }
  })
}

const OrderAnalytics = () => {
  const token = getStoredToken()
  const weekData = getCurrentWeek()
  
  const {
    data: analyticsData,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ['orderAnalytics', weekData[0].date, weekData[6].date],
    queryFn: async () => {
      console.log(token)
      const res = await axios.get(`${API_BASE_URL}/analytics/orders/weekly`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
        params: {
          startDate: weekData[0].date,
          endDate: weekData[6].date,
        },
      })
      return res.data
    },
    enabled: Boolean(token),
    retry: false,
    refetchOnWindowFocus: false,
  })

  const chartData = transformData(analyticsData?.data || analyticsData, weekData)
  const weekRange = `${weekData[0].date} to ${weekData[6].date}`

  if (isLoading) {
    return (
      <div className="flex justify-center items-center h-[400px]">
        <Loading />
      </div>
    )
  }

  if (isError) {
    return (
      <motion.div
        initial={{ opacity: 0, y: 20 }}
        animate={{ opacity: 1, y: 0 }}
        className="bg-white shadow-md rounded-[10px] p-6"
      >
        <h3 className="text-xl font-bold text-gray-800 mb-2">Weekly Order Analytics</h3>
        <p className="text-red-500">Failed to load analytics data.</p>
        <p className="text-sm text-gray-600 mt-2">
          {error?.response?.data?.message || error?.message || 'Please try again later.'}
        </p>
      </motion.div>
    )
  }

  const totalOrders = chartData.reduce((sum, day) => sum + day.orderCount, 0)

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="bg-white shadow-md rounded-[10px] p-6 w-full"
    >
      <div className="mb-6">
        <h3 className="text-2xl font-bold text-gray-800 mb-2">Weekly Order Analytics</h3>
        <p className="text-sm text-gray-600">Current Week: {weekRange}</p>
        {totalOrders > 0 && (
          <p className="text-lg font-semibold text-orange-500 mt-2">
            Total This Week: Rs.{totalOrders}
          </p>
        )}
      </div>

      {totalOrders === 0 ? (
        <div className="flex flex-col items-center justify-center h-[300px] text-gray-500">
          <p className="text-lg">No orders found for this week</p>
          <p className="text-sm mt-2">Orders will appear here once they are placed</p>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={350}>
          <BarChart data={chartData} margin={{ top: 20, right: 30, left: 20, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis
              dataKey="dayAbbr"
              stroke="#6b7280"
              style={{ fontSize: '12px', fontWeight: '500' }}
            />
            <YAxis
              stroke="#6b7280"
              style={{ fontSize: '12px', fontWeight: '500' }}
              allowDecimals={false}
            />
            <Tooltip
              contentStyle={{
                backgroundColor: '#fff',
                border: '1px solid #e5e7eb',
                borderRadius: '8px',
                padding: '10px',
              }}
              labelFormatter={(label) => {
                const day = chartData.find((d) => d.dayAbbr === label)
                return day ? `${day.dayName} (${day.date})` : label
              }}
              formatter={(value) => [value, 'Value']}
            />
            <Bar
              dataKey="orderCount"
              fill="#f97316"
              radius={[8, 8, 0, 0]}
              stroke="#ea580c"
              strokeWidth={1}
            />
          </BarChart>
        </ResponsiveContainer>
      )}
    </motion.div>
  )
}

export default OrderAnalytics

