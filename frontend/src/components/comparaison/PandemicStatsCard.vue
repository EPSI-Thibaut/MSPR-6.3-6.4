<script setup lang="ts">
import Card from 'primevue/card'
import { useFormatters } from '../../composables/useFormatters'

defineProps({
  stat: {
    type: Object,
    required: true,
  },
})

const { formatNumber } = useFormatters()
</script>

<template>
  <Card class="h-full shadow-sm hover:shadow-md transition-shadow duration-300">
    <template #header>
      <div
        class="py-4 px-6 text-center"
        :class="
          stat.name === 'SARS'
            ? 'bg-blue-50 border-b-2 border-blue-300'
            : 'bg-indigo-50 border-b-2 border-indigo-300'
        "
      >
        <div
          class="text-xl font-bold"
          :class="stat.name === 'SARS' ? 'text-blue-700' : 'text-indigo-700'"
        >
          {{ stat.name }}
        </div>
      </div>
    </template>
    <template #content>
      <div class="grid grid-cols-2 gap-4 p-4">
        <div class="p-4 bg-gray-50 rounded-lg border border-gray-200">
          <div class="text-sm text-gray-600 mb-2">Cas maximum</div>
          <div class="text-2xl font-bold text-gray-800">
            {{ formatNumber(stat.totalCases) }}
          </div>
        </div>
        <div class="p-4 bg-red-50 rounded-lg border border-red-200">
          <div class="text-sm text-gray-600 mb-2">Décès maximum</div>
          <div class="text-2xl font-bold text-red-800">
            {{ formatNumber(stat.totalDeaths) }}
          </div>
        </div>
        <div class="p-4 bg-amber-50 rounded-lg border border-amber-200">
          <div class="text-sm text-gray-600 mb-2">Taux de mortalité</div>
          <div class="text-2xl font-bold text-amber-800">{{ stat.mortalityRate.toFixed(2) }}%</div>
        </div>
        <div class="p-4 bg-green-50 rounded-lg border border-green-200">
          <div class="text-sm text-gray-600 mb-2">Guéris</div>
          <div class="text-2xl font-bold text-green-800">
            {{ formatNumber(stat.recovered) }}
          </div>
        </div>
        <div class="p-4 bg-gray-50 rounded-lg border border-gray-200 col-span-2">
          <div class="text-sm text-gray-600 mb-2">Date des statistiques maximales</div>
          <div class="text-lg font-bold text-gray-800">{{ stat.maxDate }}</div>
        </div>
      </div>
    </template>
  </Card>
</template>
