<script setup lang="ts">
import { ref, onMounted, watch } from 'vue'
import * as echarts from 'echarts'
import 'echarts-gl'
import { usePandemicData } from '@/composables/usePandemicData'

// Définition du type EChartsOption
type EChartsOption = echarts.EChartsOption & {
  series?: Array<{
    type: string
    coordinateSystem: string
    data: unknown[]
    [key: string]: unknown
  }>
  globe?: {
    baseTexture: string
    heightTexture: string
    displacementScale: number
    shading: string
    environment: string
    realisticMaterial: Record<string, unknown>
    postEffect: Record<string, unknown>
    light: Record<string, unknown>
    [key: string]: unknown
  }
}

// Initialisation des refs
const mapOptions = ref<EChartsOption>({
  series: [],
})
const {
  countriesData,
  fetchPandemicData,
  selectedPandemic,
  pandemies,
  loading,
  error,
  fetchPandemics,
} = usePandemicData()

// Dictionnaire de coordonnées pour ajouter plus de pays
const additionalCoordinates: Record<string, [number, number]> = {
  'United Kingdom': [-3.436, 55.3781],
  'United States': [-95.7129, 37.0902],
  Canada: [-106.3468, 56.1304],
  Mexico: [-102.5528, 23.6345],
  Sweden: [18.6435, 60.1282],
  Norway: [8.4689, 60.472],
  Denmark: [9.5018, 56.2639],
  Finland: [25.7482, 61.9241],
  Poland: [19.1451, 51.9194],
  France: [2.3522, 48.8566],
  Germany: [10.4515, 51.1657],
  Italy: [12.5674, 41.8719],
  Spain: [-3.7038, 40.4168],
  Portugal: [-8.611, 39.3999],
}

// Fonction pour mettre à jour les données
const updateMapData = () => {
  if (!countriesData.value || countriesData.value.length === 0) {
    console.warn('Aucune donnée de pays disponible pour la carte')
    return
  }

  // Utiliser seulement les pays qui ont des coordonnées définies
  const validCountriesData = countriesData.value.filter((item) => {
    return item.coords && item.coords[0] !== 0 && item.coords[1] !== 0
  })

  if (validCountriesData.length === 0) {
    console.warn("Aucun pays avec coordonnées valides n'est disponible")
    return
  }

  const scatterData = validCountriesData
    .map((item) => {
      if (item.coords) {
        return {
          name: item.region,
          value: [item.coords[0], item.coords[1], item.cases],
        }
      }
      return null
    })
    .filter(Boolean)

  const linesData = validCountriesData
    .map((item) => {
      if (item.coords) {
        return {
          coords: [
            [114.26667, 30.58333],
            [item.coords[0], item.coords[1]],
          ],
        }
      }
      return null
    })
    .filter(Boolean)

  // Mise à jour des options de la carte
  if (mapOptions.value.series && mapOptions.value.series.length >= 2) {
    mapOptions.value.series[0].data = scatterData
    mapOptions.value.series[1].data = linesData
  }
}

// Configuration de la carte 3D
onMounted(async () => {
  await fetchPandemics()

  if (pandemies.value && pandemies.value.length > 0) {
    const covid = pandemies.value.find((p) => p.name.includes('COVID'))
    if (covid) {
      selectedPandemic.value = covid
    } else {
      selectedPandemic.value = pandemies.value[0]
    }
  }

  const chartDom = document.getElementById('map3d') as HTMLElement
  if (chartDom && chartDom.clientWidth > 0 && chartDom.clientHeight > 0) {
    const myChart = echarts.init(chartDom)

    mapOptions.value = {
      backgroundColor: '#000',
      globe: {
        baseTexture: '/src/assets/textures/world.topo.bathy.200401.jpg',
        heightTexture: '/src/assets/textures/world.topo.bathy.200401.jpg',
        displacementScale: 0.04,
        shading: 'realistic',
        environment: '/src/assets/textures/starfield.jpg',
        realisticMaterial: {
          roughness: 0.9,
        },
        postEffect: {
          enable: false,
        },
        light: {
          main: {
            intensity: 5,
            shadow: false,
          },
          ambientCubemap: {
            texture: '/src/assets/textures/pisa.hdr',
            diffuseIntensity: 0.5,
          },
        },
        viewControl: {
          autoRotate: true, // Active la rotation automatique
          autoRotateSpeed: 5, // Vitesse de rotation (plus élevé = plus rapide)
          targetCoord: [114.26667, 30.58333], // Coordonnées vers lesquelles le globe est centré (Wuhan, Chine)
          distance: 180, // Distance de la caméra par rapport au globe
          minDistance: 50, // Distance minimale de zoom
          maxDistance: 300, // Distance maximale de zoom
          alpha: 30, // Angle de rotation verticale (en degrés)
          beta: 160, // Angle de rotation horizontale (en degrés)
        },
      },
      series: [
        {
          type: 'scatter3D',
          coordinateSystem: 'globe',
          data: [],
          symbolSize: 10,
          label: {
            show: true,
            formatter: (params: { name: string; value: number[]; [key: string]: unknown }) => {
              if (params && params.name && params.value) {
                return `${params.name}\n${params.value[2]} cas`
              }
              return ''
            },
            position: 'top',
            color: '#fff',
          },
          itemStyle: {
            color: '#ff5722',
          },
        },
        {
          type: 'lines3D',
          coordinateSystem: 'globe',
          data: [],
          effect: {
            show: true,
            trailWidth: 2,
            trailLength: 0.2,
            trailOpacity: 1,
            trailColor: '#ff5722',
          },
          lineStyle: {
            width: 1,
            color: '#ff5722',
            opacity: 0.8,
          },
        },
      ],
    }

    myChart.setOption(mapOptions.value)

    if (selectedPandemic.value) {
      await fetchPandemicData()

      if (countriesData.value && countriesData.value.length > 0) {
        countriesData.value = countriesData.value.map((item) => {
          if (!item.coords || (item.coords[0] === 0 && item.coords[1] === 0)) {
            const coords = additionalCoordinates[item.region]
            if (coords) {
              return { ...item, coords }
            }
          }
          return item
        })
      }

      updateMapData()
      myChart.setOption(mapOptions.value)
    }

    window.addEventListener('resize', () => {
      myChart.resize()
    })
  } else {
    console.error('DOM width or height is 0. Ensure #map3d has valid dimensions.')
  }
})

watch(selectedPandemic, async (newValue) => {
  if (newValue) {
    await fetchPandemicData()
    updateMapData()
  }
})

watch(countriesData, (newValue) => {
  if (newValue && newValue.length > 0) {
    updateMapData()
  }
})
</script>

<template>
  <div class=" shadow-md rounded-xl p-6 border border-gray-300">
    <div v-if="error" class="p-4 mb-4 rounded">
      {{ error }}
    </div>
    <div v-if="loading" class="flex justify-center items-center mb-4">
      <div class="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-700"></div>
      <span class="ml-2">Chargement des données...</span>
    </div>

    <!-- Carte 3D ECharts -->
    <div
      id="map3d"
      class="w-full h-[500px] rounded-lg overflow-hidden"
      style="min-height: 500px"
      role="img"
      aria-label="Carte interactive affichant les cas de COVID-19 par pays en 3D"
    ></div>
    <div class="mt-4 text-sm p-4 rounded-lg border border-gray-200">
      <h4 class="font-semibold mb-2">À propos de cette carte</h4>
      <p>
        Cette carte 3D interactive affiche le nombre de cas COVID-19 par pays. Les pays sont
        représentés par des points, et les lignes indiquent les connexions entre eux. Vous pouvez
        faire pivoter la carte pour explorer les données sous différents angles. Cliquez sur un pays
        pour voir plus de détails.
      </p>
    </div>
  </div>
</template>
