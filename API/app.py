from flask import Flask, jsonify, request
import sqlite3
import requests # <--- Nueva librería para hacer peticiones HTTP
import urllib.parse

app = Flask(__name__)

# --- NUEVOS ENDPOINTS PARA EL CATÁLOGO Y PRECIOS ---

@app.route('/api/cs2/catalog', methods=['GET'])
def get_catalog():
    query = request.args.get('query', '')
    # Python le pide el catálogo a Steam
    url = f"https://steamcommunity.com/market/search/render/?query={query}&appid=730&norender=1&count=100"
    try:
        r = requests.get(url)
        return jsonify(r.json()) # Devuelve el JSON tal cual a Java
    except Exception as e:
        return jsonify({"error": str(e)}), 500

@app.route('/api/cs2/details', methods=['GET'])
def get_details():
    base_name = request.args.get('baseName', '')
    query_encoded = urllib.parse.quote(base_name)
    # Python le pide los precios de un arma concreta a Steam
    url = f"https://steamcommunity.com/market/search/render/?query={query_encoded}&appid=730&norender=1&count=10"
    try:
        r = requests.get(url)
        return jsonify(r.json())
    except Exception as e:
        return jsonify({"error": str(e)}), 500

# --- ENDPOINTS ANTIGUOS (SE MANTIENEN PARA LOS DIAGNÓSTICOS) ---

@app.route('/api/cs2/market', methods=['GET'])
def get_market():
    item = request.args.get('item', '')
    if item.lower() == "error":
        return jsonify({"error": "Steam API Error"}), 503
    return jsonify({"success": True, "item": item, "price": "10.00"})

@app.route('/api/cs2/logs', methods=['GET'])
def read_logs():
    try:
        with open('no_existe.txt', 'r') as f: return jsonify({"data": f.read()})
    except FileNotFoundError:
        return jsonify({"error": "Archivo de logs no encontrado"}), 404

@app.route('/api/cs2/stats', methods=['GET'])
def get_stats():
    try:
        conn = sqlite3.connect('rota.db')
        conn.execute("SELECT * FROM inexistente")
    except Exception as e:
        return jsonify({"error": f"Error DB: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(port=5000, debug=True)