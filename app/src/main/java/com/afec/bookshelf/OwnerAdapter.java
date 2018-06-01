package com.afec.bookshelf;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.afec.bookshelf.Models.ChatMessage;
import com.afec.bookshelf.Models.Chat;
import com.afec.bookshelf.Models.OwnerInstanceBook;
import com.afec.bookshelf.Models.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class OwnerAdapter extends ArrayAdapter<OwnerInstanceBook> {

    String ownerID;
    String isbn;
    String bookInstanceID;
    String currentUserId;

    FirebaseDatabase firebaseDatabase;
    FirebaseUser currentUser;

    User owner;

    OwnerViewHolder viewHolder;

    //Owner est la liste des models à afficher
    public OwnerAdapter(Context context, List<OwnerInstanceBook> list) {
        super(context, 0, list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_owner,parent, false);
        }

        firebaseDatabase = FirebaseDatabase.getInstance();

        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        currentUserId = currentUser.getUid();

        ownerID = getItem(position).getOwnerID();
        isbn = getItem(position).getISBN();
        bookInstanceID = getItem(position).getBookInstanceID();
        owner = getItem(position).getOwner();
        viewHolder = (OwnerViewHolder) convertView.getTag();

        if(viewHolder == null){
            viewHolder = new OwnerViewHolder();
            viewHolder.pseudo = (TextView) convertView.findViewById(R.id.pseudo);
            viewHolder.text = (TextView) convertView.findViewById(R.id.text);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            viewHolder.distance = (TextView) convertView.findViewById(R.id.distance);
            viewHolder.reqButton = (Button) convertView.findViewById(R.id.req_button);
            viewHolder.reqButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //Display dialog box
                    AlertDialog.Builder alertB = new AlertDialog.Builder(getContext());

                    alertB.setMessage("Do you want to sent a request for this book?");
                    alertB.setCancelable(true);

                    alertB.setPositiveButton(
                            R.string.Affirmative,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    String chatId = Chat.Companion.chatID(currentUserId,ownerID);

                                    String msg = "New book request from "+currentUser.getDisplayName()+" of ...";
                                    ChatMessage message = new ChatMessage(msg, currentUserId, System.currentTimeMillis(), false);
                                    message.setBookReq(true);
                                    message.setBookInstance(bookInstanceID);
                                    message.setBookISBN(isbn);
                                    message.setToUserID(ownerID);
                                    message.setRead(false);
                                    message.setResponded(false);

                                    Chat.Companion.sendMsgToChat(message,currentUserId,ownerID);

                                    firebaseDatabase.getReference("users").child(currentUserId).child("chat").child(ownerID).setValue(chatId);
                                    firebaseDatabase.getReference("users").child(ownerID).child("chat").child(currentUserId).setValue(chatId);

                                    dialog.cancel();
                                    Intent intent = new Intent(getContext(), com.afec.bookshelf.Chat.class);
                                    intent.putExtra("userYou",ownerID);
                                    getContext().startActivity(intent);
                                }
                            });

                    alertB.setNegativeButton(
                            R.string.Negative,
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                    AlertDialog alertD = alertB.create();
                    alertD.show();
                }
            });
            convertView.setTag(viewHolder);
        }


        viewHolder.pseudo.setText(owner.getUsername());

        //il ne reste plus qu'à remplir notre vue

        //viewHolder.distance.setText(owner.getDistance());
        //Picasso.with(getContext()).load(owner.).placeholder(R.drawable.book_image_placeholder).into(viewHolder.avatar);
        return convertView;
    }

    private class OwnerViewHolder{
        public TextView pseudo;
        public TextView text;
        public TextView distance;
        public ImageView avatar;
        public Button reqButton;

    }

}