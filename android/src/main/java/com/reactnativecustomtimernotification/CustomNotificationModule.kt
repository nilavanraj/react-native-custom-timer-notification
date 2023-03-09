package com.reactnativecustomtimernotification

import android.app.*
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Typeface
import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import android.os.Handler
import android.os.SystemClock
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import java.util.*


class CustomNotificationModule: ReactContextBaseJavaModule {
  var loading : Boolean = false;
  lateinit var notificationManager: NotificationManager
  lateinit var builder: Notification.Builder
  val channelId:String = "255"
  var packageName: String = ""
  var myContext: ReactApplicationContext;
  val imageLayouts = arrayOf(R.layout.image_view_layout1, R.layout.image_view_layout);
  val imageViews = arrayOf(R.id.imageView2, R.id.imageView1);
  var imageCount = 0 ;
  lateinit var notificationBuilder:NotificationCompat.Builder;

  constructor (context:ReactApplicationContext):super(context){
      this.loading= true;
      this.myContext= context
      this.packageName=this.myContext.getPackageName()
  }

    override fun getName(): String {
        return "CustomNotificationModule"
    }

    @ReactMethod
    fun CustomNotification(objectData:ReadableMap,callback: Callback) {
      try{
        imageCount = 0;
        val payload = objectData.getString(Constants.NOTIFICATION.PAYLOAD);
        val title = objectData.getString(Constants.NOTIFICATION.TITLE);
        val body = objectData.getString(Constants.NOTIFICATION.BODY);
        val id = objectData.getInt(Constants.NOTIFICATION.ID);

        val intent = Intent(myContext, NotificationEventReceiver::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("id",id);
        intent.putExtra("action","press");
        intent.putExtra("payload",payload);
        var pendingIntent:PendingIntent? = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        pendingIntent = PendingIntent.getBroadcast(myContext, 0, intent, PendingIntent.FLAG_IMMUTABLE);
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
          PendingIntent.FLAG_MUTABLE // Set the mutability flag to mutable
        );
      } else {
          onDismissPendingIntent =
            PendingIntent.getBroadcast(myContext, 0, onCancelIntent, 0) 
      }

        val notificationLayout = RemoteViews(packageName, R.layout.notification_collapsed);

        // View Array
        val View = objectData.getArray(Constants.NOTIFICATION.VIEW);
        for (i in 0..(View?.size()?.minus(1) ?: 0)) {
          val item = View?.getMap(i);
          if(item!=null && item.hasKey(Constants.VIEW.TYPE) && item.getInt(Constants.VIEW.TYPE)==1){
            Log.i("imageCount",imageCount.toString())

            setImageView(item, notificationLayout,imageCount);
            imageCount+=1;
          }
          else if(item!=null && item.hasKey(Constants.VIEW.TYPE) && item.getInt(Constants.VIEW.TYPE)==2){
            setTextView(item,notificationLayout);
          }
          else if(item!=null && item.hasKey(Constants.VIEW.TYPE) && item.getInt(Constants.VIEW.TYPE)==3) {
            setChronometer(id,item, notificationLayout);
          }

          // textView.setString(R.id.imageView1, "layout_width", "34dp");
          // textView.setViewPadding (Align.CENTER)
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,"Lineup",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setDescription("Lineup out")
            notificationChannel.enableLights(true)
            notificationChannel.setLightColor(Color.RED)
            notificationManager = myContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)


        } else {
          notificationManager = myContext.getSystemService(NotificationManager::class.java)
        }
        notificationBuilder =
          NotificationCompat.Builder(myContext,channelId)
        notificationBuilder.setAutoCancel(true)
          .setWhen(System.currentTimeMillis())
          .setSmallIcon(myContext.getResources().getIdentifier("ic_launcher", "mipmap", myContext.getPackageName()))
          .setContentTitle(title)
          .setContentText(body)
          .setOnlyAlertOnce(true)
          .setCustomContentView(notificationLayout)
          .setContentIntent(pendingIntent)
          .setDeleteIntent(onDismissPendingIntent)
          .setPriority(NotificationCompat.PRIORITY_HIGH);
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            notificationBuilder.setStyle(NotificationCompat.DecoratedCustomViewStyle())
          
        notificationManager.notify(id,notificationBuilder.build());

    } catch (e:Exception) {
        callback.invoke(e.toString());
        Log.e("Notification crash",e.toString())
        // throw RuntimeException("Notification crash",e)
    }
    }
  private fun setPadding(item:ReadableMap?, View:RemoteViews,path:Int){
    var paddingLeft: Int = 0;
    var paddingTop: Int = 0;
    var paddingBottom: Int = 0;
    var paddingRight: Int = 0;


    if(item!=null&&item.hasKey(Constants.PADDING.TOP))
      paddingTop = item.getInt(Constants.PADDING.TOP);
    if(item!=null&&item.hasKey(Constants.PADDING.LEFT))
      paddingLeft = item.getInt(Constants.PADDING.LEFT);
    if(item!=null&&item.hasKey(Constants.PADDING.RIGHT))
      paddingRight = item.getInt(Constants.PADDING.RIGHT);
    if(item!=null&&item.hasKey(Constants.PADDING.BOTTOM))
      paddingBottom = item.getInt(Constants.PADDING.BOTTOM);
    View.setViewPadding(
      path,
      paddingLeft ?: 0,
      paddingTop ?: 0,
      paddingRight ?: 0,
      paddingBottom ?: 0
    );
  }
  private fun setTextView(item:ReadableMap?, notificationLayout:RemoteViews){
    val textView = RemoteViews(myContext.getPackageName(), R.layout.text_view_layout)
    var bold = 0;
    if(item!=null && item.hasKey(Constants.VIEW.BOLD)){
      bold = item.getInt(Constants.VIEW.BOLD)
    }
    val s = SpannableString(item?.getString("name"))
    Typeface.BOLD
    s.setSpan(StyleSpan(bold), 0, s.length, 0)
    textView.setTextViewText(R.id.textView1, s)

    val float:Float=item?.getDouble(Constants.VIEW.SIZE)?.toFloat()!!
    textView.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_SP, float);
    textView.setTextColor(R.id.textView1,Color.parseColor(item?.getString("color")));
    if(item?.getBoolean("setViewVisibility"))
      textView.setViewVisibility (R.id.textView1,
        View.INVISIBLE)
    setPadding(item,textView,R.id.textView1);
    notificationLayout.addView(R.id.main, textView)
  }
  private fun setImageView(item:ReadableMap?, notificationLayout:RemoteViews,i:Int){
    val remoteLocalImage =
      RemoteViews(myContext.getPackageName(), imageLayouts[i]);
    val url: String? = item?.getString("uri");
    val decodedString: ByteArray = Base64.decode(url, Base64.DEFAULT)
    val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    notificationLayout.addView(R.id.main, remoteLocalImage);
    notificationLayout.setImageViewBitmap(imageViews[i], decodedByte);
    setPadding(item, remoteLocalImage,imageViews[i]);
  }
  private fun setChronometer(id:Int, item:ReadableMap?, notificationLayout:RemoteViews){
    if(item!=null) {
      val datetime = item.getString(Constants.NOTIFICATION.ZEROTIME);
      val remoteLocalChronometer =
        RemoteViews(myContext.getPackageName(), R.layout.chronometer_view_layout);
      notificationLayout.addView(R.id.main, remoteLocalChronometer)
      val float:Float=item?.getDouble(Constants.VIEW.SIZE)?.toFloat()!!
      remoteLocalChronometer.setTextViewTextSize(R.id.timerCustom, TypedValue.COMPLEX_UNIT_SP, float);
      remoteLocalChronometer.setTextColor(R.id.timerCustom,Color.parseColor(item?.getString("color")));
      val sdf = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.ENGLISH)

      val startTime = SystemClock.elapsedRealtime()
      val endTime: Calendar = Calendar.getInstance()
      endTime.time = sdf.parse(datetime)

      val now = Date()
      val elapsed: Long = now.getTime() - endTime.timeInMillis
      val remainingTime = startTime - elapsed

      notificationLayout.setChronometerCountDown(R.id.timerCustom, true);
      notificationLayout.setChronometer(
        R.id.timerCustom,
        remainingTime,
        ("%tM:%tS"),
        true
      );
      setPadding(item, remoteLocalChronometer, R.id.timerCustom);
      val handler = Handler()

      handler.postDelayed({
          notificationLayout.setChronometerCountDown(R.id.timerCustom, true);
          notificationLayout.setChronometer(R.id.timerCustom, remainingTime, ("%tM:%tS"), false);
        notificationLayout.setViewVisibility (R.id.timerCustomLayout, View.GONE)
        notificationBuilder.setCustomContentView(notificationLayout)
            notificationManager.notify(id,notificationBuilder.build())
        }, Math.abs(elapsed))
    }
  }
}


