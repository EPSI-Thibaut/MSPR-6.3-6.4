from app.models import db
from sqlalchemy.sql import text

from sqlalchemy.sql import text
from app.models import db

def get_all_data_from_pandemics(pandemic_name):
    """Récupère toutes les données pour une pandémie spécifique"""
    query = text("""
        SELECT t.date_by_day, t.case_count, t.death, t.recovered, 
               r.name AS region_name, c.name AS continent_name
        FROM total_by_day t
        JOIN pandemics p ON t.id_pandemics = p.id_pandemics
        JOIN regions r ON t.id_regions = r.id_regions
        JOIN continents c ON r.id_continents = c.id_continents
        WHERE p.name = :pandemic_name
        ORDER BY t.date_by_day ASC
    """)
    result = db.session.execute(query, {"pandemic_name": pandemic_name})
    return [dict(row._mapping) for row in result]

def get_all_data_from_continents(continents):
    query = f"SELECT * FROM {continents}"
    result = db.session.execute(text(query))
    rows = result.fetchall()
    return [dict(row._mapping) for row in rows]

def get_all_data_from_countries(regions):
    query = f"SELECT * FROM {regions}"
    result = db.session.execute(text(query))
    rows = result.fetchall()
    return [dict(row._mapping) for row in rows]

def get_all_data_from_countries(countries):
    query = f"SELECT * FROM {countries}"
    result = db.session.execute(text(query))
    rows = result.fetchall()
    return [dict(row._mapping) for row in rows]

def get_all_data_from_total_by_day(total_by_day):
    query = f"SELECT * FROM {total_by_day}"
    result = db.session.execute(text(query))
    rows = result.fetchall()
    return [dict(row._mapping) for row in rows]

from app.models import db, CovidPrediction

def save_predictions_to_db(predictions_df, model_version="v1"):
    """
    Enregistre ou met à jour les prédictions dans la table covid_predictions.
    predictions_df : DataFrame Pandas avec les colonnes nécessaires.
    """
    for _, row in predictions_df.iterrows():
        existing = CovidPrediction.query.filter_by(
            prediction_date=row['date'],
            region_id=row.get('region_id'),
            region_name=row.get('region_name')
        ).first()

        if existing:
            existing.predicted_cases = int(row['predicted_cases'])
            if 'predicted_deaths' in row:
                existing.predicted_deaths = int(row['predicted_deaths'])
            if 'predicted_recovered' in row:
                existing.predicted_recovered = int(row['predicted_recovered'])
            existing.model_version = model_version
        else:
            pred = CovidPrediction(
                prediction_date=row['date'],
                region_id=row.get('region_id'),
                region_name=row.get('region_name'),
                continent_name=row.get('continent_name'),
                predicted_cases=int(row['predicted_cases']),
                predicted_deaths=int(row['predicted_deaths']) if 'predicted_deaths' in row else None,
                predicted_recovered=int(row['predicted_recovered']) if 'predicted_recovered' in row else None,
                model_version=model_version
            )
            db.session.add(pred)
    db.session.commit()