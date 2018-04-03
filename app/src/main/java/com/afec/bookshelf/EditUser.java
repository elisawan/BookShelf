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
import android.widget.ImageButton;
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
    ImageButton ib;
    AlertDialog.Builder alert;
    SharedPreferences sharedPref;
    CheckBox email_cb, whatsapp_cb, call_cb;

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
        ib = (ImageButton) findViewById(R.id.edit_profile_image_bt);
        email_cb = (CheckBox) findViewById(R.id.email_cb);
        whatsapp_cb = (CheckBox) findViewById(R.id.whatsapp_cb);
        call_cb = (CheckBox) findViewById(R.id.call_cb);
        sharedPref = this.getSharedPreferences("userPreferences", Context.MODE_PRIVATE);

        nomeUtente.setText(sharedPref.getString("nomeUtente", null));
        emailUtente.setText(sharedPref.getString("emailUtente", null));
        bioUtente.setText(sharedPref.getString("bioUtente", null));
        email_cb.setChecked(sharedPref.getBoolean("contact_email",false));
        whatsapp_cb.setChecked(sharedPref.getBoolean("contact_whatsapp",false));
        call_cb.setChecked(sharedPref.getBoolean("contact_call",false));

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent camera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                Intent gallery = new Intent(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                Intent chooser = Intent.createChooser(camera,"Profile image");
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{gallery});
                if(chooser.resolveActivity(getPackageManager())!=null){
                    startActivityForResult(chooser,1);
                }
            }
        });

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", String.valueOf(nomeUtente.getText()));
                editor.putString("emailUtente", String.valueOf(emailUtente.getText()));
                editor.putString("bioUtente", String.valueOf(bioUtente.getText()));
                editor.putBoolean("contact_email", email_cb.isChecked());
                editor.putBoolean("contact_whatsapp", whatsapp_cb.isChecked());
                editor.putBoolean("contact_call", call_cb.isChecked());
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode) {
            case 1:
                //TODO
            default:
                //TODO
        }
    }
}
