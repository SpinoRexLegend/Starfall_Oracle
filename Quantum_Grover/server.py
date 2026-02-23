from flask import Flask, jsonify, request
from flask_cors import CORS
import GroverAlgo
import os
import mysql.connector
from mysql.connector import Error

app = Flask(__name__)
CORS(app)

# Database Configuration
DB_CONFIG = {
    'host': 'localhost',
    'database': 'aegis',
    'user': 'root',
    'password': 'Dps3!2006'  # Default from schema.sql, user might need to change
}

def get_db_connection():
    try:
        connection = mysql.connector.connect(**DB_CONFIG)
        if connection.is_connected():
            return connection
    except Error as e:
        print(f"Error connecting to MySQL: {e}")
        return None

@app.route('/asteroids', methods=['GET'])
def get_asteroids():
    connection = get_db_connection()
    if connection:
        try:
            cursor = connection.cursor(dictionary=True)
            cursor.execute("SELECT * FROM asteroid")
            asteroids = cursor.fetchall()
            return jsonify({
                "status": "success",
                "data": asteroids
            })
        except Error as e:
            return jsonify({
                "status": "error",
                "message": str(e)
            }), 500
        finally:
            if connection.is_connected():
                cursor.close()
                connection.close()
    else:
        return jsonify({
            "status": "error",
            "message": "Database connection failed"
        }), 500

@app.route('/search_asteroid', methods=['POST'])
def search_asteroid():
    data = request.json
    prompt = data.get('prompt', '').strip()
    
    if not prompt:
        return jsonify({"status": "error", "message": "Empty prompt"}), 400

    connection = get_db_connection()
    if connection:
        try:
            cursor = connection.cursor(dictionary=True)
            # Simple keyword search: Check if any asteroid name is in the prompt
            # First, get all asteroid names to check against prompt (efficient for small DB)
            cursor.execute("SELECT name FROM asteroid")
            all_names = cursor.fetchall()
            
            target_name = None
            for item in all_names:
                if item['name'].lower() in prompt.lower():
                    target_name = item['name']
                    break
            
            # Fallback: SQL LIKE search if exact name not found in prompt
            if not target_name:
                # This is a bit loose, searching if prompt contains parts of name
                # For now, let's just search if the prompt *is* the name or contains it directly
                pass 

            if target_name:
                 cursor.execute("SELECT * FROM asteroid WHERE name = %s", (target_name,))
                 asteroid = cursor.fetchone()
                 return jsonify({"status": "success", "data": asteroid})
            
            # Try searching for partial matches directly in DB based on prompt words
            words = prompt.split()
            for word in words:
                if len(word) > 3: # Avoid short words
                     cursor.execute("SELECT * FROM asteroid WHERE name LIKE %s", (f"%{word}%",))
                     asteroid = cursor.fetchone()
                     if asteroid:
                         return jsonify({"status": "success", "data": asteroid})

            return jsonify({"status": "error", "message": "Asteroid not identified in prompt"}), 404

        except Error as e:
            return jsonify({"status": "error", "message": str(e)}), 500
        finally:
            if connection.is_connected():
                cursor.close()
                connection.close()
    else:
        return jsonify({"status": "error", "message": "Database connection failed"}), 500

@app.route('/run-grover', methods=['GET'])
def run_grover():
    try:
        # Use the existing maneuver_demo.json
        curent_dir = os.path.dirname(os.path.abspath(__file__))
        json_path = os.path.join(curent_dir, 'maneuver_demo.json')
        
        result = GroverAlgo.run_quantum_maneuver_search(json_path, return_image=True)
        
        if result:
            return jsonify({
                "status": "success",
                "data": result
            })
        else:
            return jsonify({
                "status": "error",
                "message": "No valid maneuver found or execution failed"
            }), 500

    except Exception as e:
        return jsonify({
            "status": "error",
            "message": str(e)
        }), 500

if __name__ == '__main__':
    app.run(port=5000, debug=True)
    