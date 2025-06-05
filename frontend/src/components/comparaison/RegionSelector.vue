<script setup lang="ts">
import Dropdown from 'primevue/dropdown'

defineProps({
  selectedRegion: {
    type: Object,
    required: true,
  },
  regions: {
    type: Array,
    required: true,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

const emit = defineEmits(['update:selectedRegion'])

const updateRegion = (newValue) => {
  emit('update:selectedRegion', newValue)
}
</script>

<template>
  <div class="flex flex-col items-center">
    <label for="region-select" class="block text-base font-medium mb-3 text-center">
      Sélectionnez un pays pour la comparaison
    </label>
    <Dropdown
      id="region-select"
      :modelValue="selectedRegion"
      :options="regions"
      optionLabel="name"
      placeholder="Choisir un pays"
      class="w-full max-w-md"
      :class="{ 'opacity-70': loading }"
      :disabled="loading"
      filter
      :showClear="true"
      @update:modelValue="updateRegion($event)"
    />
  </div>
</template>
