package com.yowmi.app

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TimePickerDialog
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.LocalLayoutDirection
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Locale

private const val CHANNEL_ID = "yowmi_reminders"
private const val ACTION_REMIND = "com.yowmi.app.REMIND"
private const val ACTION_DONE = "com.yowmi.app.DONE"

private sealed class RepeatRule {
    data object Daily : RepeatRule()
    data object AlternateDays : RepeatRule()
    data class Weekly(val days: Set<DayOfWeek>) : RepeatRule()
}

private data class RoutineTask(
    val id: String,
    val title: String,
    val subtitle: String,
    val defaultTime: LocalTime,
    val section: String,
    val repeatRule: RepeatRule = RepeatRule.Daily
)

private val routineTasks = listOf(
    RoutineTask("wake", "الاستيقاظ", "كاسة مي + ترتيب سريع", LocalTime.of(9, 0), "الصباح"),
    RoutineTask("coffee", "قهوة وفطور خفيف", "قهوة + موزة + كاسة حليب + تمر", LocalTime.of(9, 15), "الصباح"),
    RoutineTask("workout", "رياضة", "جلسة الرياضة اليومية", LocalTime.of(9, 45), "الصباح"),
    RoutineTask("shower", "دوش وتجهيز", "بعد الرياضة", LocalTime.of(10, 35), "الصباح"),
    RoutineTask("quran", "قراءة القرآن", "وقت هادئ بعد الرياضة", LocalTime.of(11, 0), "الصباح"),
    RoutineTask("breakfast", "فطور مع الأهل", "مع الكولاجين اليومي", LocalTime.of(11, 40), "الظهر"),
    RoutineTask("cleaning", "شغل البيت", "يوم إيه ويوم لا", LocalTime.of(12, 20), "الظهر", RepeatRule.AlternateDays),
    RoutineTask("turkish1", "دراسة تركي — الجلسة الأولى", "ساعة وربع", LocalTime.of(13, 30), "الظهر"),
    RoutineTask("work", "التطبيق أو الشغل", "شغل المشروع أو أي شغل مستلم", LocalTime.of(15, 15), "الظهر"),
    RoutineTask("turkish2", "دراسة تركي — الجلسة الثانية", "ساعة وربع", LocalTime.of(17, 15), "المساء"),
    RoutineTask("lunch", "الغدا مع الأهل", "وقت الغدا", LocalTime.of(19, 0), "المساء"),
    RoutineTask("husband", "وقت مع زوجي", "من 8 للمسا تقريبًا", LocalTime.of(20, 0), "المساء"),
    RoutineTask("care", "عناية مسائية", "تجهيز للنوم والعناية الشخصية", LocalTime.of(23, 0), "قبل النوم"),
    RoutineTask("scrub", "مقشر الجسم", "مرتين بالأسبوع مساءً", LocalTime.of(23, 10), "قبل النوم", RepeatRule.Weekly(setOf(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY))),
    RoutineTask("need_prayer", "صلاة قضاء الحاجة", "قبل النوم", LocalTime.of(23, 20), "قبل النوم"),
    RoutineTask("dhikr1", "100× يا حي يا قيوم برحمتك أستغيث", "ذكر قبل النوم", LocalTime.of(23, 30), "قبل النوم"),
    RoutineTask("dhikr2", "100× ربي مسني الضر وأنت أرحم الراحمين", "ذكر قبل النوم", LocalTime.of(23, 40), "قبل النوم"),
    RoutineTask("tahajjud", "التهجد + سورة يس", "عدّلي الوقت حسب الفجر", LocalTime.of(4, 30), "قبل الفجر")
)

private class RoutineStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("yowmi_prefs", Context.MODE_PRIVATE)

    init {
        if (!prefs.contains("cleaning_anchor")) {
            prefs.edit().putString("cleaning_anchor", LocalDate.now().toString()).apply()
        }
    }

    fun time(task: RoutineTask): LocalTime {
        val stored = prefs.getString("time_${task.id}", null)
        return runCatching { LocalTime.parse(stored) }.getOrDefault(task.defaultTime)
    }

    fun setTime(task: RoutineTask, time: LocalTime) {
        prefs.edit().putString("time_${task.id}", time.toString()).apply()
    }

    fun isDone(task: RoutineTask, date: LocalDate): Boolean =
        prefs.getBoolean("done_${date}_${task.id}", false)

    fun setDone(task: RoutineTask, date: LocalDate, done: Boolean) {
        prefs.edit().putBoolean("done_${date}_${task.id}", done).apply()
    }

    fun resetToday() {
        val today = LocalDate.now()
        val editor = prefs.edit()
        activeTasks(today).forEach { editor.remove("done_${today}_${it.id}") }
        editor.apply()
    }

    fun notificationsEnabled(): Boolean = prefs.getBoolean("notifications", true)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun restartCleaningCycle() {
        prefs.edit().putString("cleaning_anchor", LocalDate.now().toString()).apply()
    }

    fun activeTasks(date: LocalDate): List<RoutineTask> =
        routineTasks.filter { isActive(it, date) }

    fun isActive(task: RoutineTask, date: LocalDate): Boolean = when (val rule = task.repeatRule) {
        RepeatRule.Daily -> true
        RepeatRule.AlternateDays -> {
            val anchor = runCatching {
                LocalDate.parse(prefs.getString("cleaning_anchor", LocalDate.now().toString()))
            }.getOrDefault(LocalDate.now())
            ChronoUnit.DAYS.between(anchor, date) % 2L == 0L
        }
        is RepeatRule.Weekly -> date.dayOfWeek in rule.days
    }

    fun repeatLabel(task: RoutineTask): String = when (val rule = task.repeatRule) {
        RepeatRule.Daily -> "يوميًا"
        RepeatRule.AlternateDays -> "يوم إيه ويوم لا"
        is RepeatRule.Weekly -> rule.days.sortedBy { it.value }.joinToString("، ") { arabicDay(it) }
    }

    private fun arabicDay(day: DayOfWeek): String = when (day) {
        DayOfWeek.MONDAY -> "الاثنين"
        DayOfWeek.TUESDAY -> "الثلاثاء"
        DayOfWeek.WEDNESDAY -> "الأربعاء"
        DayOfWeek.THURSDAY -> "الخميس"
        DayOfWeek.FRIDAY -> "الجمعة"
        DayOfWeek.SATURDAY -> "السبت"
        DayOfWeek.SUNDAY -> "الأحد"
    }
}

private object ReminderScheduler {
    fun scheduleAll(context: Context) {
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
            putExtra("task_title", task.title)
        }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        val trigger = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && !alarmManager.canScheduleExactAlarms()) {
            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent)
        } else {
            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, trigger, pendingIntent)
        }
    }

    fun cancelAll(context: Context) {
        val alarmManager = context.getSystemService(AlarmManager::class.java)
        routineTasks.forEach { task ->
            val intent = Intent(context, ReminderReceiver::class.java).apply { action = ACTION_REMIND }
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                task.id.hashCode(),
                intent,
                PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )
            if (pendingIntent != null) alarmManager.cancel(pendingIntent)
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
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val taskId = intent.getStringExtra("task_id") ?: return
        val task = routineTasks.firstOrNull { it.id == taskId } ?: return
        val store = RoutineStore(context)

        if (intent.action == ACTION_DONE) {
            store.setDone(task, LocalDate.now(), true)
            NotificationManagerCompat.from(context).cancel(task.id.hashCode())
            ReminderScheduler.scheduleOne(context, task, LocalDateTime.now())
            return
        }

        createNotificationChannel(context)

        val doneIntent = Intent(context, ReminderReceiver::class.java).apply {
            action = ACTION_DONE
            putExtra("task_id", task.id)
        }
        val donePendingIntent = PendingIntent.getBroadcast(
            context,
            task.id.hashCode() + 10000,
            doneIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_popup_reminder)
            .setContentTitle(task.title)
            .setContentText(task.subtitle)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .addAction(android.R.drawable.checkbox_on_background, "تم ✓", donePendingIntent)
            .build()

        if (
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        ) {
            NotificationManagerCompat.from(context).notify(task.id.hashCode(), notification)
        }

        ReminderScheduler.scheduleOne(context, task, LocalDateTime.now().plusMinutes(1))
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        ReminderScheduler.scheduleAll(context)
    }
}

class MainActivity : ComponentActivity() {
    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createNotificationChannel(this)
        requestNotificationsIfNeeded()
        ReminderScheduler.scheduleAll(this)

        setContent {
            val colors = lightColorScheme(
                primary = Color(0xFF6750A4),
                secondary = Color(0xFF7D5260),
                tertiary = Color(0xFF386A20),
                background = Color(0xFFF8F7FC),
                surface = Color(0xFFFFFFFF),
                primaryContainer = Color(0xFFE9DDFF)
            )
            MaterialTheme(colorScheme = colors) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    YowmiApp()
                }
            }
        }
    }

    private fun requestNotificationsIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private fun createNotificationChannel(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            CHANNEL_ID,
            "تذكيرات يومي",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "تنبيهات مهام البرنامج اليومي"
        }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }
}

private enum class Screen { Today, Schedule }

@Composable
private fun YowmiApp() {
    val context = LocalContext.current
    val store = remember { RoutineStore(context) }
    var screen by remember { mutableStateOf(Screen.Today) }
    var refresh by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = screen == Screen.Today,
                    onClick = { screen = Screen.Today },
                    icon = { Icon(Icons.Rounded.Home, null) },
                    label = { Text("اليوم") }
                )
                NavigationBarItem(
                    selected = screen == Screen.Schedule,
                    onClick = { screen = Screen.Schedule },
                    icon = { Icon(Icons.Rounded.Schedule, null) },
                    label = { Text("الجدول") }
                )
            }
        }
    ) { padding ->
        when (screen) {
            Screen.Today -> TodayScreen(
                modifier = Modifier.padding(padding),
                store = store,
                refresh = refresh,
                onChanged = { refresh++ }
            )
            Screen.Schedule -> ScheduleScreen(
                modifier = Modifier.padding(padding),
                store = store,
                refresh = refresh,
                onChanged = {
                    refresh++
                    ReminderScheduler.scheduleAll(context)
                }
            )
        }
    }
}

@Composable
private fun TodayScreen(
    modifier: Modifier,
    store: RoutineStore,
    refresh: Int,
    onChanged: () -> Unit
) {
    val today = LocalDate.now()
    val tasks = remember(refresh, today) { store.activeTasks(today) }
    val done = tasks.count { store.isDone(it, today) }
    val progress = if (tasks.isEmpty()) 0f else done.toFloat() / tasks.size
    val formatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Text("روتيني اليومي", fontSize = 26.sp, fontWeight = FontWeight.Bold)
                    Text(today.format(formatter), color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Spacer(Modifier.height(18.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("إنجاز اليوم", fontWeight = FontWeight.SemiBold)
                        Text("$done / ${tasks.size}", fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text("${(progress * 100).toInt()}%", fontWeight = FontWeight.Bold)
                }
            }
        }

        val sections = tasks.groupBy { it.section }
        sections.forEach { (section, sectionTasks) ->
            item {
                Text(
                    section,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
            }
            items(sectionTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    time = store.time(task),
                    done = store.isDone(task, today),
                    onToggle = {
                        store.setDone(task, today, !store.isDone(task, today))
                        onChanged()
                    }
                )
            }
        }

        item {
            Button(
                onClick = {
                    store.resetToday()
                    onChanged()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Rounded.Refresh, null)
                Spacer(Modifier.size(8.dp))
                Text("إلغاء علامات تم لليوم")
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: RoutineTask,
    time: LocalTime,
    done: Boolean,
    onToggle: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(MaterialTheme.colorScheme.primaryContainer, RoundedCornerShape(14.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconFor(task), null, tint = MaterialTheme.colorScheme.primary)
            }

            Column(Modifier.weight(1f)) {
                Text(task.title, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(3.dp))
                Text(task.subtitle, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AccessTime, null, Modifier.size(16.dp))
                    Spacer(Modifier.size(4.dp))
                    Text(formatTime(time), fontSize = 13.sp)
                }
            }

            Button(
                onClick = onToggle,
                shape = RoundedCornerShape(16.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (done) {
                    Icon(Icons.Rounded.Check, null, Modifier.size(18.dp))
                    Spacer(Modifier.size(5.dp))
                }
                Text(if (done) "تم" else "تم ✓")
            }
        }
    }
}

@Composable
private fun ScheduleScreen(
    modifier: Modifier,
    store: RoutineStore,
    refresh: Int,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    val notifications = remember(refresh) { store.notificationsEnabled() }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("الجدول والإعدادات", fontSize = 26.sp, fontWeight = FontWeight.Bold)
            Text("اضغطي على الوقت لتغييره", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }

        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Rounded.Notifications, null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text("التنبيهات", fontWeight = FontWeight.Bold)
                        Text("تذكير عند وقت كل مهمة", fontSize = 13.sp)
                    }
                    Switch(
                        checked = notifications,
                        onCheckedChange = {
                            store.setNotificationsEnabled(it)
                            onChanged()
                        }
                    )
                }
            }
        }

        items(routineTasks, key = { it.id }) { task ->
            val currentTime = store.time(task)
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(iconFor(task), null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(12.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, fontWeight = FontWeight.SemiBold)
                        Text(store.repeatLabel(task), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    Button(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute ->
                                    store.setTime(task, LocalTime.of(hour, minute))
                                    onChanged()
                                },
                                currentTime.hour,
                                currentTime.minute,
                                true
                            ).show()
                        },
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        Text(formatTime(currentTime))
                    }
                }
            }
        }

        item {
            Button(
                onClick = {
                    store.restartCleaningCycle()
                    onChanged()
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Rounded.CleaningServices, null)
                Spacer(Modifier.size(8.dp))
                Text("اعتبري اليوم يوم تنظيف")
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(22.dp)
            ) {
                Text(
                    "وقت التهجد مضبوط افتراضيًا على 4:30 صباحًا. عدّليه من هون حسب وقت الفجر عندك.",
                    modifier = Modifier.padding(16.dp),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private fun formatTime(time: LocalTime): String {
    val h = if (time.hour % 12 == 0) 12 else time.hour % 12
    val period = if (time.hour < 12) "ص" else "م"
    return String.format(Locale("ar"), "%d:%02d %s", h, time.minute, period)
}

private fun iconFor(task: RoutineTask): ImageVector = when (task.id) {
    "wake" -> Icons.Rounded.Alarm
    "workout" -> Icons.Rounded.FitnessCenter
    "quran", "turkish1", "turkish2" -> Icons.Rounded.AutoStories
    "cleaning" -> Icons.Rounded.CleaningServices
    "work" -> Icons.Rounded.Work
    "lunch", "breakfast", "coffee" -> Icons.Rounded.Restaurant
    "husband" -> Icons.Rounded.Favorite
    "shower", "care", "scrub" -> Icons.Rounded.Spa
    "need_prayer", "dhikr1", "dhikr2", "tahajjud" -> Icons.Rounded.SelfImprovement
    else -> Icons.Rounded.AccessTime
}
