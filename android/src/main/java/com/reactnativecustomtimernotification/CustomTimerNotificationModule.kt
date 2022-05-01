package com.reactnativecustomtimernotification


import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.CountDownTimer
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import org.json.JSONObject
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;


class CustomTimerNotificationModule: ReactContextBaseJavaModule {
  var loading : Boolean = false;
  var firstForegound : Boolean = true;
  var ifCancel = false;
  lateinit var notificationManager: NotificationManager
  lateinit var builder: Notification.Builder
  val channelId:String = "255"
  var packageName: String = ""

  lateinit var myContext: ReactApplicationContext;

  constructor (context:ReactApplicationContext):super(context){
      this.loading= true;
      this.myContext= context
      this.packageName=this.myContext.getPackageName()

    myContext.registerReceiver(object : BroadcastReceiver() {
      override fun onReceive(context: Context, intent: Intent) {
        try {

          ifCancel = true
          val extras = intent.extras
          val params: WritableMap = Arguments.createMap()
          params.putInt("id", extras!!.getInt("id"))
          params.putString("action", extras!!.getString("action"))
          params.putString("payload", extras!!.getString("payload"))
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

      val intent = Intent(myContext, NotificationEventReceiver::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      intent.putExtra("id",id);
      intent.putExtra("action","press");
      intent.putExtra("payload",payload);
      val pendingIntent = PendingIntent.getBroadcast(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

      val onCancelIntent = Intent(myContext, OnClickBroadcastReceiver::class.java)
      onCancelIntent.putExtra("id",id);
      onCancelIntent.putExtra("action","cancel");
      onCancelIntent.putExtra("payload",payload);
      val onDismissPendingIntent =
        PendingIntent.getBroadcast(myContext, 0, onCancelIntent, 0)

      val notificationLayout = RemoteViews(packageName, R.layout.notification_collapsed);
      notificationLayout.setTextViewText(R.id.title,title)
      notificationLayout.setTextViewText(R.id.text,body)
      notificationLayout.setTextViewText(R.id.timer,remainingTime)

//      try {
//        notificationLayout.setTextColor(R.id.timer , Color.parseColor(objectData.getString("timeColor")));
//      } catch (e:Exception){}

      if(visbleTimer){
        notificationLayout.setViewVisibility (R.id.timer,
          View.INVISIBLE)
      }

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
            .setWhen(System.currentTimeMillis())
            .setSmallIcon(myContext.getResources().getIdentifier("ic_launcher", "mipmap", myContext.getPackageName()))
            .setContentTitle(title)
            .setContentText(body)
            .setOnlyAlertOnce(true)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setCustomContentView(notificationLayout)
            .setContentIntent(pendingIntent)
            .setDeleteIntent(onDismissPendingIntent)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
          return notificationBuilder

  }
fun updatePop(objectData:ReadableMap,remainingTime:String,visbleTimer:Boolean){
  val id =objectData.getInt("id");
  val notificationBuilder:NotificationCompat.Builder  = notificationPop(objectData,remainingTime,visbleTimer)
  notificationManager.notify(id,notificationBuilder.build())
}
  fun removeNotification (id:Int,foreground:Boolean) {

    val notificationManager = myContext.getSystemService(NotificationManager::class.java)
    if(foreground)
    ForegroundService.stopService(myContext)
    else
    notificationManager.cancel( id ) ;
  }
  fun countdown(objectData:ReadableMap,sec:Int) {
    val secLong = sec.toLong()*1000;
    val id =objectData.getInt("id");
    val foreground =objectData.getBoolean("foreground");

    object : CountDownTimer(secLong, 1000) {
      override fun onTick(millisUntilFinished: Long) {
        val remainingSec:Int = (millisUntilFinished / 1000).toInt()
        val min = convert((remainingSec/60).toInt().toString())
        val secInMin = convert((remainingSec%60).toInt().toString())
        val remainingTime:String = "$min : $secInMin"
        if(ifCancel){
          cancel();
          if(foreground){
            ForegroundService.stopService(myContext)
          }
        }
        else{
          if(foreground&&firstForegound){
            firstForegound=false
            ForegroundService.startService(myContext,objectData,remainingTime)
          } else
          updatePop(objectData,remainingTime,false)

        }
      }

      override fun onFinish() {
        try {
          val remove =objectData.getBoolean("remove");
          if(remove){
            removeNotification(id,foreground)
          } else {
            println("onFinish")
            updatePop(objectData,"",true)
          }
        } catch (e:Exception){}

      }
    }.start()
  }

    @ReactMethod
    fun TimerNotification(objectData:ReadableMap) {
      firstForegound=true;
      ifCancel = false;
      val sec =objectData.getInt("sec");
      countdown(objectData,sec)
      //promise.resolve(a * b)

    }

}

