<script setup lang="ts">
import { ref, onMounted, computed } from 'vue'
import Card from 'primevue/card'
import Chart from 'primevue/chart'
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
import TabView from 'primevue/tabview'
import TabPanel from 'primevue/tabpanel'
import ProgressSpinner from 'primevue/progressspinner'
import Button from 'primevue/button'
import Panel from 'primevue/panel'
import Divider from 'primevue/divider'
import Badge from 'primevue/badge'
import { useFormatters } from '../../composables/useFormatters'
import axios from 'axios'

const { formatNumber } = useFormatters()

// État des données
const continentsData = ref([])
const loading = ref(true)
const error = ref(null)
const activeTabIndex = ref(0)

// Configuration du graphique des cas
const casesChartData = computed(() => {
  if (!continentsData.value?.length) return { labels: [], datasets: [] }

  return {
    labels: continentsData.value.map((continent) => continent.continentName),
    datasets: [
      {
        label: 'COVID-19',
        backgroundColor: 'rgba(54, 162, 235, 0.7)',
        borderColor: 'rgba(54, 162, 235, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.covid?.totalCases || 0),
      },
      {
        label: 'SARS',
        backgroundColor: 'rgba(255, 99, 132, 0.7)',
        borderColor: 'rgba(255, 99, 132, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.sars?.totalCases || 0),
      },
    ],
  }
})

// Configuration du graphique des décès
const deathsChartData = computed(() => {
  if (!continentsData.value?.length) return { labels: [], datasets: [] }

  return {
    labels: continentsData.value.map((continent) => continent.continentName),
    datasets: [
      {
        label: 'COVID-19',
        backgroundColor: 'rgba(75, 192, 192, 0.7)',
        borderColor: 'rgba(75, 192, 192, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.covid?.totalDeaths || 0),
      },
      {
        label: 'SARS',
        backgroundColor: 'rgba(255, 159, 64, 0.7)',
        borderColor: 'rgba(255, 159, 64, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.sars?.totalDeaths || 0),
      },
    ],
  }
})

// Configuration du graphique des taux de mortalité
const mortalityRateChartData = computed(() => {
  if (!continentsData.value?.length) return { labels: [], datasets: [] }

  return {
    labels: continentsData.value.map((continent) => continent.continentName),
    datasets: [
      {
        label: 'COVID-19',
        backgroundColor: 'rgba(153, 102, 255, 0.7)',
        borderColor: 'rgba(153, 102, 255, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.covid?.mortalityRate || 0),
      },
      {
        label: 'SARS',
        backgroundColor: 'rgba(255, 206, 86, 0.7)',
        borderColor: 'rgba(255, 206, 86, 1)',
        borderWidth: 1,
        data: continentsData.value.map((continent) => continent.sars?.mortalityRate || 0),
      },
    ],
  }
})

// Options génériques pour les graphiques
const chartOptions = computed(() => {
  return {
    responsive: true,
    maintainAspectRatio: false,
    plugins: {
      legend: {
        position: 'top',
        labels: {
          usePointStyle: true,
          padding: 15,
        },
      },
      tooltip: {
        callbacks: {
          label: function (context) {
            let label = context.dataset.label || ''
            if (label) {
              label += ': '
            }
            if (context.parsed.y !== null) {
              label += formatNumber(context.parsed.y)
            }
            return label
          },
        },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            if (value >= 1000000) {
              return (value / 1000000).toFixed(1) + 'M'
            } else if (value >= 1000) {
              return (value / 1000).toFixed(1) + 'K'
            }
            return value
          },
        },
      },
    },
  }
})

// Options spécifiques pour le graphique des taux de mortalité
const mortalityChartOptions = computed(() => {
  return {
    ...chartOptions.value,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value) {
            return value.toFixed(2) + '%'
          },
        },
      },
    },
    plugins: {
      ...chartOptions.value.plugins,
      tooltip: {
        callbacks: {
          label: function (context) {
            let label = context.dataset.label || ''
            if (label) {
              label += ': '
            }
            if (context.parsed.y !== null) {
              label += context.parsed.y.toFixed(2) + '%'
            }
            return label
          },
        },
      },
    },
  }
})

// Données pour le tableau comparatif
const tableData = computed(() => {
  if (!continentsData.value) return []

  return continentsData.value.map((continent) => {
    const covidCases = continent.covid?.totalCases || 0
    const sarsCases = continent.sars?.totalCases || 0
    const covidDeaths = continent.covid?.totalDeaths || 0
    const sarsDeaths = continent.sars?.totalDeaths || 0
    const covidMortality = continent.covid?.mortalityRate || 0
    const sarsMortality = continent.sars?.mortalityRate || 0
    const casesRatio = covidCases && sarsCases ? covidCases / sarsCases : 0

    return {
      continent: continent.continentName,
      covidCases,
      sarsCases,
      covidDeaths,
      sarsDeaths,
      covidMortality: covidMortality.toFixed(2) + '%',
      sarsMortality: sarsMortality.toFixed(2) + '%',
      covidRegions: continent.covid?.affectedRegions || 0,
      sarsRegions: continent.sars?.affectedRegions || 0,
      casesRatio: sarsCases > 0 ? formatNumber(casesRatio) : 'N/A',
      mortalityDiff: (covidMortality - sarsMortality).toFixed(2) + '%',
    }
  })
})

// Fonction de chargement des données
const fetchContinentsData = async () => {
  try {
    loading.value = true
    error.value = null

    const response = await axios.get('/api/stats/continents-comparison', {})

    if (response.status === 200) {
      continentsData.value = response.data || []
      console.log('Données chargées:', continentsData.value)
    } else {
      throw new Error('Erreur lors de la récupération des données')
    }
  } catch (err) {
    console.error('Erreur lors du chargement des données de continents:', err)
    error.value = 'Impossible de charger les données de comparaison des continents'
  } finally {
    loading.value = false
  }
}

// Charger les données au montage du composant
onMounted(fetchContinentsData)
</script>

<template>
  <Card class="shadow-lg">
    <template #header>
      <div class="flex justify-content-between align-items-center">
        <h3 class="text-xl font-bold text-primary m-0">Analyse comparative par continent</h3>
        <Button
          icon="pi pi-refresh"
          @click="fetchContinentsData"
          :loading="loading"
          class="p-button-text p-button-rounded"
          tooltip="Rafraîchir les données"
        />
      </div>
    </template>
    <template #content>
      <!-- État de chargement -->
      <div
        v-if="loading"
        class="flex justify-content-center align-items-center"
        style="min-height: 300px"
      >
        <div class="flex flex-column align-items-center">
          <ProgressSpinner strokeWidth="3" style="width: 50px; height: 50px" />
          <span class="mt-3 text-600">Chargement des données continentales...</span>
        </div>
      </div>

      <!-- Message d'erreur -->
      <div v-else-if="error" class="p-4 border-round border-left-3">
        <div class="flex align-items-center">
          <i class="pi pi-exclamation-triangle mr-3" style="font-size: 1.5rem"></i>
          <span>{{ error }}</span>
        </div>
      </div>

      <!-- Aucune donnée -->
      <div
        v-else-if="!continentsData || continentsData.length === 0"
        class="p-4 border-round bg-blue-50 flex justify-content-center"
      >
        <div class="flex align-items-center">
          <i class="pi pi-info-circle mr-3 text-blue-500" style="font-size: 1.5rem"></i>
          <span class="text-blue-700">Aucune donnée de comparaison disponible.</span>
        </div>
      </div>

      <!-- Contenu principal avec données -->
      <div v-else>
        <TabView v-model:activeIndex="activeTabIndex">
          <!-- TAB 1: Vue d'ensemble -->
          <TabPanel header="Vue d'ensemble">
            <div class="grid">
              <div class="col-12 mb-4">
                <Panel header="Résumé des impacts par continent">
                  <DataTable
                    :value="tableData"
                    responsiveLayout="scroll"
                    class="p-datatable-sm"
                    :rowHover="true"
                    sortField="covidCases"
                    :sortOrder="-1"
                  >
                    <Column field="continent" header="Continent" sortable />
                    <Column field="covidCases" header="Cas COVID-19" sortable>
                      <template #body="slotProps">
                        <span class="font-semibold text-primary">{{
                          formatNumber(slotProps.data.covidCases)
                        }}</span>
                      </template>
                    </Column>
                    <Column field="covidDeaths" header="Décès COVID-19" sortable>
                      <template #body="slotProps">
                        <span class="text-pink-600">{{
                          formatNumber(slotProps.data.covidDeaths)
                        }}</span>
                      </template>
                    </Column>
                    <Column field="covidMortality" header="Tx. Mortalité COVID-19" sortable>
                      <template #body="slotProps">
                        <span>{{ slotProps.data.covidMortality }}</span>
                      </template>
                    </Column>
                    <Column field="sarsCases" header="Cas SARS" sortable>
                      <template #body="slotProps">
                        <span class="font-semibold text-indigo-600">{{
                          formatNumber(slotProps.data.sarsCases)
                        }}</span>
                      </template>
                    </Column>
                    <Column field="casesRatio" header="Ratio des cas" sortable>
                      <template #body="slotProps">
                        <span
                          class="font-bold"
                          :class="{
                            'text-green-600':
                              slotProps.data.casesRatio !== 'N/A' && slotProps.data.casesRatio > 0,
                          }"
                        >
                          {{ slotProps.data.casesRatio }}
                        </span>
                      </template>
                    </Column>
                  </DataTable>
                </Panel>
              </div>
            </div>
          </TabPanel>

          <!-- TAB 2: Graphiques - Version corrigée -->
          <TabPanel header="Graphiques">
            <div class="grid grid-nogutter">
              <!-- Première rangée avec deux graphiques côte à côte -->
              <div class="col-12 lg:col-6 p-2">
                <Panel header="Cas par continent" class="h-full">
                  <div class="chart-container">
                    <Chart type="bar" :data="casesChartData" :options="chartOptions" />
                  </div>
                </Panel>
              </div>

              <div class="col-12 lg:col-6 p-2">
                <Panel header="Décès par continent" class="h-full">
                  <div class="chart-container">
                    <Chart type="bar" :data="deathsChartData" :options="chartOptions" />
                  </div>
                </Panel>
              </div>

              <!-- Deuxième rangée pour le graphique de taux de mortalité -->
              <div class="col-12 p-2 mt-2">
                <Panel header="Taux de mortalité par continent (%)" class="h-full">
                  <div class="chart-container">
                    <Chart
                      type="bar"
                      :data="mortalityRateChartData"
                      :options="mortalityChartOptions"
                    />
                  </div>
                </Panel>
              </div>
            </div>
          </TabPanel>

          <!-- TAB 3: Fiches détaillées -->
          <TabPanel header="Fiches détaillées">
            <div class="grid">
              <div
                v-for="(continent, index) in continentsData"
                :key="index"
                class="col-12 md:col-6 lg:col-4 mb-4"
              >
                <Panel :header="continent.continentName" toggleable>
                  <template #icons>
                    <Badge
                      :value="continent.covid?.affectedRegions || 0"
                      severity="info"
                      class="mr-2"
                    ></Badge>
                  </template>

                  <div class="mb-3">
                    <h5 class="mb-2 font-bold text-primary">COVID-19</h5>
                    <div class="grid">
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Cas</div>
                        <div class="font-medium">
                          {{ formatNumber(continent.covid?.totalCases || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Décès</div>
                        <div class="font-medium text-pink-600">
                          {{ formatNumber(continent.covid?.totalDeaths || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Guéris</div>
                        <div class="font-medium text-green-600">
                          {{ formatNumber(continent.covid?.totalRecovered || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Mortalité</div>
                        <div class="font-medium">
                          {{ (continent.covid?.mortalityRate || 0).toFixed(2) }}%
                        </div>
                      </div>
                      <div class="col-12">
                        <div class="text-500 text-sm">Pays affectés</div>
                        <div class="font-medium">{{ continent.covid?.affectedRegions || 0 }}</div>
                      </div>
                    </div>
                  </div>

                  <Divider />

                  <div class="mb-3">
                    <h5 class="mb-2 font-bold text-pink-600">SARS</h5>
                    <div class="grid">
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Cas</div>
                        <div class="font-medium">
                          {{ formatNumber(continent.sars?.totalCases || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Décès</div>
                        <div class="font-medium text-pink-600">
                          {{ formatNumber(continent.sars?.totalDeaths || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Guéris</div>
                        <div class="font-medium text-green-600">
                          {{ formatNumber(continent.sars?.totalRecovered || 0) }}
                        </div>
                      </div>
                      <div class="col-6 mb-2">
                        <div class="text-500 text-sm">Mortalité</div>
                        <div class="font-medium">
                          {{ (continent.sars?.mortalityRate || 0).toFixed(2) }}%
                        </div>
                      </div>
                      <div class="col-12">
                        <div class="text-500 text-sm">Pays affectés</div>
                        <div class="font-medium">{{ continent.sars?.affectedRegions || 0 }}</div>
                      </div>
                    </div>
                  </div>

                  <Divider />

                  <div
                    class="p-3 border-round"
                    :class="{ 'bg-green-50': continent.comparison?.casesRatio > 0 }"
                  >
                    <h5 class="m-0 mb-2 font-bold">Comparaison</h5>
                    <div class="flex justify-content-between mb-2">
                      <span class="text-500">Ratio des cas (COVID/SARS)</span>
                      <span class="font-medium">{{
                        continent.comparison?.casesRatio
                          ? formatNumber(continent.comparison.casesRatio)
                          : 'N/A'
                      }}</span>
                    </div>
                    <div class="flex justify-content-between">
                      <span class="text-500">Différence de mortalité</span>
                      <span
                        class="font-medium"
                        :class="{
                          'text-green-600': continent.comparison?.mortalityRateDifference < 0,
                          'text-red-600': continent.comparison?.mortalityRateDifference > 0,
                        }"
                      >
                        {{ continent.comparison?.mortalityRateDifference?.toFixed(2) || 'N/A' }}%
                      </span>
                    </div>
                  </div>
                </Panel>
              </div>
            </div>
          </TabPanel>
        </TabView>

        <div class="mt-4 text-center text-xs text-500">
          Données mises à jour le {{ new Date().toLocaleDateString() }}
        </div>
      </div>
    </template>
  </Card>
</template>

<style scoped>
/* Styles complémentaires pour améliorer l'apparence */
:deep(.p-panel-header) {
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

:deep(.p-datatable .p-datatable-thead > tr > th) {
  background-color: #f8f9fa;
}

:deep(.p-tabview .p-tabview-nav) {
  border-bottom: 1px solid #dee2e6;
}

:deep(.p-tabview .p-tabview-nav .p-tabview-nav-link) {
  transition: all 0.2s;
}

:deep(.p-tabview .p-tabview-panels) {
  padding-top: 1.5rem;
}
</style>
