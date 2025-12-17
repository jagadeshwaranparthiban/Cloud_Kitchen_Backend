import React from 'react'
import { useQuery } from '@tanstack/react-query'
import axios from 'axios'
import { LineChart, Line, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts'
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

const months = [
  'JANUARY',
  'FEBRUARY',
  'MARCH',
  'APRIL',
  'MAY',
  'JUNE',
  'JULY',
  'AUGUST',
  'SEPTEMBER',
  'OCTOBER',
  'NOVEMBER',
  'DECEMBER',
]

const abbreviations = [
  'Jan',
  'Feb',
  'Mar',
  'Apr',
  'May',
  'Jun',
  'Jul',
  'Aug',
  'Sep',
  'Oct',
  'Nov',
  'Dec',
]

const getCurrentYear = () => new Date().getFullYear()

const normalizeMonthlyData = (apiData) => {
  const base = months.map((name, idx) => ({
    month: name,
    monthAbbr: abbreviations[idx],
    orderCount: 0,
  }))

  console.log(JSON.stringify(base))
  if (!apiData || !Array.isArray(apiData)) return base

  const map = new Map()
  apiData.forEach((item) => {
    const key = (item.month).toString().toUpperCase()
    const value = item.orderCount ?? 0
    if (key) {
      map.set(key, value)
    }
  })
  console.log(map)
  return base.map((item) => ({
    ...item,
    orderCount: map.get(item.month) ?? 0,
  }))
}

const MonthlyOrdersLine = () => {
  const token = getStoredToken()
  const currentYear = getCurrentYear()

  const {
    data: analyticsData,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ['monthlyOrders', currentYear],
    queryFn: async () => {
      const res = await axios.get(`${API_BASE_URL}/analytics/orders/monthly`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      return res.data
    },
    enabled: Boolean(token),
    retry: false,
    refetchOnWindowFocus: false,
  })

  const chartData = normalizeMonthlyData(analyticsData?.data || analyticsData)
  const totalYearOrders = chartData.reduce((sum, m) => sum + m.orderCount, 0)

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
        <h3 className="text-xl font-bold text-gray-800 mb-2">Monthly Orders</h3>
        <p className="text-red-500">Failed to load monthly analytics.</p>
        <p className="text-sm text-gray-600 mt-2">
          {error?.response?.data?.message || error?.message || 'Please try again later.'}
        </p>
      </motion.div>
    )
  }

  return (
    <motion.div
      initial={{ opacity: 0, y: 20 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.5 }}
      className="bg-white shadow-md rounded-[10px] p-6 w-full"
    >
      <div className="mb-6 flex items-start justify-between flex-wrap gap-2">
        <div>
          <h3 className="text-2xl font-bold text-gray-800 mb-1">Monthly Orders</h3>
          <p className="text-sm text-gray-600">Year: {currentYear}</p>
        </div>
        {totalYearOrders > 0 && (
          <p className="text-lg font-semibold text-orange-500">
            Total This Year: {totalYearOrders}
          </p>
        )}
      </div>

      {totalYearOrders === 0 ? (
        <div className="flex flex-col items-center justify-center h-[300px] text-gray-500">
          <p className="text-lg">No orders found for this year</p>
          <p className="text-sm mt-2">Data will appear here once orders are placed</p>
        </div>
      ) : (
        <ResponsiveContainer width="100%" height={350}>
          <LineChart data={chartData} margin={{ top: 20, right: 30, left: 10, bottom: 5 }}>
            <CartesianGrid strokeDasharray="3 3" stroke="#e5e7eb" />
            <XAxis
              dataKey="monthAbbr"
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
              formatter={(value) => [value, 'Orders']}
              labelFormatter={(label, payload) => {
                const item = payload?.[0]?.payload
                return item ? `${item.month} (${currentYear})` : label
              }}
            />
            <Line
              type="monotone"
              dataKey="orderCount"
              stroke="#f97316"
              strokeWidth={3}
              dot={{ r: 4, fill: '#f97316' }}
              activeDot={{ r: 6, stroke: '#ea580c', strokeWidth: 2, fill: '#fff' }}
            />
          </LineChart>
        </ResponsiveContainer>
      )}
    </motion.div>
  )
}

export default MonthlyOrdersLine

