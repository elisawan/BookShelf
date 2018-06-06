package com.afec.bookshelf

import android.app.Activity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.Button
import android.widget.TextView
import com.afec.bookshelf.Adapters.MessageListAdapter
import com.afec.bookshelf.Models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*

class Chat : Activity() {

    lateinit var button_send:Button
    lateinit var message_written:TextView
    lateinit var chatMessage:ChatMessage
    lateinit var userMe:FirebaseUser
    lateinit var chatID:String
    lateinit var userYouUid:String
    lateinit var userMeUid:String
    lateinit var mMessageRecycler:RecyclerView
    lateinit var mMessageAdapater:MessageListAdapter
    private var messageHistory: MutableList<ChatMessage> = mutableListOf()
    private val dbRef: DatabaseReference = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mMessageRecycler = findViewById(R.id.recyclerview_message_list)
        mMessageAdapater = MessageListAdapter( messageHistory)
        mMessageRecycler.layoutManager = LinearLayoutManager(this)

        config()
    }

    private fun config(){
        var fbAuth = FirebaseAuth.getInstance()
        userMe = fbAuth.currentUser!!
        userYouUid = intent.extras["userYou"].toString()
        userMeUid = userMe.uid
        chatID =  com.afec.bookshelf.Models.Chat.chatID(userMeUid,userYouUid)
        var fbRef = dbRef.child("chat").child(chatID)

        var receiverUnreadMessagesUpdaterReference = dbRef
                .child("users")
                .child(userYouUid)
                .child("unreadMessages")

        button_send = findViewById(R.id.button_chatbox_send)
        message_written = findViewById(R.id.edittext_chatbox)

        button_send.setOnClickListener { view ->
            var messaggio = message_written.text.toString()
            if(!messaggio.isEmpty()) {
                chatMessage = ChatMessage(messaggio, userMeUid, System.currentTimeMillis(), false)
                chatMessage.toUserID = userYouUid
                fbRef.push().setValue(chatMessage)
                message_written.text = ""
                //messageHistory.add(chatMessage)
                receiverUnreadMessagesUpdaterReference.setValue(true)
                if(messageHistory.size>3)
                    mMessageRecycler.scrollToPosition(mMessageRecycler.adapter.itemCount-1)
            }
        }

        val chatRef = dbRef.child("chat").child(chatID)

        chatRef.addChildEventListener( object : ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {

                var message : ChatMessage? = dataSnapshot!!.getValue(ChatMessage::class.java)
                var messageId = dataSnapshot.key

                message?.chatID=chatID
                message?.messageID=messageId
                messageHistory.add(message!!)

                if(message?.uid !=userMeUid && message?.read==false){
                    FirebaseDatabase.getInstance().getReference("chat").child(chatID).child(messageId).child("read").setValue(true)
                }

                mMessageRecycler.adapter = MessageListAdapter(messageHistory)
                mMessageRecycler.scrollToPosition(mMessageRecycler.adapter.itemCount-1)

                //Log.e("msg", message?.message)
            }

            override fun onCancelled(p0: DatabaseError?) {
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {

            }

            override fun onChildRemoved(p0: DataSnapshot?) {

            }
        })
    }
}