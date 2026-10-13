package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.PreferencesManager
import com.example.data.repository.CurrencyRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class RateRefreshReceiver : BroadcastReceiver() {

    companion object {
        const val ACTION_PERIODIC_REFRESH = "com.aistudio.hermesfx.ACTION_PERIODIC_REFRESH"
    }

    override fun onReceive(context: Context, intent: Intent) {
        val pendingResult = goAsync()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                Log.d("RateRefreshReceiver", "Periodic refresh triggered")
                val repository = CurrencyRepository(context)
                repository.syncRates(force = true)

                // Reschedule for next cadence
                CadenceScheduler.scheduleRefresh(context)
            } catch (e: Exception) {
                Log.e("RateRefreshReceiver", "Periodic refresh failed: ${e.message}")
            } finally {
                pendingResult.finish()
            }
        }
    }
}

object CadenceScheduler {
    fun scheduleRefresh(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val prefs = PreferencesManager(context)
        val cadenceMinutes = prefs.getCadenceMinutes()

        val intent = Intent(context, RateRefreshReceiver::class.java).apply {
            action = RateRefreshReceiver.ACTION_PERIODIC_REFRESH
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (cadenceMinutes <= 0) {
            // Manual cadence -> cancel alarms
            alarmManager.cancel(pendingIntent)
            return
        }

        val triggerAtMillis = System.currentTimeMillis() + (cadenceMinutes * 60 * 1000L)
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            } else {
                alarmManager.set(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            Log.w("CadenceScheduler", "Cannot schedule exact alarm: ${e.message}")
        }
    }

    fun cancelSchedule(context: Context) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, RateRefreshReceiver::class.java).apply {
            action = RateRefreshReceiver.ACTION_PERIODIC_REFRESH
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            2001,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
        }
    }
}
