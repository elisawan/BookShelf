package com.afec.bookshelf.Adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.ChatMessage;
import com.afec.bookshelf.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class MessageListAdapter extends RecyclerView.Adapter {

    private static final int VIEW_TYPE_MESSAGE_SENT = 1;
    private static final int VIEW_TYPE_MESSAGE_RECEIVED = 2;
    private static final int VIEW_TYPE_BOOK_REQUEST_SENT = 3;
    private static final int VIEW_TYPE_BOOK_REQUEST_RECEIVED = 4;

    private List<ChatMessage> mMessageList;
    private String currentUser;

    public MessageListAdapter(List<ChatMessage> messageList) {
        mMessageList = messageList;
        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
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
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ChatMessage message = (ChatMessage) mMessageList.get(position);

        switch (holder.getItemViewType()) {
            case VIEW_TYPE_MESSAGE_SENT:
                ((SentMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_MESSAGE_RECEIVED:
                ((ReceivedMessageHolder) holder).bind(message);
                break;
            case VIEW_TYPE_BOOK_REQUEST_SENT:
                ((SentBookRequestHolder) holder).bind(message);
                break;
            case VIEW_TYPE_BOOK_REQUEST_RECEIVED:
                ((ReceivedBookRequestHolder) holder).bind(message);
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
            timeText.setText(String.valueOf(message.getTimestamp()));
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

        void bind(ChatMessage message) {

            messageText.setText(message.getMessage());

            // Format the stored timestamp into a readable String using method.
            timeText.setText(String.valueOf(message.getTimestamp()));

            //nameText.setText(message.getSender().getNickname());

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(mContext, message.getSender().getProfileUrl(), profileImage);
        }
    }

    private class ReceivedBookRequestHolder extends RecyclerView.ViewHolder {

        TextView messageText, bookAuthor, bookTitle;
        Button acceptB, declineB;
        ImageView bookImage;

        ReceivedBookRequestHolder(View itemView) {

            super(itemView);

            bookImage = (ImageView) itemView.findViewById(R.id.req_book_image);
            bookAuthor = (TextView) itemView.findViewById(R.id.req_book_author);
            bookTitle = (TextView) itemView.findViewById(R.id.req_book_title);
            acceptB = (Button) itemView.findViewById(R.id.req_book_accept);
            declineB = (Button) itemView.findViewById(R.id.req_book_decline);
            messageText = (TextView) itemView.findViewById(R.id.text_message_body);
        }

        void bind(final ChatMessage message) {
            messageText.setText(message.getMessage());
        }
    }

    private class SentBookRequestHolder extends RecyclerView.ViewHolder {

        TextView messageText, bookAuthor, bookTitle;
        ImageView bookImage;

        SentBookRequestHolder(View itemView) {

            super(itemView);

            messageText = (TextView) itemView.findViewById(R.id.req_book_message);
        }

        void bind(final ChatMessage message) {

        }
    }


}