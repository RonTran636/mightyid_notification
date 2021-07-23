package com.example.mightyid_notification

import android.app.Activity
import android.app.KeyguardManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowManager
import android.widget.ImageView
import androidx.databinding.DataBindingUtil
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.mightyid_notification.constants.*
import com.example.mightyid_notification.databinding.ActivityIncomingInvitationBinding
import com.example.mightyid_notification.models.RequestCall
import com.example.mightyid_notification.utils.IntentUtils.getInfoExtra
import com.example.mightyid_notification.utils.IntentUtils.putInfoExtra

class IncomingInvitationActivity : Activity() {
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var binding: ActivityIncomingInvitationBinding
    private var payload: RequestCall? = null
    private var callId: String? = null
    private var meetingType: String? = null
    private var callerName: String? = null
    private var callerPhotoUrl: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        turnScreenOnAndKeyguardOff()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_incoming_invitation)

        processIncomingData(intent)
        initUI()
    }

    override fun onStart() {
        super.onStart()
        Handler(Looper.myLooper()!!).postDelayed({
            if (payload!!.response == null) {
                finish()
            }
        }, 60000)
    }

    override fun onDestroy() {
        super.onDestroy()
        turnScreenOffAndKeyguardOn()
    }

    private fun processIncomingData(intent: Intent) {
        callId = intent.getStringExtra(CALL_ID)
        meetingType = intent.getStringExtra(CALL_MEETING_TYPE)
        callerName = intent.getStringExtra(CALLER_NAME)
        callerPhotoUrl = intent.getStringExtra(CALLER_PHOTO_URL)
        payload = intent.getInfoExtra(CALL_PAYLOAD)
    }

    private fun initUI() {
        binding.callerName = callerName
        binding.incomingMessage.text = getString(R.string.incoming_message, meetingType)
        binding.incomingCallerAvatar.loadImage(callerPhotoUrl)
        binding.incomingCallerAccept.setOnClickListener { onStartCall() }
        binding.incomingCallerReject.setOnClickListener { onEndCall() }
    }

    // calls from layout file
    private fun onEndCall() {
        cancelNotification(callId.hashCode())
        finishAndRemoveTask()
        payload!!.response = CALL_STATUS_ACCEPTED
        val intent = Intent(BROADCAST_ACTION).apply {
            putInfoExtra(CALL_PAYLOAD, payload)
        }
        localBroadcastManager.sendBroadcast(intent)
    }

    // calls from layout file
    private fun onStartCall() {
        cancelNotification(callId.hashCode())
        payload!!.response = CALL_STATUS_MISSED
        val intent = Intent(BROADCAST_ACTION).apply {
            putInfoExtra(CALL_PAYLOAD, payload)
        }
        localBroadcastManager.sendBroadcast(intent)
    }
}

fun ImageView.loadImage(url: String?) {
    val option = RequestOptions()
        .error(R.drawable.ic_avatar_default)
    Glide.with(this.context)
        .setDefaultRequestOptions(option)
        .load(url)
        .into(this)
}

fun Context.cancelNotification(id: Int) {
    val notificationManager =
        this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.cancel(id)
}

fun Activity.turnScreenOnAndKeyguardOff() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(true)
        setTurnScreenOn(true)
    } else {
        @Suppress("DEPRECATION")
        window.addFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
        )
    }

    with(getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            requestDismissKeyguard(this@turnScreenOnAndKeyguardOff, null)
        }
    }
}

fun Activity.turnScreenOffAndKeyguardOn() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
        setShowWhenLocked(false)
        setTurnScreenOn(false)
    } else {
        window.clearFlags(
            WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                    or WindowManager.LayoutParams.FLAG_ALLOW_LOCK_WHILE_SCREEN_ON
        )
    }
}