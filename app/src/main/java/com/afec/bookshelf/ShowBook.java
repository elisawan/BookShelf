package com.afec.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.Owner;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import android.graphics.Color;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ShowBook extends BaseActivity {

    private ListView mListView;
    private Toolbar myToolbar;
    private TextView tv_title, tv_author, tv_publisher, tv_ed_year, tv_isbn, tv_desc;
    private ImageView iv_book;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_book);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        mListView = (ListView) findViewById(R.id.list_of_owner);
        tv_author = (TextView) findViewById(R.id.book_autor);
        tv_ed_year = (TextView) findViewById(R.id.book_year_edition);
        tv_isbn = (TextView) findViewById(R.id.book_isbn);
        tv_publisher = (TextView) findViewById(R.id.book_publisher);
        tv_title = (TextView) findViewById(R.id.book_title);
        iv_book = (ImageView) findViewById(R.id.book_image);

        Bundle b = getIntent().getExtras();
        if(b == null){
            Toast.makeText(ShowBook.this,"ISBN not valid",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getApplicationContext(), BookList.class);
            startActivity(intent);
        }
        String isbn = b.getString("isbn", null);
        if (isbn != null && isbn.length()==13) {
            DatabaseReference bookRef = FirebaseDatabase.getInstance().getReference("books").child(isbn);
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

                    Picasso.with(ShowBook.this).load(b.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                            .into(iv_book);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(ShowBook.this,"ISBN not valid",Toast.LENGTH_SHORT).show();
        }

        List<Owner> owners = genererOwner();

        OwnerAdapter adapter = new OwnerAdapter(ShowBook.this, owners);
        mListView.setAdapter(adapter);
    }

    private List<Owner> genererOwner(){
        List<Owner> owners = new ArrayList<Owner>();
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        owners.add(new Owner(Color.BLACK, "Williams", "Very good Book !!","3 km"));
        owners.add(new Owner(Color.BLUE, "Giovanna", "Questo libri e perfetto!","10 km"));
        owners.add(new Owner(Color.GREEN, "Paul", "Miam!","11 km"));
        owners.add(new Owner(Color.RED, "Mathieu", "Heuuu...","12 km"));
        owners.add(new Owner(Color.GRAY, "Domenico", "Non so... Haa si! perfetto!!","13 km"));
        return owners;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        return true;
    }
}

class OwnerViewHolder{
    public TextView pseudo;
    public TextView text;
    public ImageView avatar;
}
