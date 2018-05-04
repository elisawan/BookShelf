package com.afec.bookshelf;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.BookInstance;
import com.afec.bookshelf.Models.MyLocation;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddBook extends BaseActivity implements ZXingScannerView.ResultHandler{

    ImageView ib;
    EditText ISBN_reader, edit_location;
    Button ISBN_scan_button, Locate_button, confirm_button ;
    TextView ISBN_show, book_title, book_author, status_bar, location_bar;
    Book newBook;
    Spinner statusSpinner;
    Toolbar myToolbar;
    ZXingScannerView scannerView;
    String isbn;
    Location location;
    MyLocation myLocation;
    long status;
    String currentDateTime;

    //Web Call

    String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {android.Manifest.permission.CAMERA},2);
        }

        if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},2);
        }



        ib = (ImageView) findViewById(R.id.ib);
        final EditText ISBN_reader = (EditText) findViewById(R.id.ISBN_reader);
        edit_location = (EditText) findViewById(R.id.edit_location);
        ISBN_scan_button = (Button)  findViewById(R.id.ISBN_scan_button);
        Locate_button = (Button)  findViewById(R.id.Locate_button);
        confirm_button = (Button) findViewById(R.id.confirm_button);
        statusSpinner = (Spinner) findViewById(R.id.status_spinner);

        ISBN_show = (TextView) findViewById(R.id.textView4);
        book_title = (TextView) findViewById(R.id.textView2);
        book_author = (TextView) findViewById(R.id.textView3);
        status_bar = (TextView) findViewById(R.id.textView5);
        location_bar = (TextView) findViewById(R.id.location_bar);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);


        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newBook!=null){
                    status = statusSpinner.getSelectedItemId();
                    if(status==0){
                        Toast.makeText(AddBook.this,"Condition of the book is mandatory",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        currentDateTime = dateFormat.format(new Date());
                        getAddress();
                        addToDatabase();
                        Intent intent = new Intent(getApplicationContext(),BookList.class);
                        startActivity(intent);
                    }
                }else {
                    Toast.makeText(AddBook.this,"Scan a book first!",Toast.LENGTH_SHORT).show();
                }
            }
        });


        Bundle b = getIntent().getExtras();
        if(b != null) {
            isbn = b.getString("isbn", null);
            if (isbn != null && isbn.length()==13) {
                url = url+isbn;
                isbnHttpRequest();
            }
            else
            {
                Toast.makeText(AddBook.this,"No valid ISBN ",Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddBook.this,"ISBN must be 13 characters long",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                }
                return false;
            }
        });
    }



    public void isbnHttpRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
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
                            Toast.makeText(AddBook.this,"ISBN doesn't exists",Toast.LENGTH_SHORT).show();
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
                Toast.makeText(AddBook.this,"Network error",Toast.LENGTH_SHORT).show();
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }
    public void isbn_scan(View v) {
        scannerView = new ZXingScannerView(getApplicationContext());
        setContentView(scannerView);
        scannerView.setResultHandler(this);
        scannerView.startCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(scannerView != null)
            scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {
        String isbn = result.getText();
        //scannerView.resumeCameraPreview(this);
        scannerView.stopCamera();
        Intent intent = new Intent(getApplicationContext(),AddBook.class);
        Bundle b = new Bundle();
        b.putString("isbn",isbn);
        intent.putExtras(b);
        startActivity(intent);
        finish();
    }

    public void setBookImage(){
        Picasso.with(this).load(newBook.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
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
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        Log.d("user", user.toString());
        String uid = user.getUid();

        //Inserimento in books
        DatabaseReference bookRef = database.getReference("books")
                .child(newBook.getIsbn());
        bookRef.setValue(newBook);

        //Inserimento in book_instances
        DatabaseReference bookInstanceRef = database.getReference("book_instances");
        String bookId = bookInstanceRef.push().getKey();
        bookInstanceRef.child(bookId).setValue(new BookInstance(newBook.getIsbn(), myLocation, user.getUid(), (int) status, currentDateTime, true));

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
    }

    public void getAddress(){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        String streetAddress;

        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.removeItem(R.id.action_add_book);
        return true;
    }
}

class bookNotFound extends Exception{}

