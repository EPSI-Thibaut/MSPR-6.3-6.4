import pandas as pd
import numpy as np
from app.ia_model import CovidForecaster

def test_minimal():
    """Test minimal du modèle sans optimisation d'hyperparamètres"""
    print("Test minimal du modèle de prédiction COVID")
    
    # Initialiser le modèle
    forecaster = CovidForecaster()
    
    df = forecaster.fetch_data(region_id=1)  # Utilise une région spécifique
    if df.empty:
        print("Erreur: Aucune donnée récupérée")
        return
    
    print(f"Données complètes récupérées: {len(df)} entrées")
    
    # Vérifier la qualité des données avant prétraitement
    print("Vérification des données avant prétraitement:")
    print(f"Colonnes: {df.columns.tolist()}")
    print(f"NaN avant nettoyage: {df.isna().sum().sum()}")
    
    df = df.sort_values('date')
    
    try:
        # Nettoyage léger des données
        df = forecaster.clean_extreme_values(df)
        
        # Afficher l'état après nettoyage
        print(f"Lignes après nettoyage: {len(df)}")
        
        # Version simplifiée du prétraitement
        df['day_of_week'] = pd.to_datetime(df['date']).dt.dayofweek
        df['month'] = pd.to_datetime(df['date']).dt.month
        df['cases_lag1'] = df.groupby('region_id')['cases'].shift(1)
        df['deaths_lag1'] = df.groupby('region_id')['deaths'].shift(1)
        df['recovered_lag1'] = df.groupby('region_id')['recovered'].shift(1)
        
        # Remplir les NaN avec des méthodes plus simples
        df = df.fillna(method='ffill').fillna(method='bfill')
        
        # Supprimer les NaN restants
        df = df.dropna()
        
        print(f"Lignes après prétraitement simplifié: {len(df)}")
        
        # Si toujours pas assez de données, on utilise un échantillon plus ancien
        if len(df) < 100:
            print("Pas assez de données - utilisation des données plus anciennes")
            df = forecaster.fetch_data()
            df = df.sort_values('date').tail(5000)  # On prend plus de données
            
            # Répéter le prétraitement simplifié
            df = forecaster.clean_extreme_values(df)
            df['day_of_week'] = pd.to_datetime(df['date']).dt.dayofweek
            df['month'] = pd.to_datetime(df['date']).dt.month
            df['cases_lag1'] = df.groupby('region_id')['cases'].shift(1)
            df['deaths_lag1'] = df.groupby('region_id')['deaths'].shift(1)
            df['recovered_lag1'] = df.groupby('region_id')['recovered'].shift(1)
            df = df.fillna(method='ffill').fillna(method='bfill')
            df = df.dropna()
            
            print(f"Nouvelles lignes après prétraitement: {len(df)}")
        
        # Fixer les features pour le test
        features = ['day_of_week', 'month', 'cases_lag1', 'deaths_lag1', 'recovered_lag1']
        
        # Sélection des colonnes pertinentes
        X = df[features]
        y = df['cases'].values
        dates = df['date']
        
        # Split manuel 80/20
        split_idx = int(len(df) * 0.8)
        X_train, X_test = X.iloc[:split_idx], X.iloc[split_idx:]
        y_train, y_test = y[:split_idx], y[split_idx:]
        test_dates = dates.iloc[split_idx:].reset_index(drop=True)
        
        # Entraînement rapide
        from sklearn.ensemble import RandomForestRegressor
        model = RandomForestRegressor(n_estimators=10, max_depth=5, random_state=42)
        
        print("Entraînement du modèle (version simplifiée)...")
        model.fit(X_train, y_train)
        forecaster.model = model
        
        # Évaluation simple
        y_pred = model.predict(X_test)
        from sklearn.metrics import mean_squared_error
        mse = mean_squared_error(y_test, y_pred)
        print(f"MSE: {mse:.2f}")
        
        # Prédiction simplifiée (juste les 7 prochains jours)
        last_data = df.iloc[-1:].copy()
        predictions = []
        
        for i in range(7):
            # Créer features pour prédiction
            last_data['day_of_week'] = (last_data['day_of_week'].values[0] + 1) % 7
            # Prédire les nouveaux cas
            pred_cases = model.predict(last_data[features])[0]
            # Créer nouvelle entrée
            new_row = last_data.copy()
            new_row['cases'] = pred_cases
            new_row['date'] = pd.to_datetime(new_row['date'].values[0]) + pd.Timedelta(days=1)
            # Mise à jour des lags pour la prochaine prédiction
            new_row['cases_lag1'] = pred_cases
            predictions.append(new_row)
            last_data = new_row.copy()
        
        predictions_df = pd.concat(predictions)
        print("Prédictions simples (7 jours):")
        print(predictions_df[['date', 'cases']])
        
        return "Test terminé avec succès"
        
    except Exception as e:
        print(f"Erreur détaillée: {str(e)}")
        import traceback
        traceback.print_exc()
        return "Test échoué"

if __name__ == "__main__":
    test_minimal()