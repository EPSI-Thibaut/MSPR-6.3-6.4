<script setup lang="ts">
import { defineProps } from 'vue'
import ProgressSpinner from 'primevue/progressspinner'

const props = defineProps({
  message: {
    type: String,
    default: 'Chargement...',
  },
  fullScreen: {
    type: Boolean,
    default: false,
  },
})
</script>

<template>
  <div
    :class="[
      'flex flex-col items-center justify-center',
      fullScreen ? 'fixed inset-0 bg-white bg-opacity-80 z-50' : 'h-80',
    ]"
  >
    <ProgressSpinner
      :style="`width: ${fullScreen ? '100px' : '80px'}; height: ${fullScreen ? '100px' : '80px'}`"
      strokeWidth="5"
      fill="var(--surface-ground)"
      animationDuration=".5s"
    />
    <p
      :class="['mt-4', fullScreen ? 'text-gray-700 text-xl font-medium' : 'text-gray-600 text-lg']"
    >
      {{ message }}
    </p>
  </div>
</template>
