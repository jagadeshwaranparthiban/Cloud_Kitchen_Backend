import React from 'react'

const Loading = () => {
  return (
    <div className='flex flex-row space-x-3 items-center'>
        <div className="loader ease-linear rounded-full border-8 border-t-8 border-orange-400 h-20 w-20"></div>
        <p className='font-medium text-4xl animate-pulse'>Loading...</p>
    </div>
  )
}

export default Loading