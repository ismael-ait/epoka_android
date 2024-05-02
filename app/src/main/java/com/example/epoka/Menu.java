package com.example.epoka;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Menu extends AppCompatActivity {
    String prenom;
    String nom;
    int id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);

        // Récupération des données passées depuis MainActivity
        Intent intent = getIntent();
        prenom = intent.getStringExtra("prenom");
        nom = intent.getStringExtra("nom");
        id = intent.getIntExtra("id", 0);


        // Affichage du message de bienvenue dans le TextView
        TextView textViewMessage = findViewById(R.id.textViewMessage);
        String message = "Bonjour, " + prenom + " " + nom;
        textViewMessage.setText(message);
    }

    public void ajouter(View v) {
        // Crée un Intent pour démarrer la nouvelle activité AjoutMission
        Intent intent = new Intent(Menu.this, AjoutMission.class);

        // Ajoute les données du salarié à l'Intent
        intent.putExtra("prenom", prenom);
        intent.putExtra("nom", nom);
        intent.putExtra("id", id);

        // Démarre la nouvelle activité AjoutMission
        startActivity(intent);
    }

    public void quitter(View v) {
        // Termine l'activité
        finish();
    }
}