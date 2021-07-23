package com.example.mightyid_notification

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.util.Log
import androidx.annotation.NonNull
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.mightyid_notification.constants.*
import com.example.mightyid_notification.models.RequestCall
import com.example.mightyid_notification.utils.IntentUtils.getInfoExtra
import com.example.mightyid_notification.utils.IntentUtils.putInfoExtra
import com.google.gson.Gson
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** MightyidNotificationPlugin */
class MightyidNotificationPlugin : BroadcastReceiver(), FlutterPlugin, MethodCallHandler {
    /// The MethodChannel that will the communication between Flutter and native Android
    ///
    /// This local reference serves to register the plugin with the Flutter Engine and unregister it
    /// when the Flutter Engine is detached from the Activity
    private lateinit var channel: MethodChannel
    private var applicationContext: Context? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "mightyid_notification")
        channel.setMethodCallHandler(this)
        IntentFilter().apply {
            addAction(CALL_STATUS_ACCEPTED)
            addAction(CALL_STATUS_REJECTED)
            addAction(BROADCAST_ACTION)
        }.also {
            LocalBroadcastManager.getInstance(applicationContext!!).registerReceiver(this,it)
        }
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        if (call.method == SHOW_CALL_NOTIFICATION) {
            @Suppress("UNCHECKED_CAST")
            val data = call.arguments as Map<String,String>
            Log.d(TAG, "onMethodCall: data receive: $data")
            applicationContext!!.showCallNotification(
                callId = data[CALL_ID] as String,
                meetingType = data[CALL_MEETING_TYPE] as String,
                callerName = data[CALLER_NAME] as String,
                photoUrl = data[CALLER_PHOTO_URL] as String,
                payload = data[CALL_PAYLOAD] as String
            )
            result.success(true)
        }
        else {
            result.success(false)
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        LocalBroadcastManager.getInstance(applicationContext!!).unregisterReceiver(this)
    }

    override fun onReceive(context: Context, intent: Intent?) {
        val callId = intent?.getStringExtra(CALL_ID)
        val payload = intent?.getInfoExtra<RequestCall>(CALL_PAYLOAD)
        when (intent?.action) {
            CALL_STATUS_ACCEPTED -> {
                Log.d("NotificationReceiver", "onReceive: CALL_STATE_ACCEPT")
                context.cancelNotification(callId!!.toInt().hashCode())
                payload!!.response = CALL_STATUS_ACCEPTED
                val dataSending = Gson().toJson(payload)
                channel.invokeMethod(CALL_STATUS_ACCEPTED, dataSending.toString())
            }

            CALL_STATUS_REJECTED -> {
                Log.d("NotificationReceiver", "onReceive: CALL_STATE_REJECT")
                context.cancelNotification(callId!!.toInt().hashCode())
                payload!!.response = CALL_STATUS_REJECTED
                val dataSending = Gson().toJson(payload)
                channel.invokeMethod(CALL_STATUS_REJECTED, dataSending.toString())
            }

            BROADCAST_ACTION ->{
                val mIntent = Intent(payload!!.response)
                mIntent.putInfoExtra(CALL_PAYLOAD,payload)
                LocalBroadcastManager.getInstance(applicationContext!!).sendBroadcast(mIntent)
            }
        }
    }
}
