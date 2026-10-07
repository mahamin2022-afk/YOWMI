package com.yowmi.app

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.YearMonth
import java.time.temporal.ChronoUnit

internal sealed class RepeatRule {
    data object Daily : RepeatRule()
    data object AlternateDays : RepeatRule()
    data class Weekly(val days: Set<DayOfWeek>) : RepeatRule()
}

internal data class RoutineTask(
    val id: String,
    val title: String,
    val subtitle: String,
    val defaultTime: LocalTime,
    val section: String,
    val repeatRule: RepeatRule = RepeatRule.Daily,
    val progressGroup: String? = null,
    val isCustom: Boolean = false
)

internal val defaultRoutineTasks = listOf(
    RoutineTask("wake", "الاستيقاظ", "كاسة مي + ترتيب سريع", LocalTime.of(9, 0), "الصباح"),
    RoutineTask("coffee", "قهوة وفطور خفيف", "قهوة + موزة + كاسة حليب + تمر", LocalTime.of(9, 15), "الصباح"),
    RoutineTask("workout", "رياضة", "جلسة الرياضة اليومية", LocalTime.of(9, 45), "الصباح", progressGroup = "workout"),
    RoutineTask("shower", "دوش وتجهيز", "بعد الرياضة", LocalTime.of(10, 35), "الصباح"),
    RoutineTask("quran", "قراءة القرآن", "وقت هادئ بعد الرياضة", LocalTime.of(11, 0), "الصباح", progressGroup = "quran"),
    RoutineTask("breakfast", "فطور مع الأهل", "مع الكولاجين اليومي", LocalTime.of(11, 40), "الظهر"),
    RoutineTask("cleaning", "شغل البيت", "يوم إيه ويوم لا", LocalTime.of(12, 20), "الظهر", RepeatRule.AlternateDays),
    RoutineTask("turkish1", "دراسة تركي — الجلسة الأولى", "ساعة وربع", LocalTime.of(13, 30), "الظهر", progressGroup = "turkish"),
    RoutineTask("work", "التطبيق أو الشغل", "شغل المشروع أو أي شغل مستلم", LocalTime.of(15, 15), "الظهر", progressGroup = "work"),
    RoutineTask("turkish2", "دراسة تركي — الجلسة الثانية", "ساعة وربع", LocalTime.of(17, 15), "المساء", progressGroup = "turkish"),
    RoutineTask("lunch", "الغدا مع الأهل", "وقت الغدا", LocalTime.of(19, 0), "المساء"),
    RoutineTask("husband", "وقت مع زوجي", "من 8 للمسا تقريبًا", LocalTime.of(20, 0), "المساء"),
    RoutineTask("care", "عناية مسائية", "تجهيز للنوم والعناية الشخصية", LocalTime.of(23, 0), "قبل النوم"),
    RoutineTask("scrub", "مقشر الجسم", "مرتين بالأسبوع مساءً", LocalTime.of(23, 10), "قبل النوم", RepeatRule.Weekly(setOf(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY))),
    RoutineTask("need_prayer", "صلاة قضاء الحاجة", "قبل النوم", LocalTime.of(23, 20), "قبل النوم"),
    RoutineTask("dhikr1", "100× يا حي يا قيوم برحمتك أستغيث", "ذكر قبل النوم", LocalTime.of(23, 30), "قبل النوم"),
    RoutineTask("dhikr2", "100× ربي مسني الضر وأنت أرحم الراحمين", "ذكر قبل النوم", LocalTime.of(23, 40), "قبل النوم"),
    RoutineTask("tahajjud", "التهجد + سورة يس", "عدّلي الوقت حسب الفجر", LocalTime.of(4, 30), "قبل الفجر")
)

internal data class MonthlyProgress(
    val completed: Int,
    val target: Int
) {
    val ratio: Float
        get() = if (target <= 0) 0f else (completed.toFloat() / target).coerceIn(0f, 1f)

    val nextMilestone: Int
        get() = when {
            ratio >= 1f -> 100
            ratio >= .75f -> 100
            ratio >= .50f -> 75
            ratio >= .25f -> 50
            else -> 25
        }

    val stage: String
        get() = when {
            ratio >= 1f -> "وصلتي للهدف"
            ratio >= .75f -> "المرحلة الأخيرة"
            ratio >= .50f -> "منتصف الطريق"
            ratio >= .25f -> "تقدّم ثابت"
            else -> "بداية الهدف"
        }
}

internal class RoutineStore(private val context: Context) {
    private val prefs = context.getSharedPreferences("yowmi_prefs", Context.MODE_PRIVATE)

    init {
        if (!prefs.contains("cleaning_anchor")) {
            prefs.edit().putString("cleaning_anchor", LocalDate.now().toString()).apply()
        }
        if (!prefs.contains("tasks_json_v2")) {
            saveTasks(defaultRoutineTasks)
        }
    }

    fun tasks(): List<RoutineTask> {
        val raw = prefs.getString("tasks_json_v2", null) ?: return defaultRoutineTasks
        return runCatching {
            val array = JSONArray(raw)
            buildList {
                for (i in 0 until array.length()) {
                    add(taskFromJson(array.getJSONObject(i)))
                }
            }
        }.getOrElse { defaultRoutineTasks }
    }

    fun taskById(id: String): RoutineTask? = tasks().firstOrNull { it.id == id }

    fun addTask(task: RoutineTask) {
        saveTasks(tasks() + task)
    }

    fun updateTask(task: RoutineTask) {
        saveTasks(tasks().map { if (it.id == task.id) task else it })
    }

    fun deleteTask(id: String) {
        saveTasks(tasks().filterNot { it.id == id })
        prefs.edit().remove("time_$id").apply()
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

    fun notificationsEnabled(): Boolean = prefs.getBoolean("notifications", true)

    fun setNotificationsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean("notifications", enabled).apply()
    }

    fun restartCleaningCycle() {
        prefs.edit().putString("cleaning_anchor", LocalDate.now().toString()).apply()
    }

    fun activeTasks(date: LocalDate): List<RoutineTask> =
        tasks().filter { isActive(it, date) }.sortedBy { time(it) }

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

    fun dailyProgress(date: LocalDate): Pair<Int, Int> {
        val active = activeTasks(date)
        return active.count { isDone(it, date) } to active.size
    }

    fun goalTarget(group: String, month: YearMonth): Int {
        val key = "goal_${month}_$group"
        val saved = prefs.getInt(key, -1)
        if (saved > 0) return saved

        var scheduled = 0
        val groupTasks = tasks().filter { it.progressGroup == group }
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            groupTasks.forEach { task ->
                if (isActive(task, date)) scheduled++
            }
        }
        return scheduled.coerceAtLeast(1)
    }

    fun setGoalTarget(group: String, month: YearMonth, target: Int) {
        prefs.edit().putInt("goal_${month}_$group", target.coerceAtLeast(1)).apply()
    }

    fun monthlyProgress(group: String, month: YearMonth): MonthlyProgress {
        val groupTasks = tasks().filter { it.progressGroup == group }
        var completed = 0
        for (day in 1..month.lengthOfMonth()) {
            val date = month.atDay(day)
            groupTasks.forEach { task ->
                if (isActive(task, date) && isDone(task, date)) completed++
            }
        }
        return MonthlyProgress(completed, goalTarget(group, month))
    }

    private fun saveTasks(items: List<RoutineTask>) {
        val array = JSONArray()
        items.forEach { array.put(taskToJson(it)) }
        prefs.edit().putString("tasks_json_v2", array.toString()).apply()
    }

    private fun taskToJson(task: RoutineTask): JSONObject = JSONObject().apply {
        put("id", task.id)
        put("title", task.title)
        put("subtitle", task.subtitle)
        put("defaultTime", task.defaultTime.toString())
        put("section", task.section)
        put("progressGroup", task.progressGroup ?: JSONObject.NULL)
        put("isCustom", task.isCustom)
        when (val rule = task.repeatRule) {
            RepeatRule.Daily -> put("repeatType", "daily")
            RepeatRule.AlternateDays -> put("repeatType", "alternate")
            is RepeatRule.Weekly -> {
                put("repeatType", "weekly")
                put("days", JSONArray(rule.days.map { it.value }))
            }
        }
    }

    private fun taskFromJson(obj: JSONObject): RoutineTask {
        val repeat = when (obj.optString("repeatType", "daily")) {
            "alternate" -> RepeatRule.AlternateDays
            "weekly" -> {
                val arr = obj.optJSONArray("days") ?: JSONArray()
                val days = buildSet {
                    for (i in 0 until arr.length()) {
                        runCatching { add(DayOfWeek.of(arr.getInt(i))) }
                    }
                }
                RepeatRule.Weekly(days.ifEmpty { setOf(DayOfWeek.MONDAY) })
            }
            else -> RepeatRule.Daily
        }
        val progressGroup = if (obj.isNull("progressGroup")) null else obj.optString("progressGroup", null)
        return RoutineTask(
            id = obj.getString("id"),
            title = obj.getString("title"),
            subtitle = obj.optString("subtitle", ""),
            defaultTime = runCatching { LocalTime.parse(obj.getString("defaultTime")) }.getOrDefault(LocalTime.NOON),
            section = obj.optString("section", "اليوم"),
            repeatRule = repeat,
            progressGroup = progressGroup,
            isCustom = obj.optBoolean("isCustom", false)
        )
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
