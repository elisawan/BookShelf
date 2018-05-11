package com.afec.bookshelf;


import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;


/**
 * A simple {@link Fragment} subclass.
 */
public class NearBooks extends Fragment {

    SeekBar sb;
    GridView gv;
    List<BookInstance> foundBooks;
    private MyLocation myLocation;
    private Location location;
    List<Book> booksList;
    TextView r_display, title;
    String isbn;

    // Firebase
    FirebaseDatabase db;
    FirebaseUser CurrentUser;
    String UID;

    public NearBooks() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_near_books, container, false);

        // Firebase
        db = FirebaseDatabase.getInstance();
        CurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        UID = CurrentUser.getUid();


        // Create the grid view with nearest books
        title = (TextView) v.findViewById(R.id.textView7);
        title.setText(R.string.title_book_around_you);
        sb = (SeekBar) v.findViewById(R.id.seekBar);
        gv = (GridView) v.findViewById(R.id.List_of_book_found);
        r_display = (TextView) v.findViewById(R.id.textView6);
        r_display.setText("10 km");

        foundBooks = new ArrayList<BookInstance>();
        booksList = new ArrayList<Book>();

        //populate the grid view with a default radius of 10 km
        find_near_book(10);

        //adapt the search radius according to user selection
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

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
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content_frame, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });
        return v;
    }

    public void find_near_book(int radius){
        final DatabaseReference bookInstances = db.getReference().child("geofire");
        final GeoFire geoFire = new GeoFire(bookInstances);

        //reset list of found books
        booksList.clear();
        foundBooks.clear();

        //get current location
        getAddress();

        //get near books from GeoFire
        GeoQuery geoQuery = geoFire.queryAtLocation(new GeoLocation(myLocation.getLatitude(), myLocation.getLongitude()), radius);
        geoQuery.addGeoQueryDataEventListener(new GeoQueryDataEventListener() {

            @Override
            public void onDataEntered(DataSnapshot dataSnapshot, GeoLocation location) {

                // get book instance id
                final String instance = dataSnapshot.getKey();

                // get book isbn
                final DatabaseReference book_inst_Ref = db.getReference("book_instances").child(instance);
                book_inst_Ref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        isbn = dataSnapshot.child("isbn").getValue(String.class);
                            final DatabaseReference bookRef = db.getReference("books").child(isbn);
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
                                            if (convertView == null && !booksList.isEmpty()) {
                                                convertView = getLayoutInflater().inflate(R.layout.book_preview, parent, false);

                                                ImageView iv = (ImageView) convertView.findViewById(R.id.book_image_preview);
                                                Picasso.with(getActivity()).load(booksList.get(position).getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                                                        .into(iv);
                                                TextView title_tv = (TextView) convertView.findViewById(R.id.book_title_preview);
                                                title_tv.setText(booksList.get(position).getTitle());
                                                TextView author_tv = (TextView) convertView.findViewById(R.id.book_autor_preview);
                                                author_tv.setText(booksList.get(position).getAllAuthors());
                                            }
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
                Toast.makeText(getActivity(),"An error loading the nearest Book occurred!",Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void getAddress(){
        if(ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        LocationManager lm= (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);

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
}
