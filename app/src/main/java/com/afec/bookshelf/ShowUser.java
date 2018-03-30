package com.afec.bookshelf;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class ShowUser extends AppCompatActivity {

    ImageView immagineUtente;
    TextView nomeUtente, emailUtente, bioUtente;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        immagineUtente = (ImageView) findViewById(R.id.immagineUtente);
        nomeUtente = (TextView) findViewById(R.id.nomeUtente);
        emailUtente = (TextView) findViewById(R.id.emailUtente);
        bioUtente = (TextView) findViewById(R.id.bioUtente);

        sharedPref = this.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        String nome = sharedPref.getString("nomeUtente", null);
        String email = sharedPref.getString("emailUtente", null);
        String bio = sharedPref.getString("bioUtente", null);

        if(nome != null)
            nomeUtente.setText(nome);
        else
            nomeUtente.setText("-");
        if(email != null)
            emailUtente.setText(email);
        else
            emailUtente.setText("-");
        if(bio != null)
            bioUtente.setText(bio);
        else
            bioUtente.setText("-");

    }
}
