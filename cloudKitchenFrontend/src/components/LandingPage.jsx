import React from 'react'
import { useNavigate, useLocation } from 'react-router-dom'
import Header from './Header'
import AnimatedButton from './AnimatedButton'
import backdrop from '../assets/CKitchen_Main_Backdrop.jpg'

const LandingPage = () => {
    const navigate = useNavigate();
    const location = useLocation();
    //const { username } = location.state || { username: "User" };
    return (
        <>
        <Header signOutButtonRequired={false}/>
        <div className='relative h-screen overflow-hidden'>
          <div className='absolute inset-0 -z-10'>
            <img src={backdrop} alt='CKitchen backdrop' className='w-full h-full object-cover filter blur-sm brightness-75' />
            <div className='absolute inset-0 bg-black opacity-20' />
          </div>
          <div className='relative z-10 flex flex-col justify-center items-center h-screen space-y-5'>
            <h1 className='text-white font-semibold text-7xl'>Welcome to CKitchen</h1>
            <div className='flex flex-row space-x-4 p-2'>
                <AnimatedButton variant="outline">About us</AnimatedButton>
                <AnimatedButton variant="primary" onClick={() => navigate('/login')}>Sign up</AnimatedButton>
            </div>
          </div>
        </div>
        </>
    )
}

export default LandingPage