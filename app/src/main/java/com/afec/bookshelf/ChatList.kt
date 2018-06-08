package com.afec.bookshelf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.ArrayMap
import android.util.Log
import android.view.*
import android.widget.*
import com.afec.bookshelf.Models.ChatMessage
import com.afec.bookshelf.Models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DataSnapshot
import com.squareup.picasso.Picasso
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.storage.FirebaseStorage

class ChatList : Fragment() {

    lateinit var map_of_chat : MutableMap<String,ChatListItem>
    val dbRef : DatabaseReference = FirebaseDatabase.getInstance().reference
    val currentUser : FirebaseUser = FirebaseAuth.getInstance().currentUser!!
    lateinit var chatRef : DatabaseReference
    lateinit var v: View
    lateinit var lv: ListView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_chat_list, container, false)

        map_of_chat = ArrayMap<String,ChatListItem>()


        lv = v.findViewById(R.id.listView) as ListView
        lv.onItemClickListener = AdapterView.OnItemClickListener { adapterView, view, position, id ->
            val intent = Intent(context, Chat::class.java)
            intent.putExtra("userYou", map_of_chat.values.toList()[position].UID)
            context?.startActivity(intent)
        }

        dbRef.child("users").child(currentUser.uid).child("unreadMessages").setValue(false)

        return v
    }

    override fun onResume(){
        super.onResume()

        chatRef = dbRef.child("users").child(currentUser.uid).child("chat")
        chatRef.addValueEventListener(object : ValueEventListener {

            override fun onDataChange(dataSnapshot: DataSnapshot) {

                for (child in dataSnapshot.children) {
                    //For each chat_id find the UID associated
                    val chatWithUid : String = child.key
                    val chatId : String = child.value as String

                    val users_ref : DatabaseReference = dbRef.child("users").child(chatWithUid)
                    users_ref.addValueEventListener(object : ValueEventListener{
                        override fun onCancelled(p0: DatabaseError?) {

                        }

                        override fun onDataChange(p0: DataSnapshot?) {
                            if(!p0!!.exists()) return
                            var chatWithUser : User = p0.getValue(User::class.java) as User
                            var lastMessageQuery : Query = dbRef.child("chat").child(chatId).orderByKey().limitToLast(1)
                            lastMessageQuery.addChildEventListener(object:ChildEventListener{
                                override fun onCancelled(p0: DatabaseError?) {

                                }

                                override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

                                }

                                override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

                                }

                                override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                                    if(!p0!!.exists())return
                                    val chatMessage : ChatMessage = p0.getValue(ChatMessage::class.java) as ChatMessage
                                    var chatListItem : ChatListItem
                                    if(chatMessage.toUserID == currentUser.uid){
                                        chatListItem = ChatListItem(chatWithUid,chatWithUser.username,chatMessage.chatID,chatMessage.message,chatMessage.read)
                                    }else{
                                        chatListItem = ChatListItem(chatWithUid,chatWithUser.username,chatMessage.chatID,chatMessage.message,true)
                                    }
                                    map_of_chat.put(chatMessage.chatID, chatListItem)
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

                                override fun onChildRemoved(p0: DataSnapshot?) {

                                }


                            })
                        }

                    })
                   /* val username : DatabaseReference = db.getReference("users").child(otherUID).child("username")

                    username.addValueEventListener( object : ValueEventListener {

                        override fun onDataChange(dataSnapshot: DataSnapshot) {
                            var name : String = dataSnapshot.value as String
                            var lastMessageQuery : Query = db.getReference("chat").child(chatId).orderByKey().limitToLast(1)

                            lastMessageQuery.addValueEventListener(object : ValueEventListener{
                                override fun onDataChange(dataSnapshot: DataSnapshot) {

                                    if(dataSnapshot.value!=null){

                                        var value : String = dataSnapshot.value!!.toString()
                                        var messageId : String = value.substring(value.indexOf("{")+1, value.indexOf("="))
                                        //var sender : String = dataSnapshot.child(messageId).child("uid").value!!.toString()
                                        val message : String = dataSnapshot.child(messageId).child("message").value!!.toString()
                                        var read : Boolean = true

                                        val currentUser:FirebaseUser  = FirebaseAuth.getInstance().currentUser!!
                                        if(otherUID.equals(currentUser.uid)) {
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
                    })*/
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

    override fun onCreateOptionsMenu(menu: Menu?, inflater: MenuInflater?) {
        super.onCreateOptionsMenu(menu, inflater)
        menu!!.setGroupVisible(R.id.defaultMenu, false)
        menu.setGroupVisible(R.id.showProfileMenu, false)
    }
}

class ChatListItem(var UID : String, var othername : String, var chat_id : String, var preview : String, var isRead : Boolean){
}
