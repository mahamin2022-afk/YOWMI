package com.yowmi.app

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material.icons.rounded.Remove
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

private val GoalNavy = Color(0xFF172B4D)
private val GoalTeal = Color(0xFF00A9A5)
private val GoalPink = Color(0xFFEC4B99)
private val GoalBlue = Color(0xFF3478F6)
private val GoalGreen = Color(0xFF00B88A)
private val GoalPurple = Color(0xFF8B5CF6)
private val GoalOrange = Color(0xFFFF7A00)
private val GoalAmber = Color(0xFFFFB000)
private val GoalBg = Color(0xFFF5F7FC)
private val GoalMuted = Color(0xFF6E7A90)
private val GoalBorder = Color(0xFFE5EAF2)

private data class GoalUi(
    val kind: GoalKind,
    val id: String,
    val title: String,
    val subtitle: String,
    val color: Color,
    val icon: ImageVector
)

private val goalUiList = listOf(
    GoalUi(GoalKind.QURAN, "quran", "القرآن", "ختمات وورد يومي", GoalGreen, Icons.Rounded.AutoStories),
    GoalUi(GoalKind.TURKISH, "turkish", "التركي", "رحلة إنهاء A1", GoalBlue, Icons.Rounded.AutoStories),
    GoalUi(GoalKind.FITNESS, "workout", "الرياضة", "خطة أسبوعية + تكرارات", GoalOrange, Icons.Rounded.FitnessCenter),
    GoalUi(GoalKind.WORK, "work", "التطبيق / الشغل", "مراحل المشروع", GoalPurple, Icons.Rounded.Work)
)

@Composable
internal fun AdvancedGoalsScreen(
    modifier: Modifier,
    selectedGoalId: String,
    onSelectGoal: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { GoalJourneyStore(context) }
    var refresh by remember { mutableIntStateOf(0) }
    var showQuranTarget by remember { mutableStateOf(false) }
    var showWorkTitle by remember { mutableStateOf(false) }

    val month = YearMonth.now()
    val selected = goalUiList.firstOrNull { it.id == selectedGoalId } ?: goalUiList[1]
    val allOverview = goalUiList.map { store.overview(it.kind, month) }
    val totalXp = allOverview.sumOf { it.xp }
    val globalLevel = (totalXp / 500) + 1
    val totalRatio = if (allOverview.isEmpty()) 0f else allOverview.map { it.ratio }.average().toFloat()

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxSize().background(GoalBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            GameProfileHeader(
                level = globalLevel,
                xp = totalXp,
                overallProgress = totalRatio
            )
        }

        item {
            Text("اختر الرحلة", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(8.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goalUiList.forEach { goal ->
                    val overview = store.overview(goal.kind, month)
                    GoalSelectorChip(
                        goal = goal,
                        selected = goal.id == selected.id,
                        percent = (overview.ratio * 100).toInt(),
                        onClick = { onSelectGoal(goal.id) }
                    )
                }
            }
        }

        item {
            GoalJourneyHero(
                goal = selected,
                overview = store.overview(selected.kind, month),
                actionLabel = when (selected.kind) {
                    GoalKind.QURAN -> "تعديل عدد الختمات"
                    GoalKind.WORK -> "تعديل هدف المشروع"
                    else -> null
                },
                onAction = {
                    when (selected.kind) {
                        GoalKind.QURAN -> showQuranTarget = true
                        GoalKind.WORK -> showWorkTitle = true
                        else -> Unit
                    }
                }
            )
        }

        item {
            Text("خريطة الرحلة", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        item {
            StageRoadmap(
                stageIndex = store.overview(selected.kind, month).stageIndex,
                color = selected.color,
                labels = stageLabels(selected.kind, store)
            )
        }

        when (selected.kind) {
            GoalKind.QURAN -> quranItems(store, month, selected.color, refresh) { refresh++ }
            GoalKind.TURKISH -> turkishItems(store, selected.color, refresh) { refresh++ }
            GoalKind.FITNESS -> fitnessItems(store, month, selected.color, refresh) { refresh++ }
            GoalKind.WORK -> workItems(store, selected.color, refresh) { refresh++ }
        }
    }

    if (showQuranTarget) {
        QuranTargetDialog(
            current = store.quranTargetKhatmas(),
            onDismiss = { showQuranTarget = false },
            onSave = {
                store.setQuranTargetKhatmas(it)
                showQuranTarget = false
                refresh++
            }
        )
    }

    if (showWorkTitle) {
        WorkTitleDialog(
            current = store.workGoalTitle(),
            onDismiss = { showWorkTitle = false },
            onSave = {
                store.setWorkGoalTitle(it)
                showWorkTitle = false
                refresh++
            }
        )
    }
}

@Composable
private fun GameProfileHeader(level: Int, xp: Int, overallProgress: Float) {
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = GoalNavy)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(GoalPink, RoundedCornerShape(19.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Stars, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("رحلة أهدافي", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("كل إنجاز صغير يفتح مرحلة جديدة", color = Color.White.copy(alpha = .68f), fontSize = 12.sp)
                }
                Surface(shape = RoundedCornerShape(16.dp), color = Color.White.copy(alpha = .12f)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 13.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("LEVEL", color = Color.White.copy(alpha = .62f), fontSize = 8.sp, fontWeight = FontWeight.Bold)
                        Text("$level", color = Color.White, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    }
                }
            }
            Spacer(Modifier.height(18.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("تقدم كل الأهداف", color = Color.White.copy(alpha = .76f), fontSize = 12.sp)
                Text("$xp XP", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }
            Spacer(Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = { overallProgress },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = GoalTeal,
                trackColor = Color.White.copy(alpha = .15f)
            )
        }
    }
}

@Composable
private fun GoalSelectorChip(goal: GoalUi, selected: Boolean, percent: Int, onClick: () -> Unit) {
    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        color = if (selected) goal.color else Color.White,
        border = if (selected) null else BorderStroke(1.dp, GoalBorder)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 13.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                goal.icon,
                null,
                tint = if (selected) Color.White else goal.color,
                modifier = Modifier.size(19.dp)
            )
            Spacer(Modifier.width(7.dp))
            Column {
                Text(
                    goal.title,
                    color = if (selected) Color.White else GoalNavy,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Text(
                    "$percent%",
                    color = if (selected) Color.White.copy(alpha = .75f) else GoalMuted,
                    fontSize = 9.sp
                )
            }
        }
    }
}

@Composable
private fun GoalJourneyHero(
    goal: GoalUi,
    overview: GoalOverview,
    actionLabel: String?,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, goal.color.copy(alpha = .18f))
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .background(goal.color.copy(alpha = .12f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(goal.icon, null, tint = goal.color, modifier = Modifier.size(28.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(goal.title, color = GoalNavy, fontSize = 22.sp, fontWeight = FontWeight.Black)
                    Text(goal.subtitle, color = GoalMuted, fontSize = 11.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("${(overview.ratio * 100).toInt()}%", color = goal.color, fontSize = 24.sp, fontWeight = FontWeight.Black)
                    Text("${overview.completed}/${overview.target}", color = GoalMuted, fontSize = 10.sp)
                }
            }
            Spacer(Modifier.height(14.dp))
            LinearProgressIndicator(
                progress = { overview.ratio },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = goal.color,
                trackColor = goal.color.copy(alpha = .10f)
            )
            Spacer(Modifier.height(13.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                StatPill("LVL ${overview.level}", goal.color)
                StatPill("${overview.xp} XP", GoalTeal)
                StatPill("${overview.streak} يوم متتالي", GoalPink)
            }
            if (actionLabel != null) {
                Spacer(Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onAction,
                    modifier = Modifier.fillMaxWidth(),
                    border = BorderStroke(1.dp, goal.color.copy(alpha = .40f)),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Icon(Icons.Rounded.Edit, null, tint = goal.color, modifier = Modifier.size(17.dp))
                    Spacer(Modifier.width(6.dp))
                    Text(actionLabel, color = goal.color, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun StatPill(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .10f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun StageRoadmap(stageIndex: Int, color: Color, labels: List<String>) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(17.dp)) {
            labels.take(4).forEachIndexed { index, label ->
                val stage = index + 1
                val reached = stage <= stageIndex || stageIndex >= 4
                val current = stage == stageIndex + 1 && stageIndex < 4
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                when {
                                    reached -> color
                                    current -> color.copy(alpha = .15f)
                                    else -> Color(0xFFF0F2F7)
                                },
                                CircleShape
                            )
                            .border(
                                2.dp,
                                if (current || reached) color else GoalBorder,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            reached -> Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                            current -> Text("$stage", color = color, fontWeight = FontWeight.Black)
                            else -> Icon(Icons.Rounded.Lock, null, tint = GoalMuted, modifier = Modifier.size(17.dp))
                        }
                    }
                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text("المرحلة $stage", color = GoalNavy, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        Text(label, color = if (current || reached) GoalNavy else GoalMuted, fontWeight = FontWeight.Black)
                    }
                    if (current) {
                        Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .10f)) {
                            Text("الحالية", modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp), color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
                if (index < 3) {
                    Box(
                        modifier = Modifier
                            .padding(start = 19.dp)
                            .width(4.dp)
                            .height(24.dp)
                            .background(if (reached) color.copy(alpha = .55f) else GoalBorder, CircleShape)
                    )
                }
            }
        }
    }
}

private fun stageLabels(kind: GoalKind, store: GoalJourneyStore): List<String> = when (kind) {
    GoalKind.QURAN -> {
        val total = store.quranTargetKhatmas() * 30
        listOf(
            "الوصول إلى ${total / 4} جزء",
            "نصف الرحلة — ${total / 2} جزء",
            "ثبات الورد — ${(total * 3) / 4} جزء",
            "الهدف الكامل — $total جزء"
        ).map { it.replace("$", "$") }
    }
    GoalKind.TURKISH -> listOf("الأساس والنطق", "الحياة اليومية", "التواصل الحقيقي", "إتقان A1")
    GoalKind.FITNESS -> listOf("تهيئة الجسم", "ثبات الروتين", "رفع القدرة", "شهر مكتمل")
    GoalKind.WORK -> listOf("التخطيط", "التصميم", "التنفيذ", "الاختبار والتسليم")
}

private fun androidx.compose.foundation.lazy.LazyListScope.quranItems(
    store: GoalJourneyStore,
    month: YearMonth,
    color: Color,
    refresh: Int,
    onChanged: () -> Unit
) {
    item {
        Text("مهمة اليوم", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
    item {
        val date = LocalDate.now()
        val today = store.quranTodayJuz(date)
        val dailyTarget = store.quranDailyTarget(month)
        Card(
            shape = RoundedCornerShape(28.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            border = BorderStroke(1.dp, color.copy(alpha = .18f))
        ) {
            Column(Modifier.padding(18.dp)) {
                Text("ورد اليوم", color = GoalNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(
                    "لتحقيق ${store.quranTargetKhatmas()} ختمة/ختمات هذا الشهر: الهدف اليومي تقريبًا $dailyTarget جزء.",
                    color = GoalMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    OutlinedButton(
                        onClick = {
                            store.setQuranTodayJuz(date, (today - 1).coerceAtLeast(0))
                            onChanged()
                        },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(44.dp)
                    ) {
                        Icon(Icons.Rounded.Remove, null)
                    }
                    Spacer(Modifier.width(20.dp))
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("$today", color = color, fontSize = 42.sp, fontWeight = FontWeight.Black)
                        Text("جزء اليوم", color = GoalMuted, fontSize = 11.sp)
                    }
                    Spacer(Modifier.width(20.dp))
                    Button(
                        onClick = {
                            store.setQuranTodayJuz(date, today + 1)
                            store.markGoalActivity(GoalKind.QURAN)
                            onChanged()
                        },
                        shape = CircleShape,
                        contentPadding = PaddingValues(0.dp),
                        modifier = Modifier.size(44.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = color)
                    ) {
                        Icon(Icons.Rounded.Add, null)
                    }
                }
                Spacer(Modifier.height(14.dp))
                val dayRatio = (today.toFloat() / dailyTarget).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { dayRatio },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                    color = color,
                    trackColor = color.copy(alpha = .10f)
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    if (today >= dailyTarget) "مهمة اليوم مكتملة +20 XP" else "باقي ${(dailyTarget - today).coerceAtLeast(0)} جزء",
                    color = if (today >= dailyTarget) GoalGreen else GoalMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
    item {
        QuranMonthPlan(store, month, color)
    }
}

@Composable
private fun QuranMonthPlan(store: GoalJourneyStore, month: YearMonth, color: Color) {
    val total = store.quranTargetKhatmas() * 30
    val completed = store.quranMonthJuz(month)
    Card(shape = RoundedCornerShape(28.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Column(Modifier.padding(17.dp)) {
            Text("بلان الشهر", color = GoalNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(12.dp))
            listOf(25, 50, 75, 100).forEach { pct ->
                val target = (total * pct) / 100
                val done = completed >= target
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(if (done) color else color.copy(alpha = .10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(17.dp))
                        else Text("$pct", color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
                    }
                    Spacer(Modifier.width(10.dp))
                    Text(
                        when (pct) {
                            25 -> "ربع الرحلة — $target جزء"
                            50 -> "نصف الرحلة — $target جزء"
                            75 -> "ثلاثة أرباع الهدف — $target جزء"
                            else -> "الختمة/الختمات كاملة — $target جزء"
                        },
                        modifier = Modifier.weight(1f),
                        color = GoalNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(if (done) "+50 XP" else "مقفلة", color = if (done) GoalTeal else GoalMuted, fontSize = 10.sp)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.turkishItems(
    store: GoalJourneyStore,
    color: Color,
    refresh: Int,
    onChanged: () -> Unit
) {
    val overview = store.turkishOverview()
    val currentStage = (overview.stageIndex + 1).coerceIn(1, 4)

    item {
        Text("خطة A1 — 4 مراحل", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }

    for (stage in 1..4) {
        item {
            val missions = turkishMissions.filter { it.stage == stage }
            val previousComplete = stage == 1 || turkishMissions.filter { it.stage < stage }.all { store.isTurkishMissionDone(it.id) }
            val stageDone = missions.all { store.isTurkishMissionDone(it.id) }
            val title = when (stage) {
                1 -> "الأساس والنطق"
                2 -> "الحياة اليومية"
                3 -> "التواصل"
                else -> "إتقان A1"
            }

            Card(
                shape = RoundedCornerShape(27.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (previousComplete) Color.White else Color(0xFFF0F2F7)
                ),
                border = BorderStroke(1.dp, if (stage == currentStage) color.copy(alpha = .35f) else GoalBorder)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(42.dp)
                                .background(
                                    when {
                                        stageDone -> color
                                        previousComplete -> color.copy(alpha = .12f)
                                        else -> Color(0xFFE1E5EC)
                                    },
                                    CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            when {
                                stageDone -> Icon(Icons.Rounded.Check, null, tint = Color.White)
                                previousComplete -> Text("$stage", color = color, fontWeight = FontWeight.Black)
                                else -> Icon(Icons.Rounded.Lock, null, tint = GoalMuted, modifier = Modifier.size(18.dp))
                            }
                        }
                        Spacer(Modifier.width(11.dp))
                        Column(Modifier.weight(1f)) {
                            Text("المرحلة $stage", color = GoalMuted, fontSize = 10.sp)
                            Text(title, color = GoalNavy, fontWeight = FontWeight.Black)
                        }
                        Text(
                            "${missions.count { store.isTurkishMissionDone(it.id) }}/${missions.size}",
                            color = color,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (previousComplete) {
                        Spacer(Modifier.height(12.dp))
                        missions.forEach { mission ->
                            val done = store.isTurkishMissionDone(mission.id)
                            MissionRow(
                                title = mission.title,
                                tag = mission.category,
                                done = done,
                                color = color,
                                onToggle = {
                                    store.setTurkishMissionDone(mission.id, !done)
                                    if (!done) store.markGoalActivity(GoalKind.TURKISH)
                                    onChanged()
                                }
                            )
                        }
                    } else {
                        Spacer(Modifier.height(10.dp))
                        Text("كمّلي المرحلة السابقة لفتح هذه المرحلة.", color = GoalMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun MissionRow(title: String, tag: String, done: Boolean, color: Color, onToggle: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onToggle)
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(26.dp)
                .background(if (done) color else Color.White, CircleShape)
                .border(1.5.dp, if (done) color else GoalBorder, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
        }
        Spacer(Modifier.width(9.dp))
        Text(title, modifier = Modifier.weight(1f), color = GoalNavy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
        Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .09f)) {
            Text(tag, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp), color = color, fontSize = 9.sp)
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.fitnessItems(
    store: GoalJourneyStore,
    month: YearMonth,
    color: Color,
    refresh: Int,
    onChanged: () -> Unit
) {
    item {
        Text("برنامج الأسبوع", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
    }
    item {
        var selectedDay by remember { mutableStateOf(LocalDate.now()) }
        Column {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                (0..6).forEach { offset ->
                    val date = LocalDate.now().minusDays(LocalDate.now().dayOfWeek.value.toLong() - 1L).plusDays(offset.toLong())
                    val selected = selectedDay == date
                    val plan = store.workoutFor(date.dayOfWeek)
                    val doneCount = plan.exercises.count { store.isExerciseDone(date, it.id) }
                    Surface(
                        modifier = Modifier.clickable { selectedDay = date },
                        shape = RoundedCornerShape(18.dp),
                        color = if (selected) color else Color.White,
                        border = if (selected) null else BorderStroke(1.dp, GoalBorder)
                    ) {
                        Column(
                            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ar")), color = if (selected) Color.White else GoalMuted, fontSize = 9.sp)
                            Text("${date.dayOfMonth}", color = if (selected) Color.White else GoalNavy, fontWeight = FontWeight.Black)
                            Text("$doneCount/${plan.exercises.size}", color = if (selected) Color.White.copy(alpha=.75f) else color, fontSize = 8.sp)
                        }
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            FitnessDayCard(store, selectedDay, color, onChanged)
        }
    }
    item {
        Card(shape = RoundedCornerShape(26.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
            Column(Modifier.padding(16.dp)) {
                Text("قاعدة التقدم", color = GoalNavy, fontWeight = FontWeight.Black)
                Text("كل تمرين مكتمل = 20 XP. التقدم الشهري محسوب من مجموع تمارين كل الأيام، مو بس الحضور.", color = GoalMuted, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun FitnessDayCard(store: GoalJourneyStore, date: LocalDate, color: Color, onChanged: () -> Unit) {
    val plan = store.workoutFor(date.dayOfWeek)
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = .18f))
    ) {
        Column(Modifier.padding(17.dp)) {
            Text(plan.title, color = GoalNavy, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text(date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ar")), color = GoalMuted, fontSize = 11.sp)
            Spacer(Modifier.height(12.dp))
            plan.exercises.forEach { exercise ->
                val done = store.isExerciseDone(date, exercise.id)
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            store.setExerciseDone(date, exercise.id, !done)
                            if (!done) store.markGoalActivity(GoalKind.FITNESS, date)
                            onChanged()
                        }
                        .padding(vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .background(if (done) color else Color.White, CircleShape)
                            .border(1.5.dp, if (done) color else GoalBorder, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(17.dp))
                    }
                    Spacer(Modifier.width(10.dp))
                    Column(Modifier.weight(1f)) {
                        Text(exercise.name, color = GoalNavy, fontWeight = FontWeight.Bold)
                        Text("${exercise.sets} Sets × ${exercise.reps}", color = GoalMuted, fontSize = 11.sp)
                    }
                    Text(if (done) "+20 XP" else "ابدئي", color = if (done) GoalTeal else color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.LazyListScope.workItems(
    store: GoalJourneyStore,
    color: Color,
    refresh: Int,
    onChanged: () -> Unit
) {
    item {
        Text("بلان المشروع", color = GoalNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        Spacer(Modifier.height(4.dp))
        Text(store.workGoalTitle(), color = GoalMuted, fontSize = 12.sp)
    }

    for (stage in 1..4) {
        item {
            val items = workCheckpoints.filter { it.stage == stage }
            val previousComplete = stage == 1 || workCheckpoints.filter { it.stage < stage }.all { store.isWorkDone(it.id) }
            val title = when (stage) {
                1 -> "التخطيط"
                2 -> "التصميم"
                3 -> "التنفيذ"
                else -> "الاختبار والتسليم"
            }

            Card(
                shape = RoundedCornerShape(27.dp),
                colors = CardDefaults.cardColors(containerColor = if (previousComplete) Color.White else Color(0xFFF0F2F7)),
                border = BorderStroke(1.dp, GoalBorder)
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(if (previousComplete) color.copy(alpha=.12f) else Color(0xFFE1E5EC), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (previousComplete) Text("$stage", color = color, fontWeight = FontWeight.Black)
                            else Icon(Icons.Rounded.Lock, null, tint = GoalMuted, modifier = Modifier.size(17.dp))
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(Modifier.weight(1f)) {
                            Text("المرحلة $stage", color = GoalMuted, fontSize = 9.sp)
                            Text(title, color = GoalNavy, fontWeight = FontWeight.Black)
                        }
                        Text("${items.count { store.isWorkDone(it.id) }}/${items.size}", color = color, fontWeight = FontWeight.Black)
                    }

                    if (previousComplete) {
                        Spacer(Modifier.height(10.dp))
                        items.forEach { checkpoint ->
                            val done = store.isWorkDone(checkpoint.id)
                            MissionRow(
                                title = checkpoint.title,
                                tag = if (done) "+20 XP" else "Checkpoint",
                                done = done,
                                color = color,
                                onToggle = {
                                    store.setWorkDone(checkpoint.id, !done)
                                    if (!done) store.markGoalActivity(GoalKind.WORK)
                                    onChanged()
                                }
                            )
                        }
                    } else {
                        Spacer(Modifier.height(9.dp))
                        Text("المرحلة مقفلة لحد ما تخلصي السابقة.", color = GoalMuted, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun QuranTargetDialog(current: Int, onDismiss: () -> Unit, onSave: (Int) -> Unit) {
    var value by remember { mutableIntStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("هدف القرآن للشهر", color = GoalNavy, fontWeight = FontWeight.Black) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("كم ختمة بدك تنجزي خلال الشهر؟ التطبيق بيحسب الورد اليومي والمراحل تلقائيًا.", color = GoalMuted, fontSize = 12.sp)
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(onClick = { value = (value - 1).coerceAtLeast(1) }, shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(42.dp)) {
                        Icon(Icons.Rounded.Remove, null)
                    }
                    Text("$value", modifier = Modifier.padding(horizontal = 24.dp), color = GoalGreen, fontSize = 34.sp, fontWeight = FontWeight.Black)
                    Button(onClick = { value = (value + 1).coerceAtMost(10) }, shape = CircleShape, contentPadding = PaddingValues(0.dp), modifier = Modifier.size(42.dp), colors = ButtonDefaults.buttonColors(containerColor = GoalGreen)) {
                        Icon(Icons.Rounded.Add, null)
                    }
                }
                Text("ختمة", color = GoalMuted, fontSize = 11.sp)
            }
        },
        confirmButton = { Button(onClick = { onSave(value) }, colors = ButtonDefaults.buttonColors(containerColor = GoalGreen)) { Text("اعتماد الخطة") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}

@Composable
private fun WorkTitleDialog(current: String, onDismiss: () -> Unit, onSave: (String) -> Unit) {
    var text by remember { mutableStateOf(current) }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("هدف المشروع", color = GoalNavy, fontWeight = FontWeight.Black) },
        text = {
            androidx.compose.material3.OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("مثال: إطلاق النسخة الأولى من التطبيق") },
                maxLines = 3
            )
        },
        confirmButton = { Button(onClick = { onSave(text) }, enabled = text.isNotBlank(), colors = ButtonDefaults.buttonColors(containerColor = GoalPurple)) { Text("حفظ الهدف") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
