package com.yowmi.app

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
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TurkishNavy = Color(0xFF172B4D)
private val TurkishBlue = Color(0xFF3478F6)
private val TurkishTeal = Color(0xFF00A9A5)
private val TurkishPink = Color(0xFFEC4B99)
private val TurkishGreen = Color(0xFF00B88A)
private val TurkishPurple = Color(0xFF8B5CF6)
private val TurkishOrange = Color(0xFFFF7A00)
private val TurkishBg = Color(0xFFF5F7FC)
private val TurkishMuted = Color(0xFF6E7A90)
private val TurkishBorder = Color(0xFFE5EAF2)

private data class GoalSwitchItem(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private val goalSwitchItems = listOf(
    GoalSwitchItem("quran", "القرآن", Icons.Rounded.AutoStories, TurkishGreen),
    GoalSwitchItem("turkish", "التركي", Icons.Rounded.AutoStories, TurkishBlue),
    GoalSwitchItem("workout", "الرياضة", Icons.Rounded.FitnessCenter, TurkishOrange),
    GoalSwitchItem("work", "الشغل", Icons.Rounded.Work, TurkishPurple)
)

@Composable
internal fun ContinuousTurkishGoalScreen(
    modifier: Modifier,
    onSelectGoal: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { TurkishJourneyStore(context) }
    var refresh by remember { mutableIntStateOf(0) }
    var selectedLevelId by remember { mutableStateOf(store.currentLevel().id) }
    var showSprintDialog by remember { mutableStateOf(false) }

    val month = YearMonth.now()
    val sprint = remember(refresh, month) { store.sprint(month) }
    val currentLevel = remember(refresh) { store.currentLevel() }
    val currentLesson = remember(refresh) { store.currentLesson() }
    val selectedLevel = turkishLevels.firstOrNull { it.id == selectedLevelId } ?: currentLevel
    val monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy", Locale("ar"))

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxSize().background(TurkishBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TurkishHero(
                completed = store.completedLessonCount(),
                xp = store.xp(),
                streak = store.currentStreak(),
                currentLevel = currentLevel,
                currentLesson = currentLesson
            )
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                goalSwitchItems.forEach { item ->
                    Surface(
                        modifier = Modifier.clickable { onSelectGoal(item.id) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (item.id == "turkish") item.color else Color.White,
                        border = if (item.id == "turkish") null else BorderStroke(1.dp, TurkishBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                item.icon,
                                null,
                                tint = if (item.id == "turkish") Color.White else item.color,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                item.label,
                                color = if (item.id == "turkish") Color.White else TurkishNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            SprintCard(
                monthLabel = month.atDay(1).format(monthFormatter),
                sprint = sprint,
                onEdit = { showSprintDialog = true }
            )
        }

        item {
            Text("المسار الكامل", color = TurkishNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(
                "التقدم ما بيتصفّر بنهاية الشهر؛ الشهر فقط Sprint للتنظيم، والمسار يكمل تلقائيًا للشهر اللي بعده.",
                color = TurkishMuted,
                fontSize = 12.sp
            )
        }

        item {
            LevelJourneyMap(
                store = store,
                selectedLevelId = selectedLevel.id,
                onSelectLevel = { level ->
                    if (store.isLevelUnlocked(level)) selectedLevelId = level.id
                }
            )
        }

        item {
            SelectedLevelHeader(
                store = store,
                level = selectedLevel,
                isCurrent = selectedLevel.id == currentLevel.id
            )
        }

        for (block in 1..3) {
            val startLocal = (block - 1) * 6 + 1
            val endLocal = block * 6
            val blockLessons = selectedLevel.lessons.filter { it.localNumber in startLocal..endLocal }

            item {
                LessonBlockHeader(
                    block = block,
                    completed = blockLessons.count { store.isLessonComplete(it) },
                    total = blockLessons.size,
                    color = TurkishBlue
                )
            }

            items(blockLessons.size) { index ->
                val lesson = blockLessons[index]
                LessonProgressCard(
                    lesson = lesson,
                    unlocked = store.lessonUnlocked(lesson),
                    plannedThisMonth = store.isPlannedThisMonth(lesson.id, month),
                    sectionsDone = store.completedSections(lesson),
                    sectionState = { sectionIndex -> store.isSectionDone(lesson.id, sectionIndex) },
                    onToggleSection = { sectionIndex, done ->
                        store.setSectionDone(lesson.id, sectionIndex, done)
                        refresh++
                    },
                    onCompleteLesson = {
                        store.markLessonComplete(lesson.id, true)
                        refresh++
                    }
                )
            }

            item {
                ReviewCheckpointCard(
                    level = selectedLevel,
                    block = block,
                    unlocked = blockLessons.all { store.isLessonComplete(it) },
                    isDone = store.reviewBlockDone(selectedLevel.id, block),
                    itemDone = { idx -> store.reviewDone(selectedLevel.id, block, idx) },
                    onToggleItem = { idx, done ->
                        store.setReviewDone(selectedLevel.id, block, idx, done)
                        refresh++
                    }
                )
            }
        }

        item {
            CourseForecastCard(sprint = sprint)
        }
    }

    if (showSprintDialog) {
        SprintTargetDialog(
            current = store.monthlyTarget(month),
            onDismiss = { showSprintDialog = false },
            onSave = { value ->
                store.setMonthlyTarget(month, value)
                showSprintDialog = false
                refresh++
            }
        )
    }
}

@Composable
private fun TurkishHero(
    completed: Int,
    xp: Int,
    streak: Int,
    currentLevel: TurkishLevelPlan,
    currentLesson: TurkishLesson?
) {
    val overallRatio = completed.toFloat() / allTurkishLessons.size
    Card(
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = TurkishNavy)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(TurkishBlue, RoundedCornerShape(19.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.AutoStories, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("Türkçe Yolculuğu", color = Color.White, fontSize = 25.sp, fontWeight = FontWeight.Black)
                    Text("A1 → A2 → B1 → B2", color = Color.White.copy(alpha = .68f), fontSize = 12.sp)
                }
                Surface(shape = RoundedCornerShape(17.dp), color = Color.White.copy(alpha = .12f)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("المستوى", color = Color.White.copy(alpha = .62f), fontSize = 8.sp)
                        Text(currentLevel.id, color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            LinearProgressIndicator(
                progress = { overallRatio },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = TurkishTeal,
                trackColor = Color.White.copy(alpha = .15f)
            )
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("$completed / ${allTurkishLessons.size} درس", color = Color.White.copy(alpha = .76f), fontSize = 11.sp)
                Text("${(overallRatio * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }

            Spacer(Modifier.height(15.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                HeroStat("$xp XP", TurkishPink)
                HeroStat("$streak يوم", TurkishTeal)
                HeroStat("${turkishLessonSections.size} فقرات/درس", TurkishOrange)
            }

            currentLesson?.let {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = .10f)
                ) {
                    Column(Modifier.padding(13.dp)) {
                        Text("الدرس التالي", color = Color.White.copy(alpha = .60f), fontSize = 10.sp)
                        Text(
                            "${it.level} • الدرس ${it.localNumber} — ${it.title}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroStat(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .20f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = Color.White,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SprintCard(
    monthLabel: String,
    sprint: TurkishSprint,
    onEdit: () -> Unit
) {
    val lessonRatio = if (sprint.targetLessons == 0) 0f
        else sprint.completedThisMonth.toFloat() / sprint.targetLessons
    val sectionTarget = sprint.plannedLessonIds.size * turkishLessonSections.size
    val sectionRatio = if (sectionTarget == 0) 0f
        else sprint.completedSectionsThisMonth.toFloat() / sectionTarget

    Card(
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, TurkishBlue.copy(alpha = .18f))
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(TurkishBlue.copy(alpha = .12f), RoundedCornerShape(16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.Schedule, null, tint = TurkishBlue)
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text("Sprint الشهر", color = TurkishNavy, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    Text(monthLabel, color = TurkishMuted, fontSize = 11.sp)
                }
                OutlinedButton(
                    onClick = onEdit,
                    shape = RoundedCornerShape(14.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Icon(Icons.Rounded.Edit, null, modifier = Modifier.size(15.dp))
                    Spacer(Modifier.width(4.dp))
                    Text("تعديل", fontSize = 10.sp)
                }
            }

            Spacer(Modifier.height(16.dp))
            Text(
                "هدف الشهر: ${sprint.targetLessons} درس",
                color = TurkishNavy,
                fontWeight = FontWeight.Black,
                fontSize = 16.sp
            )
            Text(
                "يعادل تقريبًا ${sprint.targetLessons * turkishLessonSections.size} فقرة تعليمية • ${sprint.weeklyPace} دروس بالأسبوع.",
                color = TurkishMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(13.dp))
            LinearProgressIndicator(
                progress = { lessonRatio.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                color = TurkishBlue,
                trackColor = TurkishBlue.copy(alpha = .10f)
            )
            Spacer(Modifier.height(6.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("الدروس: ${sprint.completedThisMonth}/${sprint.targetLessons}", color = TurkishBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("${(lessonRatio * 100).toInt().coerceAtMost(100)}%", color = TurkishNavy, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { sectionRatio.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = TurkishPink,
                trackColor = TurkishPink.copy(alpha = .10f)
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "الفقرات: ${sprint.completedSectionsThisMonth}/$sectionTarget",
                color = TurkishPink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            if (sprint.plannedLessonIds.size < sprint.targetLessons) {
                Spacer(Modifier.height(10.dp))
                Text(
                    "باقي المسار أقل من هدف الشهر، لذلك الخطة الحالية فيها ${sprint.plannedLessonIds.size} درس فقط.",
                    color = TurkishGreen,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun LevelJourneyMap(
    store: TurkishJourneyStore,
    selectedLevelId: String,
    onSelectLevel: (TurkishLevelPlan) -> Unit
) {
    Row(
        modifier = Modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(9.dp)
    ) {
        turkishLevels.forEach { level ->
            val completed = store.levelCompletedLessons(level)
            val unlocked = store.isLevelUnlocked(level)
            val selected = level.id == selectedLevelId
            val complete = completed == level.lessons.size && store.reviewCheckpointsDone(level.id) == 3
            val color = when (level.id) {
                "A1" -> TurkishBlue
                "A2" -> TurkishTeal
                "B1" -> TurkishPurple
                else -> TurkishPink
            }

            Surface(
                modifier = Modifier
                    .width(145.dp)
                    .clickable(enabled = unlocked) { onSelectLevel(level) },
                shape = RoundedCornerShape(24.dp),
                color = when {
                    selected -> color
                    !unlocked -> Color(0xFFEEF1F6)
                    else -> Color.White
                },
                border = if (selected) null else BorderStroke(1.dp, TurkishBorder)
            ) {
                Column(Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            level.id,
                            color = if (selected) Color.White else if (unlocked) color else TurkishMuted,
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Black
                        )
                        Spacer(Modifier.weight(1f))
                        when {
                            complete -> Icon(Icons.Rounded.Check, null, tint = if (selected) Color.White else TurkishGreen, modifier = Modifier.size(18.dp))
                            !unlocked -> Icon(Icons.Rounded.Lock, null, tint = TurkishMuted, modifier = Modifier.size(17.dp))
                        }
                    }
                    Spacer(Modifier.height(9.dp))
                    Text(
                        "$completed/18 درس",
                        color = if (selected) Color.White.copy(alpha = .85f) else TurkishMuted,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(7.dp))
                    LinearProgressIndicator(
                        progress = { completed / 18f },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = if (selected) Color.White else color,
                        trackColor = if (selected) Color.White.copy(alpha = .20f) else color.copy(alpha = .10f)
                    )
                    Spacer(Modifier.height(6.dp))
                    Text(
                        "${store.reviewCheckpointsDone(level.id)}/3 مراجعات",
                        color = if (selected) Color.White.copy(alpha = .70f) else TurkishMuted,
                        fontSize = 9.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun SelectedLevelHeader(
    store: TurkishJourneyStore,
    level: TurkishLevelPlan,
    isCurrent: Boolean
) {
    val completed = store.levelCompletedLessons(level)
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(level.title, color = TurkishNavy, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text(level.subtitle, color = TurkishMuted, fontSize = 11.sp)
                }
                if (isCurrent) {
                    Surface(shape = RoundedCornerShape(50), color = TurkishBlue.copy(alpha = .10f)) {
                        Text(
                            "المستوى الحالي",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            color = TurkishBlue,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { completed / 18f },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = TurkishBlue,
                trackColor = TurkishBlue.copy(alpha = .10f)
            )
            Spacer(Modifier.height(6.dp))
            Text(
                "$completed من 18 درس • ${store.reviewCheckpointsDone(level.id)} من 3 جلسات مراجعة",
                color = TurkishMuted,
                fontSize = 10.sp
            )
        }
    }
}

@Composable
private fun LessonBlockHeader(block: Int, completed: Int, total: Int, color: Color) {
    val labels = listOf("المرحلة الأولى", "المرحلة الثانية", "المرحلة الثالثة")
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .background(color.copy(alpha = .12f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text("$block", color = color, fontWeight = FontWeight.Black)
        }
        Spacer(Modifier.width(9.dp))
        Column(Modifier.weight(1f)) {
            Text(labels[block - 1], color = TurkishNavy, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text("6 دروس ثم جلسة مراجعة", color = TurkishMuted, fontSize = 10.sp)
        }
        Text("$completed/$total", color = color, fontWeight = FontWeight.Black)
    }
}

@Composable
private fun LessonProgressCard(
    lesson: TurkishLesson,
    unlocked: Boolean,
    plannedThisMonth: Boolean,
    sectionsDone: Int,
    sectionState: (Int) -> Boolean,
    onToggleSection: (Int, Boolean) -> Unit,
    onCompleteLesson: () -> Unit
) {
    val complete = sectionsDone == turkishLessonSections.size
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = when {
                !unlocked -> Color(0xFFF0F2F7)
                complete -> TurkishGreen.copy(alpha = .06f)
                else -> Color.White
            }
        ),
        border = BorderStroke(
            1.dp,
            when {
                complete -> TurkishGreen.copy(alpha = .30f)
                plannedThisMonth -> TurkishBlue.copy(alpha = .30f)
                else -> TurkishBorder
            }
        )
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            when {
                                complete -> TurkishGreen
                                unlocked -> TurkishBlue.copy(alpha = .12f)
                                else -> Color(0xFFE0E5ED)
                            },
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        complete -> Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        !unlocked -> Icon(Icons.Rounded.Lock, null, tint = TurkishMuted, modifier = Modifier.size(18.dp))
                        else -> Text("${lesson.localNumber}", color = TurkishBlue, fontWeight = FontWeight.Black)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${lesson.level} • الدرس ${lesson.localNumber}",
                            color = TurkishMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (plannedThisMonth) {
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(50), color = TurkishPink.copy(alpha = .10f)) {
                                Text(
                                    "ضمن خطة الشهر",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    color = TurkishPink,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(
                        lesson.title,
                        color = if (unlocked) TurkishNavy else TurkishMuted,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        "$sectionsDone/${turkishLessonSections.size} فقرات",
                        color = if (complete) TurkishGreen else TurkishBlue,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (unlocked && !complete) {
                Spacer(Modifier.height(12.dp))
                turkishLessonSections.forEachIndexed { index, section ->
                    val done = sectionState(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleSection(index, !done) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if (done) TurkishBlue else Color.White, CircleShape)
                                .border(1.5.dp, if (done) TurkishBlue else TurkishBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(section, modifier = Modifier.weight(1f), color = TurkishNavy, fontSize = 11.sp)
                        Text(if (done) "+5 XP" else "", color = TurkishTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
                if (sectionsDone >= turkishLessonSections.size - 1) {
                    Spacer(Modifier.height(8.dp))
                    Button(
                        onClick = onCompleteLesson,
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(containerColor = TurkishBlue),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Icon(Icons.Rounded.Check, null)
                        Spacer(Modifier.width(6.dp))
                        Text("إنهاء الدرس كاملًا")
                    }
                }
            } else if (!unlocked) {
                Spacer(Modifier.height(9.dp))
                Text(
                    "أكملي الدرس أو المراجعة السابقة لفتح هذا الدرس.",
                    color = TurkishMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun ReviewCheckpointCard(
    level: TurkishLevelPlan,
    block: Int,
    unlocked: Boolean,
    isDone: Boolean,
    itemDone: (Int) -> Boolean,
    onToggleItem: (Int, Boolean) -> Unit
) {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) TurkishPurple.copy(alpha = .06f) else Color.White
        ),
        border = BorderStroke(1.dp, if (unlocked) TurkishPurple.copy(alpha = .28f) else TurkishBorder)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(
                            if (unlocked) TurkishPurple.copy(alpha = .12f) else Color(0xFFE0E5ED),
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    if (isDone) Icon(Icons.Rounded.Check, null, tint = TurkishPurple)
                    else if (unlocked) Icon(Icons.Rounded.Stars, null, tint = TurkishPurple)
                    else Icon(Icons.Rounded.Lock, null, tint = TurkishMuted)
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("Checkpoint $block", color = TurkishPurple, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("جلسة مراجعة بعد 6 دروس", color = TurkishNavy, fontWeight = FontWeight.Black)
                }
                Text(if (isDone) "مكتملة" else "${(0..2).count { itemDone(it) }}/3", color = TurkishPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            if (unlocked) {
                Spacer(Modifier.height(10.dp))
                turkishReviewItems.forEachIndexed { index, title ->
                    val done = itemDone(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onToggleItem(index, !done) }
                            .padding(vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .background(if (done) TurkishPurple else Color.White, CircleShape)
                                .border(1.5.dp, if (done) TurkishPurple else TurkishBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(title, modifier = Modifier.weight(1f), color = TurkishNavy, fontSize = 11.sp)
                        Text(if (done) "+10 XP" else "", color = TurkishTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(Modifier.height(8.dp))
                Text(
                    "تفتح بعد إنهاء دروس ${(block - 1) * 6 + 1}–${block * 6} من ${level.id}.",
                    color = TurkishMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CourseForecastCard(sprint: TurkishSprint) {
    Card(
        shape = RoundedCornerShape(27.dp),
        colors = CardDefaults.cardColors(containerColor = TurkishNavy)
    ) {
        Column(Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Flag, null, tint = TurkishPink)
                Spacer(Modifier.width(8.dp))
                Text("توقع الوصول", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(9.dp))
            if (sprint.remainingCourseLessons == 0) {
                Text("المسار الكامل A1 → B2 مكتمل.", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    "باقي ${sprint.remainingCourseLessons} درس. بسرعة ${sprint.targetLessons} درس بالشهر، تحتاجي تقريبًا ${sprint.estimatedMonthsRemaining} شهر/أشهر لإكمال المسار.",
                    color = Color.White.copy(alpha = .82f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "إذا ما خلصتي Sprint هذا الشهر، الدروس غير المكتملة بتدخل تلقائيًا بخطة الشهر التالي قبل الدروس الجديدة.",
                    color = TurkishTeal,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun SprintTargetDialog(
    current: Int,
    onDismiss: () -> Unit,
    onSave: (Int) -> Unit
) {
    var value by remember { mutableIntStateOf(current) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("خطة إنجاز الشهر", color = TurkishNavy, fontWeight = FontWeight.Black) },
        text = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "اختاري عدد الدروس اللي بدك تنجزيها هذا الشهر. التقدم العام ما بيتصفّر، وبيكمل للشهر التالي.",
                    color = TurkishMuted,
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(18.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedButton(
                        onClick = { value = (value - 1).coerceAtLeast(4) },
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) { Icon(Icons.Rounded.Remove, null) }
                    Text(
                        "$value",
                        modifier = Modifier.padding(horizontal = 24.dp),
                        color = TurkishBlue,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Black
                    )
                    Button(
                        onClick = { value = (value + 1).coerceAtMost(36) },
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TurkishBlue)
                    ) { Icon(Icons.Rounded.Add, null) }
                }
                Text("درس بالشهر", color = TurkishMuted, fontSize = 11.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "≈ ${kotlin.math.ceil(value / 4.0).toInt()} دروس بالأسبوع • ${value * turkishLessonSections.size} فقرة",
                    color = TurkishPink,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(value) },
                colors = ButtonDefaults.buttonColors(containerColor = TurkishBlue)
            ) { Text("اعتماد الخطة") }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("إلغاء") } }
    )
}
