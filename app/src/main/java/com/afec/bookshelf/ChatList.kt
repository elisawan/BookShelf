package com.afec.bookshelf

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import java.util.*


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

                            var NewChat: ChatListItem = ChatListItem(UID, name, chatId, "prova")

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
                                    //val iv = convertView!!.findViewById<View>(R.id.chat_image) as ImageView
                                    //Picasso.with(context).load(list_of_chat[position].placeholder(R.drawable.book_image_placeholder).into(iv))
                                    val username_tv = convertView?.findViewById<View>(R.id.chat_other_username) as TextView
                                    username_tv.setText(list_of_chat[position].othername)
                                    val preview_tv = convertView?.findViewById<View>(R.id.chat_preview) as TextView
                                    preview_tv.setText(list_of_chat[position].preview)
                                    return convertView
                                }
                            }

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

class ChatListItem(var UID : String, var othername : String, var chat_id : String, var preview : String){
}
