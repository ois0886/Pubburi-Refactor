import { defineStore } from 'pinia'
import { api } from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    user: null,
    profile: null,
    authMode: 'login',
  }),
  getters: {
    isAdmin: (state) => state.user?.role === 'ADMIN',
    profileOrders: (state) => state.profile?.orders || [],
  },
  actions: {
    async refreshMe() {
      this.user = await api.me().catch(() => null)
      return this.user
    },
    async login(payload) {
      this.user = await api.login(payload)
      await this.loadProfile()
      return this.user
    },
    async join(payload) {
      await api.join(payload)
    },
    async logout() {
      await api.logout()
      this.user = null
      this.profile = null
    },
    async loadProfile() {
      if (!this.user) return null
      this.profile = await api.profile(this.user.id)
      return this.profile
    },
  },
})
