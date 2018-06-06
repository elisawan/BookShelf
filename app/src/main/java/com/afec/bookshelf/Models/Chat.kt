package com.afec.bookshelf.Models

import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class Chat {

    companion object {

        fun firebaseRef():DatabaseReference{
            val db = FirebaseDatabase.getInstance()
            return db.reference
        }

        fun chatID(uid1: String, uid2: String): String {
            return if (uid1.compareTo(uid2) < 0) uid1 + uid2 else uid2 + uid1
        }

        fun sendMsgToChat(msg: ChatMessage, uid1: String, uid2: String): Boolean {
            val ref = Chat.firebaseRef()
            val chatID = Chat.chatID(uid1, uid2)
            ref.child("chat").child(chatID).push().setValue(msg)
            ref.child("users").child(uid2).child("unreadMessages").setValue(true)
            return true
        }
    }
}