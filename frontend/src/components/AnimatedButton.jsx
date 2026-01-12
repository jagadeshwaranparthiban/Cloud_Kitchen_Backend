import { motion } from 'motion/react'

const AnimatedButton = ({ 
  children, 
  onClick, 
  className = '', 
  variant = 'primary',
  disabled = false,
  type = 'button',
  ...props 
}) => {
  const baseClasses = 'px-4 py-2 rounded-lg shadow-md transition duration-250 cursor-pointer font-medium'
  
  const variantClasses = {
    primary: 'bg-orange-600 text-white hover:bg-orange-700 hover:text-lg font-medium',
    secondary: 'bg-transparent border border-orange-600 text-orange-500 hover:bg-orange-50 hover:text-lg font-medium hover:bg-orange-600 hover:text-white',
    danger: 'bg-red-500 text-white hover:bg-red-600 hover:text-lg font-medium',
    success: 'bg-green-600 text-white hover:bg-green-600 hover:text-lg font-medium',
    outline: 'bg-transparent border-2 border-orange-600 text-orange-500 hover:bg-orange-600 hover:text-white hover:text-lg font-medium',
    normal: 'bg-white text-orange-600 font-semibold px-4 py-2 rounded-[10px] shadow-md hover:bg-gray-100 hover:font-medium',
    add: 'rounded-full bg-green-600 text-white text-lg w-6 h-6 flex justify-center items-center',
    remove: 'rounded-full bg-red-600 text-white text-lg w-6 h-6 flex justify-center items-center'
  }

  const combinedClasses = `${baseClasses} ${variantClasses[variant] || variantClasses.primary} ${disabled ? 'opacity-50 cursor-not-allowed' : ''} ${className}`

  return (
    <motion.button
      type={type}
      onClick={onClick}
      disabled={disabled}
      className={combinedClasses}
      whileHover={disabled ? {} : { scale: 1.05 }}
      whileTap={disabled ? {} : { scale: 0.95 }}
      initial={{ opacity: 0, y: 10 }}
      animate={{ opacity: 1, y: 0 }}
      transition={{ duration: 0.2 }}
      {...props}
    >
      {children}
    </motion.button>
  )
}

export default AnimatedButton

