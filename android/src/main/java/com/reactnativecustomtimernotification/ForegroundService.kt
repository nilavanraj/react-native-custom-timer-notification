package com.reactnativecustomtimernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import android.os.CountDownTimer
import android.graphics.Color
import android.app.Notification
import android.widget.RemoteViews;
import com.facebook.react.bridge.ReadableMap;

class ForegroundService : Service() {
  private val CHANNEL_ID = "255"

  companion object {
    fun startService(context: Context, objectData: ReadableMap,remainingTime:String) {
      val startIntent = Intent(context, ForegroundService::class.java)

      val title = objectData.getString("title");
      val body = objectData.getString("body");
      val payload = objectData.getString("payload");
      val id = objectData.getInt("id");
      startIntent.putExtra("id", id)
      startIntent.putExtra("remainingTime", remainingTime)
      startIntent.putExtra("title", title)
      startIntent.putExtra("body", body)
      startIntent.putExtra("payload", payload)

      ContextCompat.startForegroundService(context, startIntent)
    }
    fun stopService(context: Context) {
      val stopIntent = Intent(context, ForegroundService::class.java)
      context.stopService(stopIntent)
    }
  }
  fun getActiveNotifications (intent: Intent?):Notification{

    val title = intent?.getStringExtra("title");
    val body = intent?.getStringExtra("body");
    val payload =  intent?.getStringExtra("payload");
    val id:Int? = intent?.getIntExtra("id",0)
    println(id)

    val remainingTime =  intent?.getStringExtra("remainingTime");


    val intent = Intent(this, NotificationEventReceiver::class.java)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
    intent.putExtra("id",id);
    intent.putExtra("action","press");
    intent.putExtra("payload",payload);
    val pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);


    val onCancelIntent = Intent(this, OnClickBroadcastReceiver::class.java)
    onCancelIntent.putExtra("id",id);
    onCancelIntent.putExtra("action","cancel");
    onCancelIntent.putExtra("payload",payload);
    val onDismissPendingIntent =
      PendingIntent.getBroadcast(this, 0, onCancelIntent, 0)

    val notificationLayout = RemoteViews(packageName, R.layout.notification_collapsed);
    notificationLayout.setTextViewText(R.id.title,title)
    notificationLayout.setTextViewText(R.id.text,body)
    notificationLayout.setTextViewText(R.id.timer,remainingTime)

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
  override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
    val id:Int? = intent?.getIntExtra("id",0)

    startForeground(id!!,  getActiveNotifications(intent))
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


