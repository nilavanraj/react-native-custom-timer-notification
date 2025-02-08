package com.reactnativecustomtimernotification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import android.text.Html
import android.util.Log
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.math.abs
import android.app.PendingIntent
import android.content.Intent

import android.content.BroadcastReceiver
import com.facebook.react.bridge.WritableMap
import com.facebook.react.modules.core.DeviceEventManagerModule
import android.content.IntentFilter
import com.facebook.react.bridge.Arguments
import com.facebook.react.bridge.ReactApplicationContext
import androidx.core.content.ContextCompat


data class NotificationConfig(
    val notificationId: Int?,
    val gifUrl: String?,
    val title: String?, 
    val subtitle: String?, 
    val smallIcon: Int = android.R.drawable.ic_dialog_info,
    val countdownDuration: Long = 5000,
    val payload: String?,
    val body: String?
)


class AnimatedNotificationManager(
    private val context: ReactApplicationContext,
    private val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
) {
    companion object {
        private const val CHANNEL_ID = "animated_notification_channel"
        private const val NOTIFICATION_ID = 1
        private const val TAG = "AnimatedNotificationManager"
        private const val GIF_MEMORY_LIMIT_MB = 4
        private const val FRAME_INTERVAL_MS = 100
    }
    var disableCurrentNotification:Boolean = false
    init {
        createNotificationChannel()

        context.registerReceiver(object : BroadcastReceiver() {
          override fun onReceive(currentContext: Context, intent: Intent) {  
            try {
              val extras = intent.extras
              val params: WritableMap = Arguments.createMap()
              params.putString("id", extras!!.getString("id"))   
              params.putString("action", extras!!.getString("action"))    
              params.putString("payload", extras!!.getString("payload"))    
              Log.d(TAG, extras?.getString("payload")?:"")
              disableCurrentNotification = true
              context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter::class.java)
              .emit(
                "notificationClick",
                params
              )
            } catch (e: Exception) {
              Log.i("ReactSystemNotification error", e.toString())
            }
          }
        }, IntentFilter("NotificationEvent"),     ContextCompat.RECEIVER_EXPORTED)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Animated Notifications",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for animated and interactive notifications"
            }
            notificationManager.createNotificationChannel(channel)
        }
    }

    fun showAnimatedNotification(config: NotificationConfig) {
        val notificationConfig = config

        Log.d(TAG, "Initiating animated notification display")

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val notificationLayout = createNotificationLayout(notificationConfig)
                val notification = buildNotification(notificationLayout, notificationConfig)

                notificationManager.notify(NOTIFICATION_ID, notification.build())

                scheduleNotificationUpdate(notificationLayout, notification, notificationConfig)
            } catch (e: Exception) {
                Log.e(TAG, "Error displaying notification", e)
            }
        }
    }

    private suspend fun createNotificationLayout(config: NotificationConfig): RemoteViews = withContext(Dispatchers.Default) {

        val remoteViews = RemoteViews(context.packageName, R.layout.gen_notification_open)

        if(config.gifUrl !== null){
          val gifProcessor = GifProcessor()
          val frames = gifProcessor.processGif(config.gifUrl, memoryLimitMB = GIF_MEMORY_LIMIT_MB)

          frames.forEach { frame ->
              val frameView = RemoteViews(context.packageName, R.layout.giffy_image)
              frameView.setImageViewBitmap(R.id.frameImage, frame)
              frameView.setViewVisibility(R.id.frameImage, View.VISIBLE)
              remoteViews.addView(R.id.viewFlipper, frameView)
          }
          remoteViews.setInt(R.id.viewFlipper, "setFlipInterval", FRAME_INTERVAL_MS)
        } else {
          remoteViews.setViewVisibility(R.id.viewFlipperContainer, View.GONE)
        }
       
        configureNotificationText(remoteViews, config)
        configureChronometer(remoteViews, config.countdownDuration)

        return@withContext remoteViews
    }

    private fun configureNotificationText(remoteViews: RemoteViews, config: NotificationConfig) {
        val titleHtml = if(config.title != null) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(config.title, Html.FROM_HTML_MODE_COMPACT)
        } else {
            Html.fromHtml(config.title)
        } 
      } else null

        val bodyHtml = if(config.title != null) {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
              Html.fromHtml(config.body, Html.FROM_HTML_MODE_COMPACT)
          } else {
              Html.fromHtml(config.body)
          }
        } else null

        if(titleHtml != null)
        remoteViews.setTextViewText(R.id.title, titleHtml)

        if(bodyHtml != null)
        remoteViews.setTextViewText(R.id.body, bodyHtml)
    }

    private fun configureChronometer(remoteViews: RemoteViews, countdownDuration: Long) {
      if(countdownDuration !== null){
        val chronometerBaseTime = countdownDuration
        remoteViews.setChronometerCountDown(R.id.simpleChronometer, true)
        remoteViews.setChronometer(R.id.simpleChronometer, chronometerBaseTime, null, true)
      } else {
        remoteViews.setViewVisibility(R.id.simpleChronometer, View.GONE)
      }
        
    }

    private fun buildNotification(remoteViews: RemoteViews, config: NotificationConfig): NotificationCompat.Builder {
      val intent = Intent(context, NotificationEventReceiver::class.java)
      intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
      intent.putExtra("id",config.notificationId);
      intent.putExtra("action","press");
      intent.putExtra("payload",config.payload);
      Log.d(TAG, config?.payload ?: "")
      var pendingIntent:PendingIntent? = null;

      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
          pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_IMMUTABLE );
          } else {
          pendingIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
          }

          val onCancelIntent = Intent(context, OnClickBroadcastReceiver::class.java)
          onCancelIntent.putExtra("id",config.notificationId);
          onCancelIntent.putExtra("action","cancel");
          onCancelIntent.putExtra("payload", config.payload);
          var onDismissPendingIntent:PendingIntent? = null;
    
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            onDismissPendingIntent = PendingIntent.getBroadcast(
              context,
              0,
              onCancelIntent,
              PendingIntent.FLAG_IMMUTABLE  // Set the mutability flag to mutable
            );
          } else {
              onDismissPendingIntent =
                PendingIntent.getBroadcast(context, 0, onCancelIntent, 0) 
          }
    
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(config.smallIcon)
            .setCustomContentView(remoteViews)
            .setCustomBigContentView(remoteViews)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setDeleteIntent(onDismissPendingIntent)
            .apply {
              config.subtitle?.let { setSubText(it) }
            }
            .setContentIntent(pendingIntent)


    }

    private fun scheduleNotificationUpdate(
        remoteViews: RemoteViews,
        notification: NotificationCompat.Builder,
        config: NotificationConfig
    ) {
        val chronometerBaseTime = config.countdownDuration
        val currentTime = SystemClock.elapsedRealtime()
        val delay = abs(chronometerBaseTime - currentTime)
        disableCurrentNotification = false

        Handler(Looper.getMainLooper()).postDelayed({
          if(!disableCurrentNotification){
            Log.d(TAG, "Countdown complete, updating notification")
            remoteViews.setViewVisibility(R.id.simpleChronometer, View.GONE)
            notification.setCustomContentView(remoteViews)
            notificationManager.notify(NOTIFICATION_ID, notification.build())
          }
        }, delay)
    }

    fun removeNotification (id:Int) { 
      notificationManager.cancel( id ) ;
    }
}