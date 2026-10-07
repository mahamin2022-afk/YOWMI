package com.yowmi.app

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId

internal const val ALARM_CHANNEL_ID = "yowmi_alarm_v2"
internal const val ACTION_REMIND = "com.yowmi.app.REMIND"
internal const val ACTION_DONE = "com.yowmi.app.DONE"

internal fun createAlarmChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        val audioAttributes = AudioAttributes.Builder()
            .setUsage(AudioAttributes.USAGE_ALARM)
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .build()

        val channel = NotificationChannel(
            ALARM_CHANNEL_ID,
            "منبّهات يومي",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "تنبيهات المهام اليومية بصوت واهتزاز"
            enableVibration(true)
            vibrationPattern = longArrayOf(0, 500, 250, 500, 250, 700)
            setSound(alarmUri, audioAttributes)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        context.getSystemService(NotificationManager::class.java)
            .createNotificationChannel(channel)
    }
}

internal fun hasNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

internal fun hasExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    val alarmManager = context.getSystemService(AlarmManager::class.java)
    return alarmManager.canScheduleExactAlarms()
}

internal object ReminderScheduler {
    fun scheduleAll(context: Context) {
        createAlarmChannel(context)
        val store = RoutineStore(context)
        if (!store.notificationsEnabled()) {
            cancelAll(context)
            return
        }
        routineTasks.forEach { scheduleOne(context, it, LocalDateTime.now()) }
    }

    fun scheduleOne(context: Context, task: RoutineTask, after: LocalDateTime) {
        val store = RoutineStore(context)
        if (!store.notificationsEnabled()) return

        val next = nextOccurrence(store, task, after) ?: return
        val alarmManager = context.getSystemService(AlarmManager::class.java)

        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMIND
            putExtra("task_id", task.id)
            putExtra("task_date", next.toLocalDate().toString())
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAt = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        } else {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAt,
                pendingIntent
            )
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        routineTasks.forEach { task ->
            val intent = Intent(context, ReminderReceiver::class.java).apply {
                action = ACTION_REMIND
            }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) alarmManager.cancel(pendingIntent)
        }
    }

    fun sendTestAlarm(context: Context) {
        createAlarmChannel(context)
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_REMIND
            putExtra("task_id", "wake")
            putExtra("task_date", LocalDate.now().toString())
            putExtra("is_test", true)
        }
        context.sendBroadcast(intent)
    }

    private fun nextOccurrence(
        store: RoutineStore,
        task: RoutineTask,
        after: LocalDateTime
    ): LocalDateTime? {
        for (offset in 0..14) {
            val date = after.toLocalDate().plusDays(offset.toLong())
            if (!store.isActive(task, date)) continue
            val candidate = LocalDateTime.of(date, store.time(task))
            if (candidate.isAfter(after.plusSeconds(2))) return candidate
        }
        return null
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("task_id") ?: return
        val task = routineTasks.firstOrNull { it.id == taskId } ?: return
        val store = RoutineStore(context)
        val date = runCatching {
            LocalDate.parse(intent.getStringExtra("task_date"))
        }.getOrDefault(LocalDate.now())

        if (intent.action == ACTION_DONE) {
            store.setDone(task, date, true)
            NotificationManagerCompat.from(context).cancel(task.id.hashCode())
            ReminderScheduler.scheduleOne(context, task, LocalDateTime.now())
            return
        }

        createAlarmChannel(context)

        val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_DONE
            putExtra("task_id", task.id)
            putExtra("task_date", date.toString())
        }

        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + 10_000,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = Intent(context, MainActivity::class.java)
        val openPendingIntent = PendingIntent.getActivity(
            context,
            task.id.hashCode() + 20_000,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val isTest = intent.getBooleanExtra("is_test", false)
        val title = if (isTest) "اختبار المنبّه — يومي" else task.title
        val text = if (isTest) {
            "إذا سمعتي الصوت والاهتزاز فالتنبيهات شغّالة بشكل صحيح."
        } else {
            task.subtitle
        }

        val notification = NotificationCompat.Builder(context, ALARM_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle(title)
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setAutoCancel(true)
            .setContentIntent(openPendingIntent)
            .setVibrate(longArrayOf(0, 500, 250, 500, 250, 700))
            .addAction(android.R.drawable.checkbox_on_background, "تم ✓", donePendingIntent)
            .build()

        if (hasNotificationPermission(context)) {
            NotificationManagerCompat.from(context).notify(
                if (isTest) 909_090 else task.id.hashCode(),
                notification
            )
        }

        if (!isTest) {
            ReminderScheduler.scheduleOne(
                context,
                task,
                LocalDateTime.now().plusMinutes(1)
            )
        }
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        ReminderScheduler.scheduleAll(context)
    }
}
