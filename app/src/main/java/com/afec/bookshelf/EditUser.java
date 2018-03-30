package com.afec.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class EditUser extends AppCompatActivity {

    ImageView immagineUtente;
    EditText nomeUtente, emailUtente, bioUtente;
    Button b;
    SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        immagineUtente = (ImageView) findViewById(R.id.editImmagineUtente);
        nomeUtente = (EditText) findViewById(R.id.editNomeUtente);
        emailUtente = (EditText) findViewById(R.id.editEmailUtente);
        bioUtente = (EditText) findViewById(R.id.editBioUtente);
        b = (Button) findViewById(R.id.editUserSubmitButton);
        sharedPref = this.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);

        String nome = sharedPref.getString("nomeUtente", null);
        String email = sharedPref.getString("emailUtente", null);
        String bio = sharedPref.getString("bioUtente", null);


        if(nome != null) {
            nomeUtente.setText(nome);
            Log.d("nomeUtente", nome);
        }
        else{
            Log.d("nomeUtente", "nullo");
        }
        if(email != null)
            emailUtente.setText(email);
        if(bio != null)
            bioUtente.setText(bio);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", String.valueOf(nomeUtente.getText()));
                editor.putString("emailUtente", String.valueOf(emailUtente.getText()));
                editor.putString("bioUtente", String.valueOf(bioUtente.getText()));
                editor.commit();
            }
        });
    }




}
