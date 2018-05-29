package com.afec.bookshelf.Adapters;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.Book;
import com.afec.bookshelf.Models.Chat;
import com.afec.bookshelf.Models.ChatMessage;
import com.afec.bookshelf.Models.Review;
import com.afec.bookshelf.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.squareup.picasso.Picasso;

import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_BOOK_REQUEST_SENT = 3;
    private static final int VIEW_TYPE_BOOK_REQUEST_RECEIVED = 4;

    private List<ChatMessage> mMessageList;
    private String currentUser;
    private FirebaseDatabase db;

    public MessageListAdapter(List<ChatMessage> messageList) {
        mMessageList = messageList;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db=FirebaseDatabase.getInstance();
    }

    @Override
    public int getItemCount() {
        return mMessageList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {

        ChatMessage message = (ChatMessage) mMessageList.get(position);

        if(message.getUid().equals(currentUser)) {
            // The current user is the sender of the message
            if (message.getBookReq())
                return VIEW_TYPE_BOOK_REQUEST_SENT;
            return VIEW_TYPE_MESSAGE_SENT;
        }else{
            // The other user sent the message
            if(message.getBookReq())
                return VIEW_TYPE_BOOK_REQUEST_RECEIVED;
            return VIEW_TYPE_MESSAGE_RECEIVED;
        }
    }

    // Inflates the appropriate layout according to the ViewType.
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view;
        RecyclerView.ViewHolder viewHolder = null;
        switch (viewType){
            case VIEW_TYPE_MESSAGE_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_sent, parent, false);
                viewHolder = new SentMessageHolder(view);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_message_received, parent, false);
                viewHolder = new ReceivedMessageHolder(view);
                break;
            case VIEW_TYPE_BOOK_REQUEST_RECEIVED:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_request_msg_received, parent, false);
                viewHolder = new ReceivedBookRequestHolder(view);
                break;
            case VIEW_TYPE_BOOK_REQUEST_SENT:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.book_request_msg_sent, parent, false);
                viewHolder = new SentBookRequestHolder(view);
                break;
        }
        return viewHolder;
    }

    // Passes the message object to a ViewHolder so that the contents can be bound to UI.
    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

        final ChatMessage message = (ChatMessage) mMessageList.get(position);
        DatabaseReference ref;
        DatabaseReference usernameReference;

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                usernameReference=db.getReference("users").child(message.getUid()).child("username");
                usernameReference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        String username= dataSnapshot.getValue(String.class);
                        if(username!=null)
                            ((ReceivedMessageHolder) holder).bind(message, username);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

                break;
            case VIEW_TYPE_BOOK_REQUEST_SENT:
                ref = FirebaseDatabase.getInstance().getReference("books").child(message.getBookISBN());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Book book = dataSnapshot.getValue(Book.class);
                        ((SentBookRequestHolder) holder).bind(message, book);
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
                break;
            case VIEW_TYPE_BOOK_REQUEST_RECEIVED:
                ref = FirebaseDatabase.getInstance().getReference("books").child(message.getBookISBN());
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        final Book book = dataSnapshot.getValue(Book.class);
                        DatabaseReference usernameReference=db.getReference("users").child(message.getUid()).child("username");
                        usernameReference.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String username= dataSnapshot.getValue(String.class);
                                if(username!=null)
                                    ((ReceivedBookRequestHolder) holder).bind(message, book, username);
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
                break;
        }
    }

    private class SentMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText;

        SentMessageHolder(View itemView) {
            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
        }

        void bind(ChatMessage message) {

            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            long duration = message.getTimestamp()/1000;
            int day = (int)TimeUnit.SECONDS.toDays(duration);
            long hours = TimeUnit.SECONDS.toHours(duration) - (day *24);
            long minutes = TimeUnit.SECONDS.toMinutes(duration) - (TimeUnit.SECONDS.toHours(duration)* 60);
            //long second = TimeUnit.SECONDS.toSeconds(duration) - (TimeUnit.SECONDS.toMinutes(duration) *60);
            String time = hours + ":" + minutes;
            timeText.setText(time);
        }
    }

    private class ReceivedMessageHolder extends RecyclerView.ViewHolder {

        TextView messageText, timeText, nameText;
        ImageView profileImage;


        ReceivedMessageHolder(View itemView) {

            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            timeText = (TextView) itemView.findViewById(R.id.text_message_time);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
            profileImage = (ImageView) itemView.findViewById(R.id.image_message_profile);

        }

        void bind(ChatMessage message, String username) {

            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            long duration = message.getTimestamp()/1000;
            int day = (int)TimeUnit.SECONDS.toDays(duration);
            long hours = TimeUnit.SECONDS.toHours(duration) - (day *24);
            long minutes = TimeUnit.SECONDS.toMinutes(duration) - (TimeUnit.SECONDS.toHours(duration)* 60);
            String time = hours + ":" + minutes;
            timeText.setText(time);

            nameText.setText(username);

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class ReceivedBookRequestHolder extends RecyclerView.ViewHolder {

        TextView messageText, bookAuthor, bookTitle, nameText;
        Button acceptB, declineB;
        ImageView bookImage;
        View itemView;

        ReceivedBookRequestHolder(View itemView) {

            super(itemView);
            this.itemView = itemView;

            bookImage = (ImageView) itemView.findViewById(R.id.req_book_image);
            bookAuthor = (TextView) itemView.findViewById(R.id.req_book_author);
            bookTitle = (TextView) itemView.findViewById(R.id.req_book_title);
            acceptB = (Button) itemView.findViewById(R.id.req_book_accept);
            declineB = (Button) itemView.findViewById(R.id.req_book_decline);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
            nameText = (TextView) itemView.findViewById(R.id.text_message_name);
        }

        void bind(final ChatMessage message, Book book, String username) {
            messageText.setText(message.getMessage());
            bookAuthor.setText(book.getAllAuthors());
            bookTitle.setText(book.getTitle());
            nameText.setText(username);
            Picasso.with(itemView.getContext()).load(book.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder).into(bookImage);
            acceptB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference();

                    //1.send message of acceptance
                    message.acceptRequest();

                    //2.add pending review to fromUser
                    Review review1 = new Review();
                    review1.setStatus(Review.STATUS_PENDING);
                    review1.setUidfrom(message.getUid());
                    review1.setUidto(message.getToUserID());
                    ref.child("users").child(message.getUid()).child("myReviews").push().setValue(review1);

                    //3.add pending review to toUser
                    Review review2 = new Review();
                    review2.setStatus(Review.STATUS_PENDING);
                    review2.setUidfrom(message.getToUserID());
                    review2.setUidto(message.getUid());
                    ref.child("users").child(message.getToUserID()).child("myReviews").push().setValue(review2);

                    //4.Display dialog box
                    AlertDialog.Builder alertB = new AlertDialog.Builder(v.getContext());
                    alertB.setMessage("Book request accepted");
                    AlertDialog alertD = alertB.create();
                    alertD.show();
                }
            });
            declineB.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    message.declineRequest();
                    //Display dialog box
                    AlertDialog.Builder alertB = new AlertDialog.Builder(v.getContext());
                    alertB.setMessage("Book request declined");
                    AlertDialog alertD = alertB.create();
                    alertD.show();
                }
            });
        }
    }

    private class SentBookRequestHolder extends RecyclerView.ViewHolder {

        TextView messageText, bookAuthor, bookTitle;
        ImageView bookImage;
        View itemView;

        SentBookRequestHolder(View itemView) {

            super(itemView);
            this.itemView = itemView;

            bookAuthor = (TextView) itemView.findViewById(R.id.req_book_author);
            bookTitle = (TextView) itemView.findViewById(R.id.req_book_title);
            bookImage = (ImageView) itemView.findViewById(R.id.req_book_image);
            messageText = (TextView) itemView.findViewById(R.id.req_book_message);
        }

        void bind(final ChatMessage message, Book book) {
            bookAuthor.setText(book.getAllAuthors());
            bookTitle.setText(book.getTitle());
            Picasso.with(itemView.getContext()).load(book.getThumbnailUrl()).placeholder(R.drawable.book_image_placeholder).into(bookImage);
        }
    }
}