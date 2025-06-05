<script setup lang="ts">
import { ref } from 'vue'
import { regions, defaultRegion } from '../constants/regionsData'
import { usePandemicData } from '../composables/usePandemicData'

// Imports des composants
import RegionSelector from '../components/comparaison/RegionSelector.vue'
import LoadingIndicator from '../components/comparaison/LoadingIndicator.vue'
import ErrorDisplay from '../components/comparaison/ErrorDisplay.vue'
import AnalysedRegion from '../components/comparaison/AnalysedRegion.vue'
import PandemicStatsCard from '../components/comparaison/PandemicStatsCard.vue'
import CasesDeathsChart from '../components/comparaison/charts/CasesDeathsChart.vue'
import MortalityChart from '../components/comparaison/charts/MortalityChart.vue'

// Région sélectionnée
const selectedRegion = ref(defaultRegion)

// Utiliser le composable pour gérer les données et l'état
const {
  loading,
  error,
  comparisonData,
  pandemicStats,
  casesDeathsData,
  mortalityData,
  barOptions,
  doughnutOptions,
} = usePandemicData(selectedRegion)
</script>

<template>
  <!-- Container principal avec max-width pour un meilleur contrôle -->
  <div class="max-w-7xl mx-auto px-4">
    <h1 class="text-center text-3xl font-bold mb-6">Comparaison des pandémies: SARS vs COVID-19</h1>

    <!-- Sélecteur de région -->
    <RegionSelector v-model:selectedRegion="selectedRegion" :regions="regions" :loading="loading" />

    <!-- Affichage des erreurs -->
    <ErrorDisplay v-if="error" :message="error" />

    <!-- Indicateur de chargement -->
    <LoadingIndicator v-if="loading" />

    <!-- Région concernée -->
    <AnalysedRegion v-if="comparisonData" :region="comparisonData.region" />
    <div class="flex">
      <!-- Cartes des statistiques - Version centrée -->
      <div class="flex justify-center mb-6">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-6 w-full max-w-4xl">
          <div v-for="stat in pandemicStats" :key="stat.id">
            <PandemicStatsCard :stat="stat" />
          </div>
        </div>
      </div>

      <!-- Graphiques -->
      <div class="flex flex-col">
        <div v-if="comparisonData && !loading" class="grid grid-cols-1 lg:grid-cols-2 gap-6 mb-6">
          <CasesDeathsChart :chartData="casesDeathsData" :chartOptions="barOptions" />
          <MortalityChart :chartData="mortalityData" :chartOptions="doughnutOptions" />
        </div>
        <div v-else-if="!loading && !error" class="text-center py-8">
          <p class="">Aucune donnée de comparaison disponible pour ce pays.</p>
        </div>
      </div>
      <!-- Message si pas de données -->
    </div>
  </div>
</template>
