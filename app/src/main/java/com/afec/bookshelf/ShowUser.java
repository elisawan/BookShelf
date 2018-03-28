package com.afec.bookshelf;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class ShowUser extends AppCompatActivity {

    ImageView immagineUtente;
    TextView nomeUtente, emailUtente, bioUtente;
    RoundedBitmapDrawable dr;
    Dialog builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(myToolbar);



        immagineUtente = (ImageView) findViewById(R.id.immagineUtente);
        nomeUtente = (TextView) findViewById(R.id.nomeUtente);
        emailUtente = (TextView) findViewById(R.id.emailUtente);
        bioUtente = (TextView) findViewById(R.id.bioUtente);

        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
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

        //percepisce il tap lungo
        immagineUtente.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                publicationQuickView();
                return true;
            }
            

        });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_edit:
                Intent intent = new Intent(getBaseContext(), EditUser.class);
                startActivity(intent);
                return true;

            /*case R.id.action_favorite:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...
                return true;*/

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.show_user_menu, menu);
        return true;
    }


    public void publicationQuickView(){
        View view = getLayoutInflater().inflate( R.layout.inflater_immagine_profilo, null);

        ImageView profileImage = (ImageView) view.findViewById(R.id.inflated_imageview);



        Picasso.with(this).load(R.drawable.imgprofilo).noPlaceholder().into(profileImage);

        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        builder.show();
    }


}
