package com.afec.bookshelf;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.Owner;
import com.afec.bookshelf.Models.OwnerInstanceBook;
import com.afec.bookshelf.Models.User;
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
    List<OwnerInstanceBook> contentList;
    OwnerAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_show_book, container, false);

        setHasOptionsMenu(true);
        mListView = (ListView) v.findViewById(R.id.list_of_owner);
        tv_author = (TextView) v.findViewById(R.id.book_autor);
        tv_ed_year = (TextView) v.findViewById(R.id.book_year_edition);
        tv_isbn = (TextView) v.findViewById(R.id.book_isbn);
        tv_publisher = (TextView) v.findViewById(R.id.book_publisher);
        tv_title = (TextView) v.findViewById(R.id.book_title);
        iv_book = (ImageView) v.findViewById(R.id.book_image);

        contentList = new ArrayList<OwnerInstanceBook>();

        Bundle b = getArguments();
        if(!b.containsKey("isbn")){
            Toast.makeText(getActivity(),"ISBN not valid",Toast.LENGTH_SHORT).show();
            myStartFragment(new NearBooks());
            return null;
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
        getOwners(isbn);
        adapter = new OwnerAdapter(getActivity(), contentList);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference("users").child(contentList.get(position).getOwnerID());
                ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User owner = dataSnapshot.getValue(User.class);
                        Bundle b = new Bundle();
                        b.putString("username", owner.getUsername());
                        b.putString("rating", ((Float)owner.getRating()).toString());
                        b.putString("borrowedBooks", ((Integer)owner.getBorrowedBooks()).toString());
                        b.putString("lentBooks", ((Integer)owner.getLentBooks()).toString());
                        b.putString("bio", owner.getBiography());
                        b.putString("uid", owner.getUid());
                        Fragment newFragment = new ShowUserPublic();
                        newFragment.setArguments(b);
                        myStartFragment(newFragment);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
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

    public void getOwners(final String isbn){
        DatabaseReference ownersRef = FirebaseDatabase.getInstance().getReference("books").child(isbn).child("owners");
        ownersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    final String userId = child.getValue(String.class);
                    final String bookInstanceId = child.getKey();

                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users");
                    userRef.child(userId).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            User owner = dataSnapshot.getValue(User.class);
                            contentList.add(new OwnerInstanceBook(userId,isbn,bookInstanceId,owner));
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

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.defaultMenu,false);
        menu.setGroupVisible(R.id.showProfileMenu,false);
    }
}
