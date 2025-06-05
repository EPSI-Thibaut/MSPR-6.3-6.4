<script setup lang="ts">
import Menubar from 'primevue/menubar'
import Button from 'primevue/button'
import { ref, computed } from 'vue'
import { useRouter } from 'vue-router'
import { useTheme } from '../composables/useTheme'

const router = useRouter()
const { isDarkTheme, toggleTheme } = useTheme()

// Calculer l'icône en fonction du thème actuel
const themeIcon = computed(() => isDarkTheme.value ? 'pi pi-sun' : 'pi pi-moon')
const themeLabel = computed(() => isDarkTheme.value ? 'Mode clair' : 'Mode sombre')

const menuItems = ref([
  {
    label: 'Accueil',
    icon: 'pi pi-home',
    command: () => router.push('/'),
  },
  {
    label: 'Statistiques',
    icon: 'pi pi-chart-bar',
    command: () => router.push('/statistiques'),
  },
  {
    label: 'Comparaison COVID vs SRAS',
    icon: 'pi pi-chart-line',
    command: () => router.push('/comparaison'),
  },
  {
    label: 'Comparaison des continents',
    icon: 'pi pi-chart-line',
    command: () => router.push('/continents'),
  },
  {
    label: 'Prédictions COVID',
    icon: 'pi pi-chart-bar',
    command: () => router.push('/predictions'),
  },
  {
    label: 'À propos',
    icon: 'pi pi-info-circle',
    command: () => router.push('/about'),
  },
])
</script>

<template>
  <header class="mb-2 shadow-md rounded-lg" role="navigation"
  aria-label="Menu principal de navigation">
    <Menubar :model="menuItems" class="rounded-lg">
      <template #end>
        <Button
          :icon="themeIcon"
          @click="toggleTheme"
          rounded
          text
          :aria-label="themeLabel"
          v-tooltip.bottom="themeLabel"
          class="theme-toggle"
        />
      </template>
    </Menubar>
  </header>
</template>

<style scoped>
.theme-toggle {
  margin-right: 0.5rem;
}
</style>
