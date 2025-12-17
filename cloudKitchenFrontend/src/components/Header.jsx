import React, { useState } from 'react';
import ckitchen_logo from '../assets/ckitchen_logo.jpg';
import AnimatedButton from './AnimatedButton';

const Header = ({title, signOutButtonRequired}) => {
  const [showConfirm, setShowConfirm] = useState(false);

  const doSignOut = () => {
    localStorage.removeItem('jwt');
    localStorage.removeItem('username');
    localStorage.removeItem('cart');
    window.location.href = '/landing';
  }

  const handleSignOutClick = () => setShowConfirm(true);
  const handleCancel = () => setShowConfirm(false);
  const handleConfirm = () => {
    setShowConfirm(false);
    doSignOut();
  }

  return (
    <>
      <div className='grid grid-cols-10 grid-rows-1 gap-4 p-3 w-full'>
        <header className='bg-linear-to-r from-orange-500 to-orange-600 col-span-10 row-span-1 flex flex-row justify-between items-center px-3 py-5 shadow-md rounded-[10px]'>
            <div className='flex flex-row space-x-4 text-center items-center'>
                <img src={ckitchen_logo} alt="logo" className='h-10 w-10 rounded-full'/>
                <h1 className='text-3xl text-white font-bold'>{title || 'Cloud Kitchen'}</h1>
            </div>
            <div className='flex flex-row space-x-4 text-center items-center justify-end'>
              {/* <button className='bg-white text-orange-600 font-semibold px-4 py-2 rounded-[10px] shadow-md hover:bg-gray-100 hover:font-medium' onClick={handleSignOutClick}>Sign out</button> */}
              {signOutButtonRequired && (
                <AnimatedButton variant="normal" onClick={handleSignOutClick}>Sign out</AnimatedButton>
              )}
            </div>
        </header>
      </div>

      {showConfirm && (
        <div className='fixed inset-0 z-50 flex items-center justify-center'>
          <div className='absolute inset-0 bg-black opacity-50' onClick={handleCancel} />
          <div className='relative bg-white rounded-lg shadow-lg p-6 w-[90%] max-w-md z-10'>
            <h2 className='text-xl font-semibold mb-2 text-gray-800'>Confirm Sign Out</h2>
            <p className='text-sm text-gray-600 mb-4'>Are you sure you want to sign out?</p>
            <div className='flex justify-end space-x-3'>
              <button onClick={handleCancel} className='px-4 py-2 rounded-md bg-orange-500 text-white font-medium hover:bg-orange-600'>Cancel</button>
              <button onClick={handleConfirm} className='px-4 py-2 rounded-md border border-orange-500 text-orange-500 bg-white hover:bg-orange-50'>Confirm</button>
            </div>
          </div>
        </div>
      )}
    </>
  )
}

export default Header