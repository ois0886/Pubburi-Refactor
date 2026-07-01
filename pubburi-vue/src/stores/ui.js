import { defineStore } from 'pinia'

export const useUiStore = defineStore('ui', {
  state: () => ({
    status: '',
    error: '',
  }),
  actions: {
    clear() {
      this.status = ''
      this.error = ''
    },
    setStatus(message) {
      this.status = message
      this.error = ''
    },
    setError(error) {
      const fields = error?.fields ? Object.values(error.fields).filter(Boolean).join(', ') : ''
      this.error = fields || error?.message || String(error)
      this.status = ''
    },
    async run(action, successMessage) {
      try {
        this.clear()
        const result = await action()
        if (successMessage) this.setStatus(successMessage)
        return result
      } catch (error) {
        this.setError(error)
        return null
      }
    },
  },
})
