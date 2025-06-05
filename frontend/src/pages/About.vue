<script setup lang="ts">
import { ref, onMounted } from 'vue'
import instance from '@/axios.ts'

interface Country {
  name: string
}
const countries = ref<Country[]>([])

onMounted(async () => {
  try {
    const response = await instance.get('/countrieses')
    countries.value = response.data._embedded?.countrieses || []
    console.log(countries.value) // Déplacé à l'intérieur de onMounted
  } catch (error) {
    console.error('Erreur lors de la récupération des pays :', error)
  }
})
</script>

<template>
  <div class="p-6">
    <h2 class="text-3xl font-bold mb-6 text-center">À propos</h2>

    <!-- Section projet -->
    <div class="shadow-lg rounded-xl p-6 mb-8 border border-gray-200"
    role="region"
    aria-label="Présentation du projet">
      <h3 class="text-xl font-semibold  mb-4">Notre projet</h3>
      <p class="">
        Cette application a été développée dans le cadre du projet MSPR1 pour visualiser et analyser
        les données relatives au COVID-19 et au SARS à travers le monde.
      </p>
    </div>

    <!-- Section ETL avec Tailwind uniquement -->
    <div
    class="shadow-lg rounded-xl p-6 mb-8 border border-gray-200"
    role="region"
    aria-label="Description du processus ETL">
      <h3 class="text-xl font-semibold  mb-4">Notre processus ETL</h3>

      <!-- Extraction -->
      <div class="mb-6">
        <p class="">
          <span class="font-bold ">Extraction des données</span> : Notre ETL commence
          par l'extraction de données provenant de sources multiples et fiables. Nous nous appuyons
          principalement sur des API gouvernementales et des bases de données sanitaires
          internationales comme l'OMS pour recueillir des informations précises sur les cas de
          COVID-19 et de SARS à travers le monde. Notre système d'extraction est programmé pour
          collecter quotidiennement ces données et vérifier leur intégrité avant tout traitement
          ultérieur. Nous avons également mis en place des mécanismes de gestion des erreurs pour
          garantir une continuité dans la collecte même en cas de défaillance temporaire des
          sources.
        </p>
      </div>

      <!-- Séparateur -->
      <hr class="my-6 border-t border-gray-200" />

      <!-- Transformation -->
      <div class="mb-6">
        <p class="">
          <span class="font-bold ">Traitement et transformation</span> : Une fois
          extraites, les données brutes subissent un processus de transformation. Ce processus
          comprend le nettoyage des données incomplètes ou incorrectes, la normalisation des formats
          pour assurer la cohérence entre différentes sources, et la consolidation des informations
          par région géographique. Nous calculons également des métriques dérivées essentielles
          comme les taux de mortalité, les taux de guérison et les comparatifs entre pandémies.
        </p>
      </div>

      <!-- Séparateur -->
      <hr class="my-6 border-t border-gray-200" />

      <!-- Chargement -->
      <div class="mb-6">
        <p class="">
          <span class="font-bold ">Chargement et stockage</span> : Les données
          transformées sont ensuite chargées dans notre base de données relationnelle MariaDB. Une
          API RESTful expose ces données de manière sécurisée à notre interface utilisateur, avec
          des mécanismes de mise en cache pour améliorer les performances.
        </p>
      </div>

      <!-- Séparateur -->
      <hr class="my-6 border-t border-gray-200" />

      <!-- Visualisation -->
      <div>
        <p class="">
          <span class="font-bold ">Visualisation et analyse</span> : Notre interface
          utilisateur exploite les données traitées pour offrir des visualisations interactives et
          informatives. Les utilisateurs peuvent explorer les données à travers différentes
          dimensions géographiques (monde, continents, pays) et temporelles (évolution journalière,
          hebdomadaire, mensuelle). Notre système de visualisation s'adapte automatiquement au
          volume de données affichées pour garantir des performances optimales sur tous les
          appareils. Les fonctionnalités de comparaison entre le COVID-19 et le SARS permettent aux
          utilisateurs d'avoir une perspective historique sur ces deux pandémies majeures.
        </p>
      </div>
    </div>

    <!-- Équipe -->
    <div
    class="shadow-lg rounded-xl p-6 mb-8 border border-gray-200"
    role="region"
    aria-label="Présentation de l'équipe">
      <h3 class="text-xl font-semibold  mb-4">Notre équipe</h3>
      <p class="">
        Ce projet a été développé par une équipe d'étudiants dans le cadre du MSPR1 de l'EPSI.
      </p>
    </div>
  </div>
</template>
