import { ref, computed, watch, onMounted } from 'vue'
import axios from 'axios'
import { regions as predefinedRegions } from '@/constants/regionsData'

// Types existants
interface PandemicStats {
  date: string
  recovered: number
  cases: number
  deaths: number
}

interface ComparisonMetrics {
  deathRatio: number
  mortalityRate1: number
  caseRatio: number
  mortalityRate2: number
}

interface ComparisonData {
  pandemic1MaxStats: PandemicStats
  pandemic2MaxStats: PandemicStats
  comparisonMetrics: ComparisonMetrics
  region: string
  pandemic1: string
  pandemic2: string
  pandemic1Id?: number
  pandemic2Id?: number
}

// Type pour les pays avec coordonnées
interface CountryData {
  region: string
  cases: number
  deaths: number
  recovered?: number
  coords?: [number, number]
}

// Type pour les options des graphiques
interface ChartOptions {
  responsive: boolean
  maintainAspectRatio: boolean
  [key: string]: unknown
}

// Type pour les données du graphique
interface ChartData {
  labels: string[]
  datasets: {
    label?: string
    data: number[]
    backgroundColor: string | string[]
    hoverBackgroundColor?: string | string[]
  }[]
}

export function usePandemicData(selectedRegion: unknown = null) {
  // États pour les données des pandémies et des régions
  const pandemies = ref<
    {
      id: number
      name: string
      totalCases: number
      totalDeaths: number
      mortalityRate: number
      affectedRegions: string[]
    }[]
  >([])
  const regions = ref(predefinedRegions) // Utiliser les régions importées
  const localSelectedRegion = ref(selectedRegion || predefinedRegions[0])
  const selectedPandemic = ref<{
    id: number
    name: string
    totalCases: number
    totalDeaths: number
    mortalityRate: number
    affectedRegions: string[]
  } | null>(null)

  // États pour les données des graphiques
  const countriesData = ref<CountryData[]>([])
  const allCountriesData = ref<CountryData[]>([])
  const timelineData = ref<{ date: string; cases: number; deaths: number; recovered?: number }[]>(
    [],
  )
  const comparisonData = ref<ComparisonData | null>(null)

  // États pour le chargement
  const loading = ref(true)
  const error = ref<string | null>(null)
  const loadingPandemicData = ref(false)
  const loadingTimelineData = ref(false)
  const loadingComparisonData = ref(false)

  // Format de date
  const formatDate = (dateString: string): string => {
    if (!dateString) return 'Non disponible'

    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    })
  }

  // Récupérer les données de comparaison (version simplifiée)
  const fetchComparisonDataSimple = async () => {
    if (!localSelectedRegion.value) return

    try {
      loading.value = true
      error.value = null

      // IDs fixes pour SARS (1) et COVID (2)
      const response = await axios.get(`/api/stats/compare/1/2/${localSelectedRegion.value.id}`)

      if (response.status !== 200) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }

      // Stocker les données de comparaison avec la région correctement formatée
      comparisonData.value = {
        ...response.data,
        // Remplacer la chaîne region par l'objet région complet
        region: localSelectedRegion.value,
      }
    } catch (err) {
      console.error('Erreur lors de la récupération des données:', err)
      error.value = `Erreur lors du chargement des données: ${err.message}`
      comparisonData.value = null
    } finally {
      loading.value = false
    }
  }

const predictions = ref<any[]>([]);
  // Récupérer les données de prédictions sur la route /api/predictions
const fetchPredictions = async () => {
  try {
    loading.value = true;
    error.value = null;
    const response = await axios.get('/api/predictions');
    if (response.status !== 200) {
      throw new Error(`Erreur HTTP: ${response.status}`);
    }
    const data = response.data;
    if (!data || data.length === 0) {
      throw new Error('Aucune donnée de prédiction disponible');
    }
    predictions.value = data; // Stocke les données ici
  } catch (err: any) {
    console.error('Erreur lors du chargement des données de prédiction:', err);
    error.value = `Erreur lors du chargement des données de prédiction: ${err.message}`;
    predictions.value = [];
  } finally {
    loading.value = false;
  }
};

  // Construire des objets pandémies à partir des données de comparaison
  const pandemicStats = computed(() => {
    if (!comparisonData.value) return []

    return [
      {
        id: 1,
        name: comparisonData.value.pandemic1 || 'SARS',
        totalCases: comparisonData.value.pandemic1MaxStats?.cases || 0,
        totalDeaths: comparisonData.value.pandemic1MaxStats?.deaths || 0,
        mortalityRate: comparisonData.value.comparisonMetrics?.mortalityRate1 || 0,
        affectedRegions: [],
        recovered: comparisonData.value.pandemic1MaxStats?.recovered || 0,
        maxDate: comparisonData.value.pandemic1MaxStats?.date
          ? formatDate(comparisonData.value.pandemic1MaxStats.date)
          : 'Non disponible',
      },
      {
        id: 2,
        name: comparisonData.value.pandemic2 || 'COVID',
        totalCases: comparisonData.value.pandemic2MaxStats?.cases || 0,
        totalDeaths: comparisonData.value.pandemic2MaxStats?.deaths || 0,
        mortalityRate: comparisonData.value.comparisonMetrics?.mortalityRate2 || 0,
        affectedRegions: [],
        recovered: comparisonData.value.pandemic2MaxStats?.recovered || 0,
        maxDate: comparisonData.value.pandemic2MaxStats?.date
          ? formatDate(comparisonData.value.pandemic2MaxStats.date)
          : 'Non disponible',
      },
    ]
  })

  // Données pour le graphique des cas et décès
  const casesDeathsData = computed((): ChartData => {
    if (!pandemicStats.value.length) return { labels: [], datasets: [] }

    return {
      labels: pandemicStats.value.map((stat) => stat.name),
      datasets: [
        {
          label: 'Cas totaux',
          backgroundColor: '#42A5F5',
          data: pandemicStats.value.map((stat) => stat.totalCases),
        },
        {
          label: 'Décès totaux',
          backgroundColor: '#FF6384',
          data: pandemicStats.value.map((stat) => stat.totalDeaths),
        },
        {
          label: 'Guéris',
          backgroundColor: '#4BC0C0',
          data: pandemicStats.value.map((stat) => stat.recovered),
        },
      ],
    }
  })

  // Données pour le graphique des taux de mortalité
  const mortalityData = computed((): ChartData => {
    if (!pandemicStats.value.length) return { labels: [], datasets: [] }

    return {
      labels: pandemicStats.value.map((stat) => `${stat.name} (${stat.mortalityRate.toFixed(2)}%)`),
      datasets: [
        {
          data: pandemicStats.value.map((stat) => stat.mortalityRate),
          backgroundColor: ['#FF6384', '#36A2EB'],
          hoverBackgroundColor: ['#FF6384', '#36A2EB'],
        },
      ],
    }
  })

  // Options des graphiques
  const barOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          callback: function (value: number) {
            if (value >= 1000000000) {
              return (value / 1000000000).toFixed(1) + ' G'
            }
            if (value >= 1000000) {
              return (value / 1000000).toFixed(1) + ' M'
            }
            if (value >= 1000) {
              return (value / 1000).toFixed(1) + ' K'
            }
            return value
          },
        },
      },
    },
    plugins: {
      tooltip: {
        callbacks: {
          label: function (context: any) {
            let label = context.dataset.label || ''
            if (label) {
              label += ': '
            }
            label += new Intl.NumberFormat('fr-FR').format(context.raw)
            return label
          },
        },
      },
      legend: {
        position: 'top',
      },
    },
  }

  const doughnutOptions: ChartOptions = {
    responsive: true,
    maintainAspectRatio: false,
    cutout: '70%',
    plugins: {
      legend: {
        position: 'bottom',
      },
      tooltip: {
        callbacks: {
          label: function (context: any) {
            return `${context.label}: ${context.raw.toFixed(2)}%`
          },
        },
      },
    },
  }

  // Conserver les fonctions existantes
  const fetchPandemics = async () => {
    try {
      loading.value = true
      const response = await axios.get('/api/pandemics')

      if (response.status !== 200) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }

      // Formater les données pour le dropdown
      pandemies.value = response.data.map((item: any) => ({
        id: item.id,
        name: item.name,
        totalCases: item.totalCases,
        totalDeaths: item.totalDeaths,
        mortalityRate: item.mortalityRate,
        affectedRegions: item.affectedRegions,
      }))

      // Sélectionner la première pandémie par défaut
      if (pandemies.value.length > 0 && !selectedPandemic.value) {
        selectedPandemic.value = pandemies.value[0]
      }
    } catch (err: any) {
      console.error('Erreur lors du chargement des pandémies:', err)
      error.value = `Erreur lors du chargement des pandémies: ${err.message}`
      pandemies.value = []
    } finally {
      loading.value = false
    }
  }

  // Charger les régions (liste prédéfinie)
  const fetchRegions = async () => {
    try {
      loading.value = true

      // Utiliser les régions importées depuis constants/regionsData.ts
      regions.value = predefinedRegions.map((region) => ({
        ...region,
        continentName: 'continentName' in region ? region.continentName : 'Non spécifié',
      }))

      // Sélectionner la première région par défaut si aucune n'a été passée en paramètre
      if (regions.value.length > 0 && !localSelectedRegion.value && !selectedRegion) {
        localSelectedRegion.value = regions.value[0]
      }
    } catch (err: any) {
      console.error('Erreur lors du chargement des régions:', err)
      error.value = `Erreur lors du chargement des régions: ${err.message}`
      regions.value = []
    } finally {
      loading.value = false
    }
  }

  // Charger les données de la pandémie sélectionnée avec les coordonnées
  const fetchPandemicData = async () => {
    if (!selectedPandemic.value) return

    try {
      loadingPandemicData.value = true
      error.value = null

      const response = await axios.get(`/api/stats/by-pandemic/${selectedPandemic.value.id}`)

      if (response.status !== 200) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }

      const data = response.data

      if (!data || data.length === 0) {
        throw new Error('Aucune donnée disponible')
      }

      // Ajouter les coordonnées géographiques (exemple simplifié)
      const regionCoordinates: Record<string, [number, number]> = {
        France: [2.2137, 46.2276],
        USA: [-95.7129, 37.0902],
        India: [78.9629, 20.5937],
        Brazil: [-51.9253, -14.235],
        Russia: [105.3188, 61.524],
        Germany: [10.4515, 51.1657],
        UK: [-3.436, 55.3781],
        Italy: [12.5674, 41.8719],
        Spain: [-3.7492, 40.4637],
        China: [104.1954, 35.8617],
        Japan: [138.2529, 36.2048],
        Australia: [133.7751, -25.2744],
      }

      allCountriesData.value = data
        .filter((item: { cases: number }) => item.cases > 0)
        .map((item: { region: string; cases: number; deaths: number; recovered?: number }) => ({
          ...item,
          coords: regionCoordinates[item.region] || [0, 0], // Ajouter les coordonnées
        }))

      countriesData.value = [...allCountriesData.value]

      // Limiter à 10 pays pour le graphique en barres
      if (countriesData.value.length > 10) {
        countriesData.value = countriesData.value.slice(0, 10)
      }
    } catch (err: any) {
      error.value = `Erreur lors du chargement des données: ${err.message}`
      console.error('Erreur lors du chargement des données:', err)
      countriesData.value = []
      allCountriesData.value = []
    } finally {
      loadingPandemicData.value = false
    }
  }

  // Charger les données de la timeline
  const fetchTimelineData = async () => {
    if (!selectedPandemic.value || !localSelectedRegion.value) return

    try {
      loadingTimelineData.value = true

      const response = await axios.get(
        `/api/stats/timeline/${selectedPandemic.value.id}/${localSelectedRegion.value.id}`,
      )

      if (response.status !== 200) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }

      const data = response.data

      if (!data || data.length === 0) {
        throw new Error('Aucune donnée de timeline disponible')
      }

      // Formater les dates
      timelineData.value = data.map((item: { date: string | number | Date }) => ({
        ...item,
        date: item.date ? new Date(item.date).toLocaleDateString() : 'N/A',
      }))
    } catch (err: any) {
      console.error('Erreur lors du chargement des données de la timeline:', err)
      timelineData.value = []
      error.value = `Erreur lors du chargement de la timeline: ${err.message}`
    } finally {
      loadingTimelineData.value = false
    }
  }

  // Charger les données de comparaison (version complète)
  const fetchComparisonData = async () => {
    if (!localSelectedRegion.value) return

    try {
      loadingComparisonData.value = true

      // Récupérer les IDs des pandémies pour la comparaison
      const sarsPandemicId = pandemies.value.find((p) => p.name.includes('SARS'))?.id
      const covidPandemicId = pandemies.value.find((p) => p.name.includes('COVID'))?.id

      if (!sarsPandemicId || !covidPandemicId) {
        throw new Error('Impossible de trouver les IDs des pandémies pour la comparaison')
      }

      const response = await axios.get<ComparisonData>(
        `/api/stats/compare/${sarsPandemicId}/${covidPandemicId}/${localSelectedRegion.value.id}`,
      )

      if (response.status !== 200) {
        throw new Error(`Erreur HTTP: ${response.status}`)
      }

      comparisonData.value = response.data

      // Ajouter des ID pour faciliter l'identification
      if (comparisonData.value) {
        comparisonData.value.pandemic1Id = sarsPandemicId
        comparisonData.value.pandemic2Id = covidPandemicId
      }
    } catch (err: any) {
      console.error('Erreur lors du chargement des données de comparaison:', err)
      comparisonData.value = null
      error.value = `Erreur lors du chargement des données de comparaison: ${err.message}`
    } finally {
      loadingComparisonData.value = false
    }
  }

  // Observer les changements de région sélectionnée et recharger les données
  if (selectedRegion) {
    watch(selectedRegion, (newValue) => {
      localSelectedRegion.value = newValue
      fetchComparisonDataSimple()
    })
  }

  // Charger les données au montage du composant
  onMounted(() => {
    if (selectedRegion) {
      fetchComparisonDataSimple()
    }
  })

  return {
    // États de base
    loading,
    error,
    comparisonData,

    // Données pour les graphiques comparatifs
    pandemicStats,
    casesDeathsData,
    mortalityData,
    barOptions,
    predictions,
    doughnutOptions,

    // États et données existants
    pandemies,
    regions,
    selectedPandemic,
    selectedRegion: localSelectedRegion,
    countriesData,
    allCountriesData,
    timelineData,
    loadingPandemicData,
    loadingTimelineData,
    loadingComparisonData,

    // Méthodes
    fetchPandemics,
    fetchRegions,
    fetchPandemicData,
    fetchPredictions,
    fetchTimelineData,
    fetchComparisonData,
    fetchComparisonDataSimple,
  }
}
