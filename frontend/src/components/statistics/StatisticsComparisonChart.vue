<script setup lang="ts">
import { defineProps, computed } from 'vue'
import Chart from 'primevue/chart'
import ChartSkeleton from './ChartSkeleton.vue'

interface Region {
  id: number
  name: string
  [key: string]: unknown
}

interface Pandemic {
  id: number
  name: string
  [key: string]: unknown
}

interface ComparisonData {
  pandemic1Id: number
  pandemic2Id: number
  pandemic1MaxStats: { cases: number; deaths: number }
  pandemic2MaxStats: { cases: number; deaths: number }
  [key: string]: unknown
}

const props = defineProps({
  comparisonData: {
    type: Object as () => ComparisonData | null,
    default: null,
  },
  pandemies: {
    type: Array as () => Pandemic[],
    required: true,
  },
  selectedRegion: {
    type: Object as () => Region | null,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

// Données pour le graphique de comparaison
const comparisonChartData = computed(() => {
  if (!props.comparisonData) return { labels: [], datasets: [] }

  // Récupérer les noms des pandémies pour les labels
  const pandemic1Name =
    props.pandemies.find((p) => p.id === props.comparisonData?.pandemic1Id)?.name || 'SARS'
  const pandemic2Name =
    props.pandemies.find((p) => p.id === props.comparisonData?.pandemic2Id)?.name || 'COVID-19'

  return {
    labels: ['Cas max', 'Décès max'],
    datasets: [
      {
        label: pandemic1Name,
        backgroundColor: 'rgba(75, 192, 192, 0.7)',
        data: [
          props.comparisonData.pandemic1MaxStats?.cases || 0,
          props.comparisonData.pandemic1MaxStats?.deaths || 0,
        ],
      },
      {
        label: pandemic2Name,
        backgroundColor: 'rgba(255, 99, 132, 0.7)',
        data: [
          props.comparisonData.pandemic2MaxStats?.cases || 0,
          props.comparisonData.pandemic2MaxStats?.deaths || 0,
        ],
      },
    ],
  }
})
</script>

<template>
  <div class="mb-8 max-w-6xl mx-auto px-4">
    <!-- Skeleton pendant le chargement -->
    <ChartSkeleton v-if="loading" type="bar" />

    <!-- Graphique réel quand chargé -->
    <div
      v-else-if="comparisonData"
      class="shadow-lg rounded-xl px-4 py-6 sm:px-6 mb-8 border border-gray-200"
    >
      <h3 class="text-xl font-semibold text-gray-700 mb-4">
        Comparaison SARS vs COVID-19 - {{ selectedRegion?.name }}
      </h3>
      <Chart type="bar" :data="comparisonChartData" class="w-full h-[300px] sm:h-[400px] md:h-[500px]"
      aria-label="Graphique de comparaison des cas et décès entre les pandémies"/>
    </div>
  </div>
</template>
