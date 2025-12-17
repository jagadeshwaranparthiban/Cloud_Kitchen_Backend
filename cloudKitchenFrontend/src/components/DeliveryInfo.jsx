import { useState, useEffect } from 'react'

const DeliveryInfo = ({ onFill }) => {
    const [customerName, setCustomerName] = useState('')
    const [customerPhone, setCustomerPhone] = useState('')
    const [customerAddress, setCustomerAddress] = useState('')

    useEffect(() => {
        if(onFill) {
            onFill({
                name: customerName,
                phone: customerPhone,
                address: customerAddress
            })
        }
    }, [customerName, customerPhone, customerAddress])

    return (
        <div className='bg-white shadow-md rounded-md flex flex-col gap-4 px-4 py-4'>
            <h3 className='text-2xl font-semibold mb-3'>Delivery Info</h3>
            <div className='flex flex-row justify-between items-center space-x-3 mb-3'>
                <div className='flex flex-col gap-2 w-2/3'>
                    <label>Customer name: </label>
                    <input 
                        type='text' 
                        value={customerName}
                        placeholder='Enter your name' 
                        className='delivery-info-custom' 
                        onChange={(e)=>setCustomerName(e.target.value)}
                    ></input>
                </div>
                <div className='flex flex-col gap-2 w-1/3'>
                    <label>Contact no:</label>
                    <input 
                        type='number'
                        value={customerPhone} 
                        placeholder='Enter your mobile number' 
                        className='delivery-info-custom' 
                        onChange={(e)=>setCustomerPhone(e.target.value)}
                    ></input>
                </div>
            </div>
            <div className='flex flex-col gap-2'>
                <label>Delivery address:</label>
                <textarea 
                    value={customerAddress}
                    className='delivery-info-custom'
                    rows={3}
                    placeholder='Building, Street, Landmark...'
                    onChange={(e)=>setCustomerAddress(e.target.value)}
                ></textarea>
            </div>
        </div>
    )
}

export default DeliveryInfo