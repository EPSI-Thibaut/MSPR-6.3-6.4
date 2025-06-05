Cette application FLASK PYthon est conçue pour créer un modèle de prédiction IA sur les données COVID en 2023 et 2024, cette application récupère les données de la base de données mariaDB générée par l'api SpringBoot qui gère de son côté l'extraction, nettoyage et transformation des données covid et sars.

### Étapes d'initialisation

1. **Créer un environnement virtuel** :
   ```bash
   python3 -m venv env
   source env/bin/activate  # Sur Windows : venv\Scripts\activate
   ```

2. **Installer les dépendances** :
   ```bash
   pip install -r requirements.txt
   ```

3. **Configurer la base de données** (si nécessaire) :
   ```bash
   flask db init
   flask db migrate -m "Initial migration"
   flask db upgrade
   ```

4. **Lancer l'application Flask et la route d'entrainement du modèle IA pour le front VUE** :
   ```bash
   python run.py
   ```
