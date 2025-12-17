import React from 'react'
import Header from './Header'
import { motion } from 'motion/react'
import AnimatedButton from './AnimatedButton'
import OrderAnalytics from './OrderAnalytics'
import MonthlyOrdersLine from './MonthlyOrdersLine'

const AdminPage = () => {
  return (
    <>
      <Header title="Admin Dashboard" signOutButtonRequired={true}/>
      <div className="grid grid-cols-10 grid-rows-20 gap-4 p-3">
        <aside className="col-span-2 row-span-10 shadow-md rounded-[10px] p-5 bg-white">
        <motion.div 
            initial={{ opacity: 0, x: -20 }}
            animate={{ opacity: 1, x: 0 }}
            transition={{ duration: 0.7, ease: 'easeInOut' }}
            className="flex flex-col space-y-4"
        >
            <h3 className="text-xl font-bold text-gray-800 mb-4 pb-3 border-b border-gray-200">Actions</h3>
            <AnimatedButton variant="primary" className="w-full">Manage Menu</AnimatedButton>
            <AnimatedButton variant="primary" className="w-full">Manage Orders</AnimatedButton>
            <AnimatedButton variant="primary" className="w-full">Manage Users</AnimatedButton>
            <AnimatedButton variant="primary" className="w-full">Manage Payments</AnimatedButton>
            <AnimatedButton variant="primary" className="w-full">Manage Discounts</AnimatedButton>
        </motion.div>
        </aside>
        <main className="col-span-8 row-span-9 overflow-y-auto">
          <div className="grid grid-cols-1 lg:grid-cols-2 gap-4 h-full p-4">
            <OrderAnalytics />
            <MonthlyOrdersLine />
          </div>
        </main>
      </div>
    </>
  )
}

export default AdminPage