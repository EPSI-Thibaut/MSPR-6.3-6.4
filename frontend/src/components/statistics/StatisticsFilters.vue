<script setup lang="ts">
import { defineProps, defineEmits } from 'vue'
import Dropdown from 'primevue/dropdown'

defineProps({
  pandemies: {
    type: Array,
    required: true,
  },
  regions: {
    type: Array,
    required: true,
  },
  selectedPandemic: {
    type: Object,
    default: null,
  },
  selectedRegion: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:selectedPandemic', 'update:selectedRegion'])

const updatePandemic = (value: unknown) => {
  emit('update:selectedPandemic', value)
}

const updateRegion = (value: unknown) => {
  emit('update:selectedRegion', value)
}
</script>

<template>
  <div class="mb-6 flex flex-wrap justify-center gap-4">
    <Dropdown
      :modelValue="selectedPandemic"
      @update:modelValue="updatePandemic"
      :options="pandemies"
      optionLabel="name"
      placeholder="Sélectionnez une pandémie"
      class="w-full md:w-1/3"
      :disabled="loading"
      aria-label="Sélection de la pandémie"
    />

    <Dropdown
      :modelValue="selectedRegion"
      @update:modelValue="updateRegion"
      :options="regions"
      optionLabel="name"
      placeholder="Sélectionnez une région"
      class="w-full md:w-1/3"
      :disabled="loading"
      filter
      :showClear="true"
      :virtualScrollerOptions="{ itemSize: 38 }"
      aria-label="Sélection de la région"
    />
  </div>
</template>
