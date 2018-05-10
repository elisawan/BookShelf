package com.afec.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.afec.bookshelf.Models.User;
import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;


public class FirebaseLogin extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;
    private FirebaseAuth auth;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            user = auth.getCurrentUser();

            final DatabaseReference userRef = database.getReference("users");
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (!snapshot.child(user.getUid()).exists()) {
                        userRef.child(user.getUid())
                                .setValue(new User(user.getUid(),0, null, 0,0,0, user.getDisplayName()));
                    }else{

                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            Intent intent = new Intent(getBaseContext(), MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // not signed in

            // Get an instance of AuthUI based on the default app
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setAvailableProviders(Arrays.asList(
                                    new AuthUI.IdpConfig.EmailBuilder().build(),
                                    new AuthUI.IdpConfig.GoogleBuilder().build()))
                            .setLogo(R.mipmap.ic_launcher)
                            .build(), RC_SIGN_IN);
        }

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        // RC_SIGN_IN is the request code you passed into startActivityForResult(...) when starting the sign in flow.
        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            // Successfully signed in
            if (resultCode == RESULT_OK) {

                //Check if user already exists in db, otherwise add
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                user = auth.getCurrentUser();

                final DatabaseReference userRef = database.getReference("users");
                userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        if (!snapshot.child(user.getUid()).exists()) {
                            userRef.child(user.getUid())
                                    .setValue(new User(user.getUid(),0, null, 0,0,0, user.getDisplayName()));
                        }else{

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });



                Intent intent = new Intent(getBaseContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(getApplicationContext(),"Operation aborted",Toast.LENGTH_LONG);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(),"No internet connection",Toast.LENGTH_LONG);
                    return;
                }

                Toast.makeText(getApplicationContext(),"Unknown error", Toast.LENGTH_LONG);
                Log.e("Sign-in error: ", response.getError().toString());
            }
        }
    }


}