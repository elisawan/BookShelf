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

public class ShowBook extends Fragment {

    private ListView mListView;
    private TextView tv_title, tv_author, tv_publisher, tv_ed_year, tv_isbn;
    private ImageView iv_book;

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

        List<Owner> owners = genererOwner();

        OwnerAdapter adapter = new OwnerAdapter(getActivity(), owners);
        mListView.setAdapter(adapter);

        return v;
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

class OwnerViewHolder{
    public TextView pseudo;
    public TextView text;
    public ImageView avatar;
}
