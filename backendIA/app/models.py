from flask_sqlalchemy import SQLAlchemy

db = SQLAlchemy()

class Continent(db.Model):
    id_continents = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)

class Country(db.Model):
    id_countries = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    id_continents = db.Column(db.Integer, db.ForeignKey('continent.id_continents'), nullable=False)

class Region(db.Model):
    id_regions = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)
    id_continents = db.Column(db.Integer, db.ForeignKey('continent.id_continents'), nullable=False)

class Pandemic(db.Model):
    id_pandemics = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255), nullable=False)

class TotalByDay(db.Model):
    id_pandemics = db.Column(db.Integer, db.ForeignKey('pandemic.id_pandemics'), primary_key=True)
    id_regions = db.Column(db.Integer, db.ForeignKey('region.id_regions'), primary_key=True)
    case_count = db.Column(db.Integer, nullable=False)
    death = db.Column(db.Integer, nullable=False)
    recovered = db.Column(db.Integer, nullable=False)
    date_by_day = db.Column(db.Date, primary_key=True)
    
class CovidPrediction(db.Model):
    __tablename__ = 'covid_predictions'

    id = db.Column(db.Integer, primary_key=True)
    continent_name = db.Column(db.String(255))
    created_at = db.Column(db.DateTime, server_default=db.func.now())
    model_version = db.Column(db.String(50))
    predicted_cases = db.Column(db.Integer, nullable=False)
    predicted_deaths = db.Column(db.Integer)
    predicted_recovered = db.Column(db.Integer)
    prediction_date = db.Column(db.Date, nullable=False)
    region_id = db.Column(db.Integer)
    region_name = db.Column(db.String(255))