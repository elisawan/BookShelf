package com.afec.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

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
import com.squareup.picasso.Picasso;

public class MyBookActivity extends BaseActivity {

    private Toolbar myToolbar;
    private TextView tv_title, tv_author, tv_publisher, tv_ed_year, tv_isbn, L;
    private ImageView iv_book;
    private Spinner S;
    private int status;
    private Button locButton, delButton;
    private CheckBox avCheckbox;
    FirebaseDatabase db;
    FirebaseUser currentUser;
    DatabaseReference item;
    String loc;
    String instance;
    MyLocation myLocation;
    Location location;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_book);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        tv_author = (TextView) findViewById(R.id.book_autor);
        tv_ed_year = (TextView) findViewById(R.id.book_year_edition);
        tv_isbn = (TextView) findViewById(R.id.book_isbn);
        tv_publisher = (TextView) findViewById(R.id.book_publisher);
        tv_title = (TextView) findViewById(R.id.book_title);
        iv_book = (ImageView) findViewById(R.id.book_image);
        S = (Spinner) findViewById(R.id.status_spinner);
        status = 0;
        locButton = (Button) findViewById(R.id.editLocationButton);
        delButton = (Button) findViewById(R.id.DeleteButton);
        L= (TextView) findViewById(R.id.location);
        avCheckbox = (CheckBox) findViewById(R.id.availableCheckbox);
        loc=null;


        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();



        Bundle b = getIntent().getExtras();
        if(b == null){
            Toast.makeText(MyBookActivity.this,"ISBN not valid",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), BookList.class);
            startActivity(intent);
        }

        String isbn = b.getString("isbn", null);
        instance= b.getString("istance");

        if (isbn != null && isbn.length()==13) {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(isbn);
            DatabaseReference bookInstanceRef= FirebaseDatabase.getInstance().getReference("book_instances").child(instance);

            bookRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    Book b = dataSnapshot.getValue(Book.class);
                    tv_title.setText(b.getTitle());
                    tv_author.setText(b.getAllAuthors());
                    tv_ed_year.setText(b.getEditionYear());
                    if(b.getPublisher()==null){
                        tv_publisher.setText(R.string.unknown);
                    }else {
                        tv_publisher.setText(b.getPublisher());
                    }
                    tv_isbn.setText(b.getIsbn());

                    Picasso.with(MyBookActivity.this).load(b.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                            .into(iv_book);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            bookInstanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    BookInstance bi = dataSnapshot.getValue(BookInstance.class);
                    status=bi.getStatus();
                    S.setSelection(status);
                    loc=bi.getLocation().toString();
                    L.setText(loc);
                    avCheckbox.setChecked(bi.getAvailability());
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(MyBookActivity.this,"Book not found",Toast.LENGTH_SHORT).show();
        }

        delButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //First remove the book in mybooks of the user
                        item = db.getReference("users").child(currentUser.getUid()).child("myBooks").child(instance);
                        item.setValue(null);

                        //Then remove it from book_instances
                        item = db.getReference("book_instances").child(instance);
                        item.setValue(null);

                        //Go back to BookList activity
                        Intent intent = new Intent(getApplicationContext(),BookList.class);
                        startActivity(intent);
                    }
                }
        );

        locButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(MyBookActivity.this, new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
                        }
                        LocationManager lm = (LocationManager)getSystemService(Context.LOCATION_SERVICE);
                        location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        item = db.getReference("users").child(currentUser.getUid()).child("myBooks").child(instance).child("location");
                        item.setValue(location);
                    }
                }
        );

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }

}
