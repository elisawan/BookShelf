package com.afec.bookshelf

import android.app.Activity
import android.app.Fragment
import android.os.Bundle
import android.os.Message
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import com.afec.bookshelf.Models.ChatMessage
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import org.w3c.dom.Text
import java.sql.Time


class Chat : Activity() {

    lateinit var button_send:Button
    lateinit var message_written:TextView
    lateinit var message:ChatMessage
    lateinit var user:FirebaseUser
    lateinit var chatID:String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        config()
    }


    private fun config(){


        var fbAuth = FirebaseAuth.getInstance()
        user = fbAuth.currentUser!!


        var fbRef = FirebaseDatabase.getInstance()
                .reference
                .child("chat/")
                .child("1234567890")


        button_send = findViewById(R.id.button_chatbox_send)
        message_written = findViewById(R.id.edittext_chatbox)




        button_send.setOnClickListener { view ->
            Toast.makeText(baseContext, "Write your message here", Toast.LENGTH_LONG).show()
            var messaggio = message_written.text.toString()
            message = ChatMessage(messaggio, user.uid, 13213481263415)
            fbRef.push()

        }
    }
}