# Documentation du modèle d'IA - CovidForecaster

## 1. Introduction

Ce document décrit le workflow, l’architecture et l’utilisation du modèle IA `CovidForecaster` pour la prédiction de l’évolution de la pandémie COVID-19 sur l’année 2023.

---

## 2. Lancement du workflow

### 2.1 Depuis le terminal

Pour lancer le workflow complet (préparation des données, entraînement, prédiction, sauvegarde en base et génération de graphiques) :

```bash
python3 app/workflow.py
```

### 2.2 Depuis une API Flask

Une route `/api/train_predict` peut être appelée pour déclencher le workflow à distance (voir le backend Flask).

---

## 3. Architecture du modèle

Le modèle est basé sur la classe `CovidForecaster` (voir `app/ia_model.py`).  
Le workflow suit les étapes suivantes :

### 3.1 Récupération des données

- Connexion à la base MariaDB via SQLAlchemy.
- Extraction des données historiques COVID (cas, décès, guérisons, régions, continents).

### 3.2 Prétraitement des données

- Nettoyage des valeurs extrêmes et infinies (`clean_extreme_values`).
- Création de variables temporelles (jour, mois, saison, week-end, etc.).
- Génération de variables décalées (lags) et de moyennes mobiles.
- Calcul des taux de croissance, mortalité et récupération.
- Gestion intelligente des valeurs manquantes.

### 3.3 Entraînement

- Séparation temporelle train/test (`prepare_train_test`).
- Normalisation des features.
- Entraînement d’un `RandomForestRegressor` avec optimisation des hyperparamètres via `RandomizedSearchCV`.
- Possibilité d’utiliser un entraînement rapide pour les tests.

### 3.4 Prédiction

- Génération de prédictions jour par jour pour l’année 2023 (`predict_future`).
- Calcul des décès et guérisons prédits à partir des taux historiques.
- Extrapolation possible si les données ne couvrent pas toute l’année (`extrapolate_to_2023`).

### 3.5 Évaluation et visualisation

- Évaluation du modèle sur l’ensemble de test (MSE, RMSE, MAE, R²).
- Génération et sauvegarde d’un graphique comparant cas réels, cas prédits et projections futures (`plot_predictions`).

### 3.6 Sauvegarde

- Sauvegarde des prédictions dans la base de données (`save_predictions_to_db`).
- Sauvegarde du modèle et des scalers pour réutilisation ultérieure (`save_model`/`load_model`).

---

## 4. Caractéristiques utilisées (features)

Le modèle utilise les variables suivantes :

- **Variables temporelles** : `day_of_week`, `month`, `season`, `is_weekend`, `quarter`
- **Variables décalées** : `cases_lag1`, `cases_lag7`, `cases_lag14`, `deaths_lag1`, `deaths_lag7`, `recovered_lag1`
- **Moyennes mobiles** : `cases_ma7`, `cases_ma30`
- **Taux** : `growth_rate`, `death_rate`, `recovery_rate`

---

## 5. Utilisation des scripts

### 5.1 Lancement du workflow complet

- Exécute `python3 app/workflow.py` pour lancer tout le processus.
- Les prédictions sont sauvegardées en base et un graphique est généré dans `static/covid_predictions.png`.

### 5.2 Test rapide

- Utilise `app/test_ia_model.py` pour un test minimal sur un petit échantillon (100 lignes).

### 5.3 API Flask

- Une route POST `/api/train_predict` permet de lancer le workflow à distance (voir backend Flask).

---

## 6. Maintenance et amélioration

- **Performance** : ajuste les paramètres d’entraînement pour accélérer ou affiner le modèle.
- **Précision** : ajoute d’autres sources de données (vaccinations, mesures, démographie), teste d’autres modèles (`GradientBoostingRegressor`, `XGBoost`…).
- **Visualisation** : développe un tableau de bord interactif pour explorer les prédictions.

---

## 7. Résolution des problèmes courants

- **Erreur "cannot convert float NaN to integer"** : vérifie l’absence de NaN dans les données sources.
- **Problèmes de connexion à la base** : vérifie le fichier `.env` et l’accessibilité de la base.
- **Erreur d’entraînement** : utilise un sous-échantillon (`df.tail(1000)`) ou le mode rapide pour le debug.

---

## 8. Description des principales méthodes

- **`__init__()`** : initialise le modèle, les scalers et la connexion à la base.
- **`fetch_data(region_id=None)`** : récupère les données COVID depuis la base.
- **`clean_extreme_values(df)`** : nettoie les valeurs aberrantes.
- **`preprocess_data(df)`** : prépare les données pour l’apprentissage.
- **`prepare_train_test(df, test_size=0.2)`** : split temporel, normalisation.
- **`train(X_train, y_train)`** : entraînement du modèle avec recherche d’hyperparamètres.
- **`evaluate(X_test, y_test)`** : évaluation du modèle.
- **`predict_future(df, days=30)`** : prédiction itérative sur la période future.
- **`extrapolate_to_2023(df)`** : extrapolation si besoin jusqu’à fin 2023.
- **`predict_2023_safe(region_name=None, retry_with_subset=True)`** : workflow robuste avec fallback.
- **`save_model(path)` / `load_model(path)`** : sauvegarde/chargement du modèle.
- **`plot_predictions(y_test, y_pred, test_dates, future_df=None)`** : graphique de comparaison.
- **`predict_2023(region_name=None)`** : workflow principal pour générer et sauvegarder les prédictions 2023.

---

## 9. Exemple d’utilisation

```python
from app.ia_model import CovidForecaster

forecaster = CovidForecaster()
predictions = forecaster.predict_2023(region_name="Europe")
print(predictions.head())
```

---

## 10. Fichiers principaux

- `app/ia_model.py` : classe et logique du modèle IA
- `app/services.py` : fonctions d’accès et de sauvegarde en base
- `app/workflow.py` : script de workflow complet
- `app/test_ia_model.py` : test rapide du modèle

---

## 11. Contact

Pour toute question ou amélioration, contacter l’équipe projet.
