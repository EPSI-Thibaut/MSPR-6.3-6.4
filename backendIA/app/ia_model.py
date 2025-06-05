import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import pickle
from datetime import datetime, timedelta
from sqlalchemy import create_engine, text
from sklearn.ensemble import RandomForestRegressor, GradientBoostingRegressor, VotingRegressor
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import train_test_split, GridSearchCV, RandomizedSearchCV
from sklearn.metrics import mean_squared_error, mean_absolute_error, r2_score
from scipy.stats import randint
from app.services import save_predictions_to_db
import os
from dotenv import load_dotenv

# Chargement des variables d'environnement
load_dotenv()

class CovidForecaster:
    def __init__(self):
        """Initialisation du modèle de prévision COVID"""
        self.model = None
        self.scaler_X = MinMaxScaler()
        self.scaler_y = MinMaxScaler()
        self.engine = create_engine(os.getenv('DATABASE_URI'))
        self.features = ['cases_lag1', 'cases_lag7', 'cases_lag14',
                         'deaths_lag1', 'deaths_lag7',
                         'recovered_lag1', 'recovered_lag7',
                         'day_of_week', 'month']

    def fetch_data(self, region_id=None):
        """Récupère les données COVID de la base de données"""
        query = """
        SELECT t.date_by_day as date, t.case_count as cases, t.death as deaths, t.recovered,
               r.name as region_name, r.id_regions as region_id,
               c.name as continent_name
        FROM total_by_day t
        JOIN regions r ON t.id_regions = r.id_regions
        JOIN continents c ON r.id_continents = c.id_continents
        JOIN pandemics p ON t.id_pandemics = p.id_pandemics
        WHERE p.name = 'COVID'
        """

        if region_id:
            query += f" AND r.id_regions = {region_id}"

        query += " ORDER BY t.date_by_day ASC"

        try:
            df = pd.read_sql(query, self.engine)
            print(f"Données récupérées: {len(df)} enregistrements")
            return df
        except Exception as e:
            print(f"Erreur lors de la récupération des données: {e}")
            return pd.DataFrame()

    def clean_extreme_values(self, df):
        """Nettoie les valeurs infinies et extrêmes dans les données"""
        # Remplacement des valeurs infinies par NaN
        df = df.replace([np.inf, -np.inf], np.nan)

        # Pour chaque colonne numérique, limiter les valeurs extrêmes (winsorizing)
        for col in df.select_dtypes(include=[np.number]).columns:
            # Définir des limites raisonnables (99ème percentile)
            q_high = df[col].quantile(0.99)
            # Remplacer les valeurs extrêmes
            df[col] = df[col].clip(upper=q_high)

        # Remplacer les valeurs NaN par la médiane de chaque colonne
        for col in df.select_dtypes(include=[np.number]).columns:
            df[col] = df[col].fillna(df[col].median())

        return df

    def preprocess_data(self, df):
        """Prétraite les données pour l'entraînement"""
        if df.empty:
            raise ValueError("Le dataframe est vide, impossible de prétraiter les données")

        # Convertir la date en datetime si ce n'est pas déjà fait
        df['date'] = pd.to_datetime(df['date'])

        # Trier par date et région
        df = df.sort_values(['region_name', 'date'])

        # Ajouter des caractéristiques temporelles avancées
        df['day_of_week'] = df['date'].dt.dayofweek
        df['month'] = df['date'].dt.month
        df['year'] = df['date'].dt.year
        df['day'] = df['date'].dt.day
        df['quarter'] = df['date'].dt.quarter
        df['is_weekend'] = df['day_of_week'].apply(lambda x: 1 if x >= 5 else 0)

        # Calculer les taux de croissance
        df['growth_rate'] = df.groupby('region_name')['cases'].pct_change()
        df['death_rate'] = df['deaths'] / df['cases'].replace(0, 1)
        df['recovery_rate'] = df['recovered'] / df['cases'].replace(0, 1)

        # Créer des variables décalées (lag features) plus étendues
        for lag in [1, 7, 14, 21, 28]:
            df[f'cases_lag{lag}'] = df.groupby('region_name')['cases'].shift(lag)

        for lag in [1, 7, 14]:
            df[f'deaths_lag{lag}'] = df.groupby('region_name')['deaths'].shift(lag)
            df[f'recovered_lag{lag}'] = df.groupby('region_name')['recovered'].shift(lag)

        # Moyennes mobiles pour capturer les tendances
        for window in [7, 14, 30]:
            df[f'cases_ma{window}'] = df.groupby('region_name')['cases'].rolling(window=window).mean().reset_index(0, drop=True)
            df[f'deaths_ma{window}'] = df.groupby('region_name')['deaths'].rolling(window=window).mean().reset_index(0, drop=True)

        # Gestion plus intelligente des valeurs manquantes
        for col in df.columns:
            if 'lag' in col or 'ma' in col:
                df[col] = df.groupby('region_name')[col].transform(lambda x: x.fillna(x.median()))

        # Pour les autres colonnes numériques
        for col in df.select_dtypes(include=['number']).columns:
            if col not in df.columns[df.columns.str.contains('lag|ma')]:
                df[col] = df[col].fillna(df[col].median())

        # Vérifier s'il reste des NaN
        if df.isna().any().any():
            print(f"ATTENTION: Valeurs manquantes restantes dans les colonnes: {df.columns[df.isna().any()].tolist()}")
            essential_cols = ['cases', 'deaths', 'recovered'] + self.features
            df = df.dropna(subset=essential_cols)

        # Créer des caractéristiques pour la saisonnalité
        df['season'] = df['month'].apply(lambda x: 1 if x in [12, 1, 2] else
                                        2 if x in [3, 4, 5] else
                                        3 if x in [6, 7, 8] else 4)

        # Ajouter des caractéristiques pour les vagues précédentes
        self.features = [
            'cases_lag1', 'cases_lag7', 'cases_lag14',
            'deaths_lag1', 'deaths_lag7', 'recovered_lag1',
            'day_of_week', 'month', 'cases_ma7', 'cases_ma30',
            'growth_rate', 'death_rate', 'recovery_rate',
            'is_weekend', 'season', 'quarter'
        ]

        return df

    def prepare_train_test(self, df, test_size=0.2):
        """Prépare les ensembles d'entraînement et de test"""
        if df.empty or len(df) < 2:
            raise ValueError("Pas assez de données pour préparer les ensembles d'entraînement et de test")

        # Définir X et y
        X = df[self.features]
        y = df['cases']

        # Vérifier si X contient toutes les features requises
        missing_features = [f for f in self.features if f not in X.columns]
        if missing_features:
            raise ValueError(f"Features manquantes dans les données: {missing_features}")

        # Vérifier les valeurs infinies ou trop grandes
        if np.any(np.isinf(X)):
            X = X.replace([np.inf, -np.inf], np.nan)
            X = X.fillna(X.median())

        # Train-test split basé sur la date pour maintenir l'ordre temporel
        train_size = int(len(df) * (1 - test_size))
        if train_size < 1 or (len(df) - train_size) < 1:
            raise ValueError(f"Pas assez de données pour le split (train_size={train_size}, test_size={len(df) - train_size})")

        X_train, X_test = X[:train_size], X[train_size:]
        y_train, y_test = y[:train_size], y[train_size:]

        # Normalisation des données
        X_train_scaled = self.scaler_X.fit_transform(X_train)
        X_test_scaled = self.scaler_X.transform(X_test)

        # Pour la sortie, normaliser uniquement pour l'entraînement, garder l'original pour l'évaluation
        y_train_scaled = self.scaler_y.fit_transform(y_train.values.reshape(-1, 1)).ravel()

        return X_train_scaled, X_test_scaled, y_train_scaled, y_test, df['date'][train_size:]

    def train(self, X_train, y_train):
        """Entraîne un modèle d'ensemble pour améliorer les prédictions (version rapide)"""
        print("Début de l'entraînement du modèle (rapide)...")

        rf_model = RandomForestRegressor(random_state=42, n_jobs=-1)

        # Données d'entraînement rapide pour Random Forest et le développement du modèle IA
        #param_dist = {
        #    'n_estimators': randint(20, 60),        # Moins d'arbres
        #    'max_depth': randint(3, 8),             # Arbres moins profonds
        #    'min_samples_split': randint(5, 20),    # Plus de samples pour split
        #    'min_samples_leaf': randint(3, 10)      # Plus de samples par feuille
        #}

        # Moins d'itérations et moins de folds
        #random_search = RandomizedSearchCV(
        #    estimator=rf_model,
        #    param_distributions=param_dist,
        #    n_iter=5,           # Seulement 5 combinaisons testées
        #    cv=2,               # Seulement 2 folds
        #    n_jobs=-1,
        #    verbose=1
        #)
        
        # Entraînement du modèle plus affiné pour la production
        param_dist = {
            'n_estimators': randint(100, 500),
            'max_depth': randint(10, 30),
            'min_samples_split': randint(2, 10),
            'min_samples_leaf': randint(1, 5)
        }
        random_search = RandomizedSearchCV(
            estimator=rf_model,
            param_distributions=param_dist,
            n_iter=30,
            cv=5,
            n_jobs=-1,
            verbose=2
        )
        
        
        random_search.fit(X_train, y_train)

        self.model = random_search.best_estimator_

        print("Entraînement terminé.")
        print(f"Meilleurs paramètres: {random_search.best_params_}")

    def evaluate(self, X_test, y_test):
        """Évalue les performances du modèle"""
        if self.model is None:
            raise ValueError("Le modèle n'a pas encore été entraîné")

        # Prédictions sur l'ensemble de test
        y_pred_scaled = self.model.predict(X_test)

        # Dénormaliser les prédictions si elles ont été normalisées
        try:
            y_pred = self.scaler_y.inverse_transform(y_pred_scaled.reshape(-1, 1)).ravel()
        except:
            y_pred = y_pred_scaled

        # Métriques d'évaluation
        mse = mean_squared_error(y_test, y_pred)
        rmse = np.sqrt(mse)
        mae = mean_absolute_error(y_test, y_pred)
        r2 = r2_score(y_test, y_pred)

        print(f"Résultats d'évaluation:")
        print(f"MSE: {mse:.2f}")
        print(f"RMSE: {rmse:.2f}")
        print(f"MAE: {mae:.2f}")
        print(f"R²: {r2:.4f}")

        return y_pred

    def predict_future(self, df, days=30):
        """Prédit les cas, décès et guérisons futurs pour les prochains jours"""
        if self.model is None:
            raise ValueError("Le modèle n'a pas encore été entraîné")

        # Date de la dernière observation
        last_date = df['date'].max()

        # Créer un DataFrame pour les prédictions futures
        future_dates = [last_date + timedelta(days=i+1) for i in range(days)]
        future_df = pd.DataFrame({'date': future_dates})
        future_df['day_of_week'] = future_df['date'].dt.dayofweek
        future_df['month'] = future_df['date'].dt.month
        future_df['year'] = future_df['date'].dt.year
        future_df['day'] = future_df['date'].dt.day
        future_df['quarter'] = future_df['date'].dt.quarter
        future_df['is_weekend'] = future_df['day_of_week'].apply(lambda x: 1 if x >= 5 else 0)
        future_df['season'] = future_df['month'].apply(lambda x: 1 if x in [12, 1, 2] else
                                                    2 if x in [3, 4, 5] else
                                                    3 if x in [6, 7, 8] else 4)

        # Initialiser avec les dernières valeurs connues
        last_known_cases = df['cases'].tail(30).values
        last_known_deaths = df['deaths'].tail(14).values
        last_known_recovered = df['recovered'].tail(14).values

        # Créer les métriques initiales
        last_growth_rate = df['growth_rate'].tail(7).mean()
        last_death_rate = df['death_rate'].tail(7).mean()
        last_recovery_rate = df['recovery_rate'].tail(7).mean()

                # Correction : si NaN ou 0, utilise une valeur par défaut raisonnable
        if not np.isfinite(last_death_rate) or last_death_rate == 0:
            last_death_rate = 0.0145  # ou une valeur moyenne issue de ton historique

        if not np.isfinite(last_recovery_rate) or last_recovery_rate == 0:
            last_recovery_rate = 0.10  # ou une valeur moyenne issue de ton historique
        # Initialiser les moyennes mobiles
        last_cases_ma7 = df['cases_ma7'].iloc[-1] if 'cases_ma7' in df.columns else df['cases'].tail(7).mean()
        last_cases_ma14 = df['cases_ma14'].iloc[-1] if 'cases_ma14' in df.columns else df['cases'].tail(14).mean()
        last_cases_ma30 = df['cases_ma30'].iloc[-1] if 'cases_ma30' in df.columns else df['cases'].tail(30).mean()

        # Listes pour stocker les prédictions
        predictions = []
        deaths_predictions = []
        recovered_predictions = []

        # Faire des prédictions progressives jour par jour
        for i in range(days):
            features = {}

            # Cases lag
            for lag in [1, 7, 14, 21, 28]:
                if lag <= len(last_known_cases):
                    features[f'cases_lag{lag}'] = last_known_cases[-lag]
                else:
                    features[f'cases_lag{lag}'] = 0

            # Deaths lag
            for lag in [1, 7, 14]:
                if lag <= len(last_known_deaths):
                    features[f'deaths_lag{lag}'] = last_known_deaths[-lag]
                else:
                    features[f'deaths_lag{lag}'] = 0

            # Recovered lag
            for lag in [1, 7, 14]:
                if lag <= len(last_known_recovered):
                    features[f'recovered_lag{lag}'] = last_known_recovered[-lag]
                else:
                    features[f'recovered_lag{lag}'] = 0

            # Caractéristiques temporelles
            features['day_of_week'] = future_df['day_of_week'].iloc[i]
            features['month'] = future_df['month'].iloc[i]
            features['year'] = future_df['year'].iloc[i]
            features['day'] = future_df['day'].iloc[i]
            features['quarter'] = future_df['quarter'].iloc[i]
            features['is_weekend'] = future_df['is_weekend'].iloc[i]
            features['season'] = future_df['season'].iloc[i]

            # Métriques calculées
            features['growth_rate'] = last_growth_rate
            features['death_rate'] = last_death_rate
            features['recovery_rate'] = last_recovery_rate

            # Moyennes mobiles
            features['cases_ma7'] = last_cases_ma7
            features['cases_ma14'] = last_cases_ma14
            features['cases_ma30'] = last_cases_ma30

            # Créer un DataFrame pour la prédiction
            X_future = pd.DataFrame([features])

            # S'assurer que toutes les features requises sont présentes
            missing_features = [f for f in self.features if f not in X_future.columns]
            if missing_features:
                for f in missing_features:
                    X_future[f] = 0

            # Normaliser
            X_future_scaled = self.scaler_X.transform(X_future[self.features])

            # Prédire
            pred_scaled = self.model.predict(X_future_scaled)

            # Dénormaliser si nécessaire
            try:
                pred = self.scaler_y.inverse_transform(pred_scaled.reshape(-1, 1)).ravel()[0]
            except:
                pred = pred_scaled[0]

            # S'assurer que la prédiction est positive
            pred = max(0, pred)

            # Prédire deaths et recovered à partir des taux moyens
            pred_deaths = pred * last_death_rate if last_death_rate > 0 else 0
            pred_recovered = pred * last_recovery_rate if last_recovery_rate > 0 else 0

            predictions.append(pred)
            deaths_predictions.append(pred_deaths)
            recovered_predictions.append(pred_recovered)

            # Mettre à jour les dernières valeurs connues
            last_known_cases = np.append(last_known_cases[1:], pred)
            last_known_deaths = np.append(last_known_deaths[1:], pred_deaths)
            last_known_recovered = np.append(last_known_recovered[1:], pred_recovered)

            # Mettre à jour les moyennes mobiles
            last_cases_ma7 = (last_cases_ma7 * 6 + pred) / 7
            last_cases_ma14 = (last_cases_ma14 * 13 + pred) / 14
            last_cases_ma30 = (last_cases_ma30 * 29 + pred) / 30

        # Créer un DataFrame pour les résultats
        results_df = pd.DataFrame({
            'date': future_dates,
            'predicted_cases': predictions,
            'predicted_deaths': deaths_predictions,
            'predicted_recovered': recovered_predictions
        })

        return results_df

    def extrapolate_to_2023(self, df):
        """Extrapoler les données pour couvrir 2023"""
        # Obtenir la dernière date disponible
        last_date = df['date'].max()

        # Créer une séquence de dates jusqu'à fin 2023
        end_date = pd.Timestamp('2023-12-31')
        if last_date < end_date:
            # Calculer les statistiques des dernières données
            recent_data = df[df['date'] > (last_date - pd.Timedelta(days=90))].copy()

            avg_growth = np.clip(recent_data['growth_rate'].mean(), -0.01, 0.01)
            avg_death_rate = np.clip(recent_data['death_rate'].mean(), 0, 0.05)
            avg_recovery_rate = np.clip(recent_data['recovery_rate'].mean(), 0, 0.1)

            # Nettoyer les valeurs pour s'assurer qu'elles sont finies
            if not np.isfinite(avg_growth):
                avg_growth = 0.001
            if not np.isfinite(avg_death_rate):
                avg_death_rate = 0.01
            if not np.isfinite(avg_recovery_rate):
                avg_recovery_rate = 0.05

            print(f"Taux de croissance utilisé pour extrapolation: {avg_growth:.4f}")
            print(f"Taux de mortalité utilisé: {avg_death_rate:.4f}")
            print(f"Taux de récupération utilisé: {avg_recovery_rate:.4f}")

            # Générer des dates manquantes jusqu'à fin 2023
            missing_dates = pd.date_range(start=last_date + pd.Timedelta(days=1), end=end_date)
            extrapolated_data = []

            # Pour chaque région unique dans les données
            for region in df['region_name'].unique():
                region_data = df[df['region_name'] == region].copy()

                # Vérifier si la région a des données
                if region_data.empty:
                    continue

                last_region_data = region_data.iloc[-1].to_dict()

                # Valeur initiale pour les cas
                last_cases = last_region_data['cases']
                last_deaths = last_region_data['deaths']
                last_recovered = last_region_data['recovered']

                # Vérifier que les valeurs initiales sont valides
                if not np.isfinite(last_cases) or last_cases > 1e7:
                    last_cases = 1000  # Valeur raisonnable par défaut
                if not np.isfinite(last_deaths) or last_deaths > 1e6:
                    last_deaths = 50
                if not np.isfinite(last_recovered) or last_recovered > 1e7:
                    last_recovered = 900

                for date in missing_dates:
                    try:
                        # Utiliser un modèle de croissance plus modéré
                        # 1. Calculer les nouveaux cas avec des limites strictes
                        new_cases = last_cases * (1 + avg_growth)
                        new_cases = min(new_cases, 5000000)
                        new_cases = max(new_cases, last_cases * 0.95)

                        # 2. Calculer les nouveaux décès avec des limites
                        new_deaths = last_deaths + (new_cases - last_cases) * avg_death_rate
                        new_deaths = min(new_deaths, 500000)
                        new_deaths = max(new_deaths, last_deaths)

                        # 3. Calculer les nouvelles récupérations avec des limites
                        new_recovered = last_recovered + (new_cases - last_cases) * avg_recovery_rate
                        new_recovered = min(new_recovered, 4500000)
                        new_recovered = max(new_recovered, last_recovered)

                        # Vérifier que toutes les valeurs sont finies
                        if not (np.isfinite(new_cases) and np.isfinite(new_deaths) and np.isfinite(new_recovered)):
                            raise ValueError("Valeur non finie détectée")

                        # Créer une nouvelle entrée
                        new_entry = {
                            'date': date,
                            'cases': int(new_cases),
                            'deaths': int(new_deaths),
                            'recovered': int(new_recovered),
                            'region_name': region,
                            'region_id': last_region_data['region_id'],
                            'continent_name': last_region_data['continent_name']
                        }

                        # Ajouter à la liste des données extrapolées
                        extrapolated_data.append(new_entry)

                        last_cases = new_cases
                        last_deaths = new_deaths
                        last_recovered = new_recovered

                    except Exception as e:
                        print(f"Erreur lors de l'extrapolation pour {region} à la date {date}: {e}")
                        new_entry = {
                            'date': date,
                            'cases': int(last_cases),
                            'deaths': int(last_deaths),
                            'recovered': int(last_recovered),
                            'region_name': region,
                            'region_id': last_region_data['region_id'],
                            'continent_name': last_region_data['continent_name']
                        }
                        extrapolated_data.append(new_entry)

            # Convertir en DataFrame et concaténer avec les données originales
            if extrapolated_data:  
                extrapolated_df = pd.DataFrame(extrapolated_data)

                # Nettoyer les valeurs extrêmes dans les données extrapolées
                numeric_cols = extrapolated_df.select_dtypes(include=[np.number]).columns
                for col in numeric_cols:
                    if col in ['cases', 'deaths', 'recovered']:
                        extrapolated_df[col] = extrapolated_df[col].clip(lower=0)

                df = pd.concat([df, extrapolated_df], ignore_index=True)

                df = df.sort_values('date')

        return df

    def predict_2023_safe(self, region_name=None, retry_with_subset=True):
        """Version sécurisée de la prédiction 2023"""
        try:
            return self.predict_2023(region_name)
        except ValueError as e:
            if retry_with_subset and "infinity" in str(e):
                print("Erreur avec valeurs infinies détectée, nouvelle tentative avec un sous-ensemble de données...")
                # Récupérer un sous-ensemble limité de données
                region_id = None
                if region_name:
                    query = f"SELECT id_regions FROM regions WHERE name = '{region_name}'"
                    result = self.engine.execute(query).fetchone()
                    if result:
                        region_id = result[0]

                df = self.fetch_data(region_id)
                # Limiter aux 1000 dernières entrées
                df = df.sort_values('date').tail(1000)
                df = self.preprocess_data(df)

                # Continuer avec l'approche habituelle mais sur des données réduites
                train_df = df[df['date'] < pd.Timestamp('2023-01-01')].copy()
                X_train, X_test, y_train, y_test, test_dates = self.prepare_train_test(train_df, test_size=0.2)
                self.train(X_train, y_train)
                y_pred = self.evaluate(X_test, y_test)
                predictions_2023 = self.predict_future(train_df, days=365)

                return predictions_2023
            else:
                raise e

    def save_model(self, path='model'):
        """Sauvegarde le modèle entraîné"""
        if self.model is None:
            raise ValueError("Le modèle n'a pas encore été entraîné")

        os.makedirs(path, exist_ok=True)

        with open(f'{path}/rf_model.pkl', 'wb') as f:
            pickle.dump(self.model, f)

        with open(f'{path}/scaler_X.pkl', 'wb') as f:
            pickle.dump(self.scaler_X, f)

        with open(f'{path}/scaler_y.pkl', 'wb') as f:
            pickle.dump(self.scaler_y, f)

        with open(f'{path}/features.pkl', 'wb') as f:
            pickle.dump(self.features, f)

        print(f"Modèle et scalers sauvegardés dans le dossier '{path}'")

    def load_model(self, path='model'):
        """Charge un modèle entraîné"""
        try:
            with open(f'{path}/rf_model.pkl', 'rb') as f:
                self.model = pickle.load(f)

            with open(f'{path}/scaler_X.pkl', 'rb') as f:
                self.scaler_X = pickle.load(f)

            with open(f'{path}/scaler_y.pkl', 'rb') as f:
                self.scaler_y = pickle.load(f)

            with open(f'{path}/features.pkl', 'rb') as f:
                self.features = pickle.load(f)

            print("Modèle chargé avec succès")
        except Exception as e:
            print(f"Erreur lors du chargement du modèle: {e}")

    def plot_predictions(self, y_test, y_pred, test_dates, future_df=None):
        """Affiche les prédictions vs les valeurs réelles"""
        plt.figure(figsize=(12, 6))

        # Données historiques: réelles vs prédites
        plt.plot(test_dates, y_test, label='Cas réels', color='blue')
        plt.plot(test_dates, y_pred, label='Cas prédits', color='red', linestyle='--')

        # Prédictions futures
        if future_df is not None:
            plt.plot(future_df['date'], future_df['predicted_cases'],
                     label='Prédictions futures', color='green', linestyle='-.')

        plt.title('Prédiction des cas COVID')
        plt.xlabel('Date')
        plt.ylabel('Nombre de cas')
        plt.legend()
        plt.grid(True)
        plt.tight_layout()

        # Créer le dossier static s'il n'existe pas
        os.makedirs('static', exist_ok=True)

        # Sauvegarder le graphique
        plt.savefig('static/covid_predictions.png')
        print("Graphique sauvegardé dans static/covid_predictions.png")

        plt.close()

    def predict_2023(self, region_name=None):
        """Fonction spécifique pour prédire les cas COVID en 2023"""
        print(f"Prédiction des cas COVID pour 2023 pour la région: {region_name if region_name else 'toutes'}")

        # Récupérer les données historiques
        region_id = None
        if region_name:
            # Obtenir l'ID de région à partir du nom
            query = f"SELECT id_regions FROM regions WHERE name = '{region_name}'"
            result = self.engine.execute(query).fetchone()
            if result:
                region_id = result[0]

        # Récupérer les données
        df = self.fetch_data(region_id)

        # Si les données ne couvrent pas 2023, extrapoler
        if df['date'].max().year < 2023:
            df = self.preprocess_data(df)
            df = self.extrapolate_to_2023(df)
        else:
            df = self.preprocess_data(df)

        # Filtrer pour ne garder que les données jusqu'au début 2023
        train_df = df[df['date'] < pd.Timestamp('2023-01-01')].copy()

        # Préparer les données d'entraînement et de test
        try:
            X_train, X_test, y_train, y_test, test_dates = self.prepare_train_test(train_df, test_size=0.1)
        except ValueError as e:
            print(f"Erreur dans le test du modèle: {e}")
            return None

        # Entraîner le modèle
        self.train(X_train, y_train)

        # Évaluer le modèle
        y_pred = self.evaluate(X_test, y_test)

        # Prédire pour l'année 2023 entière
        predictions_2023 = self.predict_future(train_df, days=365)

        # Sauvegarder les prédictions dans la base
        save_predictions_to_db(predictions_2023, model_version="v1")

        # Sauvegarder le modèle
        path = f"model_2023{'_' + region_name if region_name else ''}"
        self.save_model(path)

        # Générer et sauvegarder le graphique
        self.plot_predictions(y_test, y_pred, test_dates, predictions_2023)

        return predictions_2023
    
    
    
