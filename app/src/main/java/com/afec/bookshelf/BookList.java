package com.afec.bookshelf;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
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

public class BookList extends Fragment {

    GridView gv;
    List<Book> myBooks;
    List<String> myBooksInstances;
    Toolbar myToolbar;
    FirebaseDatabase db;
    FirebaseUser currentUser;
    DatabaseReference myBooksRef, bookInstance;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_book_list, container, false);

        gv = v.findViewById(R.id.book_list_grid);
        myBooks = new ArrayList<Book>();
        myBooksInstances = new ArrayList<String>();

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
                    final String instance = child.getKey();
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
                            myBooksInstances.add(instance);
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
                                    Picasso.with(getContext()).load(myBooks.get(position).getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                                            .into(iv);
                                    TextView title_tv =(TextView) convertView.findViewById(R.id.book_title_preview);
                                    title_tv.setText(myBooks.get(position).getTitle());
                                    TextView author_tv = (TextView) convertView.findViewById(R.id.book_autor_preview);
                                    author_tv.setText(myBooks.get(position).getAllAuthors());
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
                Fragment newFragment = new Fragment();
                Bundle b = new Bundle();
                b.putString("isbn", myBooks.get(position).getIsbn());
                b.putString("instance", myBooksInstances.get(position).toString());
                newFragment.setArguments(b);
                myStartFragment(newFragment);
            }
        });
        return v;
    }

    public void myStartFragment(Fragment newFragment){
        // Create new fragment and transaction
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content_frame, newFragment);
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();

    }
}
