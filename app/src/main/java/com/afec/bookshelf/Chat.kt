package com.afec.bookshelf

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.os.Message
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.afec.bookshelf.Adapters.MessageListAdapter
import com.afec.bookshelf.Models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import org.w3c.dom.Text
import java.sql.Time
import java.time.LocalDateTime

class Chat : Activity() {

    lateinit var button_send:Button
    lateinit var message_written:TextView
    lateinit var message:ChatMessage
    lateinit var userMe:FirebaseUser
    lateinit var chatID:String
    lateinit var userYouUid:String
    lateinit var userMeUid:String
    lateinit var mMessageRecycler:RecyclerView
    lateinit var mMessageAdapater:MessageListAdapter
    private var messageHistory: MutableList<ChatMessage> = mutableListOf()
    private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mMessageRecycler = findViewById(R.id.recyclerview_message_list)
        mMessageAdapater = MessageListAdapter(this, messageHistory)
        mMessageRecycler.layoutManager = LinearLayoutManager(this)
        mMessageRecycler.adapter = mMessageAdapater

        config()
    }

    private fun config(){
        var fbAuth = FirebaseAuth.getInstance()
        userMe = fbAuth.currentUser!!
        userYouUid = intent.extras["userYou"].toString()
        userMeUid = userMe.uid
        if(userMeUid.compareTo(userYouUid)>0){
            chatID = userYouUid+userMeUid
        }else{
            chatID = userMeUid+userYouUid
        }
        var fbRef = FirebaseDatabase.getInstance()
                .reference
                .child("chat/")
                .child(chatID)

        button_send = findViewById(R.id.button_chatbox_send)
        message_written = findViewById(R.id.edittext_chatbox)

        button_send.setOnClickListener { view ->
            var messaggio = message_written.text.toString()
            val lb = messaggio?.length  //controllo se la stringa sia vuota o meno
            if(lb!=0) {
                val time = System.currentTimeMillis()
                message = ChatMessage(messaggio, userMeUid, time)
                fbRef.push().setValue(message)
                message_written.text = ""
            }
        }

        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageReference = FirebaseDatabase.getInstance().getReference("chat").child(chatID)


        val messageEventListener = object : ChildEventListener{
            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                // A new message has been added
                // onChildAdded() will be called for each node at the first time
                val message = dataSnapshot!!.getValue(ChatMessage::class.java)
                messageHistory.add(message!!)
                Log.e("msg", message?.message)
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }


        }



        mMessageReference!!.addChildEventListener(messageEventListener)



    }

    fun getCurrentUser(): String{
        return userMeUid
    }
}