package com.afec.bookshelf

import android.app.*
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import android.view.ViewParent
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import android.app.PendingIntent
import android.widget.Toast
import android.R.string.cancel
import android.content.pm.PackageManager





 class ChatNotificationService : Service() {


     private val mBinder = LocalBinder()
     private var mDatabase: DatabaseReference? = null
    private var mMessageReference: DatabaseReference? = null
    private var userMe: FirebaseUser? = null
    private var userMeUid: String? = null

     override fun onBind(p0: Intent?): IBinder {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
         return mBinder;
     }

     inner class LocalBinder : Binder() {
         internal val service: ChatNotificationService
             get() = this@ChatNotificationService
     }

     override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
         Log.i("LocalService", "Received start id $startId: $intent")

         return Service.START_NOT_STICKY
     }

     override fun onDestroy() {
         // Cancel the persistent notification.

         // Tell the user we stopped.
         Log.i("Service End", "Service end")
     }


     override fun onCreate() {


        Log.e("asdhfablsjdf", "asdfhbaljdfhabjlsdhfalsjhdfba")

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

                if(!unreadMessage!!){

                    val intent = Intent(baseContext,ChatNotificationService::class.java)


                    val contentIntent = PendingIntent.getActivity(baseContext, 0, intent,0)


                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    val notificationBuilder = Notification.Builder(baseContext)
                            .setContentTitle("Ci sono nuovi messaggi!")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(defaultSoundUri)
                            .setContentIntent(contentIntent)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
                }
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, p1: String?) {
                var unreadMessage = dataSnapshot!!.getValue(Boolean::class.java)

                if(!unreadMessage!! ){

                    val intent = Intent(baseContext,ChatList::class.java)


                    val contentIntent = PendingIntent.getActivity(baseContext, 0, intent,0)


                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    val notificationBuilder = Notification.Builder(baseContext)
                            .setContentTitle("Ci sono nuovi messaggi!")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(defaultSoundUri)
                            .setContentIntent(contentIntent)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        }
        mMessageReference!!.addChildEventListener(messageEventListener)
    }
}