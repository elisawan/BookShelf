package com.afec.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.location.Location;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.BookInstance;
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

public class BookList extends BaseActivity {

    GridView gv;
    List<Book> myBooks;
    Toolbar myToolbar;
    FirebaseDatabase db;
    FirebaseUser currentUser;
    DatabaseReference myBooksRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_book_list);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        gv = findViewById(R.id.book_list_grid);

        Location fakeLocation = new Location("");
        fakeLocation.setLatitude(7.2342);
        fakeLocation.setLongitude(45.234);

        myBooks = new ArrayList<Book>();

        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        // get list of my books
        myBooksRef = db.getReference("users").child(currentUser.getUid()).child("myBooks");
        //myBooksRef = db.getReference("users").child("id").child("myBooks");

        myBooksRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()){
                    // get book id
                    Log.d("child",child.toString());
                    String isbn = child.getValue(String.class);
                    Log.d("isbn",isbn);
                    // get book
                    DatabaseReference bookRef = db.getReference("books").child(isbn);
                    bookRef.addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Log.d("data",dataSnapshot.toString());
                            Book b = dataSnapshot.getValue(Book.class);
                            Log.d("book",b.toString());
                            myBooks.add(b);
                            // show on view
                            gv.setAdapter(new BaseAdapter() {

                                @Override
                                public int getCount() {
                                    return myBooks.size();
                                }

                                @Override
                                public Object getItem(int position) {
                                    return myBooks.get(position);
                                }

                                @Override
                                public long getItemId(int position) {
                                    return position;
                                }

                                @Override
                                public View getView(int position, View convertView, ViewGroup parent) {
                                    if (convertView==null) {
                                        convertView = getLayoutInflater().inflate(R.layout.book_preview, parent,false);
                                    }
                                    ImageView iv = (ImageView) convertView.findViewById(R.id.book_image_preview);
                                    Display d = getWindowManager().getDefaultDisplay();
                                    Point p = new Point();
                                    int w = p.x;
                                    int h = p.y;
                                    Picasso.with(getApplicationContext()).load(myBooks.get(position).getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                                            .into(iv);
                                    TextView title_tv =(TextView) convertView.findViewById(R.id.book_title_preview);
                                    title_tv.setText(myBooks.get(position).getTitle());
                                    return convertView;
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {
                        }
                    });
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), ListViewActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar, menu);
        menu.removeItem(R.id.action_edit_profile);
        return true;
    }
}
