package com.yowmi.app

import android.Manifest
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioManager
import android.media.AudioTrack
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.Build
import android.os.IBinder
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import kotlin.math.PI
import kotlin.math.sin

internal const val REMINDER_CHANNEL_ID = "yowmi_gentle_v3"
internal const val FALLBACK_CHANNEL_ID = "yowmi_fallback_alarm_v3"
internal const val ACTION_REMIND = "com.yowmi.app.REMIND"
internal const val ACTION_FALLBACK = "com.yowmi.app.FALLBACK"
internal const val ACTION_ACK = "com.yowmi.app.ACK"
internal const val ACTION_SILENCE = "com.yowmi.app.SILENCE"
private const val FALLBACK_DELAY_MINUTES = 3L

internal fun createReminderChannels(context: Context) {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

    val manager = context.getSystemService(NotificationManager::class.java)

    val gentle = NotificationChannel(
        REMINDER_CHANNEL_ID,
        "تذكيرات يومي",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "تذكير لطيف بالمهمة التالية"
        setSound(null, null)
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 120, 100, 120)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }

    val fallback = NotificationChannel(
        FALLBACK_CHANNEL_ID,
        "منبّه يومي الاحتياطي",
        NotificationManager.IMPORTANCE_HIGH
    ).apply {
        description = "منبّه احتياطي إذا لم يتم الانتباه للتذكير"
        setSound(null, null)
        enableVibration(true)
        vibrationPattern = longArrayOf(0, 650, 250, 650)
        lockscreenVisibility = Notification.VISIBILITY_PUBLIC
    }

    manager.createNotificationChannel(gentle)
    manager.createNotificationChannel(fallback)
}

internal fun hasNotificationPermission(context: Context): Boolean =
    Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
        ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

internal fun hasExactAlarmPermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) return true
    return context.getSystemService(AlarmManager::class.java).canScheduleExactAlarms()
}

internal object ReminderScheduler {
    fun scheduleAll(context: Context) {
        createReminderChannels(context)
        val store = RoutineStore(context)
        if (!store.notificationsEnabled()) {
            cancelAll(context)
            return
        }
        store.tasks().forEach { scheduleOne(context, it, LocalDateTime.now()) }
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
            reminderRequestCode(task.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        scheduleAlarm(alarmManager, next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli(), pendingIntent)
    }

    fun scheduleFallback(context: Context, task: RoutineTask, date: LocalDate) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_FALLBACK
            putExtra("task_id", task.id)
            putExtra("task_date", date.toString())
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            fallbackRequestCode(task.id),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val triggerAt = System.currentTimeMillis() + FALLBACK_DELAY_MINUTES * 60_000L
        scheduleAlarm(alarmManager, triggerAt, pendingIntent)
    }

    fun acknowledge(context: Context, taskId: String) {
        cancelFallback(context, taskId)
        context.stopService(Intent(context, GentleReminderService::class.java))
        context.stopService(Intent(context, AlarmRingService::class.java))
        NotificationManagerCompat.from(context).cancel(gentleNotificationId(taskId))
        NotificationManagerCompat.from(context).cancel(alarmNotificationId(taskId))
    }

    fun silenceAlarm(context: Context, taskId: String) {
        context.stopService(Intent(context, AlarmRingService::class.java))
        NotificationManagerCompat.from(context).cancel(alarmNotificationId(taskId))
    }

    fun cancelTask(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        cancelPending(alarmManager, context, taskId, ACTION_REMIND, reminderRequestCode(taskId))
        cancelPending(alarmManager, context, taskId, ACTION_FALLBACK, fallbackRequestCode(taskId))
        acknowledge(context, taskId)
    }

    fun cancelAll(context: Context) {
        RoutineStore(context).tasks().forEach { cancelTask(context, it.id) }
    }

    fun sendTestReminder(context: Context) {
        createReminderChannels(context)
        val task = RoutineStore(context).taskById("wake")
            ?: RoutineTask("test", "اختبار التذكير", "هكذا سيصلك التذكير اللطيف", java.time.LocalTime.now(), "اليوم")
        startGentleService(context, task, LocalDate.now(), true)
    }

    private fun cancelFallback(context: Context, taskId: String) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        cancelPending(alarmManager, context, taskId, ACTION_FALLBACK, fallbackRequestCode(taskId))
    }

    private fun cancelPending(
        alarmManager: AlarmManager,
        context: Context,
        taskId: String,
        actionName: String,
        requestCode: Int
    ) {
        val intent = Intent(context, ReminderReceiver::class.java).apply {
            action = actionName
            putExtra("task_id", taskId)
        }
        val pending = PendingIntent.getBroadcast(
            context,
            requestCode,
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pending != null) {
            alarmManager.cancel(pending)
            pending.cancel()
        }
    }

    private fun scheduleAlarm(alarmManager: AlarmManager, triggerAt: Long, pendingIntent: PendingIntent) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerAt, pendingIntent)
        }
    }

    private fun nextOccurrence(store: RoutineStore, task: RoutineTask, after: LocalDateTime): LocalDateTime? {
        for (offset in 0..14) {
            val date = after.toLocalDate().plusDays(offset.toLong())
            if (!store.isActive(task, date)) continue
            val candidate = LocalDateTime.of(date, store.time(task))
            if (candidate.isAfter(after.plusSeconds(2))) return candidate
        }
        return null
    }

    internal fun startGentleService(context: Context, task: RoutineTask, date: LocalDate, isTest: Boolean) {
        val intent = Intent(context, GentleReminderService::class.java).apply {
            putExtra("task_id", task.id)
            putExtra("task_title", task.title)
            putExtra("task_text", task.subtitle)
            putExtra("task_date", date.toString())
            putExtra("is_test", isTest)
        }
        ContextCompat.startForegroundService(context, intent)
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("task_id") ?: return
        val store = RoutineStore(context)
        val task = store.taskById(taskId) ?: return
        val date = runCatching {
            LocalDate.parse(intent.getStringExtra("task_date"))
        }.getOrDefault(LocalDate.now())

        when (intent.action) {
            ACTION_REMIND -> {
                ReminderScheduler.startGentleService(context, task, date, false)
                ReminderScheduler.scheduleFallback(context, task, date)
                ReminderScheduler.scheduleOne(context, task, LocalDateTime.now().plusMinutes(1))
            }
            ACTION_FALLBACK -> {
                val service = Intent(context, AlarmRingService::class.java).apply {
                    putExtra("task_id", task.id)
                    putExtra("task_title", task.title)
                    putExtra("task_text", "ما انتبهتي للتذكير السابق — حان وقت ${task.title}")
                }
                ContextCompat.startForegroundService(context, service)
            }
            ACTION_ACK -> ReminderScheduler.acknowledge(context, task.id)
            ACTION_SILENCE -> ReminderScheduler.silenceAlarm(context, task.id)
        }
    }
}

class GentleReminderService : Service() {
    private var player: AudioTrack? = null
    @Volatile private var stopped = false

    override fun onCreate() {
        super.onCreate()
        createReminderChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getStringExtra("task_id") ?: return START_NOT_STICKY
        val title = intent.getStringExtra("task_title") ?: "تذكير يومي"
        val text = intent.getStringExtra("task_text").orEmpty()
        val date = intent.getStringExtra("task_date") ?: LocalDate.now().toString()
        val isTest = intent.getBooleanExtra("is_test", false)

        val ackIntent = Intent(this, ReminderReceiver::class.java).apply {
            action = ACTION_ACK
            putExtra("task_id", taskId)
            putExtra("task_date", date)
        }
        val ackPending = PendingIntent.getBroadcast(
            this,
            ackRequestCode(taskId),
            ackIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("ack_task_id", taskId)
        }
        val openPending = PendingIntent.getActivity(
            this,
            openRequestCode(taskId),
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, REMINDER_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(if (isTest) "اختبار التذكير — يومي" else title)
            .setContentText(if (isTest) "نغمة التذكير اللطيفة شغّالة الآن." else text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(if (isTest) "نغمة التذكير اللطيفة شغّالة الآن." else text))
            .setContentIntent(openPending)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setAutoCancel(true)
            .addAction(android.R.drawable.ic_menu_view, "شفت التذكير", ackPending)
            .build()

        startForeground(gentleNotificationId(taskId), notification)
        playMelodyThenDetach()
        return START_NOT_STICKY
    }

    private fun playMelodyThenDetach() {
        stopped = false
        Thread {
            val sampleRate = 22_050
            val noteDurationMs = 520
            val notes = doubleArrayOf(
                523.25, 659.25, 783.99, 659.25,
                587.33, 698.46, 880.00, 698.46,
                523.25, 659.25, 783.99, 987.77,
                880.00, 783.99, 659.25, 587.33,
                523.25, 659.25, 783.99, 659.25,
                587.33, 698.46, 880.00, 1046.50,
                987.77, 880.00, 783.99, 659.25,
                587.33, 523.25, 493.88, 523.25
            )
            val framesPerNote = sampleRate * noteDurationMs / 1000
            val pcm = ShortArray(framesPerNote * notes.size)

            for (n in notes.indices) {
                val frequency = notes[n]
                for (i in 0 until framesPerNote) {
                    val t = i.toDouble() / sampleRate
                    val fade = when {
                        i < framesPerNote / 8 -> i.toDouble() / (framesPerNote / 8)
                        i > framesPerNote * 7 / 8 -> (framesPerNote - i).toDouble() / (framesPerNote / 8)
                        else -> 1.0
                    }.coerceIn(0.0, 1.0)
                    val harmonic = sin(2.0 * PI * frequency * t) * 0.70 +
                        sin(2.0 * PI * frequency * 1.5 * t) * 0.18
                    pcm[n * framesPerNote + i] = (harmonic * fade * 7_500).toInt().toShort()
                }
            }

            val attributes = AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                .build()
            val format = AudioFormat.Builder()
                .setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(sampleRate)
                .setChannelMask(AudioFormat.CHANNEL_OUT_MONO)
                .build()

            val track = AudioTrack(
                attributes,
                format,
                pcm.size * 2,
                AudioTrack.MODE_STATIC,
                AudioManager.AUDIO_SESSION_ID_GENERATE
            )
            player = track
            track.write(pcm, 0, pcm.size)
            track.setVolume(0.45f)
            track.play()

            val totalMs = noteDurationMs.toLong() * notes.size
            var waited = 0L
            while (!stopped && waited < totalMs + 500L) {
                Thread.sleep(200L)
                waited += 200L
            }
            runCatching { track.stop() }
            track.release()
            player = null

            if (!stopped) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    stopForeground(STOP_FOREGROUND_DETACH)
                } else {
                    @Suppress("DEPRECATION")
                    stopForeground(false)
                }
                stopSelf()
            }
        }.start()
    }

    override fun onDestroy() {
        stopped = true
        runCatching { player?.stop() }
        player?.release()
        player = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

class AlarmRingService : Service() {
    private var mediaPlayer: MediaPlayer? = null
    private var vibrator: Vibrator? = null

    override fun onCreate() {
        super.onCreate()
        createReminderChannels(this)
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val taskId = intent?.getStringExtra("task_id") ?: return START_NOT_STICKY
        val title = intent.getStringExtra("task_title") ?: "منبّه يومي"
        val text = intent.getStringExtra("task_text") ?: "تذكير بالمهمة"

        val silenceIntent = Intent(this, ReminderReceiver::class.java).apply {
            action = ACTION_SILENCE
            putExtra("task_id", taskId)
        }
        val silencePending = PendingIntent.getBroadcast(
            this,
            silenceRequestCode(taskId),
            silenceIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val openIntent = Intent(this, MainActivity::class.java).apply {
            putExtra("ack_task_id", taskId)
        }
        val openPending = PendingIntent.getActivity(
            this,
            openRequestCode(taskId) + 1,
            openIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, FALLBACK_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_lock_idle_alarm)
            .setContentTitle("منبّه: $title")
            .setContentText(text)
            .setStyle(NotificationCompat.BigTextStyle().bigText(text))
            .setContentIntent(openPending)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .addAction(android.R.drawable.ic_lock_silent_mode, "إسكات", silencePending)
            .build()

        startForeground(alarmNotificationId(taskId), notification)
        startAlarmAudio()
        return START_NOT_STICKY
    }

    private fun startAlarmAudio() {
        if (mediaPlayer?.isPlaying == true) return

        val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            ?: RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)

        mediaPlayer = MediaPlayer().apply {
            setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_ALARM)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build()
            )
            setDataSource(this@AlarmRingService, uri)
            isLooping = true
            prepare()
            start()
        }

        vibrator = getSystemService(Vibrator::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(
                VibrationEffect.createWaveform(
                    longArrayOf(0, 700, 300, 700, 300),
                    0
                )
            )
        }
    }

    override fun onDestroy() {
        runCatching { mediaPlayer?.stop() }
        mediaPlayer?.release()
        mediaPlayer = null
        vibrator?.cancel()
        vibrator = null
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        ReminderScheduler.scheduleAll(context)
    }
}

private fun reminderRequestCode(id: String) = stableCode(id, 10_000)
private fun fallbackRequestCode(id: String) = stableCode(id, 20_000)
private fun ackRequestCode(id: String) = stableCode(id, 30_000)
private fun silenceRequestCode(id: String) = stableCode(id, 40_000)
private fun openRequestCode(id: String) = stableCode(id, 50_000)
internal fun gentleNotificationId(id: String) = stableCode(id, 60_000)
internal fun alarmNotificationId(id: String) = stableCode(id, 70_000)

private fun stableCode(id: String, salt: Int): Int =
    ((id.hashCode() and 0x0fffffff) + salt) and 0x7fffffff
