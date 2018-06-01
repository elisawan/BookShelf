package com.afec.bookshelf;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.afec.bookshelf.Models.Review;
import com.afec.bookshelf.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReviewList extends Fragment {

    FirebaseDatabase database;
    FirebaseUser currentUser;

    List<User> reviewAuthorList;
    List<Review> reviewsList;
    ListView lv;

    Integer type;

    public ReviewList() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        // view
        View v = inflater.inflate(R.layout.review_list, container, false);
        lv = v.findViewById(R.id.review_list_view);

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        database = FirebaseDatabase.getInstance();

        reviewsList = new ArrayList<Review>();
        reviewAuthorList = new ArrayList<User>();

        // arguments
        type = Review.STATUS_INVALID;
        Bundle b = getArguments();
        if(b.containsKey("query")) {
            type = b.getInt("query", Review.STATUS_INVALID);
        }

        switch(type){
            case Review.STATUS_PENDING:
                getPendingReviews();
                break;
            case Review.STATUS_RECEIVED:
                getReceivedReviews();
                break;
            case Review.STATUS_WRITTEN:
                getWrittenReviews();
                break;
        }

        lv.setAdapter(reviewListAdapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Fragment newFragment = new ShowBook();
                Bundle b = new Bundle();
                b.putString("rev_id", reviewsList.get(position).getId());
                newFragment.setArguments(b);
                FragmentTransaction transaction = getActivity().getSupportFragmentManager().beginTransaction();
                // Replace whatever is in the fragment_container view with this fragment,
                // and add the transaction to the back stack
                transaction.replace(R.id.rev_container, newFragment);
                transaction.addToBackStack(null);
                // Commit the transaction
                transaction.commit();
            }
        });


        return v;
    }

    private BaseAdapter reviewListAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return reviewsList.size();
        }

        @Override
        public Object getItem(int position) {
            return reviewsList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            switch(type){
                case Review.STATUS_PENDING:
                    if(convertView == null)
                        convertView = getLayoutInflater().inflate(R.layout.pending_review_layout, parent, false);
                    break;
                case Review.STATUS_RECEIVED:
                    if(convertView == null)
                        convertView = getLayoutInflater().inflate(R.layout.received_review_layout, parent, false);
                    TextView review_author = (TextView) convertView.findViewById(R.id.review_user_name);
                    review_author.setText(reviewAuthorList.get(position).getUsername());

                    TextView review_comment = (TextView) convertView.findViewById(R.id.review_comment);
                    review_comment.setText(reviewsList.get(position).getComment());

                    RatingBar review_rating = (RatingBar) convertView.findViewById(R.id.review_score);
                    review_rating.setRating(reviewsList.get(position).getScore());
                    break;
                case Review.STATUS_WRITTEN:
                    if(convertView == null)
                        convertView = getLayoutInflater().inflate(R.layout.written_review_layout, parent, false);
                    break;
            }

            return convertView;
        }
    };

    public void getPendingReviews(){
        DatabaseReference ref = database.getReference("users").child(currentUser.getUid()).child("myReviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    // get review id
                    String rev_id = child.getKey();
                    Review r = child.getValue(Review.class);
                    r.setId(rev_id);
                    if (r.getStatus() == Review.STATUS_PENDING) {
                        reviewsList.add(r);

                        database.getReference("users").child(r.getUidfrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                reviewAuthorList.add(dataSnapshot.getValue(User.class));
                                reviewListAdapter.notifyDataSetChanged();

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getWrittenReviews(){
        DatabaseReference ref = database.getReference("users").child(currentUser.getUid()).child("myReviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    // get review id
                    String rev_id = child.getKey();
                    Review r = child.getValue(Review.class);
                    r.setId(rev_id);
                    if (r.getStatus() == Review.STATUS_WRITTEN) {
                        database.getReference("reviews").child(r.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Review r = dataSnapshot.getValue(Review.class);
                                reviewsList.add(r);

                                database.getReference("users").child(r.getUidfrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        reviewAuthorList.add(dataSnapshot.getValue(User.class));
                                        reviewListAdapter.notifyDataSetChanged();

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
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void getReceivedReviews(){
        DatabaseReference ref = database.getReference("users").child(currentUser.getUid()).child("myReviews");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for(DataSnapshot child : dataSnapshot.getChildren()) {
                    // get review id
                    String rev_id = child.getKey();
                    Review r = child.getValue(Review.class);
                    r.setId(rev_id);
                    if (r.getStatus() == Review.STATUS_WRITTEN) {
                        database.getReference("reviews").child(r.getId()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                Review r = dataSnapshot.getValue(Review.class);
                                reviewsList.add(r);

                                database.getReference("users").child(r.getUidfrom()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        reviewAuthorList.add(dataSnapshot.getValue(User.class));
                                        reviewListAdapter.notifyDataSetChanged();

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
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
