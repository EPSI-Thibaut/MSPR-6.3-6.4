import { ref } from 'vue'

export function useTheme() {
  const isDarkTheme = ref(localStorage.getItem('theme') === 'dark')

  const toggleTheme = () => {
    // Inverser l'état du thème
    isDarkTheme.value = !isDarkTheme.value

    // Sauvegarder la préférence dans localStorage
    localStorage.setItem('theme', isDarkTheme.value ? 'dark' : 'light')

    // Appliquer la classe au document
    if (isDarkTheme.value) {
      document.documentElement.classList.add('dark-theme')
    } else {
      document.documentElement.classList.remove('dark-theme')
    }
  }

  return {
    isDarkTheme,
    toggleTheme
  }
}
