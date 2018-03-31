package com.afec.bookshelf;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class EditUser extends AppCompatActivity {

    ImageView immagineUtente;
    EditText nomeUtente, emailUtente, bioUtente;
    Button b;
    AlertDialog.Builder alert;
<<<<<<< HEAD
    //final CharSequence[] choice = {"Choose from Gallery","Capture a photo"};
    //int from;
    Button b;
    SharedPreferences sharedPref;
    public static final int PICK_IMAGE = 1;
    public static final int SNAP_PIC = 2;
    private Uri mUri;
    private Bitmap mPhoto;

=======
    final CharSequence[] choice = {"Choose from Gallery","Capture a photo"};
    int from;
    SharedPreferences sharedPref;
    private String[] contact_methods = {"WhatsApp","E-mail","Telegram","Instant Messaging"};
>>>>>>> master

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(myToolbar);

        immagineUtente = (ImageView) findViewById(R.id.editImmagineUtente);
        nomeUtente = (EditText) findViewById(R.id.editNomeUtente);
        emailUtente = (EditText) findViewById(R.id.editEmailUtente);
        bioUtente = (EditText) findViewById(R.id.editBioUtente);
        b = (Button) findViewById(R.id.button_edit_confirm);
        sharedPref = this.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);

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
<<<<<<< HEAD
                        if(menuItem.getTitle().equals(getResources().getString(R.string.camera))){
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                startActivityForResult(takePictureIntent, SNAP_PIC);
                            }
                        }else{
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        }
=======
>>>>>>> master
                        return true;
                    }
                });
                popupMenu.show();
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", String.valueOf(nomeUtente.getText()));
                editor.putString("emailUtente", String.valueOf(emailUtente.getText()));
                editor.putString("bioUtente", String.valueOf(bioUtente.getText()));
                editor.commit();
                Intent intent= new Intent(getApplicationContext(),ShowUser.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.action_show:
                Intent intent = new Intent(getBaseContext(), ShowUser.class);
                startActivity(intent);
                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.edit_user_menu, menu);
        return true;
    }
<<<<<<< HEAD

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    if (data == null) {
                        //Display an error
                        return;
                    }
                    try {
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        mPhoto = BitmapFactory.decodeStream(inputStream);
                        ((ImageView)findViewById(R.id.editImmagineUtente)).setImageBitmap(mPhoto);
                    } catch (FileNotFoundException e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SNAP_PIC:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    Bitmap imageBitmap = (Bitmap) extras.get("data");
                    immagineUtente.setImageBitmap(imageBitmap);
                }


                break;
        }
    }
=======
>>>>>>> master
}
