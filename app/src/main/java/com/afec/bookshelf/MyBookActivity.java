package com.afec.bookshelf;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.afec.bookshelf.Models.BookInstance;
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
    private Button locButton;
    private CheckBox avCheckbox;
    String loc;

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
        L= (TextView) findViewById(R.id.location);
        avCheckbox = (CheckBox) findViewById(R.id.availableCheckbox);
        loc=null;


        Bundle b = getIntent().getExtras();
        if(b == null){
            Toast.makeText(MyBookActivity.this,"ISBN not valid",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), BookList.class);
            startActivity(intent);
        }

        String isbn = b.getString("isbn", null);
        BookInstance instance= (BookInstance) b.get("istance");

        if (isbn != null && isbn.length()==13) {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(isbn);
            DatabaseReference bookInstanceRef= FirebaseDatabase.getInstance().getReference("book_instances");

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
                    //S.setIndex(status) ?
                    loc=bi.getLocation().toString();
                    L.setText("loc");
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}
