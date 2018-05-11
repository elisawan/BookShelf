package com.afec.bookshelf;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.BookInstance;
import com.afec.bookshelf.Models.MyLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryDataEventListener;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    // Menus
    Toolbar myToolbar;
    private DrawerLayout mDrawerLayout;
    Menu menu;
    TextView drawerUsername, drawerEmail;
    LinearLayout drawerLayout;

    // Firebase
    FirebaseDatabase db;
    private FirebaseUser user;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);
            mySearch(q);
        }else{
            Fragment startFragment = new NearBooks();
            myStartFragment(startFragment);
        }

        // Toolbar initialization
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
        actionbar.setTitle(Html.fromHtml("<font color='#ffffff'>Book Hook</font>"));
        actionbar.setHomeAsUpIndicator(R.drawable.ic_menu);

        // Navigation drawer header
        View header = ((NavigationView)findViewById(R.id.nav_view)).getHeaderView(0);
        mDrawerLayout = findViewById(R.id.main_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
            new NavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(MenuItem menuItem) {
                    // set item as selected to persist highlight
                    menuItem.setChecked(true);
                    // close drawer when item is tapped
                    mDrawerLayout.closeDrawers();

                    // Add code here to update the UI based on the item selected
                    // For example, swap UI fragments here
                    int id = menuItem.getItemId();
                    switch (id){
                        case R.id.action_show_main_activity:
                            Intent intent = new Intent(MainActivity.this, FirebaseLogin.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                            break;
                        case R.id.action_show_profile:
                            myStartFragment(new ShowUser());
                            break;
                        case R.id.action_my_books_list:
                            myStartFragment(new BookList());
                            break;
                        case R.id.action_logout:
                            Logout();
                            break;
                    }
                    return true;
                }
            });
        drawerUsername= (TextView) header.findViewById(R.id.sidebar_username);
        drawerEmail = (TextView) header.findViewById(R.id.sidebar_mail);
        drawerLayout = (LinearLayout) findViewById(R.id.drawer_layout);

        // Firebase
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        // Fill navigation drawer view
        drawerEmail.setText(user.getEmail());
        userRef = db.getReference("users").child(user.getUid()).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    drawerUsername.setText(String.valueOf(userName));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }
        });

        StorageReference mImageRef =
                FirebaseStorage.getInstance().getReference(user.getUid() + "/profilePic.png");
        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getApplicationContext()).load(uri.toString()).noPlaceholder().into((ImageView) findViewById(R.id.immagineUtente));

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERRORE RECUPERO IMG: ", exception.getMessage().toString());
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        MenuInflater inflater = getMenuInflater();
        // Inflate menu to add items to action bar if it is present.
        inflater.inflate(R.menu.toolbar, menu);
        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView =
                (SearchView) menu.findItem(R.id.menu_search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }
            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        menu.setGroupVisible(R.id.defaultMenu, true);
        menu.setGroupVisible(R.id.showProfileMenu, false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            case R.id.action_edit_profile:
                myStartFragment(new EditUser());
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void myStartFragment(Fragment newFragment){
        // Create new fragment and transaction
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }

    private void Logout(){
        //Display dialog box
        AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
        builder1.setMessage(R.string.Logout_permission);
        builder1.setCancelable(true);
        builder1.setPositiveButton(
                R.string.Affirmative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        AuthUI.getInstance()
                                .signOut(MainActivity.this)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    public void onComplete(@NonNull Task<Void> task) {
                                        // user is now signed out
                                        Intent intent = new Intent(MainActivity.this, FirebaseLogin.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                        dialog.cancel();
                    }
                });
        builder1.setNegativeButton(
                R.string.Negative,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert11 = builder1.create();
        alert11.show();
    }

    public void mySearch(String q){
        Bundle b = new Bundle();
        b.putString("query",q);
        Fragment newFragment = new SearchBooks();
        newFragment.setArguments(b);
        myStartFragment(newFragment);
    }
}

