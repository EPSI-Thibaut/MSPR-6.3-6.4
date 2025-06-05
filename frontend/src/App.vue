<script setup lang="ts">
import HeaderMenu from '@/components/HeaderMenu.vue'
import FooterMenu from '@/components/Footer.vue'
</script>

<template>
  <!-- Header -->
  <HeaderMenu />

  <!-- Contenu principal -->
  <div class="min-h-screen px-4 sm:px-6 lg:px-8 py-4">
    <!-- Router View pour afficher les différentes pages -->
    <router-view />
  </div>

  <!-- Footer -->
  <FooterMenu />
</template>
