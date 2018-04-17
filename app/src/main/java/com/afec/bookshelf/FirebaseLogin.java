package com.afec.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.firebase.ui.auth.ErrorCodes;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;


public class FirebaseLogin extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            // already signed in
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
                Intent intent = new Intent(getBaseContext(), ShowUser.class);
                startActivity(intent);
                finish();
            } else {
                // Sign in failed
                if (response == null) {
                    // User pressed back button
                    Toast.makeText(getApplicationContext(),"Operazione annullata",Toast.LENGTH_LONG);
                    return;
                }

                if (response.getError().getErrorCode() == ErrorCodes.NO_NETWORK) {
                    Toast.makeText(getApplicationContext(),"Nessuna connessione a internet",Toast.LENGTH_LONG);
                    return;
                }

                Toast.makeText(getApplicationContext(),"Errore sconosciuto", Toast.LENGTH_LONG);
                Log.e("Sign-in error: ", response.getError().toString());
            }
        }
    }


}
