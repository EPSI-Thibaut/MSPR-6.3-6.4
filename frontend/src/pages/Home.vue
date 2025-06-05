<script setup lang="ts">
import Map3D from '@/components/Map3D.vue'
</script>

<template>
  <!-- Titre principal -->
  <h2 class="text-2xl sm:text-3xl mb-6 text-center">Statistiques du COVID et SARS</h2>

  <!-- Carte 3D avec un cadre arrondi -->
  <div class="shadow-xl rounded-2xl px-4 py-6 sm:p-6 md:p-8 border border-gray-200 max-w-7xl mx-auto w-full"
  aria-label="Carte 3D des cas de COVID-19 dans le monde"
  role="region"
  >
    <div class="relative w-full h-[300px] sm:h-[400px] md:h-[500px]">
        <Map3D />
    </div>
  </div>
</template>
