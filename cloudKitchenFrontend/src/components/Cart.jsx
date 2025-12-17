import React, { useEffect, useState } from 'react'
import Header from './Header'
import cartGif from '../icons/shopping-cart.gif'
import starter from '../icons/starter-icon.png'
import mainCourse from '../icons/main-course-icon-1.png'
import dessert from '../icons/dessert-icon.png'
import beverage from '../icons/beverage-icon.png'
import otherFood from '../icons/main-course-icon-2.png'
import AnimatedButton from './AnimatedButton'

const Cart = () => {
  const [cart, setCart] = useState({ items: [] })
  const [categoryWiseItems, setCategoryWiseItems] = useState({
    "Starters": 0,
    "Main Course": 0,
    "Desserts": 0,
    "Beverages": 0,
    "Others": 0
  })

  useEffect(() => {
    try {
      const raw = localStorage.getItem('cart')
      setCart(raw ? JSON.parse(raw) : { items: [] })
    //   console.log(JSON.parse(raw))
    } catch {
      setCart({ items: [] })
    }
  }, [])

  const total = cart.items.reduce((s, it) => s + Number(it.totalCost), 0)

  // Update category counts when cart changes
  useEffect(() => {
    const updated = {
      "Starters": 0,
      "Main Course": 0,
      "Desserts": 0,
      "Beverages": 0,
      "Others": 0
    }
    cart.items.forEach((item) => {
      const cat = (item.category || '').toLowerCase().trim()
      if (cat.includes('starter') || cat.includes('snack')) {
        updated["Starters"] += item.qty
      } else if (cat.includes('main') || cat.includes('pizza') || cat.includes('burger') || cat.includes('pasta')) {
        updated["Main Course"] += item.qty
      } else if (cat.includes('dessert') || cat.includes('sweet')) {
        updated["Desserts"] += item.qty
      } else if (cat.includes('beverage') || cat.includes('drink') || cat.includes('juice') || cat.includes('coffee')) {
        updated["Beverages"] += item.qty
      } else {
        updated["Others"] += item.qty
      }
    })
    setCategoryWiseItems(updated)
  }, [cart])

  const handleCartUpdate = (cartItem, action) => {
    const itemName = cartItem.itemName
    let next
    if (action === '+') {
      next = {
        items: cart.items.map((it) =>
          it.itemName === itemName ? { ...it, qty: it.qty + 1, totalCost: Number((it.totalCost + (it.totalCost / it.qty)).toFixed(2)) } : it
        ),
      }
    } else if (action === '-') {
      const existing = cart.items.find((it) => it.itemName === itemName)
      if (existing.qty === 1) {
        next = {
          items: cart.items.filter((it) => it.itemName !== itemName),
        }
      } else {
        next = {
          items: cart.items.map((it) =>
            it.itemName === itemName ? { ...it, qty: it.qty - 1, totalCost: Number((it.totalCost - (it.totalCost / it.qty)).toFixed(2)) } : it
          ),
        }
      }
    }
    setCart(next)
    try {
      localStorage.setItem('cart', JSON.stringify(next))
    } catch {}
  }

  // Map categories to icons
  const getCategoryIcon = (category) => {
    const iconMap = {
      'Starters': starter,
      'Main Course': mainCourse,
      'Desserts': dessert,
      'Beverages': beverage,
      'Others': otherFood
    }
    return iconMap[category] || otherFood
  }

  return (
    <>
      <Header signOutButtonRequired={true}/>
      <div className='flex flex-col p-6'>
        <div className='flex flex-row space-x-4 mb-6'>
            <img src={cartGif} alt="shopping cart" className="w-12 h-12"/>
            <h1 className="text-4xl font-bold">Your Cart</h1>
        </div>

        <div className='flex flex-row gap-6'>
          {/* Left: Cart Items */}
          <div className="flex-1">
            {cart.items.length === 0 ? (
              <span className="text-gray-600 px-3">Your cart is empty. <button className='text-orange-500 font-medium cursor-pointer transition duration-300 bg-transparent focus:outline-none ring-0 animate-pulse' onClick={() => window.location.href = '/home'}>go back</button></span>
            ) : (
              <div className="space-y-3 overflow-y-auto max-h-[70vh] pr-2">
                {cart.items.map((it, idx) => (
                  <div key={idx} className="flex justify-between items-center p-4 bg-white rounded shadow hover:shadow-lg transition">
                    <div className='flex flex-row space-x-3 flex-1'>
                        <img src={it.itemImageUrl} alt={it.itemName} className="w-16 h-16 object-cover rounded"/>
                        <div className='flex flex-col gap-1'>
                            <div className="font-semibold text-lg">{it.itemName}</div>
                            <div className="text-md text-gray-500">Rs. {(it.totalCost / it.qty).toFixed(2)} x {it.qty}</div>
                        </div>
                    </div>
                    <div className='flex flex-col gap-3 items-end'>
                        <div className="font-semibold text-lg">Rs. {Number(it.totalCost).toFixed(2)}</div>
                        <div className='flex flex-row space-x-2 items-center'>
                            <button onClick={() => handleCartUpdate(it, '-')} className='px-3 py-1 bg-orange-100 text-orange-600 rounded hover:bg-orange-300 transition ease-in-out duration-200'>−</button>
                            <p className='font-semibold w-6 text-center'>{it.qty}</p>
                            <button onClick={() => handleCartUpdate(it, '+')} className='px-3 py-1 bg-orange-100 text-orange-600 rounded hover:bg-orange-300 transition ease-in-out duration-200'>+</button>
                        </div>
                    </div>
                  </div>
                ))}
                <div className="flex justify-between items-center font-bold text-2xl mt-6 pt-4 border-t-2"> 
                  <span>Total</span>
                  <span>Rs. {total.toFixed(2)}</span>
                </div>
              </div>
            )}
          </div>

          {/* Right: Category Breakdown + Recommendations */}
          {cart.items.length > 0 && (
            <div className='w-80 space-y-6'>
              {/* Category Breakdown */}
              <div className='bg-white rounded shadow p-4'>
                <h3 className='font-bold text-lg mb-4'>Order Summary</h3>
                <div className='space-y-3'>
                  {Object.entries(categoryWiseItems).map(([category, count]) => (
                    count > 0 && (
                      <div key={category} className='flex items-center justify-between pb-2 border-b'>
                        <div className='flex items-center space-x-3'>
                          <img src={getCategoryIcon(category)} alt={category} className='w-8 h-8 object-contain'/>
                          <span className='text-gray-700 font-medium'>{category}</span>
                        </div>
                        <span className='font-semibold bg-orange-100 text-orange-600 px-3 py-1 rounded-full'>{count}</span>
                      </div>
                    )
                  ))}
                </div>
              </div>

              {/* Recommendations */}
              <div className='bg-white rounded shadow p-4'>
                <h3 className='font-bold text-lg mb-4'>Recommendations</h3>
                <div className='space-y-2 text-sm text-gray-600'>
                  <p>✓ Looks good! Your order is balanced.</p>
                  <p>💡 Tip: Add a beverage or dessert to complete your meal.</p>
                </div>
              </div>

              {/* <button className='w-full bg-orange-500 text-white font-bold py-3 rounded hover:bg-orange-600 transition'>
                Proceed to Checkout
              </button> */}
              <div className='flex flex-col gap-3'>
                <AnimatedButton variant="primary" className="w-full py-3 font-bold" onClick={() => window.location.href = '/checkout'}>
                  Proceed to Checkout
                </AnimatedButton>
                <AnimatedButton variant="outline" className="w-full py-3 font-bold" onClick={() => window.location.href = '/home'}>Go back</AnimatedButton>
              </div>
            </div>
          )}
        </div>
      </div>
    </>
  )
}

export default Cart
