package com.afec.bookshelf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.security.acl.LastOwnerException
import java.util.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference




class ChatList : Fragment() {

    lateinit var list_of_chat : ArrayList<ChatListItem>
    lateinit var db : FirebaseDatabase
    lateinit var User : FirebaseUser
    lateinit var OtherReference : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        val v: View = inflater.inflate(R.layout.fragment_chat_list, container, false)
        val lv: ListView = v.findViewById(R.id.listView)

        list_of_chat = ArrayList(100)
        db = FirebaseDatabase.getInstance()
        User = FirebaseAuth.getInstance().currentUser!!
        OtherReference = db.getReference("users").child(User.uid).child("chat")

        OtherReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (child in dataSnapshot.children) {
                    //For each chat_id find the UID associated
                    val UID : String = child.key
                    var chatId : String = child.value as String
                    val username : DatabaseReference = db.getReference("users").child(UID).child("username")

                    username.addValueEventListener( object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var name : String = dataSnapshot.value as String
                            var lastMessageQuery : Query = db.getReference("chat").child(chatId).orderByKey().limitToLast(1)

                            lastMessageQuery.addListenerForSingleValueEvent(object : ValueEventListener{
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    //wrong referencing!
                                    if(dataSnapshot.value!=null){

                                        var value : String = dataSnapshot.value!!.toString()
                                        var messageId : String = value.substring(value.indexOf("{")+1, value.indexOf("="))

                                        val message : String = dataSnapshot.child(messageId).child("message").value!!.toString()
                                        val read : Boolean = (dataSnapshot.child(messageId).child("read").value as Boolean?)!!

                                        var NewChat: ChatListItem = ChatListItem(UID, name, chatId, message, read)

                                        list_of_chat.add(NewChat)

                                        lv.adapter = object : BaseAdapter() {

                                            override fun getCount(): Int {
                                                return list_of_chat.size
                                            }

                                            override fun getItem(position: Int): Any {
                                                return list_of_chat[position]
                                            }

                                            override fun getItemId(position: Int): Long {
                                                return position.toLong()
                                            }

                                            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                                                var convertView = convertView
                                                if (convertView == null) {
                                                    convertView = layoutInflater.inflate(R.layout.chat_item, parent, false)
                                                }
                                                val iv = convertView!!.findViewById<View>(R.id.chat_image) as ImageView
                                                Picasso.with(context).load(R.drawable.book_image_placeholder).into(iv)
                                                if(read==false){
                                                    db.getReference().child("chat").child(chatId).child(messageId).child("isRead").setValue(true)
                                                    val notifIcon = convertView!!.findViewById<View>(R.id.chat_notificationIcon) as ImageView
                                                    Picasso.with(context).load(android.R.drawable.ic_notification_overlay).into(notifIcon)
                                                }
                                                val username_tv = convertView?.findViewById<View>(R.id.chat_other_username) as TextView
                                                username_tv.setText(list_of_chat[position].othername)
                                                val preview_tv = convertView?.findViewById<View>(R.id.chat_preview) as TextView
                                                preview_tv.setText(list_of_chat[position].preview)

                                                return convertView
                                            }
                                        }
                                    }





                                }

                                override fun onCancelled(databaseError: DatabaseError) {
                                    //Handle possible errors.
                                }
                            })





                        }

                        override fun onCancelled(databaseError: DatabaseError) {}
                    })




                }
            }
            override fun onCancelled(databaseError: DatabaseError) {}
        })

        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("userYou", list_of_chat[position].UID)
            context?.startActivity(intent)
        }






        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onDetach() {
        super.onDetach()
    }

}

class ChatListItem(var UID : String, var othername : String, var chat_id : String, var preview : String, var isRead : Boolean){
}
