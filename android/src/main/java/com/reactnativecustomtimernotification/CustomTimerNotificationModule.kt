package com.reactnativecustomtimernotification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Color
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.CountDownTimer
import android.os.Handler
import android.os.SystemClock
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import java.util.*
import kotlin.time.milliseconds


class CustomTimerNotificationModule: ReactContextBaseJavaModule {
  var loading : Boolean = false;
  var foregound : Boolean = false;

  lateinit var notificationManager: NotificationManager
  lateinit var builder: Notification.Builder
  val channelId:String = "255"
  var packageName: String = ""
var removedNotification = false;
  lateinit var myContext: ReactApplicationContext;

  constructor (context:ReactApplicationContext):super(context){
      this.loading= true;
      this.myContext= context
      this.packageName=this.myContext.getPackageName()

  }


  override fun getName(): String {
        return "CustomTimerNotification"
    }

 
    @ReactMethod
    fun TimerNotification(objectData:ReadableMap) {
      val payload = objectData.getString(Constants.NOTIFICATION.PAYLOAD);
      val title = objectData.getString(Constants.NOTIFICATION.TITLE);
      val body = objectData.getString(Constants.NOTIFICATION.BODY);
      val id = objectData.getInt(Constants.NOTIFICATION.ID);
      val gifUrl =  objectData.getString(Constants.NOTIFICATION.GIFFY_URl)
      val notificationHelper = AnimatedNotificationManager(myContext)

      val datetime = objectData.getString("date")
      val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

      val startTime = SystemClock.elapsedRealtime()
      val endTime: Calendar = Calendar.getInstance()
      endTime.time = sdf.parse(datetime)

      val now = Date()
      val elapsed: Long = now.getTime() - endTime.timeInMillis
      val remainingTime = startTime - elapsed

      notificationHelper.showAnimatedNotification(NotificationConfig(gifUrl = gifUrl, title=title,  subtitle=body, payload=payload, notificationId=id, countdownDuration = remainingTime))
    }


  @ReactMethod
  fun RemoveTimer(objectData:ReadableMap) {
    val id =objectData.getInt("id");
    val notificationHelper = AnimatedNotificationManager(myContext)
    notificationHelper.removeNotification(id);
  }

}

