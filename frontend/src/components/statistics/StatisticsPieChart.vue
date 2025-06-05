<script setup lang="ts">
import { defineProps, computed } from 'vue'
import Chart from 'primevue/chart'
import ChartSkeleton from './ChartSkeleton.vue'
import { useChartFormatters } from '@/composables/useChartFormatters'

const { generateColors } = useChartFormatters()

const props = defineProps({
  allCountriesData: {
    type: Array as () => { region: string; deaths: number }[],
    required: true,
  },
  selectedPandemic: {
    type: Object,
    default: null,
  },
  loading: {
    type: Boolean,
    default: false,
  },
})

// Données calculées pour le graphique en camembert
const pieData = computed(() => ({
  labels: props.allCountriesData.map((country) => country.region),
  datasets: [
    {
      data: props.allCountriesData.map((country) => country.deaths),
      backgroundColor: generateColors(props.allCountriesData.length),
    },
  ],
}))

// Options pour le graphique en camembert
const pieOptions = {
  responsive: true,
  maintainAspectRatio: true,
  aspectRatio: 2,
  plugins: {
    legend: {
      position: 'right',
      labels: {
        font: { size: 11 },
        boxWidth: 10,
        padding: 5,
      },
      maxItems: 20,
    },
    tooltip: {
      callbacks: {
        title: function (tooltipItem: { label: unknown }[]) {
          return tooltipItem[0].label
        },
        label: function (tooltipItem: { raw: number | bigint }) {
          return ` Décès: ${new Intl.NumberFormat('fr-FR').format(tooltipItem.raw)}`
        },
      },
    },
  },
}
</script>

<template>
  <div class="mb-8 max-w-6xl mx-auto px-4">
    <!-- Skeleton pendant le chargement -->
    <ChartSkeleton v-if="loading" type="pie" />

    <!-- Graphique réel quand chargé -->
    <div v-else class="shadow-lg rounded-xl px-4 py-6 sm:px-6 mb-8 border border-gray-200" role="region"
    :aria-label="`Répartition des décès par pays pour ${selectedPandemic?.name}`">
      <h3 class="text-xl font-semibold text-gray-700 mb-4">
        Répartition des morts par pays - {{ selectedPandemic?.name }}
      </h3>
      <Chart type="pie" :data="pieData" :options="pieOptions" class="w-full h-[300px] sm:h-[400px] md:h-[500px]" aria-hidden="true" />
    </div>
  </div>
</template>
