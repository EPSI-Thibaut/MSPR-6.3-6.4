<script setup lang="ts">
import Chart from 'primevue/chart'
import Card from 'primevue/card'

defineProps({
  chartData: {
    type: Object,
    required: true,
  },
  chartOptions: {
    type: Object,
    required: true,
  },
  type: {
    type: String,
    default: 'bar', // ou 'doughnut' pour MortalityChart
  },
})
</script>

<template>
  <Card>
    <template #title>
      <div class="text-xl font-semibold text-center py-3 border-b">
        {{ type === 'bar' ? 'Cas, décès et guérisons' : 'Taux de mortalité (%)' }}
      </div>
    </template>
    <template #content>
      <div class="h-[450px] p-4">
        <Chart :type="type" :data="chartData" :options="chartOptions" />
      </div>
    </template>
  </Card>
</template>
