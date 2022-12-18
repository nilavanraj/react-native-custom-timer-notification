package com.reactnativecustomtimernotification

import android.app.*
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.View
import android.widget.FrameLayout
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.facebook.react.bridge.Callback
import com.facebook.react.bridge.ReactApplicationContext
import com.facebook.react.bridge.ReactContextBaseJavaModule
import com.facebook.react.bridge.ReactMethod
import com.facebook.react.bridge.ReadableMap
import android.util.Base64;


class CustomNotificationModule: ReactContextBaseJavaModule {
  var loading : Boolean = false;
  lateinit var notificationManager: NotificationManager
  lateinit var builder: Notification.Builder
  val channelId:String = "255"
  var packageName: String = ""
  lateinit var myContext: ReactApplicationContext;

  constructor (context:ReactApplicationContext):super(context){
      this.loading= true;
      this.myContext= context
      this.packageName=this.myContext.getPackageName()
  }

    override fun getName(): String {
        return "CustomNotificationModule"
    }
  private fun adjustViewHeight(resID: Int, pixels: Int, activity: Activity) {
    (activity.findViewById<View>(resID).layoutParams as FrameLayout.LayoutParams).bottomMargin =
      pixels
    activity.findViewById<View>(resID).invalidate()
    activity.findViewById<View>(resID).requestLayout()
  }

    @ReactMethod
    fun multiply(objectData:ReadableMap,callback: Callback) {
      try{

        val payload = objectData.getString("payload");
        val title = objectData.getString("title");
        val body = objectData.getString("body");
        val id =objectData.getInt("id");
        val text = objectData.getArray("TextView");
        val imageView = objectData.getArray("ImageView");

        val intent = Intent(myContext, CustomTimerNotificationPackage::class.java);
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        intent.putExtra("evenData",payload);
        val pendingIntent = PendingIntent.getActivity(myContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        val notificationLayout = RemoteViews(packageName, R.layout.notification_collapsed);

        for (i in 0..(text?.size()?.minus(1) ?: 0)) {
          val item = text?.getMap(i);
          val textView = RemoteViews(myContext.getPackageName(), R.layout.text_view_layout)
          textView.setTextViewText(R.id.textView1, item?.getString("name"))

          val float:Float=item?.getDouble("size")?.toFloat()!!
          textView.setTextViewTextSize(R.id.textView1, TypedValue.COMPLEX_UNIT_SP, float);
          textView.setTextColor(R.id.textView1,Color.parseColor("#0099cc"));
          if(item?.getBoolean("setViewVisibility"))
          textView.setViewVisibility (R.id.textView1,
            View.INVISIBLE)



          try {
                      // textView.setString(R.id.textView1, "layout_marginLeft", "34dp");
            val PaddingLeft: Int? = item?.getInt("PaddingLeft");
            val PaddingTop: Int? = item.getInt("PaddingTop");
            val PaddingRight: Int? = item.getInt("PaddingRight");
            val PaddingBottom: Int? = item.getInt("PaddingBottom");

            textView.setViewPadding(
              R.id.textView1,
              PaddingLeft ?: 0,
              PaddingTop ?: 0,
              PaddingRight ?: 0,
              PaddingBottom ?: 0
            );
          } catch (e:Exception){
          println(e)
          }
          notificationLayout.addView(R.id.main, textView)
          // notificationLayout.setImageViewBitmap(R.id.imageView1,image1)

         }
            val arrayname = arrayOf(R.layout.image_view_layout1, R.layout.image_view_layout);
            val arrayname1 = arrayOf(R.id.imageView2, R.id.imageView1);

            if (false) {
          val item = imageView?.getMap(0);
          val textView = RemoteViews(myContext.getPackageName(),R.layout.image_view_layout);
          val url:String?=item?.getString("url");
          val imageBitMap = BitmapFactory.decodeStream(java.net.URL(url).openStream());
          val float:Float=item?.getDouble("size")?.toFloat()!!
          notificationLayout.setImageViewBitmap(R.id.imageView1,imageBitMap);

          try {
             // textView.setString(R.id.imageView1, "layout_width", "34dp");
            val PaddingLeft: Int? = item?.getInt("PaddingLeft");
            val PaddingTop: Int? = item?.getInt("PaddingTop");
            val PaddingRight: Int? = item?.getInt("PaddingRight");
            val PaddingBottom: Int? = item?.getInt("PaddingBottom");

            textView.setViewPadding(
              R.id.imageView1,
              PaddingLeft ?: 0,
              PaddingTop ?: 0,
              PaddingRight ?: 0,
              PaddingBottom ?: 0
            );
          } catch (e:Exception){
            println(e)
          }
          //textView.setViewPadding (Align.CENTER)
          notificationLayout.addView(R.id.main, textView)
         // notificationLayout.setImageViewBitmap(R.id.imageView1,image1)

        }
        if (true) {
          for (i in 0..(text?.size()?.minus(1) ?: 0)) {
            val item = imageView?.getMap(i);
            val remoteLocalImage =
              RemoteViews(myContext.getPackageName(), arrayname[i]);
            val url: String? = item?.getString("url");
            val decodedString: ByteArray = Base64.decode(url, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            notificationLayout.addView(R.id.main, remoteLocalImage);
            notificationLayout.setImageViewBitmap(arrayname1[i], decodedByte);
            try {
              // textView.setString(R.id.imageView1, "layout_width", "34dp");
              val PaddingLeft: Int? = item?.getInt("PaddingLeft");
              val PaddingTop: Int? = item?.getInt("PaddingTop");
              val PaddingRight: Int? = item?.getInt("PaddingRight");
              val PaddingBottom: Int? = item?.getInt("PaddingBottom");

              remoteLocalImage.setViewPadding(
                R.id.imageView1,
                PaddingLeft ?: 0,
                PaddingTop ?: 0,
                PaddingRight ?: 0,
                PaddingBottom ?: 0
              );
            } catch (e:Exception){
              println(e)
            }
          }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(channelId,"Lineup",NotificationManager.IMPORTANCE_HIGH)
            notificationChannel.setDescription("Lineup out")
            notificationChannel.enableLights(true)
            notificationChannel.setLightColor(Color.RED)
            notificationManager = myContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(notificationChannel)
                val notificationBuilder:NotificationCompat.Builder =
                NotificationCompat.Builder(myContext,channelId)
                notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(myContext.getResources().getIdentifier("ic_launcher", "mipmap", myContext.getPackageName()))
                .setContentTitle(title)
                .setContentText(body)
                .setTicker("hearty")
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationManager.notify(id,notificationBuilder.build())

        } else {

          notificationManager = myContext.getSystemService(NotificationManager::class.java)

                val notificationBuilder:NotificationCompat.Builder =
                NotificationCompat.Builder(myContext,channelId)
                notificationBuilder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                  .setSmallIcon(myContext.getResources().getIdentifier("ic_launcher", "mipmap", myContext.getPackageName()))
                .setContentTitle(title)
                .setContentText(body)
                .setTicker("hearty")
                .setCustomContentView(notificationLayout)
                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH);
                notificationManager.notify(id,notificationBuilder.build())

        }
    } catch (e:Exception) {
        Log.i("Notification crash",e.toString())
        callback.invoke(e.toString());
    }

      //promise.resolve(a * b)

    }


}


