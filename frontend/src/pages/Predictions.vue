<script setup lang="ts">
import { ref, watchEffect } from 'vue';
import Chart from 'primevue/chart';
import Button from 'primevue/button';
import ProgressSpinner from 'primevue/progressspinner';
import { usePandemicData } from '@/composables/usePandemicData';
import axios from 'axios';

const {
  predictions,
  fetchPredictions,
  loading,
  error,
} = usePandemicData();

const chartData = ref<any>(null);
const chartOptions = ref<any>(null);

// Points clés séparés
const keyCasesData = ref<any>(null);
const keyCasesOptions = ref<any>(null);
const keyDeathsData = ref<any>(null);
const keyDeathsOptions = ref<any>(null);
const keyRecoveredData = ref<any>(null);
const keyRecoveredOptions = ref<any>(null);

const training = ref(false);
const trainingMessage = ref('');

async function launchTraining() {
  training.value = true;
  trainingMessage.value = '';
  try {
    const res = await axios.post('/api/train_predict');
    trainingMessage.value = res.data.message || 'Entraînement terminé.';
    await fetchPredictions();
  } catch (err: any) {
    trainingMessage.value = err.response?.data?.message || 'Erreur lors de l’entraînement du modèle IA.';
  } finally {
    training.value = false;
  }
}

watchEffect(() => {
  if (predictions.value && predictions.value.length > 0) {
    const sorted = [...predictions.value].sort((a, b) => new Date(a.predictionDate).getTime() - new Date(b.predictionDate).getTime());
    const labels = sorted.map((item: any) => item.predictionDate);
    const cases = sorted.map((item: any) => item.predictedCases ?? null);
    const deaths = sorted.map((item: any) => item.predictedDeaths ?? null);
    const recovered = sorted.map((item: any) => item.predictedRecovered ?? null);

    chartData.value = {
      labels,
      datasets: [
        {
          label: 'Cas prédits',
          data: cases,
          borderColor: '#42A5F5',
          backgroundColor: 'rgba(66,165,245,0.2)',
          tension: 0.3,
          fill: false,
        },
        {
          label: 'Décès prédits',
          data: deaths,
          borderColor: '#E53935',
          backgroundColor: 'rgba(229,57,53,0.2)',
          tension: 0.3,
          fill: false,
        },
        {
          label: 'Guérisons prédites',
          data: recovered,
          borderColor: '#43A047',
          backgroundColor: 'rgba(67,160,71,0.2)',
          tension: 0.3,
          fill: false,
        },
      ],
    };
    chartOptions.value = {
      responsive: true,
      plugins: {
        legend: {
          display: true,
          labels: {
            color: '#222',
            font: { size: 14 }
          }
        },
        title: {
          display: true,
          text: 'Prédictions COVID 2023 (Cas, Décès, Guérisons)',
          font: { size: 18 }
        },
        tooltip: {
          mode: 'index',
          intersect: false,
        },
      },
      interaction: {
        mode: 'nearest',
        axis: 'x',
        intersect: false
      },
      scales: {
        x: {
          title: {
            display: true,
            text: 'Date',
            color: '#222',
            font: { size: 14 }
          },
          ticks: {
            color: '#222',
            maxTicksLimit: 12,
            autoSkip: true
          }
        },
        y: {
          title: {
            display: true,
            text: 'Nombre',
            color: '#222',
            font: { size: 14 }
          },
          ticks: {
            color: '#222',
            callback: (value: number) => value.toLocaleString('fr-FR')
          }
        }
      }
    };

    // Points clés séparés
    const maxCases = Math.max(...cases);
    const minCases = Math.min(...cases);
    const maxDeaths = Math.max(...deaths);
    const minDeaths = Math.min(...deaths);
    const maxRecovered = Math.max(...recovered);
    const minRecovered = Math.min(...recovered);

    keyCasesData.value = {
      labels: ['Cas max', 'Cas min'],
      datasets: [
        {
          label: 'Cas 2023',
          backgroundColor: ['#42A5F5', '#90CAF9'],
          data: [maxCases, minCases]
        }
      ]
    };
    keyCasesOptions.value = {
      responsive: true,
      plugins: {
        legend: { display: false },
        title: { display: true, text: 'Points clés - Cas', font: { size: 16 } }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: '#222',
            callback: (value: number) => value.toLocaleString('fr-FR')
          }
        },
        x: { ticks: { color: '#222' } }
      }
    };

    keyDeathsData.value = {
      labels: ['Décès max', 'Décès min'],
      datasets: [
        {
          label: 'Décès 2023',
          backgroundColor: ['#E53935', '#FFCDD2'],
          data: [maxDeaths, minDeaths]
        }
      ]
    };
    keyDeathsOptions.value = {
      responsive: true,
      plugins: {
        legend: { display: false },
        title: { display: true, text: 'Points clés - Décès', font: { size: 16 } }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: '#222',
            callback: (value: number) => value.toLocaleString('fr-FR')
          }
        },
        x: { ticks: { color: '#222' } }
      }
    };

    keyRecoveredData.value = {
      labels: ['Guérisons max', 'Guérisons min'],
      datasets: [
        {
          label: 'Guérisons 2023',
          backgroundColor: ['#43A047', '#A5D6A7'],
          data: [maxRecovered, minRecovered]
        }
      ]
    };
    keyRecoveredOptions.value = {
      responsive: true,
      plugins: {
        legend: { display: false },
        title: { display: true, text: 'Points clés - Guérisons', font: { size: 16 } }
      },
      scales: {
        y: {
          beginAtZero: true,
          ticks: {
            color: '#222',
            callback: (value: number) => value.toLocaleString('fr-FR')
          }
        },
        x: { ticks: { color: '#222' } }
      }
    };
  }
});

fetchPredictions();
</script>





<template>
  <div class="flex flex-column align-items-center">
    <h1>Prédictions COVID</h1>

    <Button
      label="Entraînement du modèle IA de prédiction 2023"
      icon="pi pi-cog"
      class="p-mb-3"
      @click="launchTraining"
      :disabled="training"
    />

    <div v-if="training" class="p-mt-3" style="display: flex; align-items: center;">
      <ProgressSpinner style="width: 40px; height: 40px" strokeWidth="4" />
      <span class="p-ml-3">Entraînement du modèle IA en cours...</span>
    </div>
    <div v-if="trainingMessage" class="p-mt-2">{{ trainingMessage }}</div>

    <div v-if="loading" class="p-mt-4">Chargement des données...</div>
    <div v-else>
      <div v-if="error" class="p-mt-4" style="color: red;">{{ error }}</div>
      <template v-if="chartData">
        <Chart type="line" :data="chartData" :options="chartOptions" style="min-height:500px; height:500px;" />
        <div style="display: flex; flex-wrap: wrap; gap: 32px; justify-content: center; margin-top: 40px;">
          <Chart v-if="keyCasesData" type="bar" :data="keyCasesData" :options="keyCasesOptions" style="max-width:300px;" />
          <Chart v-if="keyDeathsData" type="bar" :data="keyDeathsData" :options="keyDeathsOptions" style="max-width:300px;" />
          <Chart v-if="keyRecoveredData" type="bar" :data="keyRecoveredData" :options="keyRecoveredOptions" style="max-width:300px;" />
        </div>
        <div class="mt-8 w-full max-w-md bg-white rounded-lg shadow p-6">
          <h2 class="text-xl font-semibold mb-4 text-center">Points clés</h2>
          <ul class="space-y-2">
            <li class="flex justify-between border-b pb-1">
              <span>Cas max :</span>
              <span class="font-bold text-blue-600">{{ keyCasesData.datasets[0].data[0].toLocaleString('fr-FR') }}</span>
            </li>
            <li class="flex justify-between border-b pb-1">
              <span>Cas min :</span>
              <span class="font-bold text-blue-400">{{ keyCasesData.datasets[0].data[1].toLocaleString('fr-FR') }}</span>
            </li>
            <li class="flex justify-between border-b pb-1">
              <span>Décès max :</span>
              <span class="font-bold text-red-600">{{ keyDeathsData.datasets[0].data[0].toLocaleString('fr-FR') }}</span>
            </li>
            <li class="flex justify-between border-b pb-1">
              <span>Décès min :</span>
              <span class="font-bold text-red-400">{{ keyDeathsData.datasets[0].data[1].toLocaleString('fr-FR') }}</span>
            </li>
            <li class="flex justify-between border-b pb-1">
              <span>Guérisons max :</span>
              <span class="font-bold text-green-600">{{ keyRecoveredData.datasets[0].data[0].toLocaleString('fr-FR') }}</span>
            </li>
            <li class="flex justify-between">
              <span>Guérisons min :</span>
              <span class="font-bold text-green-400">{{ keyRecoveredData.datasets[0].data[1].toLocaleString('fr-FR') }}</span>
            </li>
          </ul>
        </div>
      </template>
      <div v-else class="p-mt-4">Aucune donnée de prédiction disponible.</div>
    </div>
  </div>
</template>



