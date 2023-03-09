package com.reactnativecustomtimernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.graphics.Color
import android.app.Notification
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.*
import android.view.View
import android.widget.RemoteViews;
import androidx.annotation.RequiresApi
import com.facebook.react.bridge.ReadableMap;
import java.util.*

class ForegroundService : Service() {
  private val CHANNEL_ID = "255"

  companion object {
    fun startService(context: Context, objectData: ReadableMap) {
      val startIntent = Intent(context, ForegroundService::class.java)

      val title = objectData.getString("title");
      val body = objectData.getString("body");
      val payload = objectData.getString("payload");
      val id = objectData.getInt("id");
      val datetime = objectData.getString("date")
      val isCountDown = objectData.getBoolean("isCountDown")

      try {
        val remove =objectData.getBoolean("remove");
        startIntent.putExtra("remove", remove)
      } catch (e:Exception){}

      startIntent.putExtra("date", datetime)
      startIntent.putExtra("id", id)
      startIntent.putExtra("title", title)
      startIntent.putExtra("body", body)
      startIntent.putExtra("payload", payload)
      startIntent.putExtra("isCountDown", isCountDown)

      ContextCompat.startForegroundService(context, startIntent)
    }
    fun stopService(context: Context) {
      val stopIntent = Intent(context, ForegroundService::class.java)
      context.stopService(stopIntent)
    }
  }
  fun getActiveNotifications (intent: Intent?,displayerTimer:Boolean,remainingTime:Long):Notification{

    val title = intent?.getStringExtra("title");
    val body = intent?.getStringExtra("body");
    val payload =  intent?.getStringExtra("payload");
    val id:Int? = intent?.getIntExtra("id",0)
    val isCountDown:Boolean? = intent?.getBooleanExtra("isCountDown",false)



    val intent = Intent(this, NotificationEventReceiver::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("id",id);
    intent.putExtra("action","press");
    intent.putExtra("payload",payload);
    var pendingIntent:PendingIntent? = null;

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
    pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE);
    } else {
    pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }

    val onCancelIntent = Intent(this, OnClickBroadcastReceiver::class.java)
    onCancelIntent.putExtra("id",id);
    onCancelIntent.putExtra("action","cancel");
    onCancelIntent.putExtra("payload",payload);
    var onDismissPendingIntent:PendingIntent? = null;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        onDismissPendingIntent = PendingIntent.getBroadcast(
          this,
          0,
          onCancelIntent,
          PendingIntent.FLAG_MUTABLE // Set the mutability flag to mutable
        );
      } else {
          onDismissPendingIntent =
            PendingIntent.getBroadcast(this, 0, onCancelIntent, 0) 
      }

    val notificationLayout = RemoteViews(packageName, R.layout.notification_open);
    notificationLayout.setTextViewText(R.id.title,title)
    notificationLayout.setTextViewText(R.id.text,body)
if(displayerTimer){
  notificationLayout.setChronometerCountDown(R.id.simpleChronometer, isCountDown!!);
  notificationLayout.setChronometer(R.id.simpleChronometer, remainingTime, ("%tM:%tS"), true);
} else {
  notificationLayout.setChronometerCountDown(R.id.simpleChronometer, isCountDown!!);
  notificationLayout.setChronometer(R.id.simpleChronometer, remainingTime, ("%tM:%tS"), false);
}

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val notificationChannel =
        NotificationChannel(CHANNEL_ID, "Timer", NotificationManager.IMPORTANCE_HIGH)
      notificationChannel.setDescription("countdown Timer")
      notificationChannel.enableLights(true)
      notificationChannel.setLightColor(Color.RED)
     val notificationManager = this.getSystemService(NotificationManager::class.java)
      notificationManager.createNotificationChannel(notificationChannel)
    }
    val notification = NotificationCompat.Builder(this, CHANNEL_ID)
      .setAutoCancel(true)
      .setWhen(System.currentTimeMillis())
      .setSmallIcon(this.getResources().getIdentifier("ic_launcher", "mipmap", this.getPackageName()))
      .setContentTitle(title)
      .setContentText(body)
      .setOnlyAlertOnce(true)
      .setStyle(NotificationCompat.DecoratedCustomViewStyle())
      .setCustomContentView(notificationLayout)
      .setContentIntent(pendingIntent)
      .setDeleteIntent(onDismissPendingIntent)
      .setPriority(NotificationCompat.PRIORITY_HIGH)
      .build();


    return notification
  }
  @RequiresApi(Build.VERSION_CODES.N)
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val id:Int? = intent?.getIntExtra("id",0)
    val isCountDown:Boolean = intent!!.getBooleanExtra("isCountDown",false)

    val datetime = intent?.getStringExtra("date")
    val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

    val startTime = SystemClock.elapsedRealtime()
    val endTime: Calendar = Calendar.getInstance()
    endTime.time = sdf.parse(datetime)

    val now = Date()
    val elapsed: Long = now.getTime() - endTime.timeInMillis
    val remainingTime = startTime - elapsed

    val handler = Handler()

    if(isCountDown)
    handler.postDelayed({
      try {
        val remove =intent?.getBooleanExtra("remove",false);
        if(remove!!){
          stopService(this)
        }else{
          startForeground(id!!,getActiveNotifications(intent,false,remainingTime))
        }
      } catch (e:Exception){}
    }, Math.abs(elapsed))


    startForeground(id!!,  getActiveNotifications(intent,true,remainingTime))
    return START_NOT_STICKY
  }

  override fun onBind(intent: Intent): IBinder? {
    return null
  }
  private fun createNotificationChannel() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      val serviceChannel = NotificationChannel(CHANNEL_ID, "Foreground Service Channel",
        NotificationManager.IMPORTANCE_DEFAULT)
      val manager = getSystemService(NotificationManager::class.java)
      manager!!.createNotificationChannel(serviceChannel)
    }
  }
}


