package com.afec.bookshelf

import android.app.IntentService
import android.app.Notification
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.view.ViewParent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*


class ChatNotificationService : IntentService("ChatService") {

    private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null
    private var userMe: FirebaseUser? = null
    private var userMeUid: String? = null

    override fun onHandleIntent(intent: Intent?) {

        var fbAuth = FirebaseAuth.getInstance()
        userMe = fbAuth.currentUser!!
        userMeUid = userMe!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageReference = FirebaseDatabase.getInstance().getReference("users").child(userMeUid).child("unreadMessages")

        val messageEventListener = object : ChildEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildChanged(dataSnapshot: DataSnapshot?, p1: String?) {
                var unreadMessage = dataSnapshot!!.getValue(Boolean::class.java)

                if(unreadMessage!!){
                    val pendingIntent = PendingIntent.getActivity(baseContext, 0 /* Request code */, intent,
                            PendingIntent.FLAG_ONE_SHOT)

                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    val notificationBuilder = Notification.Builder(baseContext)
                            .setContentTitle("Ci sono nuovi messaggi!")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(defaultSoundUri)
                            .setContentIntent(pendingIntent)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
                }
            }

            override fun onChildAdded(p0: DataSnapshot?, p1: String?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
    }
}