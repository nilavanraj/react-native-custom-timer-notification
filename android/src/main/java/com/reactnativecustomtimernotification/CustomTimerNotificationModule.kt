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

    myContext.registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        try {
          val extras = intent.extras
          val params: WritableMap = Arguments.createMap()
          params.putInt("id", extras!!.getInt("id"))
          params.putString("action", extras!!.getString("action"))
          params.putString("payload", extras!!.getString("payload"))
          removeNotification(extras!!.getInt("id"),foregound)
          removedNotification = true
          sendEvent("notificationClick", params)
        } catch (e: Exception) {
          println(e)
        }
      }
    }, IntentFilter("NotificationEvent"))

  }

  private fun sendEvent(eventName: String, params: Any) {
    myContext.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
      .emit(
        eventName,
        params
      )
    Log.i("ReactSystemNotification", "NotificationModule: sendEvent (to JS): $eventName")
  }

  override fun getName(): String {
        return "CustomTimerNotification"
    }

  fun convert(n:String):String {
      if (n.length == 1) return "0" + n;
      return n
    }

  fun notificationPop(objectData:ReadableMap,remainingTime:String,visbleTimer:Boolean):NotificationCompat.Builder{
      val title = objectData.getString("title");
      val body = objectData.getString("body");
      val payload =  objectData.getString("payload");
      val id =objectData.getInt("id");
      val isCountDown = objectData.getBoolean("isCountDown")

      val setCustomContentView =
        if (objectData.hasKey("setCustomContentView"))
          objectData.getBoolean("setCustomContentView")
        else
          true;

      val datetime = objectData.getString("date")
      val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

      val startTime = SystemClock.elapsedRealtime()
      val endTime: Calendar = Calendar.getInstance()
      endTime.time = sdf.parse(datetime)

      val now = Date()
      val elapsed: Long = now.getTime() - endTime.timeInMillis
      val remainingTime = startTime - elapsed

      val intent = Intent(myContext, NotificationEventReceiver::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      intent.putExtra("id",id);
      intent.putExtra("action","press");
      intent.putExtra("payload",payload);
      var pendingIntent:PendingIntent? = null;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          pendingIntent = PendingIntent.getBroadcast(myContext, 0, intent, PendingIntent.FLAG_IMMUTABLE );
          } else {
          pendingIntent = PendingIntent.getBroadcast(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          }

      val onCancelIntent = Intent(myContext, OnClickBroadcastReceiver::class.java)
      onCancelIntent.putExtra("id",id);
      onCancelIntent.putExtra("action","cancel");
      onCancelIntent.putExtra("payload",payload);
      var onDismissPendingIntent:PendingIntent? = null;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        onDismissPendingIntent = PendingIntent.getBroadcast(
          myContext,
          0,
          onCancelIntent,
          PendingIntent.FLAG_IMMUTABLE  // Set the mutability flag to mutable
        );
      } else {
          onDismissPendingIntent =
            PendingIntent.getBroadcast(myContext, 0, onCancelIntent, 0) 
      }


      val notificationLayout = RemoteViews(packageName, R.layout.notification_open);
      notificationLayout.setTextViewText(R.id.title,title)
      notificationLayout.setTextViewText(R.id.text,body)

     // notificationLayout.setTextViewText(R.id.timer,remainingTime)
      notificationLayout.setChronometerCountDown(R.id.simpleChronometer, isCountDown);
      notificationLayout.setChronometer(R.id.simpleChronometer, remainingTime, ("%tM:%tS"), true);


//      try {
//        notificationLayout.setTextColor(R.id.timer , Color.parseColor(objectData.getString("timeColor")));
//      } catch (e:Exception){}

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val notificationChannel =
          NotificationChannel(channelId, "Timer", NotificationManager.IMPORTANCE_HIGH)
        notificationChannel.setDescription("countdown Timer")
        notificationChannel.enableLights(true)
        notificationChannel.setLightColor(Color.RED)
        notificationManager = myContext.getSystemService(NotificationManager::class.java)
        notificationManager.createNotificationChannel(notificationChannel)
      }
          val notificationBuilder:NotificationCompat.Builder =
            NotificationCompat.Builder(myContext,channelId)
          notificationBuilder.setAutoCancel(true)
            .setSmallIcon(myContext.getResources().getIdentifier("ic_launcher", "mipmap", myContext.getPackageName()))
            .setContentTitle(title)
            .setContentText(body)
            .setOnlyAlertOnce(true)
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(onDismissPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setWhen(endTime.getTimeInMillis());

    if(setCustomContentView)
      notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())

      val handler = Handler()
      if(isCountDown)
      handler.postDelayed({
        notificationLayout.setChronometerCountDown(R.id.simpleChronometer, true);
        notificationLayout.setChronometer(R.id.simpleChronometer, remainingTime, ("%tM:%tS"), false);
        try {
          val remove =objectData.getBoolean("remove");
          val foreground =objectData.getBoolean("foreground");
          if(remove){
            removeNotification(id,foreground)
          } else {
               notificationLayout.setViewVisibility (R.id.simpleChronometer,
                View.INVISIBLE)
          }
        } catch (e:Exception){
          println(e)
        }

        notificationBuilder.setCustomContentView(notificationLayout)
        if(!removedNotification)
        notificationManager.notify(id,notificationBuilder.build())
      }, Math.abs(elapsed))
          return notificationBuilder
  }

  fun updatePop(objectData:ReadableMap,remainingTime:String,visbleTimer:Boolean){
  val id =objectData.getInt("id");
  val notificationBuilder:NotificationCompat.Builder  = notificationPop(objectData,remainingTime,visbleTimer)
  notificationManager.notify(id,notificationBuilder.build())
}

  @ReactMethod
  fun RemoveTimer(objectData:ReadableMap) {
    val id =objectData.getInt("id");
    val foreground = objectData.getBoolean("foreground");

    removeNotification (id,foreground);
  }


  fun removeNotification (id:Int,foreground:Boolean) {
    val notificationManager = myContext.getSystemService(NotificationManager::class.java)
    if(foreground)
    ForegroundService.stopService(myContext)
    else
    notificationManager.cancel( id ) ;
  }
    @ReactMethod
    fun TimerNotification(objectData:ReadableMap) {

      val remainingSec:Int = (10000 / 1000).toInt()
      val min = convert((remainingSec/60).toInt().toString())
      val secInMin = convert((remainingSec%60).toInt().toString())
      val remainingTime:String = "$min : $secInMin"

      val foreground =objectData.getBoolean("foreground");

      if(foreground){
        foregound=true;
        ForegroundService.startService(myContext,objectData)
      } else
      updatePop(objectData,remainingTime,false)
      //promise.resolve(a * b)

    }

}

