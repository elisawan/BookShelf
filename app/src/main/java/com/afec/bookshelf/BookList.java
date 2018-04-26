package com.afec.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

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

        myBooks = new ArrayList<Book>();

        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        myBooksRef = db.getReference("users").child(currentUser.getUid()).child("myBooks");
        
        String bookId = myBooksRef.getKey();
        DatabaseReference bookInstancesRef = db.getReference("book_instances").child(bookId);
        bookInstancesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String isbn = dataSnapshot.getValue().toString();
                final DatabaseReference booksRef = db.getReference("books").child(isbn);
                booksRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Book b = (Book) dataSnapshot.getValue(Book.class);
                        myBooks.add(b);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        gv = findViewById(R.id.book_list_grid);
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
                myBooks.get(position).setThumbnailUrl("http://books.google.com/books/content?id=BlfqRgAACAAJ&printsec=frontcover&img=1&zoom=1&imgtk=AFLRE713W4vHhhKaoyzSnhZBzvgRoEYirqmfzR5iJ6Y7wjbRtCILro3DqXUEsAIkvUVMunOJBgJG1wgCI_ls7amybyAAZJB2Go5jF88JIGJrLcXjnjF-fdmXTy_iPU87qgTOvGDqnz_S&source=gbs_api");
                Picasso.with(getApplicationContext()).load(myBooks.get(position).getThumbnailUrl()).noPlaceholder()
                        .resize(300,400)
                        .into(iv);
                TextView title_tv =(TextView) convertView.findViewById(R.id.book_title_preview);
                myBooks.get(position).setTitle("La divina commedia");
                title_tv.setText(myBooks.get(position).getTitle());

                Log.d("isbn",myBooks.get(position).getIsbn() );
                return convertView;
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
