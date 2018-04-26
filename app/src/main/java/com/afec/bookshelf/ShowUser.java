package com.afec.bookshelf;

import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.io.InputStream;

public class ShowUser extends BaseActivity {

    ImageView immagineUtente;
    Uri imageUri;
    TextView nomeUtente, emailUtente, bioUtente, sharedBookCount, takenBookCount;
    RoundedBitmapDrawable dr;
    Dialog builder;
    SharedPreferences sharedPref;
    private FirebaseUser user;
    FirebaseDatabase database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_user);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        immagineUtente = (ImageView) findViewById(R.id.immagineUtente);
        nomeUtente = (TextView) findViewById(R.id.nomeUtente);
        emailUtente = (TextView) findViewById(R.id.emailUtente);
        bioUtente = (TextView) findViewById(R.id.bioUtente);

        sharedBookCount = (TextView) findViewById(R.id.shared_book_count);
        takenBookCount = (TextView) findViewById(R.id.taken_book_count);

        //Mettere tutte le inizializzazioni qui in config
        config();


        sharedPref = this.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);
        String nome = sharedPref.getString("nomeUtente", null);
        String email = sharedPref.getString("emailUtente", null);
        String bio = sharedPref.getString("bioUtente", null);

        String immagineSalvata = sharedPref.getString("imageUri", null);
        try{
            Uri uri = Uri.parse(sharedPref.getString("imageUri", null));
            Log.d("uri", uri.toString());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            immagineUtente.setImageBitmap(bitmap);
        }catch (Exception e){
            Log.d("ex",e.toString());
            if(immagineSalvata!= null && !immagineSalvata.isEmpty()){
                Bitmap bitmap = BitmapFactory.decodeFile(immagineSalvata);
                immagineUtente.setImageBitmap(bitmap);
            }
        }

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.removeItem(R.id.action_show_profile);
        return true;
    }

    public void publicationQuickView(){
        View view = getLayoutInflater().inflate( R.layout.inflater_immagine_profilo, null);
        ImageView profileImage = (ImageView) view.findViewById(R.id.inflated_imageview);
        Picasso.with(this).load(imageUri).noPlaceholder().into(profileImage);
        builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        builder.show();
    }

    public void config(){

        //Books Given/Taken
        user = FirebaseAuth.getInstance().getCurrentUser();
        user.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(user.getUid()).child("lentBooks");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long value =(long) dataSnapshot.getValue();
                sharedBookCount .setText(String.valueOf(value));


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }


        });

        userRef = database.getReference("users").child(user.getUid()).child("borrowedBooks");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                long value =(long) dataSnapshot.getValue();
                takenBookCount.setText(String.valueOf(value));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }


        });
    }

}
