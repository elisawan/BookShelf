package com.afec.bookshelf

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Message
import android.support.v4.app.Fragment
import android.util.ArrayMap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.firebase.ui.auth.AuthUI.getApplicationContext
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
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
import com.google.firebase.storage.FirebaseStorage
import kotlin.collections.ArrayList

class ChatList : Fragment() {

    lateinit var map_of_chat : MutableMap<String,ChatListItem>
    lateinit var db : FirebaseDatabase
    lateinit var User : FirebaseUser
    lateinit var OtherReference : DatabaseReference
    lateinit var v: View
    lateinit var lv: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat_list, container, false)
        lv = v.findViewById(R.id.listView)

        map_of_chat = ArrayMap<String,ChatListItem>()
        db = FirebaseDatabase.getInstance()
        User = FirebaseAuth.getInstance().currentUser!!
        OtherReference = db.getReference("users").child(User.uid).child("chat")

        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("userYou", map_of_chat.values.toList()[position].UID)
            context?.startActivity(intent)
        }

        return v
    }

    override fun onResume(){
        super.onResume()

        OtherReference.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (child in dataSnapshot.children) {
                    //For each chat_id find the UID associated
                    val otherUID : String = child.key
                    var chatId : String = child.value as String
                    val username : DatabaseReference = db.getReference("users").child(otherUID).child("username")

                    username.addValueEventListener( object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var name : String = dataSnapshot.value as String
                            var lastMessageQuery : Query = db.getReference("chat").child(chatId).orderByKey().limitToLast(1)

                            lastMessageQuery.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    //wrong referencing!
                                    if(dataSnapshot.value!=null){

                                        var value : String = dataSnapshot.value!!.toString()
                                        var messageId : String = value.substring(value.indexOf("{")+1, value.indexOf("="))

                                        val message : String = dataSnapshot.child(messageId).child("message").value!!.toString()
                                        var read : Boolean = true

                                        if(otherUID.equals(otherUID)) {
                                            read = (dataSnapshot.child(messageId).child("read").value as Boolean?)!!
                                        }


                                        var NewChat: ChatListItem = ChatListItem(otherUID, name, chatId, message, read)

                                        map_of_chat.put(NewChat.UID, NewChat)

                                        lv.adapter = object : BaseAdapter() {

                                            override fun getCount(): Int {
                                                return map_of_chat.size
                                            }

                                            override fun getItem(position: Int): Any {
                                                return map_of_chat.values.toList()[position]
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

                                                val mImageRef = FirebaseStorage.getInstance().getReference(map_of_chat.values.toList()[position].UID + "/profilePic.png")
                                                mImageRef.downloadUrl.addOnSuccessListener{
                                                    uri -> Picasso.with(context).load(uri.toString()).noPlaceholder().into(iv)
                                                }.addOnFailureListener {
                                                    exception -> Log.e("ERRORE RECUPERO IMG: ", exception.message.toString())
                                                    Picasso.with(context).load(R.drawable.ic_account_circle_black_24dp).into(iv)
                                                }

                                                val notifIcon = convertView!!.findViewById<View>(R.id.chat_notificationIcon) as ImageView

                                                if(map_of_chat.values.toList()[position].isRead == true)
                                                    notifIcon.visibility = View.INVISIBLE
                                                else
                                                    notifIcon.visibility = View.VISIBLE

                                                val username_tv = convertView?.findViewById<View>(R.id.chat_other_username) as TextView
                                                username_tv.setText(map_of_chat.values.toList()[position].othername)
                                                val preview_tv = convertView?.findViewById<View>(R.id.chat_preview) as TextView
                                                preview_tv.setText(map_of_chat.values.toList()[position].preview)

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
