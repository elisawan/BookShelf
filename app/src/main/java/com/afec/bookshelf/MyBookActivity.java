package com.afec.bookshelf;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
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

public class MyBookActivity extends Fragment {

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


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_my_book, container, false);

        tv_author = (TextView) v.findViewById(R.id.book_autor);
        tv_ed_year = (TextView) v.findViewById(R.id.book_year_edition);
        tv_isbn = (TextView) v.findViewById(R.id.book_isbn);
        tv_publisher = (TextView) v.findViewById(R.id.book_publisher);
        tv_title = (TextView) v.findViewById(R.id.book_title);
        iv_book = (ImageView) v.findViewById(R.id.book_image);
        S = (Spinner) v.findViewById(R.id.status_spinner);
        status = 0;
        locButton = (Button) v.findViewById(R.id.editLocationButton);
        delButton = (Button) v.findViewById(R.id.DeleteButton);
        L= (TextView) v.findViewById(R.id.location);
        avCheckbox = (CheckBox) v.findViewById(R.id.availableCheckbox);
        loc=null;

        db = FirebaseDatabase.getInstance();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();

        Bundle b = this.getArguments();
        if(b == null){
            Toast.makeText(getContext(),"ISBN not valid",Toast.LENGTH_SHORT).show();
            toBookList();
        }

        String isbn = b.getString("isbn");
        instance= b.getString("instance");

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

                    Picasso.with(getActivity()).load(b.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder)
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
            Toast.makeText(getActivity(),"Book not found",Toast.LENGTH_SHORT).show();
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

                        toBookList();
                    }
                }
        );

        locButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ContextCompat.checkSelfPermission(getContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
                            ActivityCompat.requestPermissions(getActivity(), new String[] {android.Manifest.permission.ACCESS_COARSE_LOCATION},1);
                        }
                        LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
                        location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);

                        item = db.getReference("users").child(currentUser.getUid()).child("myBooks").child(instance).child("location");
                        item.setValue(location);
                    }
                }
        );
        return v;
    }

    private void toBookList(){
        FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
        // Replace whatever is in the fragment_container view with this fragment,
        // and add the transaction to the back stack
        transaction.replace(R.id.content_frame, new BookList());
        transaction.addToBackStack(null);
        // Commit the transaction
        transaction.commit();
    }

}
