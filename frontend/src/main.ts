import { createApp } from 'vue'
import App from './App.vue'

import PrimeVue from 'primevue/config'
import Aura from '@primevue/themes/aura'
import 'primeicons/primeicons.css'
import 'primeflex/primeflex.css'
import router from './router'
import Tooltip from 'primevue/tooltip'

import ECharts from 'vue-echarts'
import 'echarts'

// Récupérer le thème initial depuis localStorage ou utiliser 'light' par défaut
const isDarkTheme = localStorage.getItem('theme') === 'dark'

const app = createApp(App)

app.use(router)

app.use(PrimeVue, {
  theme: {
    preset: Aura,
    options: {
      prefix: 'p',
      darkModeSelector: '.dark-theme', // Utiliser une classe au lieu de 'system'
      cssLayer: false
    }
  }
})

app.directive('tooltip', Tooltip)
app.component('VChart', ECharts)

app.mount('#app')

// Appliquer le thème initial
if (isDarkTheme) {
  document.documentElement.classList.add('dark-theme')
}
