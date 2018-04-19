package com.afec.bookshelf;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.view.View;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class AddBook extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ImageButton ib;
    EditText ISBN_reader, edit_location;
    Button ISBN_scan_button, Locate_button, confirm_button ;
    TextView ISBN_show, book_title, book_author, status_bar, location_bar;

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

        ib = (ImageButton) findViewById(R.id.ib);
        ISBN_reader = (EditText) findViewById(R.id.ISBN_reader);
        edit_location = (EditText) findViewById(R.id.edit_location);
        ISBN_scan_button = (Button)  findViewById(R.id.ISBN_scan_button);
        Locate_button = (Button)  findViewById(R.id.Locate_button);
        confirm_button = (Button) findViewById(R.id.confirm_button);

        ISBN_show = (TextView) findViewById(R.id.textView4);
        book_title = (TextView) findViewById(R.id.textView2);
        book_author = (TextView) findViewById(R.id.textView3);
        status_bar = (TextView) findViewById(R.id.textView5);
        location_bar = (TextView) findViewById(R.id.location_bar);

        Bundle b = getIntent().getExtras();
        if(b != null) {
            String isbn = b.getString("isbn", null);
            if (isbn != null) {
                ISBN_show.setText(isbn);
                url = url+isbn;
                isbnHttpRequest();
            }
        }
    }

    public void isbnHttpRequest() {
        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        Log.e("Response", "Response is: " + response.substring(0, 500));
                        InputStream stream = new ByteArrayInputStream(response.getBytes());
                        try {
                            readBookDetails(stream);
                        } catch (IOException e) {

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("Response error", "That didn't work!");
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

    public void setBookImage(String url){
        Picasso.with(this).load(url).noPlaceholder()
                .resize(300, 550)
                .into(ib);
    }

    public void readBookDetails (InputStream is) throws IOException{
        String name;
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
                                    book_title.setText(jr.nextString());
                                } else if (name.equals("authors")) {
                                    jr.beginArray();
                                    String author = "";
                                    while (jr.hasNext()) {
                                        author = author + jr.nextString();
                                    }
                                    jr.endArray();
                                    book_author.setText(author);
                                } else if(name.equals("imageLinks")){
                                    jr.beginObject();
                                    while(jr.hasNext()){
                                        name = jr.nextName();
                                        Log.d("name", name);
                                        if(name.equals("thumbnail")){
                                            setBookImage(jr.nextString());
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
}
