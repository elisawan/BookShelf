package com.afec.bookshelf;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

public class EditUser extends AppCompatActivity {

    ImageView immagineUtente;
    EditText nomeUtente, emailUtente, bioUtente;
    AlertDialog.Builder alert;
    final CharSequence[] choice = {"Choose from Gallery","Capture a photo"};
    int from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);


        immagineUtente = (ImageView) findViewById(R.id.editImmagineUtente);
        nomeUtente = (EditText) findViewById(R.id.editNomeUtente);
        emailUtente = (EditText) findViewById(R.id.editEmailUtente);
        bioUtente = (EditText) findViewById(R.id.editBioUtente);

        final SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        String nome = sharedPref.getString("nomeUtente", null);
        String email = sharedPref.getString("emailUtente", null);
        String bio = sharedPref.getString("bioUtente", null);

        if(nome != null)
            nomeUtente.setText(nome);
        if(email != null)
            emailUtente.setText(email);
        if(bio != null)
            bioUtente.setText(bio);





        immagineUtente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);*/
                PopupMenu popupMenu = new PopupMenu(EditUser.this, immagineUtente);
                popupMenu.getMenuInflater().inflate(R.menu.picture_popup_menu, popupMenu.getMenu());

                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(EditUser.this,"You Clicked : " + menuItem.getTitle(),Toast.LENGTH_SHORT).show();

                        return true;

                    }
                });
                popupMenu.show();
            }
        });



        nomeUtente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sharedPref.edit().putString("nomeUtente", nomeUtente.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        emailUtente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sharedPref.edit().putString("emailUtente", emailUtente.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        bioUtente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                sharedPref.edit().putString("nomeUtente", bioUtente.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1){

        }
    }




}
