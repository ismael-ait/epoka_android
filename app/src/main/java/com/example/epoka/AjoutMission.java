package com.example.epoka;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AjoutMission extends AppCompatActivity {
    String prenom;
    String nom;
    int id;

    public class Commune {
        private int id;
        private String nom;

        public Commune(int id, String nom) {
            this.id = id;
            this.nom = nom;
        }

        public int getId() {
            return id;
        }

        public String getNom() {
            return nom;
        }

        @Override
        public String toString() {
            return nom;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ajout_mission);

        // Récupération des données passées depuis MainActivity
        Intent intent = getIntent();
        prenom = intent.getStringExtra("prenom");
        nom = intent.getStringExtra("nom");
        id = intent.getIntExtra("id", 0);
        // Remplissage du Spinner avec la liste des communes
        fillSpinnerWithCommunes();

    }

    private void fillSpinnerWithCommunes() {
        // Récupération du Spinner depuis le layout
        Spinner spinnerLieu = findViewById(R.id.spinnerLieu);

        // URL de votre service web PHP qui renvoie la liste des communes au format JSON
        String urlServiceWeb = "http://172.16.57.120/epoka/liste_communes.php";

        // Exécution de la requête HTTP dans une tâche asynchrone
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    // Ouverture de la connexion HTTP
                    URL url = new URL(urlServiceWeb);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                    try {
                        // Lecture de la réponse JSON
                        InputStream in = urlConnection.getInputStream();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        StringBuilder response = new StringBuilder();
                        String line;
                        while ((line = reader.readLine()) != null) {
                            response.append(line);
                        }

                        // Traitement de la réponse JSON
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JSONArray jsonArrayCommunes = new JSONArray(response.toString());
                                    List<Commune> communesList = new ArrayList<>();
                                    // Ajout des communes à la liste
                                    for (int i = 0; i < jsonArrayCommunes.length(); i++) {
                                        JSONObject communeObject = jsonArrayCommunes.getJSONObject(i);
                                        int idCommune = communeObject.getInt("id_commune");
                                        String nomCommune = communeObject.getString("nom_commune");
                                        Commune commune = new Commune(idCommune, nomCommune);
                                        communesList.add(commune);

                                    }

                                    // Création de l'adaptateur pour le Spinner
                                    ArrayAdapter<Commune> adapter = new ArrayAdapter<>(AjoutMission.this, android.R.layout.simple_spinner_item, communesList);
                                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    // Application de l'adaptateur au Spinner
                                    spinnerLieu.setAdapter(adapter);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });
                    } finally {
                        // Fermeture de la connexion
                        urlConnection.disconnect();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();



    }


    public void insertMission(View v) {
        // Récupération des valeurs des champs editTextDateDu et editTextDateAu
        EditText editTextDateDu = findViewById(R.id.editTextDateDu);
        EditText editTextDateAu = findViewById(R.id.editTextDateAu);

        // Formatage des dates au format "année-mois-jour"
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dateDu = sdf.format(new Date(editTextDateDu.getText().toString()));
        String dateAu = sdf.format(new Date(editTextDateAu.getText().toString()));

        // Récupération de l'ID de la commune sélectionnée dans le spinner
        Spinner spinnerLieu = findViewById(R.id.spinnerLieu);
        Commune selectedCommune = (Commune) spinnerLieu.getSelectedItem();
        int idCommune = selectedCommune.getId();

        // Vérification des valeurs non nulles
        if (!dateDu.isEmpty() && !dateAu.isEmpty()) {
            // Exécution de la requête HTTP d'insertion dans une tâche asynchrone
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        // URL du service web PHP pour l'insertion
                        String urlServiceWeb = "http://172.16.57.120/epoka/ajout_mission.php";

                        // Construction de l'URL avec les paramètres GET
                        String urlString = urlServiceWeb + "?dateDu=" + dateDu + "&dateAu=" + dateAu + "&idCommune=" + idCommune + "&id_salarie=" + id;
                        Log.d("URL générée", urlString);

                        // Ouverture de la connexion HTTP
                        URL url = new URL(urlString);
                        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

                        try {
                            // Lecture de la réponse HTTP
                            InputStream in = urlConnection.getInputStream();
                            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                            StringBuilder response = new StringBuilder();
                            String line;
                            while ((line = reader.readLine()) != null) {
                                response.append(line);
                            }

                            // Affichage du message de succès ou d'erreur dans l'UI
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(AjoutMission.this, response.toString(), Toast.LENGTH_SHORT).show();
                                }
                            });
                        } finally {
                            // Fermeture de la connexion
                            urlConnection.disconnect();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else {
            // Affichage d'un message d'erreur si les champs sont vides
            Toast.makeText(this, "Veuillez remplir toutes les informations.", Toast.LENGTH_SHORT).show();
        }
    }






}