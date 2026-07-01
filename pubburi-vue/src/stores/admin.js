import { defineStore } from 'pinia'
import { api } from '../services/api'

const emptyPage = (size = 20) => ({ items: [], page: 1, size, total: 0, totalPages: 0, hasNext: false })

export const adminTabs = [
  { key: 'products', label: '상품' },
  { key: 'markets', label: '매장' },
  { key: 'orders', label: '주문' },
  { key: 'comments', label: '댓글' },
  { key: 'users', label: '회원' },
]

export const useAdminStore = defineStore('admin', {
  state: () => ({
    tab: 'products',
    ordersPage: emptyPage(),
    commentsPage: emptyPage(),
    usersPage: emptyPage(),
  }),
  getters: {
    orders: (state) => state.ordersPage.items,
    comments: (state) => state.commentsPage.items,
    users: (state) => state.usersPage.items,
  },
  actions: {
    async loadOrders(params = {}) {
      const page = { page: 1, size: 20, ...params }
      this.ordersPage = await api.adminOrders(page)
      return this.ordersPage
    },
    async loadComments(params = {}) {
      const page = { page: 1, size: 20, ...params }
      this.commentsPage = await api.adminComments(page)
      return this.commentsPage
    },
    async loadUsers(params = {}) {
      const page = { page: 1, size: 20, ...params }
      this.usersPage = await api.adminUsers(page)
      return this.usersPage
    },
    async loadActive(params = {}) {
      if (this.tab === 'orders') return this.loadOrders(params)
      if (this.tab === 'comments') return this.loadComments(params)
      if (this.tab === 'users') return this.loadUsers(params)
      return null
    },
    async load(params = {}) {
      return this.loadActive(params)
    },
    async completeOrder(id) {
      await api.completeOrder(id)
      await this.loadOrders({ page: this.ordersPage.page })
    },
    async deleteOrder(id) {
      await api.deleteOrder(id)
      await this.loadOrders({ page: this.ordersPage.page })
    },
    async deleteComment(id) {
      await api.deleteComment(id)
      await this.loadComments({ page: this.commentsPage.page })
    },
    async deleteUser(id) {
      await api.deleteUser(id)
      await this.loadUsers({ page: this.usersPage.page })
    },
  },
})
