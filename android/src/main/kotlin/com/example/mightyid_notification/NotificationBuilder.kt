package com.example.mightyid_notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.net.Uri
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ForegroundColorSpan
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.mightyid_notification.constants.*
import com.example.mightyid_notification.models.RequestCall
import com.example.mightyid_notification.utils.IntentUtils.putInfoExtra
import com.google.gson.Gson

fun Context.showCallNotification(
    callId: String,
    meetingType: String,
    callerName: String,
    photoUrl: String?,
    payload: String
) {
    Log.d(TAG, "showCallNotification: called")
    val notificationManager = NotificationManagerCompat.from(ContextHolder.getApplicationContext())
    val ringtone: Uri = RingtoneManager.getActualDefaultRingtoneUri(
        ContextHolder.getApplicationContext(),
        RingtoneManager.TYPE_RINGTONE
    )
    val mPayload = Gson().fromJson(payload,RequestCall::class.java)

    val lockedScreenAction = Intent(this, IncomingInvitationActivity::class.java)
    lockedScreenAction.apply {
        putExtra("call_id", callId)
        putExtra("meeting_type", meetingType)
        putExtra("caller_name", callerName)
        putExtra("photo_url", photoUrl)
        putInfoExtra("payload", payload)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }

    val lockScreenIntent = PendingIntent.getActivity(
        applicationContext,
        callId.hashCode(),
        lockedScreenAction,
        PendingIntent.FLAG_UPDATE_CURRENT
    )
    val contentText = "Incoming MightyID" + if (meetingType == "video") "Video call" else "Call"
    val notificationBuilder = NotificationCompat.Builder(this, CALL_CHANNEL_ID)
    notificationBuilder
        .setSmallIcon(R.drawable.notification_icon)
        .setDefaults(NotificationCompat.DEFAULT_VIBRATE)
        .setContentTitle(callerName)
        .setContentText(contentText)
        .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
        .setAutoCancel(true)
        .setOngoing(true)
        .setCategory(NotificationCompat.CATEGORY_CALL)
        .setPriority(NotificationCompat.PRIORITY_MAX)
        .setTimeoutAfter(60000)
        .setFullScreenIntent(lockScreenIntent, true)

    addRejectCallAction(notificationBuilder, callId, mPayload)

    addAcceptCallAction(notificationBuilder, callId, mPayload)

    setNotificationColor(this, notificationBuilder)

    createCallNotificationChannel(notificationManager, ringtone)
    notificationManager.notify(callId.toInt().hashCode(), notificationBuilder.build())
}

private fun Context.addAcceptCallAction(
    notificationBuilder: NotificationCompat.Builder,
    callId: String,
    payload: RequestCall
) {
    //Handle click on notification - Accept
    val receiveCallAction = Intent(this, MightyidNotificationPlugin::class.java)
    receiveCallAction.apply {
        putExtra("call_id", callId)
        putInfoExtra("payload", payload)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    receiveCallAction.action = CALL_STATUS_ACCEPTED

    val receiveCallPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        callId.hashCode(),
        receiveCallAction, PendingIntent.FLAG_UPDATE_CURRENT
    )

    val acceptAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
        this.resources.getIdentifier("ic_menu_call", "drawable", this.packageName),
        getColorizedText("Accept", "#4CB050"),
        receiveCallPendingIntent
    )
        .build()
    notificationBuilder.addAction(acceptAction)
}

private fun Context.addRejectCallAction(
    notificationBuilder: NotificationCompat.Builder,
    callId: String,
    payload: RequestCall
) {
    //Handle click on notification - Decline
    val cancelCallAction = Intent(this, MightyidNotificationPlugin::class.java)
    cancelCallAction.apply {
        putExtra("call_id", callId)
        putInfoExtra("payload", payload)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    cancelCallAction.action = CALL_STATUS_REJECTED

    val cancelCallPendingIntent = PendingIntent.getBroadcast(
        applicationContext,
        callId.hashCode(),
        cancelCallAction,
        PendingIntent.FLAG_UPDATE_CURRENT
    )

    val declineAction: NotificationCompat.Action = NotificationCompat.Action.Builder(
        this.resources.getIdentifier(
            "ic_menu_close_clear_cancel",
            "drawable",
            this.packageName
        ),
        getColorizedText("Reject", "#E02B00"),
        cancelCallPendingIntent
    )
        .build()
    notificationBuilder.addAction(declineAction)
}

private fun setNotificationColor(
    context: Context,
    notificationBuilder: NotificationCompat.Builder
) {
    val accentID = context.resources.getIdentifier(
        "call_notification_color_accent",
        "color",
        context.packageName
    )
    if (accentID != 0) {
        notificationBuilder.color = context.resources.getColor(accentID, null)
    } else {
        notificationBuilder.color = Color.parseColor("#4CAF50")
    }
}

private fun getColorizedText(string: String, colorHex: String): Spannable {
    val spannable: Spannable = SpannableString(string)
    spannable.setSpan(
        ForegroundColorSpan(Color.parseColor(colorHex)),
        0,
        spannable.length,
        Spanned.SPAN_INCLUSIVE_EXCLUSIVE
    )
    return spannable
}

private fun createCallNotificationChannel(notificationManager: NotificationManagerCompat, sound: Uri) {
    val channel = NotificationChannel(
        CALL_CHANNEL_ID,
        CALL_CHANNEL_NAME,
        NotificationManager.IMPORTANCE_HIGH
    )
    channel.setSound(
        sound, AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
            .build()
    )
    notificationManager.createNotificationChannel(channel)
}
