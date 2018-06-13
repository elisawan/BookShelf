package com.afec.bookshelf;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.BookInstance;
import com.afec.bookshelf.Models.MyLocation;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.algolia.search.saas.Client;
import com.algolia.search.saas.Index;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class AddBook extends Fragment {

    ImageView ib;
    EditText edit_location;
    Button ISBN_scan_button, Locate_button, confirm_button ;
    TextView ISBN_show, book_title, book_author, status_bar, location_bar;
    Book newBook;
    Spinner statusSpinner;
    String isbn;
    Location location;
    MyLocation myLocation;
    long status;
    String currentDateTime;
    private FirebaseAnalytics mFirebaseAnalytics;

    // Algolia: add new added book to algolia
    Client client;
    Index index;

    //Web Call
    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(getContext());

        View v = inflater.inflate(R.layout.activity_add_book, container, false);

        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.CAMERA},2);
        }

        if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {Manifest.permission.ACCESS_FINE_LOCATION},2);
        }

        ib = (ImageView) v.findViewById(R.id.ib);
        final EditText ISBN_reader = (EditText) v.findViewById(R.id.ISBN_reader);
        edit_location = (EditText) v.findViewById(R.id.edit_location);
        ISBN_scan_button = (Button)  v.findViewById(R.id.ISBN_scan_button);
        Locate_button = (Button)  v.findViewById(R.id.Locate_button);
        confirm_button = (Button) v.findViewById(R.id.confirm_button);
        statusSpinner = (Spinner) v.findViewById(R.id.status_spinner);

        ISBN_show = (TextView) v.findViewById(R.id.textView4);
        book_title = (TextView) v.findViewById(R.id.textView2);
        book_author = (TextView) v.findViewById(R.id.textView3);
        status_bar = (TextView) v.findViewById(R.id.textView5);
        location_bar = (TextView) v.findViewById(R.id.location_bar);

        ISBN_scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment newFragment = new IsbnScanner();
                // Create new fragment and transaction
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.content_frame, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });
        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newBook!=null){
                    status = statusSpinner.getSelectedItemId();
                    if(status==0){
                        Toast.makeText(getActivity(),R.string.book_condition_toast,Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        currentDateTime = dateFormat.format(new Date());
                        getAddress();

                        //--Add to Firebase--
                        addToDatabase();

                        //--Go to Book List
                        Fragment newFragment = new BookList();
                        // Create new fragment and transaction
                        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                        // Replace whatever is in the fragment_container view with this fragment,
                        // and add the transaction to the back stack
                        transaction.replace(R.id.content_frame, newFragment);
                        transaction.addToBackStack(null);
                        // Commit the transaction
                        transaction.commit();
                    }
                }else {
                    Toast.makeText(getActivity(),R.string.scan_a_book_toast,Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle b = getArguments();
        if(b != null) {
            isbn = b.getString("isbn", null);
            if (isbn != null && isbn.length()==13) {
                url = url+isbn;
                isbnHttpRequest();
            }
            else
            {
                Toast.makeText(getActivity(),R.string.no_valid_ISBN_toast,Toast.LENGTH_SHORT).show();
            }
        }

        ISBN_reader.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    isbn = ISBN_reader.getText().toString();
                    if (isbn != null && isbn.length()==13) {
                        url = url+isbn;
                        isbnHttpRequest();
                        return true;
                    }
                    else
                    {
                        Toast.makeText(getActivity(),R.string.ISBN_must_be_13_toast,Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });
        return v;
    }

    public void isbnHttpRequest() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            // Display the first 500 characters of the response string.
                            //Log.d("Response", "Response is: " + response.substring(0, 500));
                            InputStream stream = new ByteArrayInputStream(response.getBytes());
                            newBook = new Book();
                            readBookDetails(stream);
                            ISBN_show.setText(isbn);
                            newBook.setIsbn(isbn);
                            setViews();
                        } catch (bookNotFound e) {
                            Toast.makeText(getActivity(),R.string.ISBN_not_found_toast,Toast.LENGTH_SHORT).show();
                            newBook = null;
                        }catch (IOException e){
                            Log.e("error", e.getMessage());
                            newBook = null;
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Response error", "That didn't work!");
                Toast.makeText(getActivity(),R.string.Network_error,Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    public void setBookImage(){
        Picasso.with(getActivity()).load(newBook.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                .into(ib);
    }

    public void readBookDetails (InputStream is) throws bookNotFound, IOException{
        String name;
        JsonReader jr = new JsonReader(new InputStreamReader(is));

        jr.beginObject();
        while(jr.hasNext()){
            name = jr.nextName();
            Log.d("name",name);
            if(name.equals("totalItems")){
                Long count = jr.nextLong();
                Log.e("count",count.toString());
                if(count==0){
                    throw new bookNotFound();
                }
            }else if(name.equals("items")){
                Log.d("id","items");
                jr.beginArray();
                while(jr.hasNext()){
                    jr.beginObject();
                    while(jr.hasNext()){
                        name = jr.nextName();
                        Log.d("name",name);
                        if(name.equals("volumeInfo")){
                            jr.beginObject();
                            while(jr.hasNext()) {
                                name = jr.nextName();
                                Log.d("name", name);
                                if (name.equals("title")) {
                                    newBook.setTitle(jr.nextString());
                                }else if(name.equals("publisher")){
                                    newBook.setPublisher(jr.nextString());
                                }
                                else if(name.equals("publishedDate")){
                                    newBook.setEditionYear(jr.nextString());
                                }
                                else if (name.equals("authors")) {
                                    jr.beginArray();
                                    while (jr.hasNext()) {
                                        newBook.setAuthor(jr.nextString());
                                    }
                                    jr.endArray();
                                } else if(name.equals("imageLinks")){
                                    jr.beginObject();
                                    while(jr.hasNext()){
                                        name = jr.nextName();
                                        Log.d("name", name);
                                        if(name.equals("thumbnail")){
                                            newBook.setThumbnailUrl(jr.nextString());
                                        }else{
                                            jr.skipValue();
                                        }
                                    }
                                    jr.endObject();
                                } else {
                                    jr.skipValue();
                                }
                            }
                            jr.endObject();
                        }else{
                            jr.skipValue();
                        }
                    }
                    jr.endObject();
                }
                jr.endArray();
            }else{
                jr.skipValue();
            }
        }
        jr.endObject();
    }

    public void setViews(){

        book_author.setText(newBook.getAllAuthors());
        book_title.setText(newBook.getTitle());
        setBookImage();
    }

    public void addToDatabase(){
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("user", user.toString());

        //--insert new book in firebase--
        //-insert only if this book is not already present-
        DatabaseReference bookRef = database.getReference("books");
        bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.child(newBook.getIsbn()).exists()){
                    DatabaseReference bookRef = database.getReference("books").child(newBook.getIsbn());
                    bookRef.setValue(newBook);
                }

                //--Geofire
                DatabaseReference locRef = database.getReference("geofire");
                GeoFire geoFire = new GeoFire(locRef);

                //Inserimento in book_instances
                DatabaseReference bookInstanceRef = database.getReference("book_instances");
                String bookId = bookInstanceRef.push().getKey();
                bookInstanceRef.child(bookId).setValue(new BookInstance(newBook.getIsbn(), myLocation, user.getUid(), (int) status, currentDateTime, true));

                geoFire.setLocation(bookId, new GeoLocation(myLocation.getLatitude(), myLocation.getLongitude()), new GeoFire.CompletionListener() {
                    @Override
                    public void onComplete(String key, DatabaseError error) {
                        if (error != null) {
                            Toast.makeText(getActivity(),R.string.Geofire_error,Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(),R.string.Geofire_success,Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                // Add new owner to book
                DatabaseReference ownersRef = database.getReference("books").child(newBook.getIsbn()).child("owners").child(bookId);
                ownersRef.setValue(user.getUid());

                //Aggiornamento inserimento libro: update addedBooks count
                final DatabaseReference userRef = database.getReference("users").child(user.getUid()).child("addedBooks");
                userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        long value =(long) dataSnapshot.getValue(Long.class);
                        value = value + 1;
                        userRef.setValue(value);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        Log.e("db error report: ", databaseError.getDetails());
                    }
                });

                //Aggiornamento lista myBooks
                database.getReference("users").child(user.getUid()).child("myBooks").child(bookId).setValue(newBook.getIsbn());

                //Aggiungo un credito sociale per l'inserimento
                final DatabaseReference creditRef = database.getReference().child("users").child(user.getUid());
                creditRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {
                        if(mutableData.child("credit")==null) {
                            mutableData.child("credit").setValue(1);
                        }else{
                            Integer count = mutableData.child("credit").getValue(Integer.class);
                            mutableData.child("credit").setValue(count+1);
                        }
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

                Bundle bundle = new Bundle();
                bundle.putString(FirebaseAnalytics.Param.CONTENT_TYPE, "book");
                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE, bundle);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //--Add book in Algolia
        client = new Client("BDPR8QJ6ZZ", "57b47a26838971583fcb026954731774");
        index = client.getIndex("bookShelf");
        JSONObject obj = new JSONObject();
        try{
            obj.put("objectID",newBook.getIsbn());
            obj.put("title", newBook.getTitle());
            obj.put("authors", newBook.getAuthors());
            obj.put("isbn", newBook.getIsbn());
            obj.put("thumbnailUrl", newBook.getThumbnailUrl());
            if(newBook.getPublisher()!=null){
                obj.put("publisher", newBook.getPublisher());
            }
        }catch (JSONException e){

        }
        index.addObjectAsync(obj,newBook.getIsbn(),null,null);
    }

    public void getAddress(){
        Geocoder geoCoder = new Geocoder(getActivity(), Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        String streetAddress;

        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
        }
        LocationManager lm= (LocationManager) getActivity().getSystemService(getContext().LOCATION_SERVICE);

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

class bookNotFound extends Exception{}

