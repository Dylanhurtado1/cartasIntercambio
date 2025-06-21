export const Pagination = {
  props: {
    totalPages: Number,
    modelValue: Number
  },
  emits: ['update:modelValue'],
  computed: {
    visiblePages() {
      const pages = []
      const current = this.modelValue
      const total = this.totalPages
      const delta = 2

      const start = Math.max(0, current - delta)
      const end = Math.min(total - 1, current + delta)

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }

      return pages
    }
  },
  methods: {
    goTo(page) {
      if (page >= 0 && page < this.totalPages) {
        this.$emit('update:modelValue', page)
      }
    }
  },
  template: `
    <div class="pagination">
      <button @click="goTo(modelValue - 1)" :disabled="modelValue === 0">←</button>

      <button v-if="visiblePages[0] > 0" @click="goTo(0)">1</button>
      <span v-if="visiblePages[0] > 1">...</span>

      <button
        v-for="p in visiblePages"
        :key="p"
        @click="goTo(p)"
        :class="{ active: p === modelValue }"
        :disabled="p === modelValue"
      >
        {{ p + 1 }}
      </button>

      <span v-if="visiblePages[visiblePages.length - 1] < totalPages - 2">...</span>
      <button
        v-if="visiblePages[visiblePages.length - 1] < totalPages - 1"
        @click="goTo(totalPages - 1)"
      >
        {{ totalPages }}
      </button>

      <button @click="goTo(modelValue + 1)" :disabled="modelValue >= totalPages - 1">→</button>
    </div>
  `
}
