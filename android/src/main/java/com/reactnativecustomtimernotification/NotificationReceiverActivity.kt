package com.reactnativecustomtimernotification

import android.app.ActivityManager
import android.app.ActivityManager.RunningAppProcessInfo
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log


/**
 * Handles user's interaction on notifications.
 *
 * Sends broadcast to the application, launches the app if needed.
 */
class NotificationEventReceiver() : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val extras = intent.extras
    assert(extras != null)
    Log.i(
      "ReactSystemNotification", "NotificationEventReceiver: Received: " + extras!!.getString(
        ACTION
      )
        + ", Notification ID: " + extras.getInt(NOTIFICATION_ID) + ", payload: " + extras.getString(
        PAYLOAD
      )
    )

    // If the application is not running or is not in foreground, start it with the
    // notification
    // passed in
    if (!applicationIsRunning(context)) {
      val packageName = context.applicationContext.packageName
      val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
      assert(launchIntent != null)
      launchIntent!!.putExtra("initialSysNotificationId", extras.getInt(NOTIFICATION_ID))
      launchIntent.putExtra("initialSysNotificationAction", extras.getString(ACTION))
      launchIntent.putExtra("initialSysNotificationPayload", extras.getString(PAYLOAD))
      launchIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
      context.startActivity(launchIntent)
      Log.i("ReactSystemNotification", "NotificationEventReceiver: Launching: $packageName")

    } else {
      println("dfdfdfdf")
      sendBroadcast(
        context,
        extras
      ) // If the application is already running in foreground, send a broadcast too
    }
  }

  private fun sendBroadcast(context: Context, extras: Bundle?) {
    val broadcastIntent = Intent("NotificationEvent")
    broadcastIntent.putExtra("id", extras!!.getInt(NOTIFICATION_ID))
    broadcastIntent.putExtra("action", extras.getString(ACTION))
    broadcastIntent.putExtra("payload", extras.getString(PAYLOAD))
    context.sendBroadcast(broadcastIntent)
    Log.v(
      "ReactSystemNotification",
      "NotificationEventReceiver: Broadcast Sent: NotificationEvent: " + extras.getString(ACTION)
        + ", Notification ID: " + extras.getInt(NOTIFICATION_ID) + ", payload: "
        + extras.getString(PAYLOAD)
    )
  }

  private fun applicationIsRunning(context: Context): Boolean {
    val activityManager: ActivityManager? =
      context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
    assert(activityManager != null)
    val processInfoList = activityManager!!.runningAppProcesses
    for (processInfo: RunningAppProcessInfo in processInfoList) {
      if ((processInfo.processName == context.applicationContext.packageName)) {
        if (processInfo.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
          for (d: String in processInfo.pkgList) {
            Log.v("ReactSystemNotification", "NotificationEventReceiver: ok: $d")
            return true
          }
        }
      }
    }
    return false
  }

  companion object {
    val NOTIFICATION_ID = "id"
    val ACTION = "action"
    val PAYLOAD = "payload"
  }
}
