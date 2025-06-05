<!-- Dans AnalysedRegion.vue -->
<script setup lang="ts">
// Si le composant reçoit region comme prop
defineProps({
  region: {
    type: Object,
    required: true,
  },
})
</script>

<template>
  <div class="mb-6 text-center">
    <h2 class="text-xl font-semibold">Pays analysé: {{ region.name || 'Inconnu' }}</h2>
  </div>
</template>
