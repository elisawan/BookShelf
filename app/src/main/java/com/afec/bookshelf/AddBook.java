package com.afec.bookshelf;

import android.*;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.util.Log;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.widget.Toolbar;

import com.afec.bookshelf.Models.BookInstance;
import com.google.firebase.FirebaseError;
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
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddBook extends BaseActivity implements ZXingScannerView.ResultHandler{

    ImageButton ib;
    EditText ISBN_reader, edit_location;
    Button ISBN_scan_button, Locate_button, confirm_button ;
    TextView ISBN_show, book_title, book_author, status_bar, location_bar;
    Book newBook;
    Spinner statusSpinner;
    Toolbar myToolbar;
    ZXingScannerView scannerView;

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

        ib = (ImageButton) findViewById(R.id.ib);
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

        newBook = new Book();

        confirm_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(newBook!=null) {
                    newBook.setStatus((int)statusSpinner.getSelectedItemId());
                    addToDatabase();
                }else {
                    Snackbar mySnackbar = Snackbar.make(findViewById(R.id.confirm_button),
                            "Nessun libro selezionato", Snackbar.LENGTH_SHORT);
                    mySnackbar.show();
                }
                //TODO: add book instance to Firebase (location, status)
                Intent intent = new Intent(getApplicationContext(),BookList.class);
                startActivity(intent);
            }
        });


        Bundle b = getIntent().getExtras();
        if(b != null) {
            String isbn = b.getString("isbn", null);
            if (isbn != null) {
                ISBN_show.setText(isbn);
                newBook.setIsbn(isbn);
                url = url+isbn;
                isbnHttpRequest();
            }
        }

        ISBN_reader.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEND) {
                    String isbn = ISBN_reader.getText().toString();
                    if (isbn != null && isbn.length()==13) {
                        ISBN_show.setText(isbn);
                        newBook.setIsbn(isbn);
                        url = url+isbn;
                        isbnHttpRequest();
                    }
                    else
                    {
                        Toast.makeText(AddBook.this,"ISBN must be 13 char long",Toast.LENGTH_SHORT).show();
                        return false;
                    }
                    return true;
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
                        readBookDetails(stream);
                        setViews();
                        } catch (IOException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e("Response error", "That didn't work!");

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
    }

    public void setBookImage(){
        Picasso.with(this).load(newBook.getThumbnailUrl()).noPlaceholder()
                .resize(300, 550)
                .into(ib);
    }

    public void readBookDetails (InputStream is) throws IOException{
        String name;
        if(is==null)
        {
            throw new NullPointerException();
        }
        JsonReader jr = new JsonReader(new InputStreamReader(is, "UTF-8"));
        jr.beginObject();
        while(jr.hasNext()){
            name = jr.nextName();
            Log.d("name",name);
            if(name.equals("items")){
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
                                } else if (name.equals("authors")) {
                                    jr.beginArray();
                                    String author = "";
                                    while (jr.hasNext()) {
                                        author = author + jr.nextString();
                                    }
                                    jr.endArray();
                                    newBook.setAuthor(author);
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
        book_author.setText(newBook.getAuthor());
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
        bookInstanceRef.push().setValue(new BookInstance(newBook.getIsbn(),newBook.getLocation(), user.getUid(), newBook.getStatus()));

        //Aggiornamento inserimento libro
        final DatabaseReference userRef = database.getReference("users").child(user.getUid()).child("addedBooks");
        userRef.addListenerForSingleValueEvent(new ValueEventListener()  {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                long value =(long) dataSnapshot.getValue();
                value = value + 1;
                userRef.setValue(value);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("db error report: ", databaseError.getDetails());
            }


        });


    }

    public void getAddress(){
        Geocoder geoCoder = new Geocoder(this, Locale.getDefault()); //it is Geocoder
        StringBuilder builder = new StringBuilder();
        String streetAddress;

        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();

        newBook.setLocation(latitude, longitude);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.removeItem(R.id.action_add_book);
        return true;
    }

}

