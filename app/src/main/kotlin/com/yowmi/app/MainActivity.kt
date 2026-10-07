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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Alarm
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.CleaningServices
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.Favorite
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Notifications
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.SelfImprovement
import androidx.compose.material.icons.rounded.Spa
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.input.KeyboardType
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
private val AppBackground = Color(0xFFF5F7FC)
private val HeroSurface = Color(0xFFEAF0F9)
private val MutedText = Color(0xFF6E7A90)
private val SoftBorder = Color(0xFFE5EAF2)

private data class ProgressDefinition(
    val id: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector,
    val unit: String
)

private val progressDefinitions = listOf(
    ProgressDefinition("workout", "الرياضة", "استمرارية وحركة", SpeakingOrange, Icons.Rounded.FitnessCenter, "جلسة"),
    ProgressDefinition("quran", "القرآن", "ورد يومي ثابت", VocabularyGreen, Icons.Rounded.AutoStories, "ورد"),
    ProgressDefinition("turkish", "التركي", "جلسات التعلم", GrammarBlue, Icons.Rounded.AutoStories, "جلسة"),
    ProgressDefinition("work", "التطبيق / الشغل", "تقدم المشروع", MemoryPurple, Icons.Rounded.Work, "جلسة")
)

class MainActivity : ComponentActivity() {
    private val notificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            ReminderScheduler.scheduleAll(this)
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        intent.getStringExtra("ack_task_id")?.let { ReminderScheduler.acknowledge(this, it) }
        createReminderChannels(this)
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

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        intent.getStringExtra("ack_task_id")?.let { ReminderScheduler.acknowledge(this, it) }
    }

    override fun onResume() {
        super.onResume()
        createReminderChannels(this)
        ReminderScheduler.scheduleAll(this)
    }

    private fun requestNotificationPermissionIfNeeded() {
        if (
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED
        ) {
            notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }
}

private enum class Screen { Today, Month, Goals, Schedule }

@Composable
private fun YowmiApp() {
    val context = LocalContext.current
    val store = remember { RoutineStore(context) }

    var screen by remember { mutableStateOf(Screen.Today) }
    var selectedGoalId by remember { mutableStateOf("turkish") }
    var refresh by remember { mutableIntStateOf(0) }
    var today by remember { mutableStateOf(LocalDate.now()) }

    var showTaskEditor by remember { mutableStateOf(false) }
    var editingTask by remember { mutableStateOf<RoutineTask?>(null) }
    var deletingTask by remember { mutableStateOf<RoutineTask?>(null) }
    var goalToEdit by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        while (true) {
            val now = LocalDateTime.now()
            val nextMidnight = now.toLocalDate().plusDays(1).atStartOfDay().plusSeconds(2)
            delay(Duration.between(now, nextMidnight).toMillis().coerceAtLeast(1_000))
            today = LocalDate.now()
            refresh++
            ReminderScheduler.scheduleAll(context)
        }
    }

    Scaffold(
        containerColor = AppBackground,
        floatingActionButton = {
            if (screen == Screen.Today || screen == Screen.Schedule) {
                ExtendedFloatingActionButton(
                    onClick = {
                        editingTask = null
                        showTaskEditor = true
                    },
                    containerColor = Navy,
                    contentColor = Color.White,
                    icon = { Icon(Icons.Rounded.Add, null) },
                    text = { Text("مهمة جديدة", fontWeight = FontWeight.Bold) }
                )
            }
        },
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
                    selected = screen == Screen.Goals,
                    onClick = { screen = Screen.Goals },
                    icon = { Icon(Icons.Rounded.Flag, null) },
                    label = { Text("الأهداف") }
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
                },
                onOpenGoal = {
                    selectedGoalId = it
                    screen = Screen.Goals
                }
            )
            Screen.Month -> MonthScreen(
                modifier = Modifier.padding(padding),
                store = store,
                today = today,
                refresh = refresh
            )
            Screen.Goals -> AdvancedGoalsScreen(
                modifier = Modifier.padding(padding),
                selectedGoalId = selectedGoalId,
                onSelectGoal = { selectedGoalId = it }
            )
            Screen.Schedule -> ScheduleScreen(
                modifier = Modifier.padding(padding),
                store = store,
                refresh = refresh,
                onChanged = {
                    refresh++
                    ReminderScheduler.scheduleAll(context)
                },
                onEditTask = {
                    editingTask = it
                    showTaskEditor = true
                },
                onDeleteTask = { deletingTask = it }
            )
        }
    }

    if (showTaskEditor) {
        TaskEditorDialog(
            task = editingTask,
            onDismiss = { showTaskEditor = false },
            onSave = { task, time ->
                if (editingTask == null) store.addTask(task) else store.updateTask(task)
                store.setTime(task, time)
                showTaskEditor = false
                refresh++
                ReminderScheduler.scheduleAll(context)
            }
        )
    }

    deletingTask?.let { task ->
        AlertDialog(
            onDismissRequest = { deletingTask = null },
            title = { Text("حذف المهمة") },
            text = { Text("حذف «${task.title}» من الجدول؟ سجل الأيام السابقة يبقى محفوظًا.") },
            confirmButton = {
                Button(
                    onClick = {
                        ReminderScheduler.cancelTask(context, task.id)
                        store.deleteTask(task.id)
                        deletingTask = null
                        refresh++
                        ReminderScheduler.scheduleAll(context)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = WarningRed)
                ) { Text("حذف") }
            },
            dismissButton = {
                TextButton(onClick = { deletingTask = null }) { Text("إلغاء") }
            }
        )
    }

    goalToEdit?.let { goalId ->
        val definition = progressDefinitions.first { it.id == goalId }
        val month = YearMonth.from(today)
        GoalTargetDialog(
            definition = definition,
            currentTarget = store.goalTarget(goalId, month),
            onDismiss = { goalToEdit = null },
            onSave = { value ->
                store.setGoalTarget(goalId, month, value)
                goalToEdit = null
                refresh++
            }
        )
    }
}

@Composable
private fun TodayScreen(
    modifier: Modifier,
    store: RoutineStore,
    date: LocalDate,
    refresh: Int,
    onChanged: () -> Unit,
    onOpenGoal: (String) -> Unit
) {
    val context = LocalContext.current
    val tasks = remember(refresh, date) { store.activeTasks(date) }
    val (doneCount, totalCount) = store.dailyProgress(date)
    val progress = if (totalCount == 0) 0f else doneCount.toFloat() / totalCount
    val formatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))
    val currentMonth = YearMonth.from(date)
    val now = LocalTime.now()
    val nextTask = tasks.firstOrNull { !store.isDone(it, date) && store.time(it) >= now }
        ?: tasks.firstOrNull { !store.isDone(it, date) }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(13.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = Navy)
            ) {
                Column(Modifier.padding(21.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.Top
                    ) {
                        Column {
                            Text("يومي", color = Color.White, fontSize = 30.sp, fontWeight = FontWeight.Black)
                            Text(date.format(formatter), color = Color.White.copy(alpha = .72f), fontSize = 14.sp)
                        }
                        Surface(shape = CircleShape, color = Teal) {
                            Text(
                                "${(progress * 100).toInt()}%",
                                modifier = Modifier.padding(horizontal = 13.dp, vertical = 9.dp),
                                color = Color.White,
                                fontWeight = FontWeight.Black
                            )
                        }
                    }

                    Spacer(Modifier.height(20.dp))
                    LinearProgressIndicator(
                        progress = { progress },
                        modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                        color = Teal,
                        trackColor = Color.White.copy(alpha = .16f)
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$doneCount من $totalCount مهمة منجزة",
                        color = Color.White.copy(alpha = .78f),
                        fontSize = 12.sp
                    )

                    if (nextTask != null) {
                        Spacer(Modifier.height(18.dp))
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(22.dp),
                            color = Color.White.copy(alpha = .10f)
                        ) {
                            Row(
                                modifier = Modifier.padding(14.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(44.dp)
                                        .background(taskColor(nextTask), RoundedCornerShape(14.dp)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(iconFor(nextTask), null, tint = Color.White)
                                }
                                Spacer(Modifier.width(10.dp))
                                Column(Modifier.weight(1f)) {
                                    Text("المهمة التالية", color = Color.White.copy(alpha = .65f), fontSize = 11.sp)
                                    Text(nextTask.title, color = Color.White, fontWeight = FontWeight.Bold)
                                }
                                Text(formatTime(store.time(nextTask)), color = Color.White, fontWeight = FontWeight.Black)
                            }
                        }
                    }
                }
            }
        }

        if (!hasNotificationPermission(context) || !hasExactAlarmPermission(context)) {
            item { PermissionWarningCard(context) }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("أهداف هذا الشهر", color = Navy, fontWeight = FontWeight.Black, fontSize = 21.sp)
                    Text("اضغطي على أي هدف لتشوفي مساره", color = MutedText, fontSize = 12.sp)
                }
                Icon(Icons.Rounded.Stars, null, tint = AccentPink)
            }
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                items(progressDefinitions, key = { it.id }) { definition ->
                    CompactGoalCard(
                        definition = definition,
                        progress = store.monthlyProgress(definition.id, currentMonth),
                        onClick = { onOpenGoal(definition.id) }
                    )
                }
            }
        }

        val sections = tasks.groupBy { it.section }
        sections.forEach { (section, sectionTasks) ->
            item {
                Text(
                    section,
                    modifier = Modifier.padding(top = 7.dp),
                    color = Navy,
                    fontWeight = FontWeight.Black,
                    fontSize = 18.sp
                )
            }
            items(sectionTasks, key = { it.id }) { task ->
                TaskCard(
                    task = task,
                    time = store.time(task),
                    done = store.isDone(task, date),
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
                color = Color(0xFFE7F7F5)
            ) {
                Text(
                    "بعد 12:00 ليلًا يبدأ يوم جديد تلقائيًا. الأيام السابقة تبقى محفوظة بالتقويم، والمهام غير المنجزة لا تنتقل كأنها منجزة.",
                    modifier = Modifier.padding(16.dp),
                    color = Navy,
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun CompactGoalCard(
    definition: ProgressDefinition,
    progress: MonthlyProgress,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(220.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, definition.color.copy(alpha = .20f))
    ) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(definition.color.copy(alpha = .12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(definition.icon, null, tint = definition.color)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(definition.title, color = Navy, fontWeight = FontWeight.Black)
                    Text(progress.stage, color = definition.color, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Text("${(progress.ratio * 100).toInt()}%", color = Navy, fontWeight = FontWeight.Black)
            }
            LinearProgressIndicator(
                progress = { progress.ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = definition.color,
                trackColor = definition.color.copy(alpha = .12f)
            )
            Text(
                "${progress.completed} / ${progress.target} ${definition.unit}",
                color = MutedText,
                fontSize = 11.sp
            )
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
    val color = taskColor(task)

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done) color.copy(alpha = .07f) else Color.White
        ),
        border = BorderStroke(1.dp, if (done) color.copy(alpha = .30f) else SoftBorder)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(15.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .background(color.copy(alpha = .12f), RoundedCornerShape(15.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(iconFor(task), null, tint = color)
            }
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(task.title, color = Navy, fontWeight = FontWeight.Black)
                if (task.subtitle.isNotBlank()) {
                    Text(task.subtitle, color = MutedText, fontSize = 12.sp)
                }
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Rounded.AccessTime, null, tint = color, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text(formatTime(time), color = Navy, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
            Button(
                onClick = onToggle,
                colors = ButtonDefaults.buttonColors(containerColor = if (done) SuccessGreen else color),
                shape = RoundedCornerShape(18.dp),
                contentPadding = PaddingValues(horizontal = 14.dp, vertical = 10.dp)
            ) {
                if (done) {
                    Icon(Icons.Rounded.Check, null, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(4.dp))
                }
                Text(if (done) "تم" else "تم ✓")
            }
        }
    }
}

@Composable
private fun GoalsScreen(
    modifier: Modifier,
    store: RoutineStore,
    month: YearMonth,
    refresh: Int,
    selectedGoalId: String,
    onSelectGoal: (String) -> Unit,
    onEditGoal: (String) -> Unit
) {
    val definition = progressDefinitions.firstOrNull { it.id == selectedGoalId } ?: progressDefinitions.first()
    val progress = remember(refresh, selectedGoalId, month) { store.monthlyProgress(definition.id, month) }
    val streak = remember(refresh, selectedGoalId) { store.currentStreak(definition.id) }
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))
    val last7 = (6 downTo 0).map { LocalDate.now().minusDays(it.toLong()) }

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("أهدافي", color = Navy, fontSize = 30.sp, fontWeight = FontWeight.Black)
            Text(month.atDay(1).format(monthFormatter), color = MutedText, fontSize = 13.sp)
        }

        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                items(progressDefinitions, key = { it.id }) { item ->
                    FilterChip(
                        selected = item.id == definition.id,
                        onClick = { onSelectGoal(item.id) },
                        label = { Text(item.title) },
                        leadingIcon = { Icon(item.icon, null, modifier = Modifier.size(17.dp)) }
                    )
                }
            }
        }

        item {
            Card(
                shape = RoundedCornerShape(32.dp),
                colors = CardDefaults.cardColors(containerColor = definition.color)
            ) {
                Column(Modifier.padding(21.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(54.dp)
                                .background(Color.White.copy(alpha = .16f), RoundedCornerShape(18.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(definition.icon, null, tint = Color.White, modifier = Modifier.size(29.dp))
                        }
                        Spacer(Modifier.width(12.dp))
                        Column(Modifier.weight(1f)) {
                            Text(definition.title, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Black)
                            Text(progress.stage, color = Color.White.copy(alpha = .76f), fontSize = 12.sp)
                        }
                        Text(
                            "${(progress.ratio * 100).toInt()}%",
                            color = Color.White,
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Black
                        )
                    }

                    Spacer(Modifier.height(22.dp))
                    LinearProgressIndicator(
                        progress = { progress.ratio },
                        modifier = Modifier.fillMaxWidth().height(12.dp).clip(CircleShape),
                        color = Color.White,
                        trackColor = Color.White.copy(alpha = .20f)
                    )
                    Spacer(Modifier.height(9.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("${progress.completed} من ${progress.target} ${definition.unit}", color = Color.White, fontWeight = FontWeight.Bold)
                        Text("المحطة التالية ${progress.nextMilestone}%", color = Color.White.copy(alpha = .80f), fontSize = 12.sp)
                    }

                    Spacer(Modifier.height(18.dp))
                    Button(
                        onClick = { onEditGoal(definition.id) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = definition.color
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Rounded.Edit, null)
                        Spacer(Modifier.width(7.dp))
                        Text("تعديل هدفي لهذا الشهر", fontWeight = FontWeight.Black)
                    }
                }
            }
        }

        item {
            Text("طريق الهدف", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        item {
            MilestoneRoadmap(progress = progress, color = definition.color)
        }

        item {
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "السلسلة الحالية",
                    value = "$streak يوم",
                    accent = Teal
                )
                MetricCard(
                    modifier = Modifier.weight(1f),
                    title = "المتبقي",
                    value = "${(progress.target - progress.completed).coerceAtLeast(0)} ${definition.unit}",
                    accent = AccentPink
                )
            }
        }

        item {
            Text("آخر 7 أيام", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        item {
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    last7.forEach { date ->
                        val (done, total) = store.groupDayProgress(definition.id, date)
                        val complete = total > 0 && done == total
                        val partial = done > 0 && !complete
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                date.dayOfWeek.getDisplayName(java.time.format.TextStyle.SHORT, Locale("ar")),
                                color = MutedText,
                                fontSize = 10.sp
                            )
                            Spacer(Modifier.height(7.dp))
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .background(
                                        when {
                                            complete -> definition.color
                                            partial -> definition.color.copy(alpha = .22f)
                                            else -> Color(0xFFF0F2F7)
                                        },
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (complete) {
                                    Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                                } else {
                                    Text("${date.dayOfMonth}", color = Navy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MilestoneRoadmap(progress: MonthlyProgress, color: Color) {
    Card(
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(17.dp)) {
            val milestones = listOf(25, 50, 75, 100)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                milestones.forEachIndexed { index, milestone ->
                    val reached = progress.ratio * 100 >= milestone
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(if (reached) color else Color(0xFFF0F2F7), CircleShape)
                            .border(
                                2.dp,
                                if (reached) color else Color(0xFFDCE2EB),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (reached) {
                            Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        } else {
                            Text("$milestone", color = MutedText, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (index < milestones.lastIndex) {
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .height(4.dp)
                                .background(
                                    if (progress.ratio * 100 >= milestones[index + 1]) color
                                    else Color(0xFFE8ECF3),
                                    CircleShape
                                )
                        )
                    }
                }
            }
            Spacer(Modifier.height(10.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                listOf("انطلاقة", "استمرار", "تقدّم", "وصول").forEach {
                    Text(it, color = MutedText, fontSize = 10.sp)
                }
            }
        }
    }
}

@Composable
private fun MetricCard(modifier: Modifier, title: String, value: String, accent: Color) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, accent.copy(alpha = .18f))
    ) {
        Column(Modifier.padding(15.dp)) {
            Text(title, color = MutedText, fontSize = 11.sp)
            Spacer(Modifier.height(5.dp))
            Text(value, color = Navy, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Box(
                Modifier
                    .width(34.dp)
                    .height(5.dp)
                    .background(accent, CircleShape)
            )
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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 90.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("تقويم الشهر", color = Navy, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Text("سجل يومي واضح، وما بينمسح لما يبدأ يوم جديد", color = MutedText, fontSize = 12.sp)
        }

        item {
            Card(shape = RoundedCornerShape(30.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        IconButton(onClick = {
                            month = month.minusMonths(1)
                            selectedDate = month.atDay(1)
                        }) { Icon(Icons.Rounded.ChevronRight, "الشهر السابق", tint = Navy) }

                        Text(
                            month.atDay(1).format(monthFormatter),
                            color = Navy,
                            fontWeight = FontWeight.Black,
                            fontSize = 18.sp
                        )

                        IconButton(onClick = {
                            month = month.plusMonths(1)
                            selectedDate = month.atDay(1)
                        }) { Icon(Icons.Rounded.ChevronLeft, "الشهر التالي", tint = Navy) }
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
                                    onClick = { if (date != null) selectedDate = date }
                                )
                            }
                        }
                    }
                }
            }
        }

        item { SelectedDaySummary(store = store, date = selectedDate, today = today) }
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

    Column(
        modifier = modifier
            .padding(2.dp)
            .height(66.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(
                when {
                    selected -> Navy
                    ratio >= 1f -> SuccessGreen.copy(alpha = .11f)
                    ratio > 0f -> PracticeAmber.copy(alpha = .11f)
                    else -> Color.Transparent
                }
            )
            .then(if (today && !selected) Modifier.border(1.5.dp, Teal, RoundedCornerShape(16.dp)) else Modifier)
            .clickable(onClick = onClick)
            .padding(5.dp),
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
            if (ratio > 0f) {
                Text(
                    "${(ratio * 100).toInt()}%",
                    color = if (selected) Color.White.copy(alpha = .8f) else MutedText,
                    fontSize = 8.sp
                )
            }
        }
    }
}

@Composable
private fun SelectedDaySummary(store: RoutineStore, date: LocalDate, today: LocalDate) {
    val tasks = store.activeTasks(date)
    val (done, total) = store.dailyProgress(date)
    val ratio = if (total == 0) 0f else done.toFloat() / total
    val formatter = DateTimeFormatter.ofPattern("EEEE، d MMMM", Locale("ar"))

    Card(shape = RoundedCornerShape(27.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(16.dp)) {
            Text(date.format(formatter), color = Navy, fontWeight = FontWeight.Black, fontSize = 18.sp)
            Text(
                when {
                    date.isAfter(today) -> "يوم قادم"
                    ratio >= 1f -> "مكتمل 100%"
                    else -> "$done من $total مهمة"
                },
                color = if (ratio >= 1f) SuccessGreen else MutedText,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(11.dp))
            LinearProgressIndicator(
                progress = { ratio },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = if (ratio >= 1f) SuccessGreen else GrammarBlue,
                trackColor = Color(0xFFE9EDF4)
            )
            Spacer(Modifier.height(11.dp))
            tasks.forEach { task ->
                val checked = store.isDone(task, date)
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 5.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(26.dp)
                            .background(if (checked) SuccessGreen else Color(0xFFE9EDF4), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (checked) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                    }
                    Spacer(Modifier.width(9.dp))
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
    onChanged: () -> Unit,
    onEditTask: (RoutineTask) -> Unit,
    onDeleteTask: (RoutineTask) -> Unit
) {
    val context = LocalContext.current
    val tasks = remember(refresh) { store.tasks().sortedBy { store.time(it) } }
    val notificationsEnabled = remember(refresh) { store.notificationsEnabled() }
    val notificationPermission = hasNotificationPermission(context)
    val exactPermission = hasExactAlarmPermission(context)

    LazyColumn(
        modifier = modifier.fillMaxSize().background(AppBackground),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Text("جدولي", color = Navy, fontSize = 29.sp, fontWeight = FontWeight.Black)
            Text("أضيفي، عدّلي أو احذفي أي مهمة — البرنامج صار مرن بالكامل", color = MutedText, fontSize = 12.sp)
        }

        item {
            Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(46.dp)
                                .background(AccentPink.copy(alpha = .12f), RoundedCornerShape(15.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Rounded.Notifications, null, tint = AccentPink)
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text("نظام التذكير الذكي", color = Navy, fontWeight = FontWeight.Black)
                            Text("موسيقا لطيفة أولًا، وبعد 3 دقائق منبّه إذا ما انتبهتي", color = MutedText, fontSize = 11.sp)
                        }
                        Switch(
                            checked = notificationsEnabled,
                            onCheckedChange = {
                                store.setNotificationsEnabled(it)
                                if (it) ReminderScheduler.scheduleAll(context) else ReminderScheduler.cancelAll(context)
                                onChanged()
                            }
                        )
                    }

                    Spacer(Modifier.height(13.dp))
                    PermissionStatusRow(
                        title = "إذن الإشعارات",
                        ok = notificationPermission,
                        actionLabel = if (notificationPermission) "مفعّل" else "فتح الإعدادات",
                        onAction = { if (!notificationPermission) openNotificationSettings(context) }
                    )
                    Spacer(Modifier.height(7.dp))
                    PermissionStatusRow(
                        title = "التنبيهات الدقيقة",
                        ok = exactPermission,
                        actionLabel = if (exactPermission) "مفعّل" else "تفعيل",
                        onAction = { if (!exactPermission) openExactAlarmSettings(context) }
                    )

                    Spacer(Modifier.height(13.dp))
                    Button(
                        onClick = {
                            if (!notificationPermission) openNotificationSettings(context)
                            else ReminderScheduler.sendTestReminder(context)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPink),
                        shape = RoundedCornerShape(18.dp)
                    ) {
                        Icon(Icons.Rounded.Alarm, null)
                        Spacer(Modifier.width(7.dp))
                        Text("جرّبي التذكير اللطيف الآن", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text("المهام المتكررة", color = Navy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        items(tasks, key = { it.id }) { task ->
            EditableTaskRow(
                task = task,
                time = store.time(task),
                repeatLabel = store.repeatLabel(task),
                onTimeClick = {
                    val current = store.time(task)
                    TimePickerDialog(
                        context,
                        { _, hour, minute ->
                            store.setTime(task, LocalTime.of(hour, minute))
                            onChanged()
                        },
                        current.hour,
                        current.minute,
                        true
                    ).show()
                },
                onEdit = { onEditTask(task) },
                onDelete = { onDeleteTask(task) }
            )
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
                Spacer(Modifier.width(7.dp))
                Text("اعتبري اليوم يوم تنظيف", color = Teal)
            }
        }
    }
}

@Composable
private fun EditableTaskRow(
    task: RoutineTask,
    time: LocalTime,
    repeatLabel: String,
    onTimeClick: () -> Unit,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val color = taskColor(task)
    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, SoftBorder)
    ) {
        Column(Modifier.padding(14.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(45.dp)
                        .background(color.copy(alpha = .12f), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) { Icon(iconFor(task), null, tint = color) }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text(task.title, color = Navy, fontWeight = FontWeight.Black)
                    Text(repeatLabel, color = MutedText, fontSize = 11.sp)
                }

                IconButton(onClick = onEdit) {
                    Icon(Icons.Rounded.Edit, "تعديل", tint = Navy)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Rounded.Delete, "حذف", tint = WarningRed)
                }
            }
            Spacer(Modifier.height(8.dp))
            OutlinedButton(
                onClick = onTimeClick,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.dp, color.copy(alpha = .35f))
            ) {
                Icon(Icons.Rounded.AccessTime, null, tint = color, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(5.dp))
                Text(formatTime(time), color = Navy, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun TaskEditorDialog(
    task: RoutineTask?,
    onDismiss: () -> Unit,
    onSave: (RoutineTask, LocalTime) -> Unit
) {
    val context = LocalContext.current
    var title by remember(task?.id) { mutableStateOf(task?.title.orEmpty()) }
    var subtitle by remember(task?.id) { mutableStateOf(task?.subtitle.orEmpty()) }
    var time by remember(task?.id) { mutableStateOf(task?.defaultTime ?: LocalTime.of(12, 0)) }
    var section by remember(task?.id) { mutableStateOf(task?.section ?: "الظهر") }
    var repeatType by remember(task?.id) {
        mutableStateOf(
            when (task?.repeatRule) {
                RepeatRule.AlternateDays -> "alternate"
                is RepeatRule.Weekly -> "weekly"
                else -> "daily"
            }
        )
    }
    var weeklyDays by remember(task?.id) {
        mutableStateOf(
            (task?.repeatRule as? RepeatRule.Weekly)?.days ?: setOf(DayOfWeek.MONDAY)
        )
    }
    var progressGroup by remember(task?.id) { mutableStateOf(task?.progressGroup) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (task == null) "مهمة جديدة" else "تعديل المهمة", color = Navy, fontWeight = FontWeight.Black) },
        text = {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(11.dp)) {
                item {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("اسم المهمة") },
                        singleLine = true
                    )
                }
                item {
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text("ملاحظة قصيرة — اختياري") },
                        maxLines = 2
                    )
                }
                item {
                    OutlinedButton(
                        onClick = {
                            TimePickerDialog(
                                context,
                                { _, hour, minute -> time = LocalTime.of(hour, minute) },
                                time.hour,
                                time.minute,
                                true
                            ).show()
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Icon(Icons.Rounded.AccessTime, null)
                        Spacer(Modifier.width(6.dp))
                        Text(formatTime(time))
                    }
                }
                item {
                    Text("فترة اليوم", color = Navy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        items(listOf("الصباح", "الظهر", "المساء", "قبل النوم", "قبل الفجر")) { item ->
                            FilterChip(
                                selected = section == item,
                                onClick = { section = item },
                                label = { Text(item) }
                            )
                        }
                    }
                }
                item {
                    Text("التكرار", color = Navy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        FilterChip(selected = repeatType == "daily", onClick = { repeatType = "daily" }, label = { Text("يومي") })
                        FilterChip(selected = repeatType == "alternate", onClick = { repeatType = "alternate" }, label = { Text("يوم إيه/لا") })
                        FilterChip(selected = repeatType == "weekly", onClick = { repeatType = "weekly" }, label = { Text("أيام محددة") })
                    }
                }
                if (repeatType == "weekly") {
                    item {
                        LazyRow(horizontalArrangement = Arrangement.spacedBy(5.dp)) {
                            items(DayOfWeek.entries) { day ->
                                FilterChip(
                                    selected = day in weeklyDays,
                                    onClick = {
                                        weeklyDays = if (day in weeklyDays) weeklyDays - day else weeklyDays + day
                                    },
                                    label = { Text(shortArabicDay(day)) }
                                )
                            }
                        }
                    }
                }
                item {
                    Text("هل تدخل ضمن هدف؟", color = Navy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        item {
                            FilterChip(
                                selected = progressGroup == null,
                                onClick = { progressGroup = null },
                                label = { Text("بدون هدف") }
                            )
                        }
                        items(progressDefinitions) { goal ->
                            FilterChip(
                                selected = progressGroup == goal.id,
                                onClick = { progressGroup = goal.id },
                                label = { Text(goal.title) }
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isBlank()) return@Button
                    val rule = when (repeatType) {
                        "alternate" -> RepeatRule.AlternateDays
                        "weekly" -> RepeatRule.Weekly(weeklyDays.ifEmpty { setOf(DayOfWeek.MONDAY) })
                        else -> RepeatRule.Daily
                    }
                    onSave(
                        RoutineTask(
                            id = task?.id ?: "custom_${System.currentTimeMillis()}",
                            title = title.trim(),
                            subtitle = subtitle.trim(),
                            defaultTime = time,
                            section = section,
                            repeatRule = rule,
                            progressGroup = progressGroup,
                            isCustom = task?.isCustom ?: true
                        ),
                        time
                    )
                },
                enabled = title.isNotBlank()
            ) { Text("حفظ") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun GoalTargetDialog(
    definition: ProgressDefinition,
    currentTarget: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var value by remember(definition.id) { mutableStateOf(currentTarget.toString()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("هدف ${definition.title}", color = Navy, fontWeight = FontWeight.Black) },
        text = {
            Column {
                Text(
                    "حددي الرقم اللي بدك توصليله خلال الشهر. كل مرة تعملي «تم» لمهمة مرتبطة بهذا الهدف بتنحسب خطوة.",
                    color = MutedText,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it.filter(Char::isDigit).take(4) },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("الهدف الشهري (${definition.unit})") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { value.toIntOrNull()?.takeIf { it > 0 }?.let(onSave) },
                enabled = (value.toIntOrNull() ?: 0) > 0
            ) { Text("اعتماد الهدف") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun PermissionWarningCard(context: Context) {
    val notificationOk = hasNotificationPermission(context)
    val exactOk = hasExactAlarmPermission(context)
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF2E5)),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, SpeakingOrange.copy(alpha = .30f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Alarm, null, tint = SpeakingOrange)
                Spacer(Modifier.width(8.dp))
                Text("فعّلي التنبيهات الكاملة", color = Navy, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(7.dp))
            Text(
                if (!notificationOk) "إذن الإشعارات مطفأ، لذلك التطبيق ما بيقدر يعرض تذكيراته."
                else "فعّلي التنبيهات الدقيقة حتى التذكير يوصل بنفس الساعة المحددة.",
                color = MutedText,
                fontSize = 12.sp
            )
            Spacer(Modifier.height(10.dp))
            Button(
                onClick = {
                    if (!notificationOk) openNotificationSettings(context) else openExactAlarmSettings(context)
                },
                colors = ButtonDefaults.buttonColors(containerColor = SpeakingOrange),
                shape = RoundedCornerShape(16.dp)
            ) { Text("تفعيل الآن") }
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
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(10.dp).background(if (ok) SuccessGreen else WarningRed, CircleShape))
        Spacer(Modifier.width(8.dp))
        Text(title, modifier = Modifier.weight(1f), color = Navy, fontSize = 13.sp)
        if (ok) {
            Text(actionLabel, color = SuccessGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        } else {
            OutlinedButton(
                onClick = onAction,
                shape = RoundedCornerShape(14.dp),
                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp)
            ) { Text(actionLabel, fontSize = 11.sp) }
        }
    }
}

private fun calendarCells(month: YearMonth): List<LocalDate?> {
    val first = month.atDay(1)
    val offset = (first.dayOfWeek.value - DayOfWeek.SATURDAY.value + 7) % 7
    return List(42) { index ->
        val day = index - offset + 1
        if (day in 1..month.lengthOfMonth()) month.atDay(day) else null
    }
}

private fun openNotificationSettings(context: Context) {
    context.startActivity(
        Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
            putExtra(Settings.EXTRA_APP_PACKAGE, context.packageName)
        }
    )
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

private fun shortArabicDay(day: DayOfWeek): String = when (day) {
    DayOfWeek.SATURDAY -> "س"
    DayOfWeek.SUNDAY -> "ح"
    DayOfWeek.MONDAY -> "ن"
    DayOfWeek.TUESDAY -> "ث"
    DayOfWeek.WEDNESDAY -> "ر"
    DayOfWeek.THURSDAY -> "خ"
    DayOfWeek.FRIDAY -> "ج"
}

private fun formatTime(time: LocalTime): String {
    val hour = if (time.hour % 12 == 0) 12 else time.hour % 12
    val period = if (time.hour < 12) "ص" else "م"
    return String.format(Locale("ar"), "%d:%02d %s", hour, time.minute, period)
}

private fun taskColor(task: RoutineTask): Color = when (task.progressGroup ?: task.id) {
    "workout" -> SpeakingOrange
    "quran" -> VocabularyGreen
    "turkish", "turkish1", "turkish2" -> GrammarBlue
    "work" -> MemoryPurple
    "cleaning" -> Teal
    "husband" -> AccentPink
    "shower", "care", "scrub" -> ListeningCyan
    "need_prayer", "dhikr1", "dhikr2", "tahajjud" -> PronunciationRed
    "coffee", "breakfast", "lunch" -> PracticeAmber
    else -> Navy
}

private fun iconFor(task: RoutineTask): ImageVector = when (task.progressGroup ?: task.id) {
    "workout" -> Icons.Rounded.FitnessCenter
    "quran", "turkish", "turkish1", "turkish2" -> Icons.Rounded.AutoStories
    "work" -> Icons.Rounded.Work
    "cleaning" -> Icons.Rounded.CleaningServices
    "husband" -> Icons.Rounded.Favorite
    "shower", "care", "scrub" -> Icons.Rounded.Spa
    "need_prayer", "dhikr1", "dhikr2", "tahajjud" -> Icons.Rounded.SelfImprovement
    "coffee", "breakfast", "lunch" -> Icons.Rounded.Restaurant
    "wake" -> Icons.Rounded.Alarm
    else -> Icons.Rounded.Schedule
}
