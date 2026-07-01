import { beforeEach, describe, expect, it } from 'vitest'
import { createPinia, setActivePinia } from 'pinia'
import { useCartStore } from './cart'

describe('cart store', () => {
  beforeEach(() => {
    const storage = new Map()
    Object.defineProperty(globalThis, 'localStorage', {
      value: {
        getItem: (key) => storage.get(key) || null,
        setItem: (key, value) => storage.set(key, value),
        removeItem: (key) => storage.delete(key),
        clear: () => storage.clear(),
      },
      configurable: true,
    })
    setActivePinia(createPinia())
  })

  it('adds, persists, and removes cart items', () => {
    const cart = useCartStore()
    const product = { id: 1, name: '탁주', price: 1000 }

    cart.add(product)
    cart.add(product)

    expect(cart.count).toBe(2)
    expect(cart.total).toBe(2000)
    expect(JSON.parse(localStorage.getItem('pubburi-cart'))[0].quantity).toBe(2)

    cart.remove(1)

    expect(cart.items).toEqual([])
  })
})
