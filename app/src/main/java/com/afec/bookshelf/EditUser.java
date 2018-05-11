package com.afec.bookshelf;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class EditUser extends Fragment {

    ImageView immagineUtente;
    EditText nomeUtente, bioUtente;
    Button b;
    CheckBox email_cb, whatsapp_cb, call_cb;
    ImageButton ib;
    SharedPreferences sharedPref;
    public static final int PICK_IMAGE = 1;
    public static final int SNAP_PIC = 2;
    String mCurrentPhotoPath;
    Bitmap bitmap;
    File image;
    private FirebaseUser user;
    FirebaseDatabase database;
    View v;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_edit_user, container, false);

        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.WRITE_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1);
        }
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA},2);
        }

        setHasOptionsMenu(true);

        immagineUtente = (ImageView) v.findViewById(R.id.editImmagineUtente);
        nomeUtente = (EditText) v.findViewById(R.id.editNomeUtente);
        bioUtente = (EditText) v.findViewById(R.id.editBioUtente);
        b = (Button) v.findViewById(R.id.button_edit_confirm);
        ib = (ImageButton) v.findViewById(R.id.edit_profile_image_bt);
        email_cb = (CheckBox) v.findViewById(R.id.email_cb);
        whatsapp_cb = (CheckBox) v.findViewById(R.id.whatsapp_cb);
        call_cb = (CheckBox) v.findViewById(R.id.call_cb);
        sharedPref = getActivity().getSharedPreferences("userPreferences", Context.MODE_PRIVATE);

        /*nomeUtente.setText(sharedPref.getString("nomeUtente", null));
        bioUtente.setText(sharedPref.getString("bioUtente", null));
        email_cb.setChecked(sharedPref.getBoolean("contact_email",false));
        whatsapp_cb.setChecked(sharedPref.getBoolean("contact_whatsapp",false));
        call_cb.setChecked(sharedPref.getBoolean("contact_call",false));

        String immagineSalvata = sharedPref.getString("imageUri", null);
        try{
            Uri uri = Uri.parse(sharedPref.getString("imageUri", null));
            Log.d("uri", uri.toString());
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            immagineUtente.setImageBitmap(bitmap);
        }catch (Exception e){
            Log.d("ex",e.toString());
            if(immagineSalvata!= null && !immagineSalvata.isEmpty()){
                Bitmap bitmap = BitmapFactory.decodeFile(immagineSalvata);
                immagineUtente.setImageBitmap(bitmap);
            }
        }*/

        config();

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            PopupMenu popupMenu = new PopupMenu(getActivity(), immagineUtente);
            popupMenu.getMenuInflater().inflate(R.menu.picture_popup_menu, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {
                    Toast.makeText(getActivity(),"You Clicked : " + menuItem.getTitle(),Toast.LENGTH_SHORT).show();
                    if(menuItem.getTitle().equals(getString(R.string.camera))){
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(getActivity(),
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
              /*  SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", String.valueOf(nomeUtente.getText()));
                editor.putString("bioUtente", String.valueOf(bioUtente.getText()));
                editor.putBoolean("contact_email", email_cb.isChecked());
                editor.putBoolean("contact_whatsapp", whatsapp_cb.isChecked());
                editor.putBoolean("contact_call", call_cb.isChecked());
                editor.commit();
*/
                updateFirebase(String.valueOf(bioUtente.getText()), String.valueOf(nomeUtente.getText()));
                myStartFragment(new ShowUser());
            }
        });
        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode,data);
        switch (requestCode) {
            case PICK_IMAGE:
                if (resultCode == RESULT_OK) {
                    try{
                        File outputFile = createImageFile();
                        InputStream inputStream = getActivity().getContentResolver().openInputStream(data.getData());
                        OutputStream outStream = new FileOutputStream(outputFile);
                        byte[] buf = new byte[1024];
                        int len;
                        while((len=inputStream.read(buf))>0){
                            outStream.write(buf,0,len);
                        }
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("imageUri", mCurrentPhotoPath);
                        editor.commit();

                        bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                        immagineUtente.setImageBitmap(bitmap);
                    } catch (Exception e) {
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
                break;
            case SNAP_PIC:
                if (resultCode == RESULT_OK) {
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("imageUri",mCurrentPhotoPath);
                    editor.commit();
                    bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath);
                    immagineUtente.setImageBitmap(bitmap);
                }
                break;
        }
    }



    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }


    public void updateFirebase(String biografia, String username){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference userRef = database.getReference("users")
                .child(user.getUid())
                .child("biography");

        if(!biografia.isEmpty()) userRef.setValue(biografia);

        if(!username.isEmpty()) userRef.getParent().child("username").setValue(username);

        if(image != null){
            StorageReference mStorageRef = FirebaseStorage.getInstance().getReference(user.getUid()+"/profilePic.png");
            mStorageRef.putFile(Uri.fromFile(image))
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        Toast.makeText(getActivity(), "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                    }
                });
        }
    }

    public void myStartFragment(Fragment newFragment){
        // Create new fragment and transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.defaultMenu,false);
        menu.setGroupVisible(R.id.showProfileMenu,false);
    }

    public void config(){

        user = FirebaseAuth.getInstance().getCurrentUser();
        user.getUid();
        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef;

        userRef = database.getReference("users").child(user.getUid()).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    nomeUtente.setText(String.valueOf(userName));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }
        });

        userRef = database.getReference("users").child(user.getUid()).child("biography");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String biography = dataSnapshot.getValue(String.class);
                    bioUtente.setText(String.valueOf(biography));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }
        });

        final ImageView profileImage = (ImageView) v.findViewById(R.id.immagineUtente);

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference(user.getUid() + "/profilePic.png");
        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).noPlaceholder().into(immagineUtente);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERRORE RECUPERO IMG: ", exception.getMessage().toString());
            }
        });
    }
}
