package com.afec.bookshelf;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.Owner;
import com.google.firebase.auth.FirebaseUser;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class ShowBook extends Fragment {

    private ListView mListView;
    private TextView tv_title, tv_author, tv_publisher, tv_ed_year, tv_isbn, tv_desc;
    private ImageView iv_book;
    List<Owner> owners;
    OwnerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_show_book, container, false);

        mListView = (ListView) v.findViewById(R.id.list_of_owner);
        tv_author = (TextView) v.findViewById(R.id.book_autor);
        tv_ed_year = (TextView) v.findViewById(R.id.book_year_edition);
        tv_isbn = (TextView) v.findViewById(R.id.book_isbn);
        tv_publisher = (TextView) v.findViewById(R.id.book_publisher);
        tv_title = (TextView) v.findViewById(R.id.book_title);
        iv_book = (ImageView) v.findViewById(R.id.book_image);
        tv_desc = (TextView) v.findViewById(R.id.book_description);

        owners = new ArrayList<Owner>();


        Bundle b = getArguments();
        if(b == null){
            Toast.makeText(getActivity(),"ISBN not valid",Toast.LENGTH_SHORT).show();
            myStartFragment(new BookList());
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
                    tv_desc.setText(b.getDescription());
                    Picasso.with(getActivity()).load(b.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
                            .into(iv_book);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }
        else
        {
            Toast.makeText(getActivity(),"ISBN not valid",Toast.LENGTH_SHORT).show();
        }

        adapter = new OwnerAdapter(getActivity(), owners);
        getOwners(isbn);

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

    public void getOwners(String isbn){
        DatabaseReference ownersRef = FirebaseDatabase.getInstance().getReference("books").child(isbn).child("owners");
        ownersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    String userId = child.getValue(String.class);
                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(userId);
                    userRef.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            Owner owner = dataSnapshot.getValue(Owner.class);
                            owners.add(owner);
                            mListView.setAdapter(adapter);
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
    }
}
