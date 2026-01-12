import React from 'react'
import AnimatedButton from './AnimatedButton'

const DiscountCard = ({discountCode, discountType, discountValue, onApply}) => {
    const handleDiscount= () => {
        if(onApply) {
            onApply({
                code: discountCode,
                type: discountType,
                value: discountValue
            })
        }
    }
    return (
      <div className='bg-white shadow-md flex flex-row border-l-7 border-green-600 rounded-md p-3 justify-between items-center'>
          <div className='flex flex-col gap-2 p-2'>
              <h3 className='font-bold text-lg text-gray-800'>{discountCode}</h3>
              <p className='text-md text-gray-600'>
                  {discountType === 'percentage' ? `Get ${discountValue}% off on your order` : `Get Rs.${discountValue} off on your order`}
              </p>
          </div>
          <AnimatedButton variant="success" className="px-4 py-2 items-center" onClick={handleDiscount}>Apply</AnimatedButton>
      </div>
    )
}

export default DiscountCard