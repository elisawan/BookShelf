package com.afec.bookshelf;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.algolia.search.saas.AlgoliaException;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.CompletionHandler;
import com.algolia.search.saas.Index;
import com.algolia.search.saas.Query;
import com.algolia.search.saas.Searchable;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

    // Menus
    Toolbar myToolbar;
    private DrawerLayout mDrawerLayout;
    Menu menu;
    TextView username, email;

    // Firebase
    FirebaseDatabase db;
    private FirebaseUser user;
    DatabaseReference userRef;

    // Algolia search
    Client client;
    Query query;
    Index index;
    SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Algolia setup
        client = new Client("BDPR8QJ6ZZ", "57b47a26838971583fcb026954731774");
        query = new Query();
        query.setAttributesToRetrieve("title","authors");
        query.setHitsPerPage(10);
        index = client.getIndex("bookShelf");

        // Get the intent, verify the action and get the query
        Intent intent = getIntent();
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String q = intent.getStringExtra(SearchManager.QUERY);
            mySearch(q);
        }

        // Toolbar initialization
        myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setDisplayHomeAsUpEnabled(true);
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
        username= (TextView) header.findViewById(R.id.sidebar_username);
        email = (TextView) header.findViewById(R.id.sidebar_mail);

        // Firebase
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        user.getUid();

        // Fill navigation drawer view
        email.setText(user.getEmail());
        userRef = db.getReference("users").child(user.getUid()).child("username");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists()) {
                    String userName = dataSnapshot.getValue(String.class);
                    username.setText(String.valueOf(userName));
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
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
                                        startActivity(intent);
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
        query.setQuery(q);
        index.searchAsync(query, new CompletionHandler() {
            @Override
            public void requestCompleted(JSONObject jsonObject, AlgoliaException e) {
                Log.d("msg","requestCompleted");
                Log.d("msg",jsonObject.toString());
                
            }
        });
    }
}


