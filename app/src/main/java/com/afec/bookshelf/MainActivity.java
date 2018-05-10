package com.afec.bookshelf;

import android.app.AlertDialog;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static java.security.AccessController.getContext;

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

    // Others
    GridView gv;
    List<BookInstance> foundBooks;
    GeoFire geoFire;
    private MyLocation myLocation;
    private Location location;
    List<Book> booksList;
    List<String> myBooksInstances;
    SeekBar SB;
    TextView r_display, title;
    String isbn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        username= (TextView) header.findViewById(R.id.sidebar_username);
        email = (TextView) header.findViewById(R.id.sidebar_mail);

        // Firebase
        db = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        //if(user!=null)
            user.getUid();
        //else
        //    finish();

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

        // Create the grid view with nearest books
        title = (TextView) findViewById(R.id.textView7);
        SB = findViewById(R.id.seekBar);
        gv = (GridView) findViewById(R.id.List_of_book_found);
        foundBooks = new ArrayList<BookInstance>();
        r_display = (TextView) findViewById(R.id.textView6);
        booksList = new ArrayList<Book>();
        myBooksInstances = new ArrayList<String>();

        title.setText(R.string.title_book_around_you);
        //populate the grid view with a default radius of 10 km
        r_display.setText("10 km");
        find_near_book(10);

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = new MyBookActivity();
                Bundle b = new Bundle();
                b.putString("isbn", booksList.get(position).getIsbn());
                b.putString("instance", myBooksInstances.get(position));
                newFragment.setArguments(b);
                myStartFragment(newFragment);
            }
        });

        SB.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                String s = seekBar.getProgress() + " km";
                r_display.setText(s);
                find_near_book(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,boolean fromUser) {

            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = new ShowBook();
                Bundle b = new Bundle();
                b.putString("isbn", booksList.get(position).getIsbn());
                newFragment.setArguments(b);
                myStartFragment(newFragment);
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

    public void getAddress(){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        String streetAddress;

        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        LocationManager lm= (LocationManager) getSystemService(LOCATION_SERVICE);

        lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, mLocationListener);

        myLocation= new MyLocation();

        location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if(location==null) {
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,0,0, this.mLocationListener);
            location = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        }
        else{
            myLocation.setLatitude(location.getLatitude());
            myLocation.setLongitude(location.getLongitude());
        }

    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            //your code here
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void find_near_book(int radius){
        final DatabaseReference bookInstances = db.getReference().child("geofire");
        final GeoFire geoFire = new GeoFire(bookInstances);

        booksList.clear();

        getAddress();

        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(myLocation.getLatitude(), myLocation.getLongitude()), radius);

        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {
                // get book isbn and bookInstance
                final String instance = dataSnapshot.getKey();
                // get book

                DatabaseReference book_inst_Ref = db.getReference("book_instances").child(instance);

                 book_inst_Ref.addValueEventListener(new ValueEventListener() {
                     @Override
                     public void onDataChange(DataSnapshot dataSnapshot) {
                         isbn = dataSnapshot.child("isbn").getValue(String.class);
                         myBooksInstances.add(dataSnapshot.getKey());

                         DatabaseReference bookRef = db.getReference("books").child(isbn);

                         bookRef.addValueEventListener(new ValueEventListener() {

                             @Override
                             public void onDataChange(DataSnapshot dataSnapshot) {
                                 Book b = dataSnapshot.getValue(Book.class);
                                 booksList.add(b);

                                 // show on view

                                 gv.setAdapter(new BaseAdapter() {

                                     @Override
                                     public int getCount() {
                                         return booksList.size();
                                     }

                                     @Override
                                     public Object getItem(int position) {
                                         return booksList.get(position);
                                     }

                                     @Override
                                     public long getItemId(int position) {
                                         return position;
                                     }

                                     @Override
                                     public View getView(int position, View convertView, ViewGroup parent) {
                                         if (convertView==null && !booksList.isEmpty()) {
                                             convertView = getLayoutInflater().inflate(R.layout.book_preview, parent,false);
                                         }
                                         ImageView iv = (ImageView) convertView.findViewById(R.id.book_image_preview);
                                         Picasso.with(MainActivity.this).load(booksList.get(position).getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                                                 .into(iv);
                                         TextView title_tv =(TextView) convertView.findViewById(R.id.book_title_preview);
                                         title_tv.setText(booksList.get(position).getTitle());
                                         TextView author_tv = (TextView) convertView.findViewById(R.id.book_autor_preview);
                                         author_tv.setText(booksList.get(position).getAllAuthors());
                                         return convertView;
                                     }
                                 });

                             }
                             @Override
                             public void onCancelled(DatabaseError databaseError) {
                             }
                         });

                     }

                     @Override
                     public void onCancelled(DatabaseError databaseError) {

                     }
                 });


            }

            @Override
            public void onDataExited(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onDataMoved(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onDataChanged(DataSnapshot dataSnapshot, GeoLocation location) {
                // ...
            }

            @Override
            public void onGeoQueryReady() {

            }

            @Override
            public void onGeoQueryError(DatabaseError error) {
                Toast.makeText(MainActivity.this,"An error loading the nearest Book occurred!",Toast.LENGTH_SHORT).show();
            }

        });
    }


}

