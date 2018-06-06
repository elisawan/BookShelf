package com.afec.bookshelf;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import com.afec.bookshelf.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ShowUser extends Fragment {

    ImageView immagineUtente;
    TextView nomeUtente, bioUtente, lentBookCount, borrowedBookCount;
    RatingBar ratingBar;
    Dialog builder;
    String uid;
    User currentUser;
    FirebaseDatabase database;
    View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_show_user, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return false;
            }
        });

        //Mettere tutte le inizializzazioni qui in config
        config();

        return v;
    }

    public void publicationQuickView(){
        View view = getLayoutInflater().inflate( R.layout.inflater_immagine_profilo, null);

        builder = new Dialog(getContext());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        builder.show();
    }

    public void config(){

        setHasOptionsMenu(true);

        immagineUtente = (ImageView) v.findViewById(R.id.immagineUtente);
        //percepisce il tap lungo
        immagineUtente.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                publicationQuickView();
                return true;
            }
        });
        nomeUtente = (TextView) v.findViewById(R.id.nomeUtente);
        bioUtente = (TextView) v.findViewById(R.id.bioUtente);

        lentBookCount = (TextView) v.findViewById(R.id.lent_book_count);
        borrowedBookCount = (TextView) v.findViewById(R.id.borrowed_book_count);
        ratingBar = (RatingBar) v.findViewById(R.id.ratingUser);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUser = new User();
        if(!currentUser.getUserLocalData(getContext(),uid)){
            currentUser=null;
        }else{
            updateViewContent(currentUser);
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef;

        //listener for capturing changes on user attributes
        userRef= database.getReference("users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                User newUser = dataSnapshot.getValue(User.class);
                if((currentUser==null || newUser.getTimestamp()>currentUser.getTimestamp())) {
                    currentUser=newUser;
                    updateViewContent(newUser);
                    newUser.updateSharedPrefContent(getContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //listener for capturing profile image changes
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uid + "/profilePic.png");
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.showProfileMenu,true);
        menu.setGroupVisible(R.id.defaultMenu,false);
    }

    private void updateViewContent(User u){
        lentBookCount.setText(String.valueOf(u.getLentBooks()));
        borrowedBookCount.setText(String.valueOf(u.getBorrowedBooks()));
        ratingBar.setRating(u.getRating());
        nomeUtente.setText(String.valueOf(u.getUsername()));
        bioUtente.setText(String.valueOf(u.getBiography()));
    }
}
