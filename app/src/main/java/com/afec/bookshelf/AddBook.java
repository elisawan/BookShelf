package com.afec.bookshelf;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.Result;

import org.w3c.dom.Text;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import static java.lang.System.in;

public class AddBook extends AppCompatActivity implements ZXingScannerView.ResultHandler{

    ImageButton ib;
    EditText ISBN_reader, edit_location;
    Button ISBN_scan_button, Locate_button, confirm_button ;
    TextView ISBN_show, book_title, book_author, status_bar, location_bar;
    ZXingScannerView scannerView;

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
                ISBN_reader.setText(isbn);
            }
        }


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
}
