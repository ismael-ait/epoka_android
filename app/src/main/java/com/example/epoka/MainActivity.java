package com.example.epoka;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {
    EditText editTextPassword;
    EditText editTextId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Récupération des références aux vues
        editTextId = findViewById(R.id.editTextId);
        editTextPassword = findViewById(R.id.editTextPassword);
    }

    public void connecter(View v) {
        // Récupère le texte saisi par l'utilisateur dans editTextId et editTextPassword
        String id = editTextId.getText().toString();
        String password = editTextPassword.getText().toString();

        // Encodage des paramètres pour les ajouter à l'URL
        try {
            id = URLEncoder.encode(id, "UTF-8");
            password = URLEncoder.encode(password, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Construit l'URL avec les paramètres 'id_salarie' et 'mdp_salarie' pour la requête HTTP
        String urlServiceWeb = "http://172.16.57.120/epoka/connexion.php?id_salarie=" + id + "&mdp_salarie=" + password;
        Log.d(urlServiceWeb, "Voici l'url du service");


        // Exécute la tâche asynchrone pour effectuer la requête HTTP
        new TacheAsynchrone().execute(urlServiceWeb);
    }

    private class TacheAsynchrone extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                // Ouvre la connexion HTTP avec l'URL spécifiée
                URL url = new URL(urls[0]);
                HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                try {
                    // Lit la réponse du serveur
                    InputStream in = urlConnection.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    StringBuilder response = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }
                    return response.toString();
                } finally {
                    // Ferme la connexion
                    urlConnection.disconnect();
                }
            } catch (IOException e) {
                // En cas d'erreur, retourne null
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                // Convertit la réponse en objet JSON
                JSONObject jsonObject = new JSONObject(result);

                // Vérifie si la connexion est réussie
                boolean success = jsonObject.getBoolean("success");

                if (success) {
                    // Si la connexion est réussie, récupère le prénom, le nom et l'id du salarié
                    String prenom = jsonObject.getString("prenom_salarie");
                    String nom = jsonObject.getString("nom_salarie");
                    int id = jsonObject.getInt("id_salarie"); // Récupère l'id comme un entier

                    // Construit le message de salutation
                    String message = "Bonjour, " + prenom + " " + nom;

                    // Affiche le message de salutation à l'utilisateur
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();

                    // Crée un Intent pour démarrer la nouvelle activité Menu
                    Intent intent = new Intent(MainActivity.this, Menu.class);

                    // Ajoute les données du salarié à l'Intent
                    intent.putExtra("prenom", prenom);
                    intent.putExtra("nom", nom);
                    intent.putExtra("id", id); // Ajoute l'id à l'Intent

                    // Démarre la nouvelle activité Menu
                    startActivity(intent);
                    finish();

                } else {
                    // Si la connexion échoue, affiche le message d'erreur du serveur
                    String message = jsonObject.getString("message");
                    Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                // En cas d'erreur lors de l'analyse JSON, affiche une erreur
                Toast.makeText(MainActivity.this, "Erreur lors de l'analyse de la réponse du serveur", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }


    }
}
