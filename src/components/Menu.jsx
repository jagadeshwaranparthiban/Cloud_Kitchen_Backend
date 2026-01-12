import React from 'react'
import AnimatedButton from './AnimatedButton'
import vegIcon from '../icons/veg-icon.png'
import nonVegIcon from '../icons/non-veg-icon.png'

const Menu = ({ menu, onAdd }) => {
  return (
    <div>
        <ul className='p-5 grid grid-cols-3 gap-3 shadow-md rounded-[10px]'>
            {menu && menu.map((item, idx)=>(
                <li key={idx} className='flex flex-col gap-3 shadow-md'>
                    <img src={item.image} alt={item.itemName} className='w-full h-48 object-cover rounded-[10px]'/>
                    <div className='flex flex-row justify-between items-center px-2'>
                        <h2 className='font-bold text-lg'>{item.itemName}</h2>
                        <span className='text-sm text-gray-500'>
                            {item.isVeg ? 
                            <img src={vegIcon} alt="Veg icon" className='h-5 w-5'></img> : 
                            <img src={nonVegIcon} alt='Non Veg icon' className='h-5 w-5'></img>}
                        </span>
                    </div>
                    <p className='text-sm text-gray-600 px-2'>{item.desc}</p>
                    <div className='mt-auto flex flex-row justify-between items-center px-2'>
                        <span className='font-semibold text-md'>Rs.{item.price}</span>
                        <AnimatedButton variant="primary" className="mb-2 px-2 py-1 mt-3 text-sm" onClick={() => onAdd && onAdd(item)}>Add</AnimatedButton>
                    </div>
                </li>
            ))}
        </ul>
    </div>
  )
}

export default Menu