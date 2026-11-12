package com.aimsync.pro.system

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.provider.Settings
import android.widget.Toast

object SystemOptimizer {

    fun openPointerSpeedSettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_INPUT_METHOD_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "Set Pointer Speed to MAX for linear response", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            context.startActivity(Intent(Settings.ACTION_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        }
    }

    fun openTouchDelaySettings(context: Context) {
        try {
            val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            }
            context.startActivity(intent)
            Toast.makeText(context, "Set Touch & Hold Delay to SHORT", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(context, "Open Accessibility > Touch & Hold Delay", Toast.LENGTH_SHORT).show()
        }
    }

    fun toggleGamingDnd(context: Context) {
        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (!nm.isNotificationPolicyAccessGranted) {
            Toast.makeText(context, "Grant DND Access to block call/notification lag", Toast.LENGTH_LONG).show()
            context.startActivity(Intent(Settings.ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK
            })
        } else {
            nm.setInterruptionFilter(NotificationManager.INTERRUPTION_FILTER_PRIORITY)
            Toast.makeText(context, "Gaming DND Activated", Toast.LENGTH_SHORT).show()
        }
    }
}
