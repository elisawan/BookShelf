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
import com.afec.bookshelf.Models.Owner;
import com.afec.bookshelf.Models.Chat;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.List;

public class OwnerAdapter extends ArrayAdapter<Owner> {

    //Owner est la liste des models à afficher
    public OwnerAdapter(Context context, List<Owner> owners) {
        super(context, 0, owners);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null){
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_owner,parent, false);
        }

        final Owner owner = getItem(position);

        OwnerViewHolder viewHolder = (OwnerViewHolder) convertView.getTag();

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
                                    FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                                    FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
                                    String currUserId = currentUser.getUid();
                                    String ownerId = owner.getUid();
                                    String chatId = Chat.Companion.chatID(currUserId,ownerId);

                                    String msg = "New book request from "+currentUser.getDisplayName()+" of ...";
                                    ChatMessage message = new ChatMessage(msg, currentUser.getUid(),System.currentTimeMillis());
                                    message.setBookReq(true);

                                    Chat.Companion.sendMsgToChat(message,currentUser.getUid(),owner.getUid());

                                    firebaseDatabase.getReference("users").child(currUserId).child("chat").child(ownerId).setValue(chatId);
                                    firebaseDatabase.getReference("users").child(ownerId).child("chat").child(currUserId).setValue(chatId);

                                    dialog.cancel();
                                    Intent intent = new Intent(getContext(),Chat.class);
                                    intent.putExtra("userYou",ownerId);
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
        //il ne reste plus qu'à remplir notre vue
        viewHolder.pseudo.setText(owner.getUsername());
        viewHolder.distance.setText(owner.getDistance());
        Picasso.with(getContext()).load(owner.getProfileImage()).placeholder(R.drawable.book_image_placeholder)
                .into(viewHolder.avatar);
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