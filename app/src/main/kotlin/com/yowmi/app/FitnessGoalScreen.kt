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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AutoStories
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.FitnessCenter
import androidx.compose.material.icons.rounded.Flag
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Restaurant
import androidx.compose.material.icons.rounded.RestartAlt
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.Stars
import androidx.compose.material.icons.rounded.Warning
import androidx.compose.material.icons.rounded.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val FitnessNavy = YowmiPalette.Text
private val FitnessOrange = YowmiPalette.PeachOrange
private val FitnessTeal = YowmiPalette.Secondary
private val FitnessPink = YowmiPalette.Accent
private val FitnessBlue = YowmiPalette.GrammarBlue
private val FitnessGreen = YowmiPalette.MintGreen
private val FitnessPurple = YowmiPalette.OrchidPurple
private val FitnessAmber = YowmiPalette.WarmYellow
private val FitnessBg = YowmiPalette.Canvas
private val FitnessMuted = YowmiPalette.SecondaryText
private val FitnessBorder = YowmiPalette.Border
private val FitnessDanger = YowmiPalette.SoftRed

private data class FitnessGoalSwitch(
    val id: String,
    val label: String,
    val icon: ImageVector,
    val color: Color
)

private val fitnessGoalSwitches = listOf(
    FitnessGoalSwitch("quran", "القرآن", Icons.Rounded.AutoStories, FitnessGreen),
    FitnessGoalSwitch("turkish", "التركي", Icons.Rounded.AutoStories, FitnessBlue),
    FitnessGoalSwitch("workout", "الرياضة", Icons.Rounded.FitnessCenter, FitnessOrange),
    FitnessGoalSwitch("work", "الشغل", Icons.Rounded.Work, FitnessPurple)
)

@Composable
internal fun FitnessGoalScreen(
    modifier: Modifier,
    onSelectGoal: (String) -> Unit
) {
    val context = LocalContext.current
    val store = remember { FitnessJourneyStore(context) }
    var refresh by remember { mutableIntStateOf(0) }
    var selectedWeek by remember { mutableIntStateOf(store.weekFor()) }
    var selectedDate by remember {
        mutableStateOf(
            if (LocalDate.now().isBefore(store.startDate())) store.startDate()
            else LocalDate.now()
        )
    }

    val progress = remember(refresh) { store.progress() }
    val selectedPhase = store.phaseFor(selectedWeek)
    val selectedWeekStart = store.weekStart(selectedWeek)
    val plan = store.planFor(selectedDate.dayOfWeek)
    val dateFormatter = DateTimeFormatter.ofPattern("d MMM", Locale("ar"))

    androidx.compose.foundation.lazy.LazyColumn(
        modifier = modifier.fillMaxSize().background(FitnessBg),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 18.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            FitnessHero(store = store, progress = progress)
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                fitnessGoalSwitches.forEach { item ->
                    Surface(
                        modifier = Modifier.clickable { onSelectGoal(item.id) },
                        shape = RoundedCornerShape(18.dp),
                        color = if (item.id == "workout") item.color else Color.White,
                        border = if (item.id == "workout") null else BorderStroke(1.dp, FitnessBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 9.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                item.icon,
                                null,
                                tint = if (item.id == "workout") Color.White else item.color,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(Modifier.width(6.dp))
                            Text(
                                item.label,
                                color = if (item.id == "workout") Color.White else FitnessNavy,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        item {
            ProgramSummaryCard(store.startDate())
        }

        item {
            Text("مراحل الـ 8 أسابيع", color = FitnessNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        item {
            PhaseRoadmap(
                currentWeek = progress.week,
                selectedWeek = selectedWeek,
                onWeekSelected = {
                    selectedWeek = it
                    selectedDate = store.weekStart(it)
                }
            )
        }

        item {
            PhaseDetailCard(
                week = selectedWeek,
                phase = selectedPhase,
                current = selectedWeek == progress.week
            )
        }

        item {
            Text("أيام الأسبوع §selectedWeek", color = FitnessNavy, fontSize = 20.sp, fontWeight = FontWeight.Black)
        }

        item {
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(7.dp)
            ) {
                repeat(7) { offset ->
                    val date = selectedWeekStart.plusDays(offset.toLong())
                    DayChip(
                        date = date,
                        selected = date == selectedDate,
                        progress = store.dayProgress(date),
                        type = store.planFor(date.dayOfWeek).type,
                        onClick = { selectedDate = date }
                    )
                }
            }
        }

        item {
            WorkoutDayHeader(
                date = selectedDate,
                plan = plan,
                dateLabel = selectedDate.format(dateFormatter)
            )
        }

        when (plan.type) {
            FitnessDayType.REST -> {
                item { RestDayCard() }
            }
            else -> {
                if (plan.warmup.isNotBlank()) {
                    item {
                        InfoCard(
                            title = "الإحماء",
                            text = plan.warmup,
                            color = FitnessAmber
                        )
                    }
                }

                items(
                    count = plan.exercises.size,
                    key = { index -> "${selectedDate}_${plan.exercises[index].id}" }
                ) { index ->
                    val exercise = plan.exercises[index]
                    ExerciseCard(
                        exercise = exercise,
                        week = selectedWeek,
                        done = store.isDone(selectedDate, exercise.id),
                        recommendedSets = store.recommendedSets(exercise, selectedWeek),
                        progressionNote = store.progressionNote(exercise, selectedWeek),
                        onToggle = {
                            store.setDone(
                                selectedDate,
                                exercise.id,
                                !store.isDone(selectedDate, exercise.id)
                            )
                            refresh++
                        }
                    )
                }

                if (plan.cooldown.isNotBlank()) {
                    item {
                        InfoCard(
                            title = "بعد التمرين",
                            text = plan.cooldown,
                            color = FitnessTeal
                        )
                    }
                }
            }
        }

        item {
            ProgressionRulesCard()
        }

        item {
            SafetyCard()
        }

        item {
            OutlinedButton(
                onClick = {
                    store.restartFromNextSaturday()
                    selectedWeek = 1
                    selectedDate = store.startDate()
                    refresh++
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                border = BorderStroke(1.dp, FitnessBorder)
            ) {
                Icon(Icons.Rounded.RestartAlt, null, tint = FitnessMuted)
                Spacer(Modifier.width(6.dp))
                Text("إعادة البرنامج من السبت القادم", color = FitnessMuted)
            }
        }
    }
}

@Composable
private fun FitnessHero(
    store: FitnessJourneyStore,
    progress: FitnessProgramProgress
) {
    val phase = store.phaseFor(progress.week)
    val start = store.startDate()
    val end = start.plusDays(55)
    val format = DateTimeFormatter.ofPattern("d MMM", Locale("ar"))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .background(YowmiPalette.FitnessHero, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Column(Modifier.padding(20.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .background(FitnessOrange, RoundedCornerShape(19.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Rounded.FitnessCenter, null, tint = Color.White, modifier = Modifier.size(30.dp))
                }

                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text("رحلة الجسم الأقوى ✦", color = FitnessNavy, fontSize = 23.sp, fontWeight = FontWeight.Black)
                    Text("برنامج منزلي • 8 أسابيع", color = FitnessMuted, fontSize = 12.sp)
                }

                Surface(shape = RoundedCornerShape(17.dp), color = FitnessNavy.copy(alpha = .12f)) {
                    Column(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("WEEK", color = FitnessMuted, fontSize = 8.sp)
                        Text("${progress.week}/8", color = FitnessNavy, fontSize = 19.sp, fontWeight = FontWeight.Black)
                    }
                }
            }

            Spacer(Modifier.height(18.dp))
            LinearProgressIndicator(
                progress = { progress.ratio },
                modifier = Modifier.fillMaxWidth().height(10.dp).clip(CircleShape),
                color = FitnessTeal,
                trackColor = YowmiPalette.Border
            )
            Spacer(Modifier.height(7.dp))
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(
                    "${progress.completed} / ${progress.target} تمرين أساسي",
                    color = FitnessMuted,
                    fontSize = 11.sp
                )
                Text(
                    "${(progress.ratio * 100).toInt()}%",
                    color = FitnessNavy,
                    fontWeight = FontWeight.Black,
                    fontSize = 12.sp
                )
            }

            Spacer(Modifier.height(14.dp))
            Row(
                modifier = Modifier.horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FitnessHeroStat("${progress.xp} XP", FitnessPink)
                FitnessHeroStat("${progress.streak} حصة متتالية", FitnessTeal)
                FitnessHeroStat(phase.title, FitnessOrange)
            }

            Spacer(Modifier.height(14.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                color = FitnessNavy.copy(alpha = .10f)
            ) {
                Column(Modifier.padding(13.dp)) {
                    Text("مدة البرنامج", color = FitnessMuted, fontSize = 10.sp)
                    Text(
                        "${start.format(format)} → ${end.format(format)}",
                        color = FitnessNavy,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun FitnessHeroStat(text: String, color: Color) {
    Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .20f)) {
        Text(
            text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            color = FitnessNavy,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun ProgramSummaryCard(startDate: LocalDate) {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, FitnessBorder)
    ) {
        Column(Modifier.padding(17.dp)) {
            Text("البرنامج المعتمد", color = FitnessNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(11.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                ProgramMetric(Modifier.weight(1f), "المدة", "8 أسابيع", FitnessOrange)
                ProgramMetric(Modifier.weight(1f), "الحصة", "25–45 د", FitnessBlue)
                ProgramMetric(Modifier.weight(1f), "المكان", "بالبيت", FitnessTeal)
            }
            Spacer(Modifier.height(9.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(17.dp),
                color = FitnessPurple.copy(alpha = .07f)
            ) {
                Column(Modifier.padding(12.dp)) {
                    Text("المعدات", color = FitnessPurple, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    Text(
                        "سجادة + كرسي ثابت • حقيبة ظهر أو مطاط مقاومة اختياريين.",
                        color = FitnessNavy,
                        fontSize = 11.sp
                    )
                }
            }
        }
    }
}

@Composable
private fun ProgramMetric(modifier: Modifier, label: String, value: String, color: Color) {
    Surface(modifier = modifier, shape = RoundedCornerShape(17.dp), color = color.copy(alpha = .08f)) {
        Column(
            modifier = Modifier.padding(vertical = 11.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(value, color = color, fontWeight = FontWeight.Black, fontSize = 14.sp)
            Text(label, color = FitnessMuted, fontSize = 9.sp)
        }
    }
}

@Composable
private fun PhaseRoadmap(
    currentWeek: Int,
    selectedWeek: Int,
    onWeekSelected: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(9.dp)) {
        fitnessPhases.forEachIndexed { phaseIndex, phase ->
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedWeek in phase.weeks) FitnessOrange.copy(alpha = .07f) else Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    if (selectedWeek in phase.weeks) FitnessOrange.copy(alpha = .30f) else FitnessBorder
                )
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(13.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .background(
                                if (currentWeek > phase.weeks.last) FitnessGreen
                                else if (currentWeek in phase.weeks) FitnessOrange
                                else YowmiPalette.LilacWash,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        when {
                            currentWeek > phase.weeks.last ->
                                Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(19.dp))
                            currentWeek in phase.weeks ->
                                Icon(Icons.Rounded.Stars, null, tint = Color.White, modifier = Modifier.size(19.dp))
                            else ->
                                Text("${phaseIndex + 1}", color = FitnessMuted, fontWeight = FontWeight.Black)
                        }
                    }

                    Spacer(Modifier.width(11.dp))
                    Column(Modifier.weight(1f)) {
                        Text(
                            "الأسبوع ${phase.weeks.first}–${phase.weeks.last}",
                            color = FitnessMuted,
                            fontSize = 9.sp
                        )
                        Text(phase.title, color = FitnessNavy, fontWeight = FontWeight.Black)
                        Text(phase.subtitle, color = FitnessMuted, fontSize = 10.sp)
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                        phase.weeks.forEach { week ->
                            Surface(
                                modifier = Modifier.clickable { onWeekSelected(week) },
                                shape = CircleShape,
                                color = if (selectedWeek == week) FitnessOrange else YowmiPalette.LilacWash
                            ) {
                                Text(
                                    "$week",
                                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 7.dp),
                                    color = if (selectedWeek == week) Color.White else FitnessNavy,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Black
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun PhaseDetailCard(week: Int, phase: FitnessPhase, current: Boolean) {
    Card(
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(containerColor = FitnessOrange)
    ) {
        Column(Modifier.padding(17.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(Modifier.weight(1f)) {
                    Text("الأسبوع $week", color = Color.White.copy(alpha = .70f), fontSize = 10.sp)
                    Text(phase.title, color = Color.White, fontSize = 19.sp, fontWeight = FontWeight.Black)
                }
                if (current) {
                    Surface(shape = RoundedCornerShape(50), color = Color.White.copy(alpha = .18f)) {
                        Text(
                            "مرحلتك الحالية",
                            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp),
                            color = Color.White,
                            fontSize = 9.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(Modifier.height(9.dp))
            Text(phase.rule, color = Color.White.copy(alpha = .88f), fontSize = 12.sp)
        }
    }
}

@Composable
private fun DayChip(
    date: LocalDate,
    selected: Boolean,
    progress: Pair<Int, Int>,
    type: FitnessDayType,
    onClick: () -> Unit
) {
    val (done, target) = progress
    val completed = target > 0 && done == target
    val typeColor = when (type) {
        FitnessDayType.STRENGTH -> FitnessOrange
        FitnessDayType.OPTIONAL -> FitnessTeal
        FitnessDayType.REST -> FitnessPurple
    }

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(19.dp),
        color = if (selected) typeColor else Color.White,
        border = if (selected) null else BorderStroke(1.dp, FitnessBorder)
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 9.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("ar")),
                color = if (selected) Color.White else FitnessMuted,
                fontSize = 9.sp
            )
            Text(
                "${date.dayOfMonth}",
                color = if (selected) Color.White else FitnessNavy,
                fontWeight = FontWeight.Black
            )
            Text(
                when {
                    type == FitnessDayType.REST -> "راحة"
                    type == FitnessDayType.OPTIONAL -> "اختياري"
                    completed -> "✓"
                    else -> "$done/$target"
                },
                color = if (selected) Color.White.copy(alpha = .78f) else typeColor,
                fontSize = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun WorkoutDayHeader(
    date: LocalDate,
    plan: FitnessDayPlan,
    dateLabel: String
) {
    val color = when (plan.type) {
        FitnessDayType.STRENGTH -> FitnessOrange
        FitnessDayType.OPTIONAL -> FitnessTeal
        FitnessDayType.REST -> FitnessPurple
    }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = BorderStroke(1.dp, color.copy(alpha = .20f))
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(17.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(color.copy(alpha = .12f), RoundedCornerShape(17.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    if (plan.type == FitnessDayType.REST) Icons.Rounded.Home else Icons.Rounded.FitnessCenter,
                    null,
                    tint = color
                )
            }

            Spacer(Modifier.width(11.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    date.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("ar")) + " • " + dateLabel,
                    color = FitnessMuted,
                    fontSize = 10.sp
                )
                Text(plan.title, color = FitnessNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
                Text(plan.subtitle, color = color, fontSize = 10.sp, fontWeight = FontWeight.Bold)
            }

            Surface(shape = RoundedCornerShape(50), color = color.copy(alpha = .09f)) {
                Text(
                    plan.duration,
                    modifier = Modifier.padding(horizontal = 9.dp, vertical = 6.dp),
                    color = color,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun ExerciseCard(
    exercise: FitnessExercise,
    week: Int,
    done: Boolean,
    recommendedSets: Int,
    progressionNote: String,
    onToggle: () -> Unit
) {
    var expanded by remember(exercise.id, week) { mutableStateOf(false) }
    val color = if (exercise.required) FitnessOrange else FitnessTeal

    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (done) FitnessGreen.copy(alpha = .06f) else Color.White
        ),
        border = BorderStroke(
            1.dp,
            if (done) FitnessGreen.copy(alpha = .30f) else color.copy(alpha = .18f)
        )
    ) {
        Column(Modifier.padding(15.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(if (done) FitnessGreen else Color.White, CircleShape)
                        .border(1.5.dp, if (done) FitnessGreen else FitnessBorder, CircleShape)
                        .clickable(onClick = onToggle),
                    contentAlignment = Alignment.Center
                ) {
                    if (done) Icon(Icons.Rounded.Check, null, tint = Color.White, modifier = Modifier.size(18.dp))
                }

                Spacer(Modifier.width(10.dp))
                Column(Modifier.weight(1f)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(exercise.name, color = FitnessNavy, fontWeight = FontWeight.Black)
                        if (!exercise.required) {
                            Spacer(Modifier.width(6.dp))
                            Surface(shape = RoundedCornerShape(50), color = FitnessTeal.copy(alpha = .10f)) {
                                Text(
                                    "اختياري",
                                    modifier = Modifier.padding(horizontal = 7.dp, vertical = 3.dp),
                                    color = FitnessTeal,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                    Text(exercise.arabicName, color = FitnessMuted, fontSize = 11.sp)
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        "$recommendedSets × ${exercise.reps}",
                        color = color,
                        fontWeight = FontWeight.Black,
                        fontSize = 12.sp
                    )
                    if (done && exercise.required) {
                        Text("+20 XP", color = FitnessTeal, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(Modifier.height(9.dp))
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                color = color.copy(alpha = .06f)
            ) {
                Text(
                    progressionNote,
                    modifier = Modifier.padding(10.dp),
                    color = FitnessNavy,
                    fontSize = 10.sp
                )
            }

            Spacer(Modifier.height(8.dp))
            Text(
                if (expanded) "إخفاء طريقة الأداء" else "طريقة الأداء + الملاحظات",
                modifier = Modifier.clickable { expanded = !expanded },
                color = color,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )

            if (expanded) {
                Spacer(Modifier.height(10.dp))
                exercise.instructions.forEachIndexed { index, instruction ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 3.dp),
                        verticalAlignment = Alignment.Top
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .background(color.copy(alpha = .10f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("${index + 1}", color = color, fontSize = 9.sp, fontWeight = FontWeight.Black)
                        }
                        Spacer(Modifier.width(7.dp))
                        Text(
                            instruction,
                            modifier = Modifier.weight(1f),
                            color = FitnessNavy,
                            fontSize = 11.sp
                        )
                    }
                }

                if (exercise.caution.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Rounded.Warning, null, tint = FitnessDanger, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(exercise.caution, color = FitnessDanger, fontSize = 10.sp)
                    }
                }

                if (exercise.alternative.isNotBlank()) {
                    Spacer(Modifier.height(7.dp))
                    Row(verticalAlignment = Alignment.Top) {
                        Icon(Icons.Rounded.Info, null, tint = FitnessBlue, modifier = Modifier.size(17.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(exercise.alternative, color = FitnessBlue, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun RestDayCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = FitnessPurple.copy(alpha = .07f)),
        border = BorderStroke(1.dp, FitnessPurple.copy(alpha = .18f))
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(22.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Rounded.Stars, null, tint = FitnessPurple, modifier = Modifier.size(34.dp))
            Spacer(Modifier.height(9.dp))
            Text("راحة كاملة", color = FitnessNavy, fontSize = 19.sp, fontWeight = FontWeight.Black)
            Text(
                "التعافي جزء من البرنامج. ما في حاجة لتمرين قوي اليوم.",
                color = FitnessMuted,
                fontSize = 11.sp
            )
        }
    }
}

@Composable
private fun InfoCard(title: String, text: String, color: Color) {
    Card(
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = color.copy(alpha = .07f)),
        border = BorderStroke(1.dp, color.copy(alpha = .16f))
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(title, color = color, fontWeight = FontWeight.Black, fontSize = 12.sp)
            Spacer(Modifier.height(4.dp))
            Text(text, color = FitnessNavy, fontSize = 11.sp)
        }
    }
}

@Composable
private fun ProgressionRulesCard() {
    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(Modifier.padding(17.dp)) {
            Text("قواعد التدرّج", color = FitnessNavy, fontSize = 18.sp, fontWeight = FontWeight.Black)
            Spacer(Modifier.height(10.dp))
            fitnessPhases.forEach { phase ->
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .background(FitnessOrange.copy(alpha = .10f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "${phase.weeks.first}–${phase.weeks.last}",
                            color = FitnessOrange,
                            fontSize = 8.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.width(9.dp))
                    Column(Modifier.weight(1f)) {
                        Text(phase.title, color = FitnessNavy, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text(phase.rule, color = FitnessMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun SafetyCard() {
    Card(
        shape = RoundedCornerShape(25.dp),
        colors = CardDefaults.cardColors(containerColor = FitnessDanger.copy(alpha = .06f)),
        border = BorderStroke(1.dp, FitnessDanger.copy(alpha = .18f))
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Rounded.Warning, null, tint = FitnessDanger)
                Spacer(Modifier.width(8.dp))
                Text("متى توقفي التمرين؟", color = FitnessNavy, fontWeight = FontWeight.Black)
            }
            Spacer(Modifier.height(7.dp))
            Text(
                "إذا صار ألم حاد، دوخة، ضغط غير طبيعي بالحوض أو تسريب بول، أوقفي الحركة. وإذا في حمل، ولادة حديثة أو إصابة لازم يتعدّل البرنامج قبل التطبيق.",
                color = FitnessMuted,
                fontSize = 11.sp
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "الهدف الأساسي: القوة وامتلاء عضلات الأرداف وتحسين شكل الجسم، مو خسارة الوزن. الخفسة والسيلوليت أو دهون أسفل البطن ما في ضمان تختفي بالكامل.",
                color = FitnessNavy,
                fontSize = 10.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
