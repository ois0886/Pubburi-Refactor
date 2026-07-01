import { defineStore } from 'pinia'
import { api } from '../services/api'

function readCart() {
  try {
    return JSON.parse(localStorage.getItem('pubburi-cart') || '[]')
  } catch {
    return []
  }
}

export const useCartStore = defineStore('cart', {
  state: () => ({
    items: readCart(),
    orderForm: { orderType: 'TAKEOUT', orderTable: '' },
  }),
  getters: {
    count: (state) => state.items.reduce((sum, item) => sum + item.quantity, 0),
    total: (state) => state.items.reduce((sum, item) => sum + item.product.price * item.quantity, 0),
  },
  actions: {
    add(product) {
      const existing = this.items.find((item) => item.product.id === product.id)
      if (existing) {
        existing.quantity += 1
      } else {
        this.items.push({ product, quantity: 1 })
      }
      this.persist()
    },
    remove(productId) {
      this.items = this.items.filter((item) => item.product.id !== productId)
      this.persist()
    },
    persist() {
      this.items = this.items.filter((item) => item.quantity > 0)
      localStorage.setItem('pubburi-cart', JSON.stringify(this.items))
    },
    async checkout(userId) {
      await api.createOrder({
        userId,
        orderType: this.orderForm.orderType,
        orderTable: this.orderForm.orderTable,
        details: this.items.map((item) => ({ productId: item.product.id, quantity: item.quantity })),
      })
      this.items = []
      this.persist()
      this.orderForm.orderTable = ''
    },
  },
})
