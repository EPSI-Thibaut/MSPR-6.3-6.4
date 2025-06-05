import { createRouter, createWebHistory } from 'vue-router'
import Home from '../pages/Home.vue'
import Statistiques from '../pages/Statistiques.vue'
import Comparaison from '../pages/Comparaison.vue'
import Continents from '../pages/Continents.vue'
import Predictions from '@/pages/Predictions.vue'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: Home,
    },
    {
      path: '/statistiques',
      name: 'statistiques',
      component: Statistiques,
    },
    {
      path: '/comparaison',
      name: 'comparaison',
      component: Comparaison,
    },
    {
      path: '/continents',
      name: 'continents',
      component: Continents,
    },
    {
      path: '/predictions',
      name: 'predictions',
      component: Predictions,
    },
    {
      path: '/about',
      name: 'about',
      component: () => import('../pages/About.vue'),
    },
  ],
})

export default router
