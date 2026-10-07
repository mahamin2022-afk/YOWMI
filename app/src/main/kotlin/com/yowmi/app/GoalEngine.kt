package com.yowmi.app

import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

internal enum class GoalKind { QURAN, TURKISH, FITNESS, WORK }

internal data class GoalStage(
    val id: String,
    val title: String,
    val subtitle: String,
    val target: Int
)

internal data class TurkishMission(
    val id: String,
    val stage: Int,
    val title: String,
    val category: String
)

internal data class ExerciseItem(
    val id: String,
    val name: String,
    val sets: Int,
    val reps: String
)

internal data class WorkoutDay(
    val day: DayOfWeek,
    val title: String,
    val exercises: List<ExerciseItem>
)

internal data class WorkCheckpoint(
    val id: String,
    val stage: Int,
    val title: String
)

internal data class GoalOverview(
    val completed: Int,
    val target: Int,
    val xp: Int,
    val level: Int,
    val streak: Int,
    val stageIndex: Int
) {
    val ratio: Float get() = if (target <= 0) 0f else (completed.toFloat() / target).coerceIn(0f, 1f)
}

internal val turkishMissions = listOf(
    TurkishMission("tr_01", 1, "الأحرف والنطق التركي", "نطق"),
    TurkishMission("tr_02", 1, "التحية والتعريف عن النفس", "محادثة"),
    TurkishMission("tr_03", 1, "الضمائر والجملة الاسمية", "قواعد"),
    TurkishMission("tr_04", 1, "الأرقام والوقت والتاريخ", "مفردات"),
    TurkishMission("tr_05", 1, "80 كلمة أساسية", "مفردات"),
    TurkishMission("tr_06", 2, "زمن الحاضر المستمر", "قواعد"),
    TurkishMission("tr_07", 2, "حالات الاسم الأساسية", "قواعد"),
    TurkishMission("tr_08", 2, "الملكية والعائلة", "قواعد"),
    TurkishMission("tr_09", 2, "البيت والروتين اليومي", "مفردات"),
    TurkishMission("tr_10", 2, "استماع يومي 15 دقيقة", "استماع"),
    TurkishMission("tr_11", 3, "صياغة السؤال والنفي", "قواعد"),
    TurkishMission("tr_12", 3, "الماضي البسيط للمبتدئ", "قواعد"),
    TurkishMission("tr_13", 3, "التسوق والاتجاهات", "محادثة"),
    TurkishMission("tr_14", 3, "قراءة نص A1 وفهمه", "قراءة"),
    TurkishMission("tr_15", 3, "محادثة 10 دقائق", "محادثة"),
    TurkishMission("tr_16", 4, "مراجعة قواعد A1", "مراجعة"),
    TurkishMission("tr_17", 4, "500 كلمة فعّالة", "مفردات"),
    TurkishMission("tr_18", 4, "3 محادثات كاملة", "محادثة"),
    TurkishMission("tr_19", 4, "نص قراءة بدون ترجمة فورية", "قراءة"),
    TurkishMission("tr_20", 4, "اختبار A1 تجريبي", "اختبار")
)

internal val fitnessPlan = listOf(
    WorkoutDay(
        DayOfWeek.SATURDAY, "Lower Body",
        listOf(
            ExerciseItem("fit_sat_1", "Squat", 3, "12"),
            ExerciseItem("fit_sat_2", "Glute Bridge", 3, "15"),
            ExerciseItem("fit_sat_3", "Reverse Lunge", 3, "10 لكل رجل"),
            ExerciseItem("fit_sat_4", "Plank", 3, "30 ثانية")
        )
    ),
    WorkoutDay(
        DayOfWeek.SUNDAY, "Upper + Core",
        listOf(
            ExerciseItem("fit_sun_1", "Wall Push-up", 3, "12"),
            ExerciseItem("fit_sun_2", "Bird Dog", 3, "10 لكل جهة"),
            ExerciseItem("fit_sun_3", "Dead Bug", 3, "10"),
            ExerciseItem("fit_sun_4", "Side Plank", 2, "20 ثانية")
        )
    ),
    WorkoutDay(
        DayOfWeek.MONDAY, "Recovery",
        listOf(
            ExerciseItem("fit_mon_1", "Walking", 1, "25 دقيقة"),
            ExerciseItem("fit_mon_2", "Full Body Stretch", 1, "15 دقيقة")
        )
    ),
    WorkoutDay(
        DayOfWeek.TUESDAY, "Lower Body",
        listOf(
            ExerciseItem("fit_tue_1", "Sumo Squat", 3, "12"),
            ExerciseItem("fit_tue_2", "Hip Thrust", 3, "15"),
            ExerciseItem("fit_tue_3", "Step Back Lunge", 3, "10 لكل رجل"),
            ExerciseItem("fit_tue_4", "Calf Raise", 3, "15")
        )
    ),
    WorkoutDay(
        DayOfWeek.WEDNESDAY, "Upper + Core",
        listOf(
            ExerciseItem("fit_wed_1", "Incline Push-up", 3, "10"),
            ExerciseItem("fit_wed_2", "Shoulder Tap", 3, "12"),
            ExerciseItem("fit_wed_3", "Dead Bug", 3, "12"),
            ExerciseItem("fit_wed_4", "Plank", 3, "35 ثانية")
        )
    ),
    WorkoutDay(
        DayOfWeek.THURSDAY, "Mobility",
        listOf(
            ExerciseItem("fit_thu_1", "Walking", 1, "30 دقيقة"),
            ExerciseItem("fit_thu_2", "Mobility Flow", 1, "15 دقيقة")
        )
    ),
    WorkoutDay(
        DayOfWeek.FRIDAY, "Light Full Body",
        listOf(
            ExerciseItem("fit_fri_1", "Bodyweight Squat", 2, "12"),
            ExerciseItem("fit_fri_2", "Glute Bridge", 2, "15"),
            ExerciseItem("fit_fri_3", "Wall Push-up", 2, "12"),
            ExerciseItem("fit_fri_4", "Stretch", 1, "10 دقائق")
        )
    )
)

internal val workCheckpoints = listOf(
    WorkCheckpoint("work_01", 1, "تحديد الهدف النهائي للمشروع"),
    WorkCheckpoint("work_02", 1, "تقسيم المشروع إلى مهام واضحة"),
    WorkCheckpoint("work_03", 1, "تحديد أولويات النسخة الحالية"),
    WorkCheckpoint("work_04", 2, "إنهاء تجربة المستخدم الأساسية"),
    WorkCheckpoint("work_05", 2, "إنهاء التصميم البصري"),
    WorkCheckpoint("work_06", 2, "مراجعة الشاشات والحالات"),
    WorkCheckpoint("work_07", 3, "برمجة الوظائف الأساسية"),
    WorkCheckpoint("work_08", 3, "ربط البيانات والحالات"),
    WorkCheckpoint("work_09", 3, "إغلاق المشاكل الرئيسية"),
    WorkCheckpoint("work_10", 4, "اختبار الاستخدام"),
    WorkCheckpoint("work_11", 4, "إصلاح الأخطاء"),
    WorkCheckpoint("work_12", 4, "تجهيز نسخة التسليم")
)

internal class GoalJourneyStore(context: Context) {
    private val prefs = context.getSharedPreferences("goal_journeys_v1", Context.MODE_PRIVATE)

    fun quranTargetKhatmas(): Int = prefs.getInt("quran_target_khatmas", 2).coerceAtLeast(1)
    fun setQuranTargetKhatmas(value: Int) = prefs.edit().putInt("quran_target_khatmas", value.coerceIn(1, 10)).apply()

    fun quranTodayJuz(date: LocalDate): Int = prefs.getInt("quran_juz_$date", 0)
    fun setQuranTodayJuz(date: LocalDate, value: Int) = prefs.edit().putInt("quran_juz_$date", value.coerceAtLeast(0)).apply()

    fun quranMonthJuz(month: YearMonth): Int =
        (1..month.lengthOfMonth()).sumOf { quranTodayJuz(month.atDay(it)) }

    fun quranDailyTarget(month: YearMonth): Int =
        ceil((quranTargetKhatmas() * 30.0) / month.lengthOfMonth()).toInt().coerceAtLeast(1)

    fun quranOverview(month: YearMonth): GoalOverview {
        val completed = quranMonthJuz(month)
        val target = quranTargetKhatmas() * 30
        return overview(completed, target, streakQuran())
    }

    fun turkishTargetLevel(): String = prefs.getString("turkish_target_level", "A1") ?: "A1"
    fun setTurkishTargetLevel(level: String) = prefs.edit().putString("turkish_target_level", level).apply()
    fun isTurkishMissionDone(id: String): Boolean = prefs.getBoolean("turkish_$id", false)
    fun setTurkishMissionDone(id: String, done: Boolean) = prefs.edit().putBoolean("turkish_$id", done).apply()
    fun turkishOverview(): GoalOverview {
        val done = turkishMissions.count { isTurkishMissionDone(it.id) }
        val target = turkishMissions.size
        return overview(done, target, streakBoolean("tr_day_"))
    }

    fun isExerciseDone(date: LocalDate, id: String): Boolean = prefs.getBoolean("fitness_${date}_$id", false)
    fun setExerciseDone(date: LocalDate, id: String, done: Boolean) = prefs.edit().putBoolean("fitness_${date}_$id", done).apply()
    fun workoutFor(day: DayOfWeek): WorkoutDay = fitnessPlan.first { it.day == day }
    fun fitnessOverview(month: YearMonth): GoalOverview {
        var completed = 0
        var target = 0
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            val plan = workoutFor(date.dayOfWeek)
            target += plan.exercises.size
            completed += plan.exercises.count { isExerciseDone(date, it.id) }
        }
        return overview(completed, target, streakFitness())
    }

    fun workGoalTitle(): String = prefs.getString("work_goal_title", "إنهاء النسخة الحالية من المشروع") ?: "إنهاء النسخة الحالية من المشروع"
    fun setWorkGoalTitle(value: String) = prefs.edit().putString("work_goal_title", value.ifBlank { "إنهاء النسخة الحالية من المشروع" }).apply()
    fun isWorkDone(id: String): Boolean = prefs.getBoolean("work_$id", false)
    fun setWorkDone(id: String, done: Boolean) = prefs.edit().putBoolean("work_$id", done).apply()
    fun workOverview(): GoalOverview {
        val done = workCheckpoints.count { isWorkDone(it.id) }
        return overview(done, workCheckpoints.size, streakBoolean("work_day_"))
    }

    fun markGoalActivity(kind: GoalKind, date: LocalDate = LocalDate.now()) {
        prefs.edit().putBoolean(activityKey(kind, date), true).apply()
    }

    fun isGoalActiveOn(kind: GoalKind, date: LocalDate): Boolean = prefs.getBoolean(activityKey(kind, date), false)

    fun overview(kind: GoalKind, month: YearMonth): GoalOverview = when (kind) {
        GoalKind.QURAN -> quranOverview(month)
        GoalKind.TURKISH -> turkishOverview()
        GoalKind.FITNESS -> fitnessOverview(month)
        GoalKind.WORK -> workOverview()
    }

    private fun overview(completed: Int, target: Int, streak: Int): GoalOverview {
        val ratio = if (target == 0) 0f else completed.toFloat() / target
        val xp = completed * 20
        val level = (xp / 100) + 1
        val stage = when {
            ratio >= 1f -> 4
            ratio >= .75f -> 3
            ratio >= .50f -> 2
            ratio >= .25f -> 1
            else -> 0
        }
        return GoalOverview(completed, target, xp, level, streak, stage)
    }

    private fun streakQuran(): Int {
        var streak = 0
        var date = LocalDate.now()
        repeat(366) {
            if (quranTodayJuz(date) > 0) {
                streak++
                date = date.minusDays(1)
            } else return streak
        }
        return streak
    }

    private fun streakFitness(): Int {
        var streak = 0
        var date = LocalDate.now()
        repeat(366) {
            val plan = workoutFor(date.dayOfWeek)
            val any = plan.exercises.any { isExerciseDone(date, it.id) }
            if (any) {
                streak++
                date = date.minusDays(1)
            } else return streak
        }
        return streak
    }

    private fun streakBoolean(prefix: String): Int {
        var streak = 0
        var date = LocalDate.now()
        repeat(366) {
            if (prefs.getBoolean("$prefix$date", false)) {
                streak++
                date = date.minusDays(1)
            } else return streak
        }
        return streak
    }

    private fun activityKey(kind: GoalKind, date: LocalDate) = "activity_${kind.name}_$date"
}
