package com.afec.bookshelf;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afec.bookshelf.Models.Review;
import com.afec.bookshelf.Models.User;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


public class ShowUser extends Fragment {

    ImageView immagineUtente;
    TextView nomeUtente, bioUtente, lentBookCount, borrowedBookCount, credit;
    RatingBar ratingBar;
    Dialog builder;
    String uid;
    User currentUser;
    FirebaseDatabase database;
    FirebaseUser currentUserFirebase;
    View v;

    private ReviewPage.SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.activity_show_user, container, false);
        v.setFocusableInTouchMode(true);
        v.requestFocus();
        v.setOnKeyListener( new View.OnKeyListener()
        {
            @Override
            public boolean onKey( View v, int keyCode, KeyEvent event )
            {
                if( keyCode == KeyEvent.KEYCODE_BACK )
                {
                    Intent intent = new Intent(getContext(), MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    startActivity(intent);
                }
                return false;
            }
        });

        //Mettere tutte le inizializzazioni qui in config
        config();

        database = FirebaseDatabase.getInstance();
        currentUserFirebase = FirebaseAuth.getInstance().getCurrentUser();


        return v;
    }

    private void setupViewPager(ViewPager mViewPager){
        ReviewPage.SectionsPagerAdapter adapter = new ReviewPage.SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        Bundle b_pending = new Bundle();
        b_pending.putInt("query", Review.STATUS_PENDING);
        Fragment pendingReviews = new ReviewList();
        pendingReviews.setArguments(b_pending);
        adapter.addFragment(pendingReviews,getResources().getString(R.string.pending));

        Bundle b_written = new Bundle();
        b_written.putInt("query",Review.STATUS_WRITTEN);
        Fragment writtenReviews = new ReviewList();
        writtenReviews.setArguments(b_written);
        adapter.addFragment(writtenReviews,getResources().getString(R.string.written));

        Bundle b_received = new Bundle();
        b_received.putInt("query",Review.STATUS_RECEIVED);
        Fragment receivedReviews = new ReviewList();
        receivedReviews.setArguments(b_received);
        adapter.addFragment(receivedReviews,getResources().getString(R.string.received));

        mViewPager.setAdapter(adapter);
    }

    public static class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        @Override
        public String getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }

        public void addFragment(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }
    }

    public void publicationQuickView(){
        View view = getLayoutInflater().inflate( R.layout.inflater_immagine_profilo, null);

        builder = new Dialog(getContext());
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        builder.setContentView(view);
        builder.show();
    }

    public void config(){

        setHasOptionsMenu(true);

        immagineUtente = (ImageView) v.findViewById(R.id.immagineUtente);
        //percepisce il tap lungo
        immagineUtente.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                publicationQuickView();
                return true;
            }
        });
        nomeUtente = (TextView) v.findViewById(R.id.nomeUtente);
        bioUtente = (TextView) v.findViewById(R.id.bioUtente);

        lentBookCount = (TextView) v.findViewById(R.id.lent_book_count);
        borrowedBookCount = (TextView) v.findViewById(R.id.borrowed_book_count);
        credit = (TextView) v.findViewById(R.id.credit_user_count);

        ratingBar = (RatingBar) v.findViewById(R.id.ratingUser);

        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        currentUser = new User();
        if(!currentUser.getUserLocalData(getContext(),uid)){
            currentUser=null;
        }else{
            updateViewContent(currentUser);
        }

        database = FirebaseDatabase.getInstance();
        DatabaseReference userRef;

        //for the review part ----------------------------------------------------------------------
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new ReviewPage.SectionsPagerAdapter(getActivity().getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) v.findViewById(R.id.rev_container_user);
        setupViewPager(mViewPager);

        TabLayout tabLayout = (TabLayout) v.findViewById(R.id.rev_tabs_user);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(mViewPager));

        //------------------------------------------------------------------------------------------

        //listener for capturing changes on user attributes
        userRef= database.getReference("users").child(uid);
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(!dataSnapshot.exists()) return;
                User newUser = dataSnapshot.getValue(User.class);
                if((currentUser==null || newUser.getTimestamp()>currentUser.getTimestamp())) {
                    currentUser=newUser;
                    updateViewContent(newUser);
                    newUser.updateSharedPrefContent(getContext());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //listener for capturing profile image changes
        StorageReference mImageRef = FirebaseStorage.getInstance().getReference(uid + "/profilePic.png");
        mImageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.with(getContext()).load(uri.toString()).noPlaceholder().into(immagineUtente);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ERRORE RECUPERO IMG: ", exception.getMessage().toString());
                Picasso.with(getContext()).load(R.drawable.ic_account_circle_black_24dp).into(immagineUtente);
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.setGroupVisible(R.id.showProfileMenu,true);
        menu.setGroupVisible(R.id.defaultMenu,false);
    }

    private void updateViewContent(User u){
        lentBookCount.setText(String.valueOf(u.getLentBooks()));
        borrowedBookCount.setText(String.valueOf(u.getBorrowedBooks()));
        ratingBar.setRating(u.getRating());
        nomeUtente.setText(String.valueOf(u.getUsername()));
        bioUtente.setText(String.valueOf(u.getBiography()));
    }
}
