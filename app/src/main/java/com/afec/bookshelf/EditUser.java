package com.afec.bookshelf;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

public class EditUser extends AppCompatActivity {

    ImageView immagineUtente;
    EditText nomeUtente, emailUtente, bioUtente;
    Button b;
    CheckBox email_cb, whatsapp_cb, call_cb;
    ImageButton ib;
    AlertDialog.Builder alert;
    SharedPreferences sharedPref;
    public static final int PICK_IMAGE = 1;
    public static final int SNAP_PIC = 2;
    private Uri mUri;
    private Bitmap mPhoto;
    String mCurrentPhotoPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.show_toolbar);
        setSupportActionBar(myToolbar);

        if(ContextCompat.checkSelfPermission(EditUser.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(EditUser.this, new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }

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

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PopupMenu popupMenu = new PopupMenu(EditUser.this, immagineUtente);
                popupMenu.getMenuInflater().inflate(R.menu.picture_popup_menu, popupMenu.getMenu());
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        Toast.makeText(EditUser.this,"You Clicked : " + menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                        if(menuItem.getTitle().equals(getString(R.string.camera))){
                            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            // Ensure that there's a camera activity to handle the intent
                            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                // Create the File where the photo should go
                                File photoFile = null;
                                try {
                                    photoFile = createImageFile();
                                } catch (IOException ex) {
                                    // Error occurred while creating the File
                                }
                                // Continue only if the File was successfully created
                                if (photoFile != null) {
                                    Uri photoURI = FileProvider.getUriForFile(EditUser.this,
                                            "com.afec.bookshelf.fileprovider",
                                            photoFile);
                                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                    startActivityForResult(takePictureIntent, SNAP_PIC);
                                }
                            }


                        }else{
                            Intent intent = new Intent();
                            intent.setType("image/*");
                            intent.setAction(Intent.ACTION_GET_CONTENT);
                            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
                        }
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
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == Activity.RESULT_OK) {
                    try{
                        File outputFile = createImageFile();
                        InputStream inputStream = getContentResolver().openInputStream(data.getData());
                        OutputStream outStream = new FileOutputStream(outputFile);
                        byte[] buf = new byte[1024];
                        int len;
                        while((len=inputStream.read(buf))>0){
                            outStream.write(buf,0,len);
                        }
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("imageUri", mCurrentPhotoPath);
                        editor.commit();

                        Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                        immagineUtente.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SNAP_PIC:
                if (resultCode == RESULT_OK) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("imageUri",mCurrentPhotoPath);
                    editor.commit();
                    Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    immagineUtente.setImageBitmap(bitmap);
                }
                break;
        }
    }


    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }
}
