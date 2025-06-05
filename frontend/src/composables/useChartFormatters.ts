export function useChartFormatters() {
  // Formater les grands nombres
  const formatNumber = (value: number) => {
    return new Intl.NumberFormat('fr-FR').format(value)
  }

  // Générer des couleurs pour les graphiques
  const generateColors = (count: number) => {
    const baseColors = [
      '#FF6384',
      '#36A2EB',
      '#FFCE56',
      '#4BC0C0',
      '#9966FF',
      '#FF9F40',
      '#4BC0C0',
      '#7FC8A9',
      '#5D8AA8',
      '#D3A121',
      // ...autres couleurs de base
    ]

    const colors = [...baseColors]

    // Générer des couleurs supplémentaires si nécessaire
    for (let i = baseColors.length; i < count; i++) {
      const r = Math.floor(Math.random() * 200) + 55
      const g = Math.floor(Math.random() * 200) + 55
      const b = Math.floor(Math.random() * 200) + 55
      colors.push(`rgb(${r}, ${g}, ${b})`)
    }

    return colors.slice(0, count)
  }

  return {
    formatNumber,
    generateColors,
  }
}
