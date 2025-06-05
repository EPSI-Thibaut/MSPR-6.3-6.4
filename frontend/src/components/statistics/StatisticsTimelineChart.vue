<script setup lang="ts">
import { defineProps, computed } from 'vue'
import Chart from 'primevue/chart'
import ChartSkeleton from './ChartSkeleton.vue'

const props = defineProps({
  timelineData: {
    type: Array as () => Array<{ date: string; cases: number; deaths: number; recovered?: number }>,
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

// Données calculées pour le graphique de la timeline
const timelineChartData = computed(() => ({
  labels: props.timelineData.map((item) => item.date),
  datasets: [
    {
      label: 'Cas',
      backgroundColor: 'rgba(54, 162, 235, 0.2)',
      borderColor: '#36A2EB',
      pointBackgroundColor: '#36A2EB',
      data: props.timelineData.map((item) => item.cases),
      fill: true,
    },
    {
      label: 'Morts',
      backgroundColor: 'rgba(255, 99, 132, 0.2)',
      borderColor: '#FF6384',
      pointBackgroundColor: '#FF6384',
      data: props.timelineData.map((item) => item.deaths),
      fill: true,
    },
    {
      label: 'Guéris',
      backgroundColor: 'rgba(75, 192, 192, 0.2)',
      borderColor: '#4BC0C0',
      pointBackgroundColor: '#4BC0C0',
      data: props.timelineData.map((item) => item.recovered || 0),
      fill: true,
    },
  ],
}))
</script>

<template>
  <div class="mb-8">
    <!-- Skeleton pendant le chargement -->
    <ChartSkeleton v-if="loading" type="line" />

    <!-- Graphique réel quand chargé -->
    <div
      v-else-if="timelineData.length > 0"
      class="shadow-lg rounded-xl px-4 py-6 sm:px-6 mb-8 border border-gray-200 max-w-6xl mx-auto"
      role="region"
      :aria-label="`Graphique de l'évolution dans le temps pour ${selectedRegion?.name} pendant ${selectedPandemic?.name}`"
    >
      <h3 class="text-xl font-semibold text-gray-700 mb-4">
        Évolution dans le temps - {{ selectedRegion?.name }} ({{ selectedPandemic?.name }})
      </h3>
      <Chart type="line" :data="timelineChartData" class="w-full h-[300px] sm:h-[400px] md:h-[500px]" aria-hidden="true" />
    </div>
  </div>
</template>
