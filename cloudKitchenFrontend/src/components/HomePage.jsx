import React, { useState, useMemo } from 'react'
import { Navigate, useLocation } from 'react-router-dom'
import axios from 'axios'
import { useQuery } from '@tanstack/react-query'
import { motion } from 'motion/react'
import Menu from './Menu'
import Loading from './Loading'
import Header from './Header'
import AnimatedButton from './AnimatedButton'

const API_BASE_URL = import.meta.env.VITE_API_URL || 'http://localhost:5000'

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

const HomePage = () => {
  const location = useLocation()
  const token = getStoredToken()
  const [searchQuery, setSearchQuery] = useState('')
  const [selectedCategories, setSelectedCategories] = useState(['All'])

  const categories = [
    'All',
    'Veg',
    'Non Veg',
    'Starter',
    'Main_Course',
    'Dessert',
    'Snacks',
    'Beverage',
    'Add_ons',
    'Burger',
    'Pizza',
    'Pasta',
    'Salad',
    'Soup',
    'Sandwich'
  ]
  const [cart, setCart] = useState(() => {
    try {
      const raw = localStorage.getItem('cart')
      return raw ? JSON.parse(raw) : { items: [] }
    } catch {
      return { items: [] }
    }
  })

  const persistCart = (nextCart) => {
    setCart(nextCart)
    try {
      localStorage.setItem('cart', JSON.stringify(nextCart))
    } catch {}
  }

  const addToCart = (menuItem) => {
    if (!menuItem) return
    const itemName = menuItem.itemName || menuItem.name
    const price = Number(menuItem.price || menuItem.cost || 0)
    const itemImageUrl =  menuItem.image || ''
    const category = menuItem.category || 'Uncategorized'

    const existing = cart.items.find((it) => it.itemName === itemName)
    let next
    if (existing) {
      next = {
        items: cart.items.map((it) =>
          it.itemName === itemName ? { ...it, qty: it.qty + 1, totalCost: Number((it.totalCost + price).toFixed(2)) } : it
        ),
      }
    } else {
      next = { items: [...cart.items, { itemName, qty: 1, totalCost: Number(price.toFixed(2)), itemImageUrl: itemImageUrl, category:category }] }
    }
    //console.log('Adding to cart:', menuItem, 'Next cart:', next)
    persistCart(next)
  }

  const {
    data: menu,
    isLoading,
    isError,
    error,
  } = useQuery({
    queryKey: ['fetchMenu'],
    queryFn: async () => {
      const res = await axios.get(`${API_BASE_URL}/menu`, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      })
      console.log('Fetched menu:', res.data)
      return res.data
    },
    enabled: Boolean(token),
    retry: false,
  })

  // Filter menu items based on search query and selected categories
  const filteredMenu = useMemo(() => {
    if (!menu) return []

    const query = (searchQuery || '').toLowerCase().trim()

    const matchesCategory = (item) => {
      // If 'All' is selected, show all items
      if (selectedCategories.includes('All')) return true

      // Check if item matches any selected category
      return selectedCategories.some((cat) => {
        const itemCat = (item.category || '').toString().toLowerCase()
        const selCat = cat.toLowerCase()
        
        if (selCat === 'veg') return !!item.isVeg
        if (selCat === 'non veg' || selCat === 'non-veg' || selCat === 'nonveg') return !item.isVeg
        return itemCat.includes(selCat) || selCat.includes(itemCat)
      })
    }

    return menu.filter((item) => {
      const matchesQuery = !query || (item.itemName && item.itemName.toLowerCase().includes(query)) || (item.description && item.description.toLowerCase().includes(query)) || (item.desc && item.desc.toLowerCase().includes(query))
      return matchesQuery && matchesCategory(item)
    })
  }, [menu, searchQuery, selectedCategories])

  if (!token) {
    return <Navigate to="/login" replace />
  }

  const username = location.state?.username || localStorage.getItem('username') || 'User'

  if (isLoading) {
    return (
      <>
        <Header signOutButtonRequired={true}/>
        <div className="flex justify-center items-center h-[80vh]">
          <Loading />
        </div>
      </>
    )
  }

  if (isError) {
    return (
      <>
        <Header signOutButtonRequired={true}/>
        <div className="flex flex-col justify-center items-center h-[80vh] space-y-3">
          <p className="text-2xl text-red-500">Failed to load menu.</p>
          <p className="text-md text-gray-700">{'Please try again later.'}</p>
        </div>
      </>
    )
  }

  return (
    <>
      <Header signOutButtonRequired={true}/>
      <div className="grid grid-cols-10 grid-rows-10 gap-4 p-3">
        <aside className="col-span-2 row-span-10 shadow-md rounded-[10px] p-3 flex flex-col">
          <div className="flex flex-row space-x-2 text-center items-center mb-4">
            <div className="flex h-12 w-12 rounded-full bg-orange-500 text-white font-medium text-2xl justify-center items-center">
              {username[0]}
            </div>
            <div className="flex flex-col">
              <h3 className="text-lg">{username}</h3>
              <span className="text-sm text-gray-500">Welcome back</span>
            </div>
          </div>

          <div className="mb-4">
            <h4 className="text-lg font-semibold mb-2">Categories</h4>
            <div className="flex flex-col space-y-2 overflow-y-auto max-h-60 pr-1">
              {categories.map((cat) => {
                const active = selectedCategories.includes(cat)
                const toggle = () => {
                  setSelectedCategories((prev) => {
                    // if selecting All, reset to ['All']
                    if (cat === 'All') return ['All']
                    // remove 'All' if any other is selected
                    const withoutAll = prev.filter((p) => p !== 'All')
                    if (prev.includes(cat)) {
                      const next = withoutAll.filter((p) => p !== cat)
                      return next.length === 0 ? ['All'] : next
                    }
                    return [...withoutAll, cat]
                  })
                }

                return (
                  <motion.label
                    key={cat}
                    className={`flex items-center gap-2 px-3 py-2 rounded-md cursor-pointer select-none ${active ? 'bg-orange-500 text-white' : 'bg-transparent hover:bg-orange-50'}`}
                    onClick={toggle}
                    initial={false}
                    animate={active ? { scale: 1.02 } : { scale: 1 }}
                    transition={{ type: 'spring', stiffness: 300, damping: 20 }}
                  >
                    <input
                      type="checkbox"
                      checked={active}
                      readOnly
                      className="w-4 h-4"
                    />
                    <span className="text-md">{cat.includes("_") ? cat.replace("_","  ") : cat}</span>
                  </motion.label>
                )
              })}
            </div>
          </div>

          <div className="mt-auto">
            <h4 className="text-lg font-semibold mb-2">Quick Actions</h4>
            <div className="flex flex-col space-y-2">
              {/* <button className="pxy-3 py-2 rounded-md bg-white border border-gray-200 hover:bg-gray-50 text-sm" onClick={() => window.location.href = '/cart'}>View Cart</button> */}
              {/* <button className="px-3 py-2 rounded-md bg-white border border-gray-200 hover:bg-gray-50 text-sm" onClick={() => window.location.href = '/orders'}>My Orders</button> */}
              <AnimatedButton variant="outline" onClick={() => window.location.href = '/cart'}>View Cart</AnimatedButton>
              <AnimatedButton variant="outline" onClick={() => window.location.href = '/orders'}>My Orders</AnimatedButton>
            </div>
          </div>
        </aside>
        {menu && (
          <div className="col-span-8 row-span-10 flex flex-col h-[72vh]">
            <div className="sticky top-0 z-20 bg-white/90 p-3 rounded-md mb-2 shadow-sm">
              <div className="flex flex-row gap-2">
                <input
                  type="text"
                  placeholder="Search for items..."
                  value={searchQuery}
                  onChange={(e) => setSearchQuery(e.target.value)}
                  className="input_custom flex-1"
                />
                <AnimatedButton variant="primary" onClick={() => setSearchQuery(searchQuery)}>Search</AnimatedButton>
              </div>
            </div>

            <div className="flex-1 overflow-y-auto max-h-[80vh]">
              {filteredMenu && filteredMenu.length > 0 ? (
                <Menu menu={filteredMenu} onAdd={addToCart} />
              ) : (
                <div className="flex justify-center items-center h-[40vh]">
                  <p className="text-gray-500 text-lg">No items found matching "{searchQuery}"</p>
                </div>
              )}
            </div>
          </div>
        )}
      
      {/* Cart popup at bottom */}
      {cart?.items?.length > 0 && (
        <motion.div
          className="fixed left-0 right-0 bottom-0 flex justify-center"
          initial={{ y: 200, opacity: 0 }}
          animate={{ y: 0, opacity: 1 }}
          exit={{ y: 200, opacity: 0 }}
          transition={{ duration: 0.6, ease: 'easeOut' }}
        >
          <div className="w-full bg-white shadow-lg p-4 flex items-center justify-between">
            <div className="flex flex-row justify-between flex-1">
              <div className="flex flex-col">
                <span className="text-md text-gray-700">Items added</span>
                <span className="font-medium">{cart.items.length}</span>
              </div>
              <div className="flex flex-col">
                <span className="text-md text-gray-700">Total</span>
                <span className="font-bold">Rs. {cart.items.reduce((s, it) => s + Number(it.totalCost), 0).toFixed(2)}</span>
              </div>
            </div>
            <div className="flex justify-end ml-6">
              <AnimatedButton variant="secondary" onClick={() => { window.location.href = '/cart' }}>View Cart</AnimatedButton>
            </div>
          </div>
        </motion.div>
      )}
      </div>
    </>
  )
}

export default HomePage