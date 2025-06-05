<script setup lang="ts">
import ContinentsComparison from '../components/comparaison/ContinentsComparison.vue'
</script>

<template>
  <div class="px-4 py-6 sm:px-6 lg:px-8">
    <!-- Titre de la page -->
    <div class="mb-8 max-w-3xl">
      <h1
        class="text-2xl sm:text-3xl lg:text-4xl font-bold mb-3"
      >
      Analyse par continents
      </h1>
      <p class="text-base sm:text-lg text-gray-600">
        Comparaison détaillée de l'impact des pandémies SARS et COVID-19 par continent
      </p>
    </div>

    <!-- Composant de comparaison des continents -->
    <section role="region" aria-label="Comparaison des pandémies par continent" class="max-w-6xl mx-auto">
      <ContinentsComparison />
    </section>
  </div>
</template>
