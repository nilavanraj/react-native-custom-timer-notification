package com.reactnativecustomtimernotification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent


class OnClickBroadcastReceiver : BroadcastReceiver() {
  override fun onReceive(context: Context, intent: Intent) {
    val broadcastIntent = Intent("NotificationEvent")
    val extras = intent.extras
println(extras!!.getString("action"))
   broadcastIntent.putExtra("id", extras!!.getInt("id"))
   broadcastIntent.putExtra("action", extras!!.getString("action"))
   broadcastIntent.putExtra("payload", extras!!.getString("payload"))
    context.sendBroadcast(broadcastIntent)

  }
}
