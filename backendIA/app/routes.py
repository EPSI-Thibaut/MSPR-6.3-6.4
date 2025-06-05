from flask import Blueprint, jsonify, request
from app.models import db
from sqlalchemy.sql import text
from app.services import get_all_data_from_pandemics, get_all_data_from_continents
from app.ia_model import CovidForecaster
import threading
import time

main = Blueprint('main', __name__)

# Variable globale pour tracker l'état d'entraînement
training_status = {"active": False, "start_time": None, "message": "", "thread": None}

def check_training_status():
    """Vérifier si l'entraînement doit être arrêté"""
    return training_status["active"]

@main.route('/', methods=['GET'])
def home():
    """Route d'accueil"""
    return jsonify({
        "message": "Backend IA Flask fonctionne !", 
        "version": "1.0",
        "training_active": training_status["active"]
    }), 200

@main.route('/test', methods=['GET'])
def test_route():
    """Route de test simple"""
    return jsonify({"message": "Flask IA test réussi !"}), 200

@main.route('/training_status', methods=['GET'])
def get_training_status():
    """Obtenir le statut de l'entraînement en cours"""
    status = {
        "active": training_status["active"],
        "start_time": training_status["start_time"],
        "message": training_status["message"],
        "duration": None
    }
    
    if training_status["active"] and training_status["start_time"]:
        status["duration"] = time.time() - training_status["start_time"]
    
    return jsonify(status), 200

@main.route('/stop_training', methods=['POST'])
def stop_training():
    """Arrêter l'entraînement en cours"""
    global training_status
    
    if training_status["active"]:
        training_status["active"] = False
        training_status["message"] = "Arrêt demandé par l'utilisateur..."
        return jsonify({
            "status": "success", 
            "message": "Arrêt de l'entraînement demandé"
        }), 200
    else:
        return jsonify({
            "status": "info",
            "message": "Aucun entraînement en cours"
        }), 200

@main.route('/test_db_connection', methods=['GET'])
def test_db_connection():
    try:
        db.session.execute(text('SELECT 1'))
        return jsonify({"message": "Connection to the database successful!"}), 200
    except Exception as e:
        print(f"Database connection error: {e}") 
        return jsonify({"message": "Connection to the database failed!", "error": str(e)}), 500

@main.route('/get_data/<pandemics>', methods=['GET'])
def get_data_pandemics(pandemics):
    try:
        data = get_all_data_from_pandemics(pandemics)
        if not data:
            return jsonify({"message": f"Aucune donnée trouvée pour la pandémie '{pandemics}'"}), 404
        return jsonify({"data": data}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@main.route('/get_data_continents/<continents>', methods=['GET'])
def get_data_continents(continents):
    try:
        data = get_all_data_from_continents(continents)
        return jsonify({"data": data}), 200
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@main.route('/predict_2023/<region_name>', methods=['GET'])
def predict_2023_region(region_name):
    """Prédire les cas COVID pour 2023 pour une région spécifique"""
    try:
        forecaster = CovidForecaster()
        predictions = forecaster.predict_2023(region_name)
        
        return jsonify({
            "region": region_name,
            "predictions_2023": predictions.to_dict(orient='records')
        })
    except Exception as e:
        return jsonify({"error": str(e)}), 500

def run_training_workflow():
    """
    Fonction qui exécute le workflow d'entraînement en arrière-plan
    """
    global training_status
    
    try:
        # Import du workflow ici pour éviter l'import circulaire
        from app.workflow import test_model_predictions
        
        training_status["message"] = "Démarrage du workflow d'entraînement..."
        print("=== Démarrage du workflow d'entraînement ===")
        
        if not check_training_status():
            return
        
        training_status["message"] = "Exécution du workflow complet..."
        
        # Exécuter le workflow complet
        predictions = test_model_predictions()
        
        if not check_training_status():
            training_status["message"] = "Entraînement arrêté par l'utilisateur"
            return
        
        if predictions is not None and len(predictions) > 0:
            training_status["message"] = f"Workflow terminé avec succès ! {len(predictions)} prédictions générées"
            print(f"=== Workflow terminé: {len(predictions)} prédictions ===")
        else:
            training_status["message"] = "Workflow terminé mais aucune prédiction générée"
            print("=== Workflow terminé sans prédictions ===")
            
    except Exception as e:
        training_status["message"] = f"Erreur lors de l'exécution du workflow: {str(e)}"
        print(f"Erreur workflow: {str(e)}")
    finally:
        training_status["active"] = False
        training_status["thread"] = None

@main.route('/train_predict', methods=['POST'])
def train_and_predict():
    """
    Lance le workflow d'entraînement et de prédiction IA en utilisant le fichier workflow.py
    """
    global training_status
    
    # Vérifier si un entraînement est déjà en cours
    if training_status["active"]:
        return jsonify({
            "status": "error",
            "message": "Un entraînement est déjà en cours. Veuillez attendre qu'il se termine ou l'arrêter."
        }), 409
    
    try:
        print("=== Lancement du workflow d'entraînement ===")
        
        # Marquer l'entraînement comme actif
        training_status["active"] = True
        training_status["start_time"] = time.time()
        training_status["message"] = "Préparation de l'entraînement..."
        
        # Lancer l'entraînement dans un thread séparé
        training_thread = threading.Thread(target=run_training_workflow)
        training_status["thread"] = training_thread
        training_thread.start()
        
        return jsonify({
            "status": "success",
            "message": "Workflow d'entraînement démarré en arrière-plan."
        }), 200
        
    except Exception as e:
        training_status["active"] = False
        print(f"=== Erreur lors du lancement: {str(e)} ===")
        return jsonify({
            "status": "error", 
            "message": f"Erreur lors du lancement de l'entraînement: {str(e)}"
        }), 500