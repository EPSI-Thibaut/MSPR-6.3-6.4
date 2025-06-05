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
})
</script>

<template>
  <Card>
    <template #title>
      <div class="text-xl font-bold">Cas, décès et guérisons</div>
    </template>
    <template #content>
      <div class="h-[500px]">
        <Chart type="bar" :data="chartData" :options="chartOptions" />
      </div>
    </template>
  </Card>
</template>
