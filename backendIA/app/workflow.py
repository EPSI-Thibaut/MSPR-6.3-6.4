import matplotlib.pyplot as plt
import pandas as pd
import numpy as np
import sys
import os
import traceback

# Assurer que le chemin de base est dans le PYTHONPATH
sys.path.append(os.path.dirname(os.path.dirname(os.path.abspath(__file__))))

# Imports nécessaires
from app.ia_model import CovidForecaster
from app import create_app  # Importation de la fonction de création d'application Flask

def test_model_predictions():
    """
    Teste le modèle de prédiction COVID et enregistre les résultats en base de données.
    Utilise un contexte d'application Flask pour assurer l'accès à la base de données.
    """
    print("Initialisation du modèle...")
    forecaster = CovidForecaster()
    
    try:
        app = create_app()
        
        with app.app_context():
            print("\nPrédiction pour 2023 (toutes régions):")
            predictions_2023 = forecaster.predict_2023_safe() 
            
            if predictions_2023 is not None:
                print("\nPremières prédictions:")
                print(predictions_2023.head())
                print("\nDernières prédictions:")
                print(predictions_2023.tail())
                
                print(f"\nTotal de {len(predictions_2023)} prédictions générées.")
                print(f"Graphique sauvegardé dans: static/covid_predictions.png")
            else:
                print("Aucune prédiction n'a pu être générée.")
                
            return predictions_2023
            
    except Exception as e:
        print(f"Erreur dans le test du modèle: {str(e)}")
        traceback.print_exc()
        return pd.DataFrame({'date': [], 'predicted_cases': []})

if __name__ == "__main__":
    predictions = test_model_predictions()