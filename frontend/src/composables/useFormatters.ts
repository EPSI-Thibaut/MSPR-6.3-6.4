/**
 * Composable pour les fonctions de formatage
 */
export function useFormatters() {
  /**
   * Formate un nombre avec séparateur de milliers
   */
  const formatNumber = (num: number | bigint) => {
    return new Intl.NumberFormat('fr-FR').format(num)
  }

  /**
   * Formate une date en français
   */
  const formatDate = (dateString: string | number | Date) => {
    if (!dateString) return 'Non disponible'

    const date = new Date(dateString)
    return date.toLocaleDateString('fr-FR', {
      day: 'numeric',
      month: 'long',
      year: 'numeric',
    })
  }

  return {
    formatNumber,
    formatDate,
  }
}
