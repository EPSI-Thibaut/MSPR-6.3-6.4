<script setup lang="ts">
import { onMounted, watch } from 'vue'
import StatisticsFilters from '@/components/statistics/StatisticsFilters.vue'
import StatisticsPieChart from '@/components/statistics/StatisticsPieChart.vue'
import StatisticsBarChart from '@/components/statistics/StatisticsBarChart.vue'
import StatisticsTimelineChart from '@/components/statistics/StatisticsTimelineChart.vue'
import StatisticsComparisonChart from '@/components/statistics/StatisticsComparisonChart.vue'
import LoadingIndicator from '@/components/common/LoadingIndicator.vue'
import { usePandemicData } from '@/composables/usePandemicData'

// Récupérer toutes les données et fonctions liées aux pandémies
const {
  loading,
  error,
  pandemies,
  regions,
  selectedPandemic,
  selectedRegion,
  countriesData,
  allCountriesData,
  timelineData,
  comparisonData,
  loadingPandemicData,
  loadingTimelineData,
  loadingComparisonData,
  fetchPandemics,
  fetchRegions,
  fetchPandemicData,
  fetchTimelineData,
  fetchComparisonData,
} = usePandemicData()

// Observer les changements de sélection
watch([selectedPandemic, selectedRegion], () => {
  if (selectedPandemic.value && selectedRegion.value) {
    loadingPandemicData.value = true
    loadingTimelineData.value = true
    loadingComparisonData.value = true

    fetchPandemicData()
    fetchTimelineData()
    fetchComparisonData()
  }
})

// Charger les données initiales
onMounted(async () => {
  try {
    await Promise.all([fetchPandemics(), fetchRegions()])

    if (selectedPandemic.value && selectedRegion.value) {
      fetchPandemicData()
      fetchTimelineData()
      fetchComparisonData()
    }
  } catch (error) {
    console.error("Erreur lors de l'initialisation des données:", error)
  }
})
</script>

<template>
  <div class="px-4 sm:px-6 lg:px-8 py-6 max-w-6xl mx-auto">
    <header class="text-center mb-6" role="region" aria-label="Titre de la page Statistiques">
      <h2 class="text-3xl font-bold">Statistiques des maladies</h2>
    </header>

    <!-- Filtres (sélecteurs de pandémie et région) -->
    <section role="region" aria-label="Filtres de sélection de pandémie et de région">
      <StatisticsFilters
        :pandemies="pandemies"
        :regions="regions"
        v-model:selectedPandemic="selectedPandemic"
        v-model:selectedRegion="selectedRegion"
        :loading="loading"
      />
    </section>

    <!-- Chargement initial des données -->
    <LoadingIndicator v-if="loading" message="Chargement des données..." />

    <!-- Message d'erreur global -->
    <div
      v-else-if="error"
      role="alert"
      class="px-4 py-6 mb-6 text-center max-w-xl mx-auto"
    >
      <p class="text-red-600 text-lg">{{ error }}</p>
    </div>

    <!-- Message quand aucune sélection -->
    <div
      v-else-if="!selectedPandemic || !selectedRegion"
      class="text-center py-12 px-4 max-w-xl mx-auto"
      role="region"
      aria-label="Message de données indisponibles"
    >
      <p class="text-gray-600 text-lg">Veuillez sélectionner une pandémie et une région.</p>
    </div>

    <!-- Message quand aucune donnée n'est disponible -->
    <div
      v-else-if="countriesData.length === 0 && !loadingPandemicData"
      class="text-center py-12 px-4 max-w-xl mx-auto"
      role="alert"
    >
      <p class="text-gray-600 text-lg">Aucune donnée disponible pour cette pandémie.</p>
    </div>

    <!-- Spinner central pendant le chargement des données après sélection -->
    <LoadingIndicator
      v-else-if="loadingPandemicData && loadingTimelineData && loadingComparisonData"
      :fullScreen="true"
      message="Chargement des données..."
      role="status"
      aria-live="polite"
    />

    <!-- Contenu principal avec les graphiques -->
    <div v-else role="region" aria-label="Visualisation des statistiques">

            <!-- Graphique timeline -->
      <StatisticsTimelineChart
        v-if="timelineData.length > 0 || loadingTimelineData"
        :timelineData="timelineData"
        :selectedPandemic="selectedPandemic"
        :selectedRegion="selectedRegion"
        :loading="loadingTimelineData"
      />

      <!-- Graphique comparaison -->
      <StatisticsComparisonChart
        v-if="comparisonData || loadingComparisonData"
        :comparisonData="comparisonData"
        :pandemies="pandemies"
        :selectedRegion="selectedRegion"
        :loading="loadingComparisonData"
      />

      <!-- Graphique en camembert -->
      <StatisticsPieChart
        :allCountriesData="allCountriesData"
        :selectedPandemic="selectedPandemic"
        :loading="loadingPandemicData"
      />

      <!-- Graphique en barres -->
      <StatisticsBarChart
        :countriesData="countriesData"
        :selectedPandemic="selectedPandemic"
        :loading="loadingPandemicData"
      />


    </div>
  </div>
</template>
