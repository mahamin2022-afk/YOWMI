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
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.util.Locale

private val TurkishNavy = Color(0xFF344C49)
private val TurkishBlue = Color(0xFF6E8EC5)
private val TurkishTeal = Color(0xFF4F9587)
private val TurkishPink = Color(0xFFBE829B)
private val TurkishGreen = Color(0xFF4E9A80)
private val TurkishPurple = Color(0xFF827BAE)
private val TurkishOrange = Color(0xFFC88458)
private val TurkishBg = Color(0xFFF7F8F6)
private val TurkishMuted = Color(0xFF76847F)
private val TurkishBorder = Color(0xFFE4EBE7)

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
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            TurkishHero(
                completedLessons = store.completedLessonCount(),
                completedVocabulary = store.totalCompletedVocabulary(),
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
            CurriculumSummaryCard()
        }

        item {
            SprintCard(
                monthLabel = month.atDay(1).format(monthFormatter),
                sprint = sprint,
                currentLevel = currentLevel,
                monthsForCurrentLevel = store.estimatedMonthsForLevel(currentLevel, month),
                onEdit = { showSprintDialog = true }
            )
        }

        item {
            Text("المسار الكامل", color = TurkishNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
            Text(
                "الإنجاز محفوظ دائمًا. الشهر مجرد خطة تنفيذ؛ إذا ما خلصتيها، الدروس المتبقية بتسبق الدروس الجديدة بالشهر التالي.",
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

        val ranges = lessonBlocks(selectedLevel)
        ranges.forEachIndexed { blockIndex, range ->
            val blockLessons = selectedLevel.lessons.filter { it.localNumber in range }

            item {
                LessonBlockHeader(
                    block = blockIndex + 1,
                    range = range,
                    completed = blockLessons.count { store.isLessonComplete(it) },
                    total = blockLessons.size,
                    color = levelColor(selectedLevel.id)
                )
            }

            items(
                count = blockLessons.size,
                key = { index -> blockLessons[index].id }
            ) { index ->
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
                    }
                )
            }

            item {
                ReviewCheckpointCard(
                    level = selectedLevel,
                    block = blockIndex + 1,
                    afterLesson = selectedLevel.reviewAfterLessonNumbers[blockIndex],
                    unlocked = store.reviewUnlocked(selectedLevel, blockIndex + 1),
                    isDone = store.reviewBlockDone(selectedLevel.id, blockIndex + 1),
                    itemDone = { idx -> store.reviewDone(selectedLevel.id, blockIndex + 1, idx) },
                    onToggleItem = { idx, done ->
                        store.setReviewDone(selectedLevel.id, blockIndex + 1, idx, done)
                        refresh++
                    }
                )
            }
        }

        item {
            FinalExamCard(
                level = selectedLevel,
                unlocked = store.examUnlocked(selectedLevel),
                completed = store.levelExamDone(selectedLevel.id),
                partsDone = store.examPartsDone(selectedLevel.id),
                partDone = { idx -> store.examPartDone(selectedLevel.id, idx) },
                onTogglePart = { idx, done ->
                    store.setExamPartDone(selectedLevel.id, idx, done)
                    refresh++
                    if (store.levelExamDone(selectedLevel.id)) {
                        val currentIndex = turkishLevels.indexOfFirst { it.id == selectedLevel.id }
                        turkishLevels.getOrNull(currentIndex + 1)?.let { selectedLevelId = it.id }
                    }
                }
            )
        }

        item {
            CourseForecastCard(
                store = store,
                sprint = sprint,
                currentLevel = currentLevel
            )
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
    completedLessons: Int,
    completedVocabulary: Int,
    xp: Int,
    streak: Int,
    currentLevel: TurkishLevelPlan,
    currentLesson: TurkishLesson?
) {
    val overallRatio = completedLessons.toFloat() / allTurkishLessons.size

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
                    Text("A1 → A2 → B1 • منهاج واحد متدرج", color = Color.White.copy(alpha = .68f), fontSize = 12.sp)
                }
                Surface(shape = RoundedCornerShape(17.dp), color = Color.White.copy(alpha = .12f)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("الحالي", color = Color.White.copy(alpha = .62f), fontSize = 8.sp)
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
                Text("$completedLessons / 58 درس/مهارة", color = Color.White.copy(alpha = .76f), fontSize = 11.sp)
                Text("${(overallRatio * 100).toInt()}%", color = Color.White, fontWeight = FontWeight.Black, fontSize = 12.sp)
            }

            Spacer(Modifier.height(15.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                HeroStat("$xp XP", TurkishPink)
                HeroStat("$streak يوم متتالي", TurkishTeal)
                HeroStat("$completedVocabulary / 2700 كلمة", TurkishOrange)
                HeroStat("10 مراجعات", TurkishPurple)
                HeroStat("3 اختبارات", TurkishGreen)
            }

            currentLesson?.let {
                Spacer(Modifier.height(16.dp))
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White.copy(alpha = .10f)
                ) {
                    Column(Modifier.padding(13.dp)) {
                        Text("المهمة التعليمية التالية", color = Color.White.copy(alpha = .60f), fontSize = 10.sp)
                        Text(
                            "${it.level} • ${it.localNumber}. ${it.title}",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(it.arabicTitle, color = Color.White.copy(alpha = .72f), fontSize = 11.sp)
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
private fun CurriculumSummaryCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, TurkishBorder)
    ) {
        Column(Modifier.padding(17.dp)) {
            Text("بنية المنهاج المعتمدة", color = TurkishNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(11.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetric(Modifier.weight(1f), "A1", "22", TurkishBlue)
                SummaryMetric(Modifier.weight(1f), "A2", "18", TurkishTeal)
                SummaryMetric(Modifier.weight(1f), "B1", "18", TurkishPurple)
            }
            Spacer(Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                SummaryMetric(Modifier.weight(1f), "المفردات", "2700", TurkishOrange)
                SummaryMetric(Modifier.weight(1f), "المراجعات", "10", TurkishPink)
                SummaryMetric(Modifier.weight(1f), "الاختبارات", "3", TurkishGreen)
            }
        }
    }
}

@Composable
private fun SummaryMetric(modifier: Modifier, title: String, value: String, color: Color) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = color.copy(alpha = .08f)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Text(title, color = TurkishMuted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun SprintCard(
    monthLabel: String,
    sprint: TurkishSprint,
    currentLevel: TurkishLevelPlan,
    monthsForCurrentLevel: Int,
    onEdit: () -> Unit
) {
    val lessonTarget = sprint.plannedLessonIds.size
    val lessonRatio = if (lessonTarget == 0) 0f else sprint.completedThisMonth.toFloat() / lessonTarget
    val sectionTarget = lessonTarget * 6
    val sectionRatio = if (sectionTarget == 0) 0f else sprint.completedSectionsThisMonth.toFloat() / sectionTarget

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
                    Text("خطة إنجاز الشهر", color = TurkishNavy, fontSize = 19.sp, fontWeight = FontWeight.Black)
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

            Spacer(Modifier.height(15.dp))
            Text(
                "هدف الشهر: ${sprint.targetLessons} درس/مهارة",
                color = TurkishNavy,
                fontSize = 16.sp,
                fontWeight = FontWeight.Black
            )
            Text(
                "≈ ${sprint.weeklyPace} دروس بالأسبوع • حتى ${sprint.targetLessons * 6} فقرة إنجاز.",
                color = TurkishMuted,
                fontSize = 11.sp
            )

            Spacer(Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { lessonRatio.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(9.dp).clip(CircleShape),
                color = TurkishBlue,
                trackColor = TurkishBlue.copy(alpha = .10f)
            )
            Spacer(Modifier.height(5.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("الدروس ${sprint.completedThisMonth}/$lessonTarget", color = TurkishBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Text("${(lessonRatio * 100).toInt().coerceAtMost(100)}%", color = TurkishNavy, fontSize = 11.sp, fontWeight = FontWeight.Black)
            }

            Spacer(Modifier.height(11.dp))
            LinearProgressIndicator(
                progress = { sectionRatio.coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(7.dp).clip(CircleShape),
                color = TurkishPink,
                trackColor = TurkishPink.copy(alpha = .10f)
            )
            Spacer(Modifier.height(5.dp))
            Text(
                "الفقرات ${sprint.completedSectionsThisMonth}/$sectionTarget",
                color = TurkishPink,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(12.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                color = TurkishBlue.copy(alpha = .06f)
            ) {
                Text(
                    if (monthsForCurrentLevel <= 1)
                        "بهذا الإيقاع ممكن تكملي دروس ${currentLevel.id} ضمن Sprint واحد تقريبًا، مع المراجعات والاختبار."
                    else
                        "دروس ${currentLevel.id} تحتاج تقريبًا $monthsForCurrentLevel Sprint شهري بهذا الإيقاع، والتقدم بيستمر بدون تصفير.",
                    modifier = Modifier.padding(12.dp),
                    color = TurkishNavy,
                    fontSize = 11.sp
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
            val complete = store.isLevelComplete(level)
            val color = levelColor(level.id)

            Surface(
                modifier = Modifier
                    .width(170.dp)
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
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "$completed/${level.lessons.size} درس/مهارة",
                        color = if (selected) Color.White.copy(alpha = .88f) else TurkishMuted,
                        fontSize = 10.sp
                    )
                    Spacer(Modifier.height(6.dp))
                    LinearProgressIndicator(
                        progress = { completed.toFloat() / level.lessons.size },
                        modifier = Modifier.fillMaxWidth().height(6.dp).clip(CircleShape),
                        color = if (selected) Color.White else color,
                        trackColor = if (selected) Color.White.copy(alpha = .20f) else color.copy(alpha = .10f)
                    )
                    Spacer(Modifier.height(7.dp))
                    Text(
                        "900 مفردة • ${level.reviewAfterLessonNumbers.size} مراجعات",
                        color = if (selected) Color.White.copy(alpha = .70f) else TurkishMuted,
                        fontSize = 9.sp
                    )
                    Text(
                        if (store.levelExamDone(level.id)) "الاختبار مكتمل" else "اختبار نهاية المستوى",
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
    val color = levelColor(level.id)

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = .15f))
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Column(Modifier.weight(1f)) {
                    Text(level.title, color = TurkishNavy, fontSize = 21.sp, fontWeight = FontWeight.Black)
                    Text(level.subtitle, color = TurkishMuted, fontSize = 11.sp)
                }
                if (isCurrent) {
                    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .10f)) {
                        Text(
                            "المستوى الحالي",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            color = color,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Spacer(Modifier.height(13.dp))
            LinearProgressIndicator(
                progress = { store.currentLevelRatio().takeIf { isCurrent } ?: (completed.toFloat() / level.lessons.size) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(CircleShape),
                color = color,
                trackColor = color.copy(alpha = .10f)
            )
            Spacer(Modifier.height(8.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                LevelStat("$completed/${level.lessons.size} دروس", color)
                LevelStat("${store.completedVocabulary(level)}/900 كلمة", TurkishOrange)
                LevelStat("${store.reviewCheckpointsDone(level.id)}/${level.reviewAfterLessonNumbers.size} مراجعات", TurkishPink)
            }
            Spacer(Modifier.height(7.dp))
            LevelStat(
                "الاختبار ${store.examPartsDone(level.id)}/${turkishExamParts.size}",
                if (store.levelExamDone(level.id)) TurkishGreen else TurkishPurple
            )
        }
    }
}

@Composable
private fun LevelStat(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .09f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 5.dp),
            color = color,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun LessonBlockHeader(
    block: Int,
    range: IntRange,
    completed: Int,
    total: Int,
    color: Color
) {
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
            Text("مرحلة الدروس ${range.first}–${range.last}", color = TurkishNavy, fontWeight = FontWeight.Black, fontSize = 16.sp)
            Text("بعدها مراجعة مرحلية إلزامية", color = TurkishMuted, fontSize = 10.sp)
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
    onToggleSection: (Int, Boolean) -> Unit
) {
    val complete = sectionsDone == 6
    val color = levelColor(lesson.level)
    val labels = lessonSectionLabels(lesson)

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
                plannedThisMonth -> TurkishPink.copy(alpha = .35f)
                else -> TurkishBorder
            }
        )
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            when {
                                complete -> TurkishGreen
                                unlocked -> color.copy(alpha = .12f)
                                else -> Color(0xFFE0E5ED)
                            },
                            RoundedCornerShape(14.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        complete -> Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(20.dp))
                        !unlocked -> Icon(Icons.Rounded.Lock, null, tint = TurkishMuted, modifier = Modifier.size(18.dp))
                        else -> Text("${lesson.localNumber}", color = color, fontWeight = FontWeight.Black)
                    }
                }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "${lesson.level} • الدرس/المهارة ${lesson.localNumber}",
                            color = TurkishMuted,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                        if (plannedThisMonth) {
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(50), color = TurkishPink.copy(alpha = .10f)) {
                                Text(
                                    "خطة الشهر",
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
                    Text(lesson.arabicTitle, color = TurkishMuted, fontSize = 11.sp)
                    if (lesson.focus.isNotBlank()) {
                        Spacer(Modifier.height(3.dp))
                        Text(lesson.focus, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                    Spacer(Modifier.height(4.dp))
                    Text(
                        "$sectionsDone/6 فقرات • ${lesson.vocabularyTarget} مفردة",
                        color = if (complete) TurkishGreen else color,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            if (unlocked && !complete) {
                Spacer(Modifier.height(12.dp))
                labels.forEachIndexed { index, section ->
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
                                .background(if (done) color else Color.White, CircleShape)
                                .border(1.5.dp, if (done) color else TurkishBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        Spacer(Modifier.width(8.dp))
                        Text(section, modifier = Modifier.weight(1f), color = TurkishNavy, fontSize = 11.sp)
                        Text(if (done) "+5 XP" else "", color = TurkishTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else if (!unlocked) {
                Spacer(Modifier.height(9.dp))
                Text(
                    "مقفلة لحد ما تكملي الدرس أو المراجعة السابقة.",
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
    afterLesson: Int,
    unlocked: Boolean,
    isDone: Boolean,
    itemDone: (Int) -> Boolean,
    onToggleItem: (Int, Boolean) -> Unit
) {
    val color = TurkishPink

    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isDone) color.copy(alpha = .06f) else Color.White
        ),
        border = BorderStroke(1.dp, if (unlocked) color.copy(alpha = .30f) else TurkishBorder)
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .background(if (unlocked) color.copy(alpha = .12f) else Color(0xFFE0E5ED), RoundedCornerShape(14.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        isDone -> Icon(Icons.Rounded.Check, null, tint = color)
                        unlocked -> Icon(Icons.Rounded.Stars, null, tint = color)
                        else -> Icon(Icons.Rounded.Lock, null, tint = TurkishMuted)
                    }
                }
                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Text("المراجعة $block", color = color, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    Text("مراجعة بعد الدرس $afterLesson من ${level.id}", color = TurkishNavy, fontWeight = FontWeight.Black)
                }
                Text(
                    if (isDone) "مكتملة" else "${turkishReviewItems.indices.count { itemDone(it) }}/${turkishReviewItems.size}",
                    color = color,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold
                )
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
                                .background(if (done) color else Color.White, CircleShape)
                                .border(1.5.dp, if (done) color else TurkishBorder, CircleShape),
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
                    "تفتح بعد إنهاء كل الدروس حتى رقم $afterLesson.",
                    color = TurkishMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun FinalExamCard(
    level: TurkishLevelPlan,
    unlocked: Boolean,
    completed: Boolean,
    partsDone: Int,
    partDone: (Int) -> Boolean,
    onTogglePart: (Int, Boolean) -> Unit
) {
    val color = TurkishPurple

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (completed) TurkishGreen.copy(alpha = .07f) else Color.White
        ),
        border = BorderStroke(1.5.dp, if (unlocked) color.copy(alpha = .35f) else TurkishBorder)
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            if (completed) TurkishGreen.copy(alpha = .12f)
                            else if (unlocked) color.copy(alpha = .12f)
                            else Color(0xFFE0E5ED),
                            RoundedCornerShape(16.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        completed -> Icon(Icons.Rounded.Check, null, tint = TurkishGreen)
                        unlocked -> Icon(Icons.Rounded.Flag, null, tint = color)
                        else -> Icon(Icons.Rounded.Lock, null, tint = TurkishMuted)
                    }
                }
                Spacer(Modifier.width(11.dp))
                Column(Modifier.weight(1f)) {
                    Text(level.examTitle, color = TurkishNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
                    Text(
                        if (completed) "المستوى مكتمل — المستوى التالي صار متاح."
                        else "4 أجزاء لتأكيد إغلاق المستوى.",
                        color = TurkishMuted,
                        fontSize = 10.sp
                    )
                }
                Text("$partsDone/4", color = if (completed) TurkishGreen else color, fontWeight = FontWeight.Black)
            }

            if (unlocked) {
                Spacer(Modifier.height(12.dp))
                turkishExamParts.forEachIndexed { index, part ->
                    val done = partDone(index)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onTogglePart(index, !done) }
                            .padding(vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(26.dp)
                                .background(if (done) color else Color.White, CircleShape)
                                .border(1.5.dp, if (done) color else TurkishBorder, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(15.dp))
                        }
                        Spacer(Modifier.width(9.dp))
                        Text(part, modifier = Modifier.weight(1f), color = TurkishNavy, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        Text(if (done) "+25 XP" else "", color = TurkishTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            } else {
                Spacer(Modifier.height(9.dp))
                Text(
                    "الاختبار يفتح بعد إنهاء كل دروس ومراجعات ${level.id}.",
                    color = TurkishMuted,
                    fontSize = 10.sp
                )
            }
        }
    }
}

@Composable
private fun CourseForecastCard(
    store: TurkishJourneyStore,
    sprint: TurkishSprint,
    currentLevel: TurkishLevelPlan
) {
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
            Spacer(Modifier.height(10.dp))

            if (sprint.remainingCourseLessons == 0 && turkishLevels.all { store.isLevelComplete(it) }) {
                Text("A1 + A2 + B1 مكتملين بالكامل.", color = Color.White, fontWeight = FontWeight.Bold)
            } else {
                Text(
                    "باقي ${sprint.remainingCourseLessons} درس/مهارة من أصل 58. بسرعة ${sprint.targetLessons} درس بالشهر، تحتاجي تقريبًا ${sprint.estimatedMonthsRemaining} شهر/أشهر للدروس، إضافة للمراجعات والاختبارات.",
                    color = Color.White.copy(alpha = .82f),
                    fontSize = 12.sp
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "المستوى الحالي: ${currentLevel.id} • باقي ${store.remainingLessonsInLevel(currentLevel)} درس/مهارة.",
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
                    "اختاري عدد الدروس/المهارات اللي بدك تنجزيها خلال الشهر. المسار نفسه مستمر وما بيتصفّر.",
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
                        onClick = { value = (value + 1).coerceAtMost(30) },
                        shape = CircleShape,
                        modifier = Modifier.size(44.dp),
                        contentPadding = PaddingValues(0.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TurkishBlue)
                    ) { Icon(Icons.Rounded.Add, null) }
                }

                Text("درس/مهارة بالشهر", color = TurkishMuted, fontSize = 11.sp)
                Spacer(Modifier.height(10.dp))
                Text(
                    "≈ ${kotlin.math.ceil(value / 4.0).toInt()} بالأسبوع • حتى ${value * 6} فقرة متابعة",
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
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("إلغاء") }
        }
    )
}

private fun lessonBlocks(level: TurkishLevelPlan): List<IntRange> {
    var start = 1
    return buildList {
        level.reviewAfterLessonNumbers.forEach { boundary ->
            add(start..boundary)
            start = boundary + 1
        }
    }
}

private fun levelColor(levelId: String): Color = when (levelId) {
    "A1" -> TurkishBlue
    "A2" -> TurkishTeal
    else -> TurkishPurple
}
