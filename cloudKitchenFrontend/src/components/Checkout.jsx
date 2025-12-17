import React, { useEffect, useState } from 'react'
import axios from 'axios'
import Header from './Header'
import AnimatedButton from './AnimatedButton'
import checkoutIcon from '../icons/checkout-icon.gif'
import DiscountCard from './DiscountCard'
import DeliveryInfo from './DeliveryInfo'


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

const Checkout = () => {
  const [cart, setCart] = useState({ items: [] })
  const [subtotal, setSubtotal] = useState(0)
  const [availableDiscounts, setAvailableDiscounts] = useState([])
  const [appliedCode, setAppliedCode] = useState(null)
  const [showConfirm, setShowConfirm] = useState(false)
  const [discountAmount, setDiscountAmount] = useState(0)
  const [discountPercentage, setDiscountPercentage] = useState(0)
  const [deliveryFee, setDeliveryFee] = useState(0)
  const [tax, setTax] = useState(0)
  const [total, setTotal] = useState(0)
  const [paymentMethod, setPaymentMethod] = useState('WALLET')
  const [customerInfo, setCustomerInfo] = useState({ name: '', phone: '', address: '', instructions: '' })
  const [promoError, setPromoError] = useState('')
  const [loadingDiscounts, setLoadingDiscounts] = useState(false)

  const BASE_API_URL = 'http://localhost:5000' 
  const token = getStoredToken()
  useEffect(() => {
    try {
      const raw = localStorage.getItem('cart')
      const parsed = raw ? JSON.parse(raw) : { items: [] }
      setCart(parsed)
    } catch {
      setCart({ items: [] })
    }
  }, [])

  // Fetch available discounts from server when subtotal changes
  useEffect(() => {
    const fetchDiscounts = async () => {
      if (subtotal === 0 || !token) {
        setAvailableDiscounts([])
        return
      }

      setLoadingDiscounts(true)
      try {
        const response = await axios.get(`${BASE_API_URL}/get_discounts`, {
          params: { orderCost: subtotal },
          headers: {
            Authorization: `Bearer ${token}`
          }
        })
        const rawDiscounts = response.data.validDiscounts || []
        const discounts = rawDiscounts.map((d) => ({
          code: d.discountCode,
          type: d.discountType,
          value: d.discountValue
        }))
        setAvailableDiscounts(discounts.slice(0, 3))
      } catch (error) {
        console.error('Failed to fetch discounts:', error)
        setAvailableDiscounts([])
      } finally {
        setLoadingDiscounts(false)
      }
    }

    // Debounce fetch to avoid excessive requests
    const timer = setTimeout(fetchDiscounts, 500)
    return () => clearTimeout(timer)
  }, [subtotal, token])

  useEffect(() => {
    const s = cart.items.reduce((sum, it) => sum + Number(it.totalCost), 0)
    setSubtotal(s)

    // Delivery fee: free for orders >= 800, else 50
    const delivery = s >= 800 ? 0 : s === 0 ? 0 : 50
    setDeliveryFee(delivery)

    // If discount is applied, tax is 5% of (subtotal - discount)
    // Otherwise, tax is 5% of subtotal
    let d = 0
    let discPercent = 0
    if (appliedCode) {
      const discount = availableDiscounts.find((disc) => disc.code === appliedCode)
      if (discount) {
        if (discount.type === 'FLAT') {
          d = +(discount.value).toFixed(2)
        } else if (discount.type === 'PERCENTAGE') {
          d = +(s * discount.value / 100).toFixed(2)
          if (discount.cap) d = Math.min(d, discount.cap)
          discPercent = discount.value
        }
      }
    }
    setDiscountAmount(d)
    setDiscountPercentage(discPercent)

    // Tax: 5% 
    const t = +((s - d) * 0.05).toFixed(2)
    setTax(t)

    // Total: subtotal + delivery + tax - discount
    setTotal(+(s + delivery + t - d).toFixed(2))
  }, [cart, appliedCode, availableDiscounts])

  const handleDiscountApplication = (disc) => {
    setPromoError('')
    setAppliedCode(disc.code)
    setDiscountAmount(disc.type === 'FLAT' ? disc.value : +(subtotal * disc.value / 100).toFixed(2))
    setDiscountPercentage(disc.type === 'PERCENTAGE' ? disc.value : 0)
  }

  const handleRemovePromo = () => {
    setAppliedCode(null)
    setDiscountAmount(0)
    setDiscountPercentage(0)
    setPromoError('')
  }

  const handleCustomerInfoFill = (info) => {
    setCustomerInfo({
      ...customerInfo,
      ...info
    })
  }

  const handleConfirm = () => {
    setShowConfirm(false)
    handlePlaceOrder()
  }

  const handleCancel = () => {
    setShowConfirm(false)
  }

  const handlePlaceOrder = async () => {
    if (!customerInfo.name || !customerInfo.phone || !customerInfo.address) {
      alert('Please fill in delivery details')
      return
    }
    const orderRequest = {
      customerName: customerInfo.name || localStorage.getItem('username') || 'Guest',
      items: (cart.items.map((it) => ({
        itemName: it.itemName,
        qty: it.qty,
      }))),
      discountCode: appliedCode,
    }

    const response = await axios.post(`${BASE_API_URL}/order/place`, orderRequest, {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
    console.log('Order response:', response.data)
    if (paymentMethod !== 'WALLET') {
      initiateRazorpayPayment()
    } else {
      // Cash on Delivery
      completeOrder(response.data)
    }
  }

  const initiateRazorpayPayment = () => {
    if (!window.Razorpay) {
      alert('Razorpay not loaded. Please refresh and try again.')
      return
    }

    const options = {
      key: 'rzp_test_K8dxLN81bYm2lK', 
      amount: Math.round(total * 100), 
      currency: 'INR',
      name: 'Cloud Kitchen',
      description: `Order for ${customerInfo.name}`,
      prefill: {
        name: customerInfo.name,
        contact: customerInfo.phone,
      },
      handler: (response) => {
        // success
        console.log('Payment successful:', response)
        //completeOrder(response.razorpay_payment_id)
        completeOrder({ orderId: response.razorpay_payment_id, totalCost: total })

      },
      onError: (error) => {
        console.error('Payment failed:', error)
        alert(`Payment failed: ${error.description}`)
      },
    }

    const rzp = new window.Razorpay(options)
    rzp.open()
  }

  const completeOrder = (orderResponse) => {
    console.log('Order placed:', orderResponse)
    alert(`Order placed successfully!\nOrder ID: ${orderResponse.orderId}\nTotal: Rs. ${orderResponse.totalCost}`)
    try {
      localStorage.removeItem('cart')
    } catch {}
    window.location.href = '/home'
  }

  return (
    <>
      <Header signOutButtonRequired={true} />
      <div className="p-6 w-full grid grid-cols-6 gap-5">
        <div className='flex flex-row space-x-5 col-span-full px-5'>
            <img src={checkoutIcon} alt="Checkout" className="w-12 h-12"/>
            <h1 className="text-4xl font-bold">Checkout</h1>
        </div>
        
        <div className="h-full col-span-4">
          {/* Left: Delivery & Payment Details */}
          <div className="flex flex-col gap-4">
            {/* Delivery Details */}
            {/* <div className="bg-white rounded shadow p-4">
              <h2 className="font-semibold text-2xl mb-3">Delivery Details</h2>
              <div className="space-y-3 text-sm text-gray-700">
                <div>
                  <label className="block font-medium mb-1 text-md">Name</label>
                  <input
                    placeholder="Your full name"
                    value={customerInfo.name}
                    onChange={(e) => setCustomerInfo({ ...customerInfo, name: e.target.value })}
                    className="w-full outline-gray-300 rounded px-2 py-1 hover:outline-orange-400 transition duration-300"
                  />
                </div>
                <div>
                  <label className="block font-medium mb-1 text-md">Phone</label>
                  <input
                    placeholder="10-digit phone number"
                    value={customerInfo.phone}
                    onChange={(e) => setCustomerInfo({ ...customerInfo, phone: e.target.value })}
                    className="w-full outline-gray-300 rounded px-2 py-1 hover:outline-orange-400 transition duration-300"
                  />
                </div>
                <div>
                  <label className="block font-medium mb-1 text-md">Delivery Address</label>
                  <textarea
                    placeholder="Building, Street, Landmark..."
                    value={customerInfo.address}
                    onChange={(e) => setCustomerInfo({ ...customerInfo, address: e.target.value })}
                    className="w-full outline-gray-300 rounded px-2 py-1 hover:outline-orange-400 transition duration-300"
                    rows={3}
                  />
                </div>
                <p className="text-xs text-gray-600">Provide a landmark to help the rider locate you faster.</p>
              </div>
            </div> */}
            <DeliveryInfo onFill={handleCustomerInfoFill}/>

            {/* Promo Code Section */}
            <div className="bg-white rounded shadow-md p-4">
              <h2 className="font-semibold text-2xl mb-3">Available Discounts</h2>
              <div className="space-y-3">
                {appliedCode && (
                  <div className="bg-green-100 border-2 border-green-500 rounded-md p-3 flex justify-between items-center">
                    <div>
                      <p className="font-semibold text-green-700">{appliedCode} Applied</p>
                      <p className="text-md text-green-600">
                        {availableDiscounts.find((d) => d.code === appliedCode)?.type === 'PERCENTAGE'
                          ? `${discountPercentage}% off`
                          : `Rs. ${discountAmount} off`}
                      </p>
                    </div>
                    <AnimatedButton variant="danger" className="px-4 py-2" onClick={handleRemovePromo}>Remove</AnimatedButton>
                  </div>
                )}
                {!appliedCode && (
                  <>
                    {/* <div className="flex gap-2">
                      <input
                        type="text"
                        placeholder="Enter promo code"
                        value={promoInput}
                        onChange={(e) => setPromoInput(e.target.value)}
                        className="flex-1 border rounded px-2 py-1"
                      />
                      <button
                        onClick={handleApplyPromo}
                        className="bg-orange-500 text-white px-4 py-1 rounded hover:bg-orange-600 font-semibold"
                      >
                        Apply
                      </button>
                    </div> */}
                    {promoError && <p className="text-red-500 text-sm">{promoError}</p>}
                    {loadingDiscounts && <p className="text-gray-500 text-sm">Loading discounts...</p>}
                    {!loadingDiscounts && availableDiscounts.length > 0 && (
                      <div>
                        <p className="font-semibold text-md mb-2">Available promo codes:</p>
                        <div className="grid grid-cols-1 gap-2">
                          {availableDiscounts.map((discount, idx) => (
                            <DiscountCard
                                key={idx}
                                discountCode={discount.code}
                                discountType={discount.type}
                                discountValue={discount.value}
                                onApply={handleDiscountApplication}
                            /> 
                          ))}
                        </div>
                      </div>
                    )}
                    {!loadingDiscounts && availableDiscounts.length === 0 && subtotal > 0 && (
                      <p className="text-gray-500 text-sm">No discounts available for this order.</p>
                    )}
                  </>
                )}
              </div>
            </div>

            {/* Payment Method */}
            <div className="bg-white rounded shadow p-4">
              <h2 className="font-semibold text-2xl mb-3">Payment Method</h2>
              <div className="space-y-3 text-md text-gray-700">
                <label className="payment-input-custom">
                  <input
                    type="radio"
                    name="payment"
                    value="WALLET"
                    checked={paymentMethod === 'WALLET'}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  />
                  <div>
                    <span className="font-medium">Cash on Delivery (COD)</span>
                    <p className="text-xs text-gray-500">Pay when your order arrives</p>
                  </div>
                </label>
                <label className="payment-input-custom">
                  <input
                    type="radio"
                    name="payment"
                    value="UPI"
                    checked={paymentMethod === 'UPI'}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  />
                  <div>
                    <span className="font-medium">UPI</span>
                    <p className="text-xs text-gray-500">Google Pay, PhonePe, Paytm...</p>
                  </div>
                </label>
                <label className="payment-input-custom">
                  <input
                    type="radio"
                    name="payment"
                    value="CARD"
                    checked={paymentMethod === 'CARD'}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  />
                  <div>
                    <span className="font-medium">Credit/Debit Card</span>
                    <p className="text-xs text-gray-500">Visa, Mastercard, Amex</p>
                  </div>
                </label>
                <label className="payment-input-custom">
                  <input
                    type="radio"
                    name="payment"
                    value="NET_BANKING"
                    checked={paymentMethod === 'NET_BANKING'}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                  />
                  <div>
                    <span className="font-medium">Net Banking</span>
                    <p className="text-xs text-gray-500">All major banks supported</p>
                  </div>
                </label>
                <p className="text-xs text-gray-500 mt-3">💳 Powered by <strong>Razorpay</strong> — Secure payment gateway</p>
              </div>
            </div>
          </div>
        </div>
        
        <div className='col-span-2 flex flex-col gap-5'>
        {/* Right: Order Summary */}
        <div className="bg-white rounded shadow p-4 gap-4 flex flex-col">
            <h2 className="font-bold text-2xl mb-3">Order Summary</h2>
            <div className="space-y-2 text-md text-gray-700 max-h-64 overflow-y-auto">
              {cart.items.length === 0 ? (
                <p className="text-gray-500">No items in cart.</p>
              ) : (
                cart.items.map((it, idx) => (
                  <div key={idx} className="flex justify-between items-center pb-2 border-b">
                    <div>
                      <div className="font-medium">{it.itemName}</div>
                      <div className="text-xs text-gray-500">{it.qty} x Rs. {(it.totalCost / it.qty).toFixed(2)}</div>
                    </div>
                    <div className="font-semibold">Rs. {Number(it.totalCost).toFixed(2)}</div>
                  </div>
                ))
              )}
            </div>

            <div className="mt-4 border-t pt-3 space-y-2 text-sm">
              <div className="flex justify-between">
                <span>Subtotal</span>
                <span>Rs. {subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between font-semibold">
                <span>Tax (5%)</span>
                <span className='text-red-600'>Rs. {tax.toFixed(2)}</span>
              </div>
              <div className="flex justify-between">
                <span>Delivery</span>
                <span>{deliveryFee === 0 ? 'Free' : `Rs. ${deliveryFee.toFixed(2)}`}</span>
              </div>
              {appliedCode && (
                <div className="flex justify-between text-green-600 font-semibold">
                  <span>Discount ({appliedCode})</span>
                  <span>- Rs. {discountAmount.toFixed(2)}</span>
                </div>
              )}
              <div className="flex justify-between font-bold text-lg border-t pt-2">
                <span>Total</span>
                <span>Rs. {total.toFixed(2)}</span>
              </div>
            </div>

            <div className="mt-4 space-y-2">
              <AnimatedButton
                variant="primary"
                className="w-full py-3 font-bold"
                onClick={() => setShowConfirm(true)}
              >
                Place Order
              </AnimatedButton>
              <AnimatedButton
                variant="outline"
                className="w-full py-3 font-bold"
                onClick={() => window.location.href = '/cart'}
              >
                Back to Cart
              </AnimatedButton>
            </div>
            <div className="bg-white rounded shadow p-4">
              <h2 className="font-semibold text-2xl mb-4">Special Instructions</h2>
              <textarea
                placeholder="Any special requests? (e.g., no onions, extra spice, etc.)"
                value={customerInfo.instructions}
                onChange={(e) => setCustomerInfo({ ...customerInfo, instructions: e.target.value })}
                className="border-2 outline-0 border-gray-300 rounded-md px-2 py-1 hover:border-orange-500 transition duration-300 w-full hover:outline-orange-500"
                rows={3}
              />
            </div>
        </div>
        </div>
      </div>
      {showConfirm && (
        <div className='fixed inset-0 z-50 flex items-center justify-center'>
          <div className='absolute inset-0 bg-black opacity-50' onClick={handleCancel} />
          <div className='relative bg-white rounded-lg shadow-lg p-6 w-[90%] max-w-md z-10'>
            <h2 className='text-xl font-semibold mb-2 text-gray-800'>Place Order</h2>
            <p className='text-md text-gray-600 mb-4'>Are you sure you want to place this order?</p>
            <div className='flex justify-end space-x-3'>
              <button onClick={handleCancel} className='px-4 py-2 rounded-md bg-orange-500 text-white font-medium hover:bg-orange-600 transition duration-250'>Cancel</button>
              <button onClick={handleConfirm} className='px-4 py-2 rounded-md border border-orange-500 text-orange-500 bg-white hover:bg-orange-600 hover:text-white transition duration-250'>Confirm</button>
            </div>
          </div>
        </div>
      )}
      {/* Razorpay Script */}
      <script src="https://checkout.razorpay.com/v1/checkout.js" async></script>
    </>
  )
}

export default Checkout
