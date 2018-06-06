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
import android.content.ComponentName
import android.content.pm.PackageManager
import android.support.v4.app.NotificationCompat
import com.afec.bookshelf.MainActivity.CHANNEL_ID
import java.util.*

class ChatNotificationService : Service() {

     private val mBinder = LocalBinder()
     private var mDatabase: DatabaseReference? = null
     private var mMessageReference: DatabaseReference? = null
     private var userMe: FirebaseUser? = null
     private var userMeUid: String? = null

     private lateinit var notificationBuilder: NotificationCompat.Builder

     override fun onBind(p0: Intent?): IBinder {
         TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
         return mBinder;
     }

     inner class LocalBinder : Binder() {
         internal val service: ChatNotificationService
             get() = this@ChatNotificationService
     }

     override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
         //Log.i("LocalService", "Received start id $startId: $intent")

         return Service.START_STICKY
     }

     override fun onDestroy() {
         // Cancel the persistent notification.

         // Tell the user we stopped.
         Log.i("Service End", "Service end")
     }


     override fun onCreate() {
        var fbAuth = FirebaseAuth.getInstance()
        userMe = fbAuth.currentUser!!
        userMeUid = userMe!!.uid

        mDatabase = FirebaseDatabase.getInstance().reference
        mMessageReference = FirebaseDatabase.getInstance().getReference("users").child(userMeUid).child("unreadMessages")

        val messageEventListener = object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                return;
            }

            override fun onDataChange(p0: DataSnapshot?) {
                if (!p0!!.exists()){
                    return;
                }
                var unreadMessage = p0!!.getValue(Boolean::class.java)

                if (unreadMessage!!) {

                    val intent = Intent(applicationContext, MainActivity::class.java)
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK or  Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    intent.putExtra("fragment","ChatList")

                    val contentIntent = PendingIntent.getActivity(applicationContext, 1, intent, PendingIntent.FLAG_UPDATE_CURRENT)

                    val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

                    notificationBuilder = NotificationCompat.Builder(applicationContext,CHANNEL_ID)
                            .setContentTitle("Ci sono nuovi messaggi!")
                            .setSmallIcon(R.mipmap.ic_launcher)
                            .setSound(defaultSoundUri)
                            .setContentIntent(contentIntent)
                            .setAutoCancel(true)
                            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

                    notificationManager.notify(1, notificationBuilder.build())
                }
            }
        }

        mMessageReference!!.addValueEventListener(messageEventListener)

    }

}