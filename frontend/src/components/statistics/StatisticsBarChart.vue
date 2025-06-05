<script setup lang="ts">
import { defineProps, computed } from 'vue'
import Chart from 'primevue/chart'
import ChartSkeleton from './ChartSkeleton.vue'
import { useTheme } from '@/composables/useTheme'

const { isDarkTheme } = useTheme()

const props = defineProps({
  countriesData: {
    type: Array,
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

// Données calculées pour le graphique en barres verticales
const barData = computed(() => ({
  labels: props.countriesData.map((country) => country.region),
  datasets: [
    {
      label: 'Morts',
      backgroundColor: 'var(--chart-bg-secondary)',
      data: props.countriesData.map((country) => country.deaths),
    },
    {
      label: 'Cas',
      backgroundColor: 'var(--chart-bg-primary)',
      data: props.countriesData.map((country) => country.cases),
    },
    {
      label: 'Guéris',
      backgroundColor: 'var(--chart-bg-tertiary)',
      data: props.countriesData.map((country) => country.recovered || 0),
    },
  ],
}))

// Options du graphique adaptées au thème
const chartOptions = computed(() => ({
  responsive: true,
  maintainAspectRatio: false,
  plugins: {
    legend: {
      labels: {
        color: isDarkTheme.value ? '#f3f4f6' : '#2c3e50'
      }
    },
    tooltip: {
      backgroundColor: isDarkTheme.value ? '#374151' : '#ffffff',
      titleColor: isDarkTheme.value ? '#f3f4f6' : '#2c3e50',
      bodyColor: isDarkTheme.value ? '#d1d5db' : '#4b5563',
      borderColor: isDarkTheme.value ? '#4b5563' : '#e5e7eb',
    }
  },
  scales: {
    x: {
      ticks: {
        color: isDarkTheme.value ? '#d1d5db' : '#4b5563'
      },
      grid: {
        color: isDarkTheme.value ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)'
      }
    },
    y: {
      ticks: {
        color: isDarkTheme.value ? '#d1d5db' : '#4b5563'
      },
      grid: {
        color: isDarkTheme.value ? 'rgba(255, 255, 255, 0.1)' : 'rgba(0, 0, 0, 0.1)'
      }
    }
  }
}))
</script>

<template>
  <div  class="mb-8 max-w-6xl mx-auto px-4">
    <!-- Skeleton pendant le chargement -->
    <ChartSkeleton v-if="loading" type="bar" />

    <!-- Graphique réel quand chargé -->
    <div v-else class="shadow-lg rounded-xl px-4 py-6 sm:px-6 mb-8 border overflow-x-auto"

      :style="{
        backgroundColor: 'var(--card-background)',
        borderColor: 'var(--card-border)',
        boxShadow: 'var(--card-shadow)'
      }">
      <h3 class="text-xl font-semibold mb-4" :style="{ color: 'var(--color-text)' }">
        Nombre de morts, cas ou guéris - {{ selectedPandemic?.name }}
      </h3>
      <Chart type="bar" :data="barData" :options="chartOptions" class="w-full h-[300px] sm:h-[400px] md:h-[500px]"
      role="img" aria-label="Graphique des morts, cas et guéris par région pour la pandémie sélectionnée" />
    </div>
  </div>
</template>
