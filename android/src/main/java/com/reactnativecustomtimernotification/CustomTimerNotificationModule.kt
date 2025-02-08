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
    fun TimerNotification(objectData: ReadableMap) {
        val payload = if (objectData.hasKey(Constants.NOTIFICATION.PAYLOAD)) objectData.getString(Constants.NOTIFICATION.PAYLOAD) else ""
        val title = if (objectData.hasKey(Constants.NOTIFICATION.TITLE)) objectData.getString(Constants.NOTIFICATION.TITLE) else "Default Title"
        val body = if (objectData.hasKey(Constants.NOTIFICATION.BODY)) objectData.getString(Constants.NOTIFICATION.BODY) else "Default Body"
        val subtitle = if (objectData.hasKey("subtitle")) objectData.getString("subtitle") else null
        val id = if (objectData.hasKey(Constants.NOTIFICATION.ID)) objectData.getInt(Constants.NOTIFICATION.ID) else 0
        val gifUrl = if (objectData.hasKey(Constants.NOTIFICATION.GIFFY_URl)) objectData.getString(Constants.NOTIFICATION.GIFFY_URl) else null
    
        val datetime = if (objectData.hasKey("date")) objectData.getString("date") else null
        val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)
    
        val endTime: Calendar = Calendar.getInstance()
        try {
            if (!datetime.isNullOrEmpty()) {
                endTime.time = sdf.parse(datetime) ?: Date()
            }
        } catch (e: Exception) {
            Log.e("TimerNotification", "Date parsing failed: ${e.message}")
            endTime.time = Date()  
        }
    
        val startTime = SystemClock.elapsedRealtime()
        val now = System.currentTimeMillis()
        val elapsed: Long = now - endTime.timeInMillis
        val remainingTime = maxOf(startTime - elapsed, 0L)  
    
        val notificationHelper = AnimatedNotificationManager(myContext)
        notificationHelper.showAnimatedNotification(
            NotificationConfig(
                gifUrl = gifUrl,
                title = title,
                subtitle = subtitle,
                body = body,
                payload = payload,
                notificationId = id,
                countdownDuration = remainingTime
            )
        )
    }
    


  @ReactMethod
  fun RemoveTimer(objectData:ReadableMap) {
    val id =objectData.getInt("id");
    val notificationHelper = AnimatedNotificationManager(myContext)
    notificationHelper.removeNotification(id);
  }

}

