package com.yowmi.app

import android.content.Context
import org.json.JSONArray
import java.time.LocalDate
import java.time.YearMonth
import kotlin.math.ceil

internal data class TurkishLesson(
    val id: String,
    val globalNumber: Int,
    val level: String,
    val localNumber: Int,
    val title: String
)

internal data class TurkishLevelPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val lessons: List<TurkishLesson>
)

internal data class TurkishSprint(
    val month: YearMonth,
    val targetLessons: Int,
    val plannedLessonIds: List<String>,
    val completedThisMonth: Int,
    val completedSectionsThisMonth: Int,
    val weeklyPace: Int,
    val remainingCourseLessons: Int,
    val estimatedMonthsRemaining: Int
)

internal val turkishLessonSections = listOf(
    "القاعدة",
    "المحادثة",
    "القراءة والاستماع",
    "50 مفردة",
    "التمارين",
    "الواجب + الاختبار الصغير"
)

private fun lesson(n: Int, level: String, local: Int, title: String) =
    TurkishLesson("turkish_lesson_$n", n, level, local, title)

internal val turkishLevels: List<TurkishLevelPlan> = listOf(
    TurkishLevelPlan(
        "A1",
        "A1 — الأساس والتواصل اليومي",
        "من الصفر إلى التعارف، وصف الحياة اليومية، المكان والوقت والأسرة والبنى الأساسية.",
        listOf(
            lesson(1, "A1", 1, "Tanışma ve Kendini Tanıtma"),
            lesson(2, "A1", 2, "Türk Alfabesi ve Sesler"),
            lesson(3, "A1", 3, "Bu Ne? O Kim?"),
            lesson(4, "A1", 4, "Neredesin?"),
            lesson(5, "A1", 5, "Sayılar ve Sıra"),
            lesson(6, "A1", 6, "Ben Öğrenciyim"),
            lesson(7, "A1", 7, "Ne Yapıyorsun?"),
            lesson(8, "A1", 8, "Nereye? Nereden?"),
            lesson(9, "A1", 9, "Ne Yapmak İstiyorsun?"),
            lesson(10, "A1", 10, "Benim Ailem"),
            lesson(11, "A1", 11, "Ülkeler, Milliyetler ve Diller"),
            lesson(12, "A1", 12, "Onu Tanıyorum"),
            lesson(13, "A1", 13, "Saat Kaç?"),
            lesson(14, "A1", 14, "Önce ve Sonra"),
            lesson(15, "A1", 15, "Ne Zamandan Beri?"),
            lesson(16, "A1", 16, "Ailem ve Çevrem"),
            lesson(17, "A1", 17, "Evdeki Adam"),
            lesson(18, "A1", 18, "Daha Güzel, En Güzel")
        )
    ),
    TurkishLevelPlan(
        "A2",
        "A2 — توسيع الحياة اليومية",
        "الماضي والمستقبل والعادات والقدرة والاحتمال ومواقف السفر والعمل والحياة الاجتماعية.",
        listOf(
            lesson(19, "A2", 1, "Mutfakta: Emir Kipi"),
            lesson(20, "A2", 2, "Birlikte Yapalım"),
            lesson(21, "A2", 3, "İnsanları ve Şeyleri Tanımlamak"),
            lesson(22, "A2", 4, "Dün Ne Yaptın?"),
            lesson(23, "A2", 5, "Dün Nasıldın?"),
            lesson(24, "A2", 6, "Kiminle? Neyle?"),
            lesson(25, "A2", 7, "Yarın Ne Yapacaksın?"),
            lesson(26, "A2", 8, "Gelecekte Nasıl Olacak?"),
            lesson(27, "A2", 9, "Senin Gibi, Benim Kadar"),
            lesson(28, "A2", 10, "...Duyduğuma Göre"),
            lesson(29, "A2", 11, "Bembeyaz ve Küçücük"),
            lesson(30, "A2", 12, "“...Dedi ki”"),
            lesson(31, "A2", 13, "Her Gün Ne Yaparsın?"),
            lesson(32, "A2", 14, "Geniş Zamanın İncelikleri"),
            lesson(33, "A2", 15, "Genel Bilgiler"),
            lesson(34, "A2", 16, "Yapabilirim"),
            lesson(35, "A2", 17, "İzin, Rica ve Olasılık"),
            lesson(36, "A2", 18, "Yapıp Çıkmak")
        )
    ),
    TurkishLevelPlan(
        "B1",
        "B1 — التعبير المستقل",
        "ربط الأحداث وشرح الأسباب والأهداف والضرورة والشرط والخبرة والموضوعات الأطول.",
        listOf(
            lesson(37, "B1", 1, "Nasıl Yaptın?"),
            lesson(38, "B1", 2, "Ne Yapıyordun?"),
            lesson(39, "B1", 3, "Ben Çocukken"),
            lesson(40, "B1", 4, "Yüzmek Güzeldir"),
            lesson(41, "B1", 5, "Onun Gülüşü"),
            lesson(42, "B1", 6, "Bana “Bekle” Dedi"),
            lesson(43, "B1", 7, "Yapmalıyım"),
            lesson(44, "B1", 8, "Gerek, Lazım, Zorunda"),
            lesson(45, "B1", 9, "Yapmalıydım"),
            lesson(46, "B1", 10, "Başarmak İçin"),
            lesson(47, "B1", 11, "...Bunu Yapmaktansa"),
            lesson(48, "B1", 12, "Yağmura Rağmen"),
            lesson(49, "B1", 13, "...Eğer"),
            lesson(50, "B1", 14, "...Keşke"),
            lesson(51, "B1", 15, "Şartın Birleşik Kullanımları"),
            lesson(52, "B1", 16, "Birbirimizi Anlıyoruz"),
            lesson(53, "B1", 17, "Yaptıkça Öğreniyorum"),
            lesson(54, "B1", 18, "Buraya Geldiğimden Beri")
        )
    ),
    TurkishLevelPlan(
        "B2",
        "B2 — الطلاقة المتقدمة",
        "المبني للمجهول والسببية والصفات الفعلية والنقل غير المباشر والروابط المتقدمة.",
        listOf(
            lesson(55, "B2", 1, "Kendiliğinden ve Kendi Kendine"),
            lesson(56, "B2", 2, "Yaptıkça ve Sürdükçe"),
            lesson(57, "B2", 3, "Gelinceye Kadar"),
            lesson(58, "B2", 4, "Nasıl Yapılır?"),
            lesson(59, "B2", 5, "Tarafından ve -CA"),
            lesson(60, "B2", 6, "Olduğu Zaman"),
            lesson(61, "B2", 7, "Yaptırmak"),
            lesson(62, "B2", 8, "...Neden? Çünkü"),
            lesson(63, "B2", 9, "Artık ve Sanki"),
            lesson(64, "B2", 10, "Gelen Adam"),
            lesson(65, "B2", 11, "Okuduğum Kitap"),
            lesson(66, "B2", 12, "Geçmişten Geleceğe Sıfat-Fiiller"),
            lesson(67, "B2", 13, "Dedi ki / Dediğini Söyledi"),
            lesson(68, "B2", 14, "Sordu: “Gelecek misin?”"),
            lesson(69, "B2", 15, "Onun Doktor Olduğunu Söyledi"),
            lesson(70, "B2", 16, "Oysaki, Hâlbuki, Meğerse..."),
            lesson(71, "B2", 17, "Bağlaç Olan da/de"),
            lesson(72, "B2", 18, "Ayrıca, Üstelik, Hatta")
        )
    )
)

internal val allTurkishLessons = turkishLevels.flatMap { it.lessons }

internal class TurkishJourneyStore(context: Context) {
    private val prefs = context.getSharedPreferences("turkish_journey_v2", Context.MODE_PRIVATE)

    fun monthlyTarget(month: YearMonth = YearMonth.now()): Int =
        prefs.getInt("monthly_target_$month", 18).coerceIn(4, 36)

    fun setMonthlyTarget(month: YearMonth, target: Int) {
        prefs.edit()
            .putInt("monthly_target_$month", target.coerceIn(4, 36))
            .remove("sprint_plan_$month")
            .apply()
    }

    fun isSectionDone(lessonId: String, sectionIndex: Int): Boolean =
        prefs.getBoolean("section_${lessonId}_$sectionIndex", false)

    fun setSectionDone(lessonId: String, sectionIndex: Int, done: Boolean) {
        val key = "section_${lessonId}_$sectionIndex"
        val editor = prefs.edit().putBoolean(key, done)
        if (done && isLessonCompleteAfterChange(lessonId, sectionIndex)) {
            if (!prefs.contains("lesson_completed_at_$lessonId")) {
                editor.putString("lesson_completed_at_$lessonId", LocalDate.now().toString())
            }
        } else if (!done) {
            editor.remove("lesson_completed_at_$lessonId")
        }
        editor.apply()
    }

    fun markLessonComplete(lessonId: String, done: Boolean) {
        val editor = prefs.edit()
        turkishLessonSections.indices.forEach {
            editor.putBoolean("section_${lessonId}_$it", done)
        }
        if (done) editor.putString("lesson_completed_at_$lessonId", LocalDate.now().toString())
        else editor.remove("lesson_completed_at_$lessonId")
        editor.apply()
    }

    fun isLessonComplete(lesson: TurkishLesson): Boolean =
        turkishLessonSections.indices.all { isSectionDone(lesson.id, it) }

    fun completedSections(lesson: TurkishLesson): Int =
        turkishLessonSections.indices.count { isSectionDone(lesson.id, it) }

    fun completedLessonCount(): Int = allTurkishLessons.count { isLessonComplete(it) }

    fun currentLesson(): TurkishLesson? = allTurkishLessons.firstOrNull { !isLessonComplete(it) }

    fun currentLevel(): TurkishLevelPlan =
        turkishLevels.firstOrNull { level -> level.lessons.any { !isLessonComplete(it) } } ?: turkishLevels.last()

    fun levelCompletedLessons(level: TurkishLevelPlan): Int = level.lessons.count { isLessonComplete(it) }

    fun isLevelUnlocked(level: TurkishLevelPlan): Boolean {
        val idx = turkishLevels.indexOfFirst { it.id == level.id }
        if (idx <= 0) return true
        return turkishLevels.take(idx).all { previous ->
            previous.lessons.all { isLessonComplete(it) } && reviewCheckpointsDone(previous.id) == 3
        }
    }

    fun reviewDone(levelId: String, block: Int, item: Int): Boolean =
        prefs.getBoolean("review_${levelId}_${block}_$item", false)

    fun setReviewDone(levelId: String, block: Int, item: Int, done: Boolean) {
        prefs.edit().putBoolean("review_${levelId}_${block}_$item", done).apply()
    }

    fun reviewBlockDone(levelId: String, block: Int): Boolean =
        (0..2).all { reviewDone(levelId, block, it) }

    fun reviewCheckpointsDone(levelId: String): Int =
        (1..3).count { reviewBlockDone(levelId, it) }

    fun lessonUnlocked(lesson: TurkishLesson): Boolean {
        val index = allTurkishLessons.indexOfFirst { it.id == lesson.id }
        if (index <= 0) return true
        val previous = allTurkishLessons[index - 1]
        if (!isLessonComplete(previous)) return false
        if (lesson.localNumber == 7 && !reviewBlockDone(lesson.level, 1)) return false
        if (lesson.localNumber == 13 && !reviewBlockDone(lesson.level, 2)) return false
        if (lesson.localNumber == 1) {
            val previousLevel = turkishLevels.getOrNull(turkishLevels.indexOfFirst { it.id == lesson.level } - 1)
            if (previousLevel != null && !reviewBlockDone(previousLevel.id, 3)) return false
        }
        return true
    }

    fun completionDate(lesson: TurkishLesson): LocalDate? =
        prefs.getString("lesson_completed_at_${lesson.id}", null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }

    fun completedInMonth(month: YearMonth): Int =
        allTurkishLessons.count { completionDate(it)?.let(YearMonth::from) == month }

    fun completedSectionsInMonth(month: YearMonth): Int {
        var count = 0
        allTurkishLessons.forEach { lesson ->
            if (completionDate(lesson)?.let(YearMonth::from) == month) {
                count += turkishLessonSections.size
            }
        }
        return count
    }

    fun sprint(month: YearMonth = YearMonth.now()): TurkishSprint {
        val target = monthlyTarget(month)
        val planned = sprintPlan(month, target)
        val completedMonth = completedInMonth(month)
        val remaining = (allTurkishLessons.size - completedLessonCount()).coerceAtLeast(0)
        val pace = ceil(target / 4.0).toInt().coerceAtLeast(1)
        val months = if (remaining == 0) 0 else ceil(remaining / target.toDouble()).toInt()
        return TurkishSprint(
            month = month,
            targetLessons = target,
            plannedLessonIds = planned,
            completedThisMonth = completedMonth,
            completedSectionsThisMonth = completedSectionsInMonth(month),
            weeklyPace = pace,
            remainingCourseLessons = remaining,
            estimatedMonthsRemaining = months
        )
    }

    fun isPlannedThisMonth(lessonId: String, month: YearMonth = YearMonth.now()): Boolean =
        sprint(month).plannedLessonIds.contains(lessonId)

    fun overallRatio(): Float =
        if (allTurkishLessons.isEmpty()) 0f else completedLessonCount().toFloat() / allTurkishLessons.size

    fun currentLevelRatio(): Float {
        val level = currentLevel()
        return levelCompletedLessons(level).toFloat() / level.lessons.size
    }

    private fun sprintPlan(month: YearMonth, target: Int): List<String> {
        val key = "sprint_plan_$month"
        val existing = prefs.getString(key, null)
        if (existing != null) {
            val arr = JSONArray(existing)
            return buildList {
                for (i in 0 until arr.length()) add(arr.getString(i))
            }
        }

        val remaining = allTurkishLessons.filter { !isLessonComplete(it) }
        val chosen = remaining.take(target).map { it.id }
        prefs.edit().putString(key, JSONArray(chosen).toString()).apply()
        return chosen
    }

    private fun isLessonCompleteAfterChange(lessonId: String, changedSection: Int): Boolean =
        turkishLessonSections.indices.all { index ->
            if (index == changedSection) true else isSectionDone(lessonId, index)
        }
}

internal val turkishReviewItems = listOf(
    "إعادة محادثات الدروس الستة",
    "اختيار ومراجعة 30 كلمة",
    "كتابة فقرة تستخدم 3 قواعد مختلفة"
)
