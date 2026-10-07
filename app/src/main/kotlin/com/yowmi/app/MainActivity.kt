package com.yowmi.app

import android.Manifest
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import java.time.DayOfWeek
import java.time.Duration
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlinx.coroutines.delay

private val Navy = Color(0xFF172B4D)
private val Teal = Color(0xFF00A9A5)
private val AccentPink = Color(0xFFEC4B99)
private val GrammarBlue = Color(0xFF3478F6)
private val VocabularyGreen = Color(0xFF00B88A)
private val MemoryPurple = Color(0xFF8B5CF6)
private val SpeakingOrange = Color(0xFFFF7A00)
private val ListeningCyan = Color(0xFF00B7C7)
private val PronunciationRed = Color(0xFFF43F5E)
private val PracticeAmber = Color(0xFFFFB000)
private val WarningRed = Color(0xFFE5484D)
private val SuccessGreen = Color(0xFF16A66A)
private val AppBackground = Color(0xFFF6F8FC)
private val HeroSurface = Color(0xFFEAF0F9)
private val MutedText = Color(0xFF6E7A90)

private data class ProgressDefinition(
    val id: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector,
    val unit: String
)

private val progressDefinitions = listOf(
    ProgressDefinition("workout", "الرياضة", "استمرارية الشهر", SpeakingOrange, Icons.Rounded.FitnessCenter, "يوم"),
    ProgressDefinition("quran", "القرآن", "وردك اليومي", VocabularyGreen, Icons.Rounded.AutoStories, "يوم"),
    ProgressDefinition("turkish", "التركي", "جلستان كل يوم", GrammarBlue, Icons.Rounded.AutoStories, "جلسة"),
    ProgressDefinition("work", "التطبيق / الشغل", "تقدّم المشروع", MemoryPurple, Icons.Rounded.Work, "يوم")
)

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            ReminderScheduler.scheduleAll(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        createAlarmChannel(this)
        requestNotificationPermissionIfNeeded()
        ReminderScheduler.scheduleAll(this)

        setContent {
            val colors = lightColorScheme(
                primary = Navy,
                onPrimary = Color.White,
                secondary = Teal,
                tertiary = AccentPink,
                background = AppBackground,
                surface = Color.White,
                primaryContainer = HeroSurface,
                secondaryContainer = Color(0xFFDDF7F5),
                error = WarningRed
            )
            MaterialTheme(colorScheme = colors) {
                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    YowmiApp()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        createAlarmChannel(this)
        ReminderScheduler.scheduleAll(this)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private enum class Screen { Today, Month, Schedule }

@Composable
private fun YowmiApp() {
    val context = LocalContext.current
    val store = remember { RoutineStore(context) }
    var screen by remember { mutableStateOf(Screen.Today) }
    var refresh by remember { mutableIntStateOf(0) }
    var today by remember { mutableStateOf(LocalDate.now()) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusSeconds(2)
            val waitMs = Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000)
            delay(waitMs)
            today = LocalDate.now()
            refresh++
            ReminderScheduler.scheduleAll(context)
        }
    }

    Scaffold(
        containerColor = AppBackground,
        bottomBar = {
            NavigationBar(containerColor = Color.White) {
                NavigationBarItem(
                    selected = screen == Screen.Today,
                    onClick = { screen = Screen.Today },
                    icon = { Icon(Icons.Rounded.Home, null) },
                    label = { Text("اليوم") }
                )
                NavigationBarItem(
                    selected = screen == Screen.Month,
                    onClick = { screen = Screen.Month },
                    icon = { Icon(Icons.Rounded.CalendarMonth, null) },
                    label = { Text("الشهر") }
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
                date = today,
                refresh = refresh,
                onChanged = {
                    refresh++
                    ReminderScheduler.scheduleAll(context)
                }
            )
            Screen.Month -> MonthScreen(
                modifier = Modifier.padding(padding),
                store = store,
                today = today,
                refresh = refresh
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
    date: LocalDate,
    refresh: Int,
    onChanged: () -> Unit
) {
    val context = LocalContext.current
    val tasks = remember(refresh, date) { store.activeTasks(date) }
    val (doneCount, totalCount) = store.dailyProgress(date)
    val progress = if (totalCount == 0) 0f else doneCount.toFloat() / totalCount
    val formatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))
    val currentMonth = YearMonth.from(date)

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(30.dp),
                colors = CardDefaults.cardColors(containerColor = HeroSurface)
            ) {
                Column(Modifier.padding(20.dp)) {
                    Surface(shape = RoundedCornerShape(50), color = Navy) {
                        Text(
                            "برنامج اليوم",
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            color = Color.White,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(Modifier.height(14.dp))
                    Text("يومي", fontSize = 30.sp, fontWeight = FontWeight.Black, color = Navy)
                    Text(date.format(formatter), color = MutedText, fontSize = 15.sp)
                    Spacer(Modifier.height(20.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("إنجاز اليوم", color = Navy, fontWeight = FontWeight.Bold)
                        Text("$doneCount / $totalCount", color = Navy, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.height(8.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(11.dp).clip(CircleShape),
                        color = Teal,
                        trackColor = Color.White
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "${(progress * 100).toInt()}% مكتمل",
                        color = Teal,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }

        if (!hasNotificationPermission(context) || !hasExactAlarmPermission(context)) {
            item { PermissionWarningCard(context) }
        }

        item {
            Text("تطوّرك هذا الشهر", color = Navy, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(progressDefinitions, key = { it.id }) { definition ->
                    MonthlyProgressCard(
                        definition = definition,
                        progress = store.monthlyProgress(definition.id, currentMonth)
                    )
                }
            }
        }

        val sections = tasks.groupBy { it.section }
        sections.forEach { (section, sectionTasks) ->
            item {
                Text(
                    section,
                    modifier = Modifier.padding(top = 8.dp, bottom = 2.dp),
                    color = Navy,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }

            items(sectionTasks, key = { it.id }) { task ->
                val monthlyProgress = task.progressGroup?.let {
                    store.monthlyProgress(it, currentMonth)
                }
                TaskCard(
                    task = task,
                    time = store.time(task),
                    done = store.isDone(task, date),
                    monthlyProgress = monthlyProgress,
                    onToggle = {
                        store.setDone(task, date, !store.isDone(task, date))
                        onChanged()
                    }
                )
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = Color(0xFFEFF9F8)
            ) {
                Text(
                    "عند الساعة 12:00 ليلًا ينتقل التطبيق تلقائيًا لليوم الجديد. إنجاز الأيام السابقة يبقى محفوظًا داخل التقويم.",
                    modifier = Modifier.padding(16.dp),
                    color = Navy,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionWarningCard(context: Context) {
    val notificationOk = hasNotificationPermission(context)
    val exactOk = hasExactAlarmPermission(context)

    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E5)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SpeakingOrange.copy(alpha = 0.30f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Alarm, null, tint = SpeakingOrange)
                Spacer(Modifier.size(8.dp))
                Text("التنبيه بحاجة لتفعيل", color = Navy, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(8.dp))
            Text(
                if (!notificationOk) "فعّلي إذن الإشعارات حتى يقدر التطبيق يرن ويعرض التنبيه."
                else "فعّلي المنبّهات الدقيقة حتى يوصل التنبيه بنفس الساعة المحددة.",
                color = MutedText,
                fontSize = 13.sp
            )
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    if (!notificationOk) openNotificationSettings(context)
                    else openExactAlarmSettings(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SpeakingOrange),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("تفعيل الآن")
            }
        }
    }
}

@Composable
private fun MonthlyProgressCard(
    definition: ProgressDefinition,
    progress: MonthlyProgress
) {
    Card(
        modifier = Modifier.size(width = 205.dp, height = 150.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, definition.color.copy(alpha = 0.18f))
    ) {
        Column(
            modifier = Modifier.padding(15.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .background(definition.color.copy(alpha = 0.12f), RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(definition.icon, null, tint = definition.color, modifier = Modifier.size(21.dp))
                }
                Spacer(Modifier.size(9.dp))
                Column {
                    Text(definition.title, color = Navy, fontWeight = FontWeight.Black)
                    Text(definition.subtitle, color = MutedText, fontSize = 11.sp)
                }
            }
            LinearProgressIndicator(
                progress = { progress.ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = definition.color,
                trackColor = definition.color.copy(alpha = 0.12f)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(progress.stage, color = definition.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text(
                    "${progress.completed}/${progress.target} ${definition.unit}",
                    color = Navy,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun TaskCard(
    task: RoutineTask,
    time: LocalTime,
    done: Boolean,
    monthlyProgress: MonthlyProgress?,
    onToggle: () -> Unit
) {
    val color = taskColor(task)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done) color.copy(alpha = 0.08f) else Color.White
        ),
        border = BorderStroke(1.dp, if (done) color.copy(alpha = 0.28f) else Color(0xFFE5EAF2))
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(color.copy(alpha = 0.12f), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(iconFor(task), null, tint = color)
                }

                Column(Modifier.weight(1f)) {
                    Text(task.title, color = Navy, fontWeight = FontWeight.Black)
                    Text(task.subtitle, color = MutedText, fontSize = 12.sp)
                    Spacer(Modifier.height(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Rounded.AccessTime, null, tint = color, modifier = Modifier.size(15.dp))
                        Spacer(Modifier.size(4.dp))
                        Text(formatTime(time), color = Navy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = onToggle,
                    shape = RoundedCornerShape(18.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (done) SuccessGreen else color
                    ),
                    contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
                ) {
                    if (done) {
                        Icon(Icons.Rounded.Check, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.size(4.dp))
                    }
                    Text(if (done) "تم" else "تم ✓")
                }
            }

            if (monthlyProgress != null) {
                Spacer(Modifier.height(12.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    color = color.copy(alpha = 0.07f)
                ) {
                    Column(Modifier.padding(horizontal = 12.dp, vertical = 9.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(monthlyProgress.stage, color = color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            Text(
                                "${monthlyProgress.completed}/${monthlyProgress.target}",
                                color = Navy,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { monthlyProgress.ratio },
                            modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                            color = color,
                            trackColor = color.copy(alpha = 0.14f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MonthScreen(
    modifier: Modifier,
    store: RoutineStore,
    today: LocalDate,
    refresh: Int
) {
    var month by remember { mutableStateOf(YearMonth.from(today)) }
    var selectedDate by remember { mutableStateOf(today) }

    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))
    val cells = remember(month) { calendarCells(month) }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("تقويم الشهر", color = Navy, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("كل يوم يحتفظ بإنجازه بشكل مستقل", color = MutedText, fontSize = 13.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(
                            onClick = {
                                month = month.minusMonths(1)
                                selectedDate = month.atDay(1)
                            }
                        ) {
                            Icon(Icons.Rounded.ChevronRight, "الشهر السابق", tint = Navy)
                        }
                        Text(
                            month.atDay(1).format(monthFormatter),
                            color = Navy,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )
                        IconButton(
                            onClick = {
                                month = month.plusMonths(1)
                                selectedDate = month.atDay(1)
                            }
                        ) {
                            Icon(Icons.Rounded.ChevronLeft, "الشهر التالي", tint = Navy)
                        }
                    }

                    Row(Modifier.fillMaxWidth()) {
                        listOf("س", "ح", "ن", "ث", "ر", "خ", "ج").forEach { dayName ->
                            Text(
                                dayName,
                                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                                textAlign = TextAlign.Center,
                                color = MutedText,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }

                    repeat(6) { weekIndex ->
                        Row(Modifier.fillMaxWidth()) {
                            repeat(7) { dayIndex ->
                                val date = cells[weekIndex * 7 + dayIndex]
                                CalendarCell(
                                    modifier = Modifier.weight(1f),
                                    date = date,
                                    selected = date == selectedDate,
                                    today = date == today,
                                    store = store,
                                    refresh = refresh,
                                    onClick = {
                                        if (date != null) selectedDate = date
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }

        item {
            SelectedDaySummary(store = store, date = selectedDate, today = today)
        }
    }
}

@Composable
private fun CalendarCell(
    modifier: Modifier,
    date: LocalDate?,
    selected: Boolean,
    today: Boolean,
    store: RoutineStore,
    refresh: Int,
    onClick: () -> Unit
) {
    if (date == null) {
        Spacer(modifier = modifier.height(66.dp))
        return
    }

    val (done, total) = remember(refresh, date) { store.dailyProgress(date) }
    val ratio = if (total == 0) 0f else done.toFloat() / total
    val isFuture = date.isAfter(LocalDate.now())
    val container = when {
        selected -> Navy
        ratio >= 1f -> SuccessGreen.copy(alpha = 0.12f)
        ratio > 0f -> PracticeAmber.copy(alpha = 0.12f)
        else -> Color.Transparent
    }

    Column(
        modifier = modifier
            .padding(2.dp)
            .height(66.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(container)
            .then(
                if (today && !selected) Modifier.border(1.5.dp, Teal, RoundedCornerShape(16.dp))
                else Modifier
            )
            .clickable(onClick = onClick)
            .padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            "${date.dayOfMonth}",
            color = if (selected) Color.White else Navy,
            fontWeight = if (today || selected) FontWeight.Black else FontWeight.SemiBold,
            fontSize = 14.sp
        )
        Spacer(Modifier.height(4.dp))
        if (!isFuture) {
            Box(
                modifier = Modifier
                    .size(7.dp)
                    .background(
                        when {
                            ratio >= 1f -> if (selected) Color.White else SuccessGreen
                            ratio > 0f -> PracticeAmber
                            else -> Color(0xFFD7DEE9)
                        },
                        CircleShape
                    )
            )
            if (ratio > 0f && total > 0) {
                Text(
                    "${(ratio * 100).toInt()}%",
                    color = if (selected) Color.White.copy(alpha = 0.85f) else MutedText,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun SelectedDaySummary(
    store: RoutineStore,
    date: LocalDate,
    today: LocalDate
) {
    val tasks = store.activeTasks(date)
    val (done, total) = store.dailyProgress(date)
    val ratio = if (total == 0) 0f else done.toFloat() / total
    val formatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))

    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(date.format(formatter), color = Navy, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
                when {
                    date.isAfter(today) -> "يوم قادم"
                    ratio >= 1f -> "مكتمل 100%"
                    else -> "$done من $total مهمة"
                },
                color = if (ratio >= 1f) SuccessGreen else MutedText,
                fontSize = 13.sp
            )

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (ratio >= 1f) SuccessGreen else GrammarBlue,
                trackColor = Color(0xFFE9EDF4)
            )
            Spacer(Modifier.height(12.dp))

            tasks.forEach { task ->
                val checked = store.isDone(task, date)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(
                                if (checked) SuccessGreen else Color(0xFFE9EDF4),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (checked) {
                            Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                        }
                    }
                    Spacer(Modifier.size(9.dp))
                    Text(task.title, color = Navy, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text(formatTime(store.time(task)), color = MutedText, fontSize = 11.sp)
                }
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
    val notificationsEnabled = remember(refresh) { store.notificationsEnabled() }
    val notificationPermission = hasNotificationPermission(context)
    val exactPermission = hasExactAlarmPermission(context)

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("الجدول والتنبيهات", color = Navy, fontSize = 28.sp, fontWeight = FontWeight.Black)
            Text("غيّري أي ساعة واضبطي صلاحيات المنبّه من هون", color = MutedText, fontSize = 13.sp)
        }

        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(44.dp)
                                .background(AccentPink.copy(alpha = 0.12f), RoundedCornerShape(14.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Notifications, null, tint = AccentPink)
                        }
                        Spacer(Modifier.size(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("التنبيهات اليومية", color = Navy, fontWeight = FontWeight.Black)
                            Text("صوت منبّه + اهتزاز + إشعار", color = MutedText, fontSize = 12.sp)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                store.setNotificationsEnabled(it)
                                if (it) ReminderScheduler.scheduleAll(context)
                                else ReminderScheduler.cancelAll(context)
                                onChanged()
                            }
                        )
                    }

                    Spacer(Modifier.height(14.dp))
                    PermissionStatusRow(
                        title = "إذن الإشعارات",
                        ok = notificationPermission,
                        actionLabel = if (notificationPermission) "مفعّل" else "فتح الإعدادات",
                        onAction = {
                            if (!notificationPermission) openNotificationSettings(context)
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                    PermissionStatusRow(
                        title = "المنبّهات الدقيقة",
                        ok = exactPermission,
                        actionLabel = if (exactPermission) "مفعّل" else "تفعيل",
                        onAction = {
                            if (!exactPermission) openExactAlarmSettings(context)
                        }
                    )

                    Spacer(Modifier.height(14.dp))
                    Button(
                        onClick = {
                            if (!notificationPermission) openNotificationSettings(context)
                            else ReminderScheduler.sendTestAlarm(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Rounded.Alarm, null)
                        Spacer(Modifier.size(7.dp))
                        Text("اختبار التنبيه الآن")
                    }
                }
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFFDDF7F5)
            ) {
                Text(
                    "التطبيق ينتقل تلقائيًا لليوم الجديد بعد 12:00 ليلًا، ويعيد جدولة تنبيهات اليوم التالي حتى بعد إعادة تشغيل الموبايل.",
                    modifier = Modifier.padding(15.dp),
                    color = Navy,
                    fontSize = 13.sp
                )
            }
        }

        item {
            Text("أوقات المهام", color = Navy, fontWeight = FontWeight.Black, fontSize = 20.sp)
        }

        items(routineTasks, key = { it.id }) { task ->
            val currentTime = store.time(task)
            val color = taskColor(task)

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(color.copy(alpha = 0.12f), RoundedCornerShape(13.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(iconFor(task), null, tint = color, modifier = Modifier.size(21.dp))
                    }
                    Spacer(Modifier.size(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(task.title, color = Navy, fontWeight = FontWeight.Bold)
                        Text(store.repeatLabel(task), color = MutedText, fontSize = 11.sp)
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
                        colors = ButtonDefaults.buttonColors(containerColor = color),
                        shape = RoundedCornerShape(16.dp),
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 9.dp)
                    ) {
                        Text(formatTime(currentTime), fontSize = 12.sp)
                    }
                }
            }
        }

        item {
            OutlinedButton(
                onClick = {
                    store.restartCleaningCycle()
                    onChanged()
                },
                modifier = Modifier.fillMaxWidth(),
                border = BorderStroke(1.dp, Teal),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(Icons.Rounded.CleaningServices, null, tint = Teal)
                Spacer(Modifier.size(7.dp))
                Text("اعتبري اليوم يوم تنظيف", color = Teal)
            }
        }

        item {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = HeroSurface
            ) {
                Text(
                    "وقت التهجد مضبوط افتراضيًا على 4:30 صباحًا. عدّليه حسب وقت الفجر عندك.",
                    modifier = Modifier.padding(16.dp),
                    color = Navy,
                    fontSize = 13.sp
                )
            }
        }
    }
}

@Composable
private fun PermissionStatusRow(
    title: String,
    ok: Boolean,
    actionLabel: String,
    onAction: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(10.dp)
                .background(if (ok) SuccessGreen else WarningRed, CircleShape)
        )
        Spacer(Modifier.size(8.dp))
        Text(title, modifier = Modifier.weight(1f), color = Navy, fontSize = 13.sp)
        if (ok) {
            Text(actionLabel, color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(actionLabel, fontSize = 11.sp)
            }
        }
    }
}

private fun calendarCells(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val saturdayValue = DayOfWeek.SATURDAY.value
    val offset = (first.dayOfWeek.value - saturdayValue + 7) % 7
    return List(42) { index ->
        val day = index - offset + 1
        if (day in 1..month.lengthOfMonth()) month.atDay(day) else null
    }
}

private fun openNotificationSettings(context: Context) {
    val intent = Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
        putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
    }
    context.startActivity(intent)
}

private fun openExactAlarmSettings(context: Context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        val intent = Intent(
            Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM,
            Uri.parse("package:${context.packageName}")
        )
        runCatching { context.startActivity(intent) }
            .onFailure {
                context.startActivity(
                    Intent(
                        Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                        Uri.parse("package:${context.packageName}")
                    )
                )
            }
    }
}

private fun formatTime(time: LocalTime): String {
    val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
    val period = if (time.hour < 12) "ص" else "م"
    return String.format(Locale("ar"), "%d:%02d %s", hour, time.minute, period)
}

private fun taskColor(task: RoutineTask): Color = when (task.id) {
    "wake" -> Navy
    "coffee", "breakfast", "lunch" -> PracticeAmber
    "workout" -> SpeakingOrange
    "quran" -> VocabularyGreen
    "turkish1", "turkish2" -> GrammarBlue
    "work" -> MemoryPurple
    "cleaning" -> Teal
    "husband" -> AccentPink
    "shower", "care", "scrub" -> ListeningCyan
    "need_prayer", "dhikr1", "dhikr2", "tahajjud" -> PronunciationRed
    else -> Navy
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
