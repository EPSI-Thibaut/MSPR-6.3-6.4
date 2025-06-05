from flask import Flask
from flask_cors import CORS
from app.config import Config
from app.models import db
from app.routes import main
from flask_migrate import Migrate
from app.ia_model import CovidForecaster

def create_app():
    app = Flask(__name__)
    app.config.from_object(Config)

    # Configuration CORS les requêtes depuis le frontend Vue.js entrainement IA boutton
    CORS(app, origins=[
        "http://localhost:5173",
        "http://127.0.0.1:5173",
        "http://localhost:3000",
    ], methods=['GET', 'POST', 'PUT', 'DELETE'], allow_headers=['Content-Type'])

    db.init_app(app)
    migrate = Migrate(app, db)

    app.register_blueprint(main)

    return app