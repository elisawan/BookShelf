package com.afec.bookshelf;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.JsonReader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ShowBook extends BaseActivity {

    private ListView mListView;
    private Toolbar myToolbar;
    private TextView tv_title, tv_author, tv_publisher, tv_ed_year, tv_isbn;
    private FirebaseUser currUser;

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
                    tv_author.setText(b.getAuthor());
                    //tv_ed_year.setText();
                    //tv_publisher.setText();
                    tv_isbn.setText(b.getIsbn());
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