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
    val title: String,
    val arabicTitle: String,
    val focus: String = "",
    val vocabularyTarget: Int
)

internal data class TurkishLevelPlan(
    val id: String,
    val title: String,
    val subtitle: String,
    val lessons: List<TurkishLesson>,
    val vocabularyTotal: Int,
    val reviewAfterLessonNumbers: List<Int>,
    val examTitle: String
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

private fun lesson(
    id: String,
    global: Int,
    level: String,
    local: Int,
    title: String,
    arabic: String,
    focus: String = "",
    vocab: Int
) = TurkishLesson(id, global, level, local, title, arabic, focus, vocab)

private fun a1Vocab(local: Int): Int = if (local <= 20) 41 else 40

internal val turkishLevels: List<TurkishLevelPlan> = listOf(
    TurkishLevelPlan(
        id = "A1",
        title = "A1 — الأساس والتواصل اليومي",
        subtitle = "بناء الجملة والتصرف في المواقف الأساسية، ثم إغلاق المستوى بمهارات الحياة اليومية.",
        lessons = listOf(
            lesson("turkish_lesson_1", 1, "A1", 1, "Tanışma ve Kendini Tanıtma", "التعارف والتعريف عن النفس", vocab = a1Vocab(1)),
            lesson("turkish_lesson_2", 2, "A1", 2, "Türk Alfabesi ve Sesler", "الأبجدية التركية والأصوات", vocab = a1Vocab(2)),
            lesson("turkish_lesson_3", 3, "A1", 3, "Bu Ne? O Kim?", "ما هذا؟ من ذاك؟", vocab = a1Vocab(3)),
            lesson("turkish_lesson_4", 4, "A1", 4, "Neredesin?", "أين أنت؟", vocab = a1Vocab(4)),
            lesson("turkish_lesson_5", 5, "A1", 5, "Sayılar ve Sıra", "الأرقام والترتيب", vocab = a1Vocab(5)),
            lesson("turkish_lesson_6", 6, "A1", 6, "Ben Öğrenciyim", "أنا طالب/طالبة", vocab = a1Vocab(6)),
            lesson("turkish_lesson_7", 7, "A1", 7, "Ne Yapıyorsun?", "ماذا تفعل الآن؟", vocab = a1Vocab(7)),
            lesson("turkish_lesson_8", 8, "A1", 8, "Nereye? Nereden?", "إلى أين؟ من أين؟", vocab = a1Vocab(8)),
            lesson("turkish_lesson_9", 9, "A1", 9, "Ne Yapmak İstiyorsun?", "ماذا تريد أن تفعل؟", vocab = a1Vocab(9)),
            lesson("turkish_lesson_10", 10, "A1", 10, "Benim Ailem", "عائلتي", vocab = a1Vocab(10)),
            lesson("turkish_lesson_11", 11, "A1", 11, "Ülkeler, Milliyetler ve Diller", "البلدان والجنسيات واللغات", vocab = a1Vocab(11)),
            lesson("turkish_lesson_12", 12, "A1", 12, "Onu Tanıyorum", "أنا أعرفه/أعرفها", vocab = a1Vocab(12)),
            lesson("turkish_lesson_13", 13, "A1", 13, "Saat Kaç?", "كم الساعة؟", vocab = a1Vocab(13)),
            lesson("turkish_lesson_14", 14, "A1", 14, "Önce ve Sonra", "قبل وبعد", vocab = a1Vocab(14)),
            lesson("turkish_lesson_15", 15, "A1", 15, "Ne Zamandan Beri?", "منذ متى؟", vocab = a1Vocab(15)),
            lesson("turkish_lesson_16", 16, "A1", 16, "Ailem ve Çevrem", "عائلتي ومحيطي", vocab = a1Vocab(16)),
            lesson("turkish_lesson_17", 17, "A1", 17, "Evdeki Adam", "الرجل الموجود في البيت", "استعمال -ki", a1Vocab(17)),
            lesson("turkish_lesson_18", 18, "A1", 18, "Daha Güzel, En Güzel", "المقارنة: أجمل / الأجمل", "المقارنة والتفضيل", a1Vocab(18)),
            lesson("turkish_a1_skill_19", 19, "A1", 19, "Günler, Aylar, Tarihler ve Randevular", "الأيام والأشهر والتواريخ والمواعيد", vocab = a1Vocab(19)),
            lesson("turkish_a1_skill_20", 20, "A1", 20, "Sağlık, Vücut ve Eczane", "الصحة وأجزاء الجسم والصيدلية", vocab = a1Vocab(20)),
            lesson("turkish_a1_skill_21", 21, "A1", 21, "Telefonda Konuşma ve Kısa Mesajlar", "الهاتف والرسائل القصيرة", vocab = a1Vocab(21)),
            lesson("turkish_a1_skill_22", 22, "A1", 22, "Form Doldurma ve A1 Günlük Yaşam Görevi", "تعبئة النماذج والمعلومات الشخصية + مهمة حياة يومية", vocab = a1Vocab(22))
        ),
        vocabularyTotal = 900,
        reviewAfterLessonNumbers = listOf(6, 12, 18, 22),
        examTitle = "اختبار نهاية A1"
    ),
    TurkishLevelPlan(
        id = "A2",
        title = "A2 — توسيع الحياة اليومية",
        subtitle = "الماضي والمستقبل والعادات والقدرة والاحتمال ووصف الأشياء والتواصل الأوسع.",
        lessons = listOf(
            lesson("turkish_lesson_19", 23, "A2", 1, "Mutfakta: Emir Kipi", "في المطبخ: صيغة الأمر", "Emir kipi", 50),
            lesson("turkish_lesson_20", 24, "A2", 2, "Birlikte Yapalım", "لنقم بذلك معًا / صيغة الاقتراح", "İstek / öneri", 50),
            lesson("turkish_lesson_21", 25, "A2", 3, "İnsanları ve Şeyleri Tanımlamak", "وصف الأشخاص والأشياء", "-lı / -sız / -lık", 50),
            lesson("turkish_lesson_22", 26, "A2", 4, "Dün Ne Yaptın?", "ماذا فعلت أمس؟", "الماضي المباشر", 50),
            lesson("turkish_lesson_23", 27, "A2", 5, "Dün Nasıldın?", "كيف كنت أمس؟", "الماضي مع الجمل الاسمية", 50),
            lesson("turkish_lesson_24", 28, "A2", 6, "Kiminle? Neyle?", "مع من؟ وبماذا؟", "ile / -la / -le", 50),
            lesson("turkish_lesson_25", 29, "A2", 7, "Yarın Ne Yapacaksın?", "ماذا ستفعل غدًا؟", "المستقبل", 50),
            lesson("turkish_lesson_26", 30, "A2", 8, "Gelecekte Nasıl Olacak?", "كيف سيكون في المستقبل؟", "olacak", 50),
            lesson("turkish_lesson_27", 31, "A2", 9, "Senin Gibi, Benim Kadar", "مثلك، بقدري", "gibi / kadar", 50),
            lesson("turkish_lesson_28", 32, "A2", 10, "Duyduğuma Göre...", "بحسب ما سمعت", "الماضي غير المباشر -miş", 50),
            lesson("turkish_lesson_29", 33, "A2", 11, "Bembeyaz ve Küçücük", "تقوية وتصغير الصفات", "تقوية وتصغير الصفات", 50),
            lesson("turkish_lesson_30", 34, "A2", 12, "Dedi ki...", "قال إن... / نقل الكلام", "نقل الكلام", 50),
            lesson("turkish_lesson_31", 35, "A2", 13, "Her Gün Ne Yaparsın?", "ماذا تفعل عادة؟", "المضارع العام", 50),
            lesson("turkish_lesson_32", 36, "A2", 14, "Geniş Zamanın İncelikleri", "الاستخدامات المتقدمة للمضارع العام", "Geniş zaman", 50),
            lesson("turkish_lesson_33", 37, "A2", 15, "Genel Bilgiler", "المعلومات العامة", "-dir", 50),
            lesson("turkish_lesson_34", 38, "A2", 16, "Yapabilirim", "أستطيع", "-(y)Abil", 50),
            lesson("turkish_lesson_35", 39, "A2", 17, "İzin, Rica ve Olasılık", "الإذن والطلب والاحتمال", "الإذن والطلب والاحتمال", 50),
            lesson("turkish_lesson_36", 40, "A2", 18, "Yapıp Çıkmak", "ربط الأفعال: أفعل ثم...", "-(y)Ip", 50)
        ),
        vocabularyTotal = 900,
        reviewAfterLessonNumbers = listOf(6, 12, 18),
        examTitle = "اختبار نهاية A2"
    ),
    TurkishLevelPlan(
        id = "B1",
        title = "B1 — التعبير المستقل",
        subtitle = "ربط الأفكار وشرح الطريقة والهدف والضرورة والشرط والندم والخبرة بشكل أطول وأكثر استقلالًا.",
        lessons = listOf(
            lesson("turkish_lesson_37", 41, "B1", 1, "Nasıl Yaptın?", "كيف فعلت ذلك؟", "-(y)ArAk / -A -A", 50),
            lesson("turkish_lesson_38", 42, "B1", 2, "Ne Yapıyordun?", "ماذا كنت تفعل؟", "-iyordu", 50),
            lesson("turkish_lesson_39", 43, "B1", 3, "Ben Çocukken", "عندما كنت طفلًا", "-(y)ken", 50),
            lesson("turkish_lesson_40", 44, "B1", 4, "Yüzmek Güzeldir", "السباحة جميلة", "تحويل الفعل إلى مفهوم/اسم", 50),
            lesson("turkish_lesson_41", 45, "B1", 5, "Onun Gülüşü", "ضحكته", "تراكيب أعمق للاسم والفعل والملكية", 50),
            lesson("turkish_lesson_42", 46, "B1", 6, "Bana “Bekle” Dedi", "قال لي: انتظر", "نقل الكلام والقول", 50),
            lesson("turkish_lesson_43", 47, "B1", 7, "Yapmalıyım", "يجب أن أفعل", "-malı / -meli", 50),
            lesson("turkish_lesson_44", 48, "B1", 8, "Gerek, Lazım, Zorunda", "درجات الضرورة", "gerek / lazım / zorunda", 50),
            lesson("turkish_lesson_45", 49, "B1", 9, "Yapmalıydım", "كان يجب أن أفعل", "الضرورة والندم في الماضي", 50),
            lesson("turkish_lesson_46", 50, "B1", 10, "Başarmak İçin", "لكي أنجح", "التعبير عن الهدف والغاية", 50),
            lesson("turkish_lesson_47", 51, "B1", 11, "Bunu Yapmaktansa...", "بدلًا من أن...", "المقارنة بين خيارين", 50),
            lesson("turkish_lesson_48", 52, "B1", 12, "Yağmura Rağmen", "رغم المطر", "التعبير عن التناقض", 50),
            lesson("turkish_lesson_49", 53, "B1", 13, "Eğer...", "إذا...", "الشرط", 50),
            lesson("turkish_lesson_50", 54, "B1", 14, "Keşke...", "ليت...", "التمني والندم", 50),
            lesson("turkish_lesson_51", 55, "B1", 15, "Şartın Birleşik Kullanımları", "الاستخدامات المركبة للشرط", "الشرط مع الأزمنة والبنى الأخرى", 50),
            lesson("turkish_lesson_52", 56, "B1", 16, "Birbirimizi Anlıyoruz", "نحن نفهم بعضنا", "العلاقات المتبادلة", 50),
            lesson("turkish_lesson_53", 57, "B1", 17, "Yaptıkça Öğreniyorum", "كلما فعلت أتعلم", "-dıkça / -dikçe", 50),
            lesson("turkish_lesson_54", 58, "B1", 18, "Buraya Geldiğimden Beri", "منذ أن جئت إلى هنا", "المدة منذ حدث معيّن", 50)
        ),
        vocabularyTotal = 900,
        reviewAfterLessonNumbers = listOf(6, 12, 18),
        examTitle = "اختبار نهاية B1"
    )
)

internal val allTurkishLessons = turkishLevels.flatMap { it.lessons }

internal val turkishReviewItems = listOf(
    "مراجعة القواعد والتراكيب السابقة",
    "مراجعة المفردات المصورة واسترجاعها",
    "محادثة أو مهمة تواصل تستخدم محتوى المرحلة",
    "قراءة/استماع + كتابة فقرة قصيرة"
)

internal val turkishExamParts = listOf(
    "القواعد والتراكيب",
    "المفردات",
    "القراءة والاستماع",
    "المحادثة والكتابة"
)

internal fun lessonSectionLabels(lesson: TurkishLesson): List<String> = listOf(
    "شرح الدرس والقاعدة",
    "المحادثة والتطبيق",
    "القراءة والاستماع",
    "${lesson.vocabularyTarget} مفردة مصورة",
    "التمارين",
    "الواجب + الاختبار الصغير"
)

internal class TurkishJourneyStore(context: Context) {
    private val prefs = context.getSharedPreferences("turkish_journey_v2", Context.MODE_PRIVATE)

    fun monthlyTarget(month: YearMonth = YearMonth.now()): Int =
        prefs.getInt("monthly_target_$month", 12).coerceIn(4, 30)

    fun setMonthlyTarget(month: YearMonth, target: Int) {
        prefs.edit()
            .putInt("monthly_target_$month", target.coerceIn(4, 30))
            .remove("sprint_plan_$month")
            .apply()
    }

    fun isSectionDone(lessonId: String, sectionIndex: Int): Boolean =
        prefs.getBoolean("section_${lessonId}_$sectionIndex", false)

    fun setSectionDone(lessonId: String, sectionIndex: Int, done: Boolean) {
        val key = "section_${lessonId}_$sectionIndex"
        val editor = prefs.edit().putBoolean(key, done)
        if (done) editor.putBoolean("activity_${LocalDate.now()}", true)
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
        turkishLessonSectionsIndices().forEach {
            editor.putBoolean("section_${lessonId}_$it", done)
        }
        if (done) {
            editor.putString("lesson_completed_at_$lessonId", LocalDate.now().toString())
            editor.putBoolean("activity_${LocalDate.now()}", true)
        } else {
            editor.remove("lesson_completed_at_$lessonId")
        }
        editor.apply()
    }

    fun isLessonComplete(lesson: TurkishLesson): Boolean =
        turkishLessonSectionsIndices().all { isSectionDone(lesson.id, it) }

    fun completedSections(lesson: TurkishLesson): Int =
        turkishLessonSectionsIndices().count { isSectionDone(lesson.id, it) }

    fun completedLessonCount(): Int = allTurkishLessons.count { isLessonComplete(it) }

    fun totalCompletedSections(): Int =
        allTurkishLessons.sumOf { completedSections(it) }

    fun completedVocabulary(level: TurkishLevelPlan): Int =
        level.lessons.filter { isSectionDone(it.id, 3) }.sumOf { it.vocabularyTarget }

    fun totalCompletedVocabulary(): Int =
        turkishLevels.sumOf { completedVocabulary(it) }

    fun xp(): Int =
        totalCompletedSections() * 5 +
            turkishLevels.sumOf { reviewCheckpointsDone(it.id) } * 35 +
            turkishLevels.sumOf { examPartsDone(it.id) } * 25

    fun currentStreak(): Int {
        var streak = 0
        var date = LocalDate.now()
        repeat(366) {
            if (prefs.getBoolean("activity_$date", false)) {
                streak++
                date = date.minusDays(1)
            } else return streak
        }
        return streak
    }

    fun currentLesson(): TurkishLesson? =
        allTurkishLessons.firstOrNull { !isLessonComplete(it) && lessonUnlocked(it) }

    fun currentLevel(): TurkishLevelPlan =
        turkishLevels.firstOrNull { !isLevelComplete(it) } ?: turkishLevels.last()

    fun levelCompletedLessons(level: TurkishLevelPlan): Int =
        level.lessons.count { isLessonComplete(it) }

    fun isLevelComplete(level: TurkishLevelPlan): Boolean =
        level.lessons.all { isLessonComplete(it) } &&
            reviewCheckpointsDone(level.id) == level.reviewAfterLessonNumbers.size &&
            levelExamDone(level.id)

    fun isLevelUnlocked(level: TurkishLevelPlan): Boolean {
        val index = turkishLevels.indexOfFirst { it.id == level.id }
        if (index <= 0) return true
        return turkishLevels.take(index).all { isLevelComplete(it) }
    }

    fun reviewDone(levelId: String, block: Int, item: Int): Boolean =
        prefs.getBoolean("review_${levelId}_${block}_$item", false)

    fun setReviewDone(levelId: String, block: Int, item: Int, done: Boolean) {
        prefs.edit()
            .putBoolean("review_${levelId}_${block}_$item", done)
            .apply()
    }

    fun reviewBlockDone(levelId: String, block: Int): Boolean =
        turkishReviewItems.indices.all { reviewDone(levelId, block, it) }

    fun reviewCheckpointsDone(levelId: String): Int {
        val level = turkishLevels.firstOrNull { it.id == levelId } ?: return 0
        return (1..level.reviewAfterLessonNumbers.size).count { reviewBlockDone(levelId, it) }
    }

    fun examPartDone(levelId: String, part: Int): Boolean =
        prefs.getBoolean("exam_${levelId}_$part", false)

    fun setExamPartDone(levelId: String, part: Int, done: Boolean) {
        prefs.edit()
            .putBoolean("exam_${levelId}_$part", done)
            .apply()
    }

    fun examPartsDone(levelId: String): Int =
        turkishExamParts.indices.count { examPartDone(levelId, it) }

    fun levelExamDone(levelId: String): Boolean =
        turkishExamParts.indices.all { examPartDone(levelId, it) }

    fun lessonUnlocked(lesson: TurkishLesson): Boolean {
        val level = turkishLevels.firstOrNull { it.id == lesson.level } ?: return false
        if (!isLevelUnlocked(level)) return false

        val localIndex = level.lessons.indexOfFirst { it.id == lesson.id }
        if (localIndex < 0) return false
        if (localIndex > 0 && !isLessonComplete(level.lessons[localIndex - 1])) return false

        level.reviewAfterLessonNumbers.forEachIndexed { reviewIndex, boundary ->
            if (lesson.localNumber == boundary + 1 && !reviewBlockDone(level.id, reviewIndex + 1)) {
                return false
            }
        }
        return true
    }

    fun reviewUnlocked(level: TurkishLevelPlan, reviewIndex: Int): Boolean {
        val boundary = level.reviewAfterLessonNumbers.getOrNull(reviewIndex - 1) ?: return false
        return level.lessons.filter { it.localNumber <= boundary }.all { isLessonComplete(it) }
    }

    fun examUnlocked(level: TurkishLevelPlan): Boolean =
        level.lessons.all { isLessonComplete(it) } &&
            reviewCheckpointsDone(level.id) == level.reviewAfterLessonNumbers.size

    fun completionDate(lesson: TurkishLesson): LocalDate? =
        prefs.getString("lesson_completed_at_${lesson.id}", null)?.let {
            runCatching { LocalDate.parse(it) }.getOrNull()
        }

    fun sprint(month: YearMonth = YearMonth.now()): TurkishSprint {
        val target = monthlyTarget(month)
        val planned = sprintPlan(month, target)
        val plannedLessons = planned.mapNotNull { id ->
            allTurkishLessons.firstOrNull { it.id == id }
        }
        val completedMonth = plannedLessons.count { isLessonComplete(it) }
        val sectionProgress = plannedLessons.sumOf { completedSections(it) }
        val remaining = (allTurkishLessons.size - completedLessonCount()).coerceAtLeast(0)
        val pace = ceil(target / 4.0).toInt().coerceAtLeast(1)
        val months = if (remaining == 0) 0 else ceil(remaining / target.toDouble()).toInt()

        return TurkishSprint(
            month = month,
            targetLessons = target,
            plannedLessonIds = planned,
            completedThisMonth = completedMonth,
            completedSectionsThisMonth = sectionProgress,
            weeklyPace = pace,
            remainingCourseLessons = remaining,
            estimatedMonthsRemaining = months
        )
    }

    fun isPlannedThisMonth(lessonId: String, month: YearMonth = YearMonth.now()): Boolean =
        sprint(month).plannedLessonIds.contains(lessonId)

    fun overallRatio(): Float =
        if (allTurkishLessons.isEmpty()) 0f
        else completedLessonCount().toFloat() / allTurkishLessons.size

    fun currentLevelRatio(): Float {
        val level = currentLevel()
        val lessonWeight = levelCompletedLessons(level).toFloat()
        val reviewWeight = reviewCheckpointsDone(level.id) * 0.5f
        val examWeight = if (levelExamDone(level.id)) 1f else examPartsDone(level.id) / 4f
        val max = level.lessons.size + level.reviewAfterLessonNumbers.size * 0.5f + 1f
        return ((lessonWeight + reviewWeight + examWeight) / max).coerceIn(0f, 1f)
    }

    fun remainingLessonsInLevel(level: TurkishLevelPlan): Int =
        level.lessons.count { !isLessonComplete(it) }

    fun estimatedMonthsForLevel(level: TurkishLevelPlan, month: YearMonth = YearMonth.now()): Int {
        val remaining = remainingLessonsInLevel(level)
        if (remaining == 0) return 0
        return ceil(remaining / monthlyTarget(month).toDouble()).toInt()
    }

    private fun sprintPlan(month: YearMonth, target: Int): List<String> {
        val key = "sprint_plan_$month"
        val existing = prefs.getString(key, null)
        if (existing != null) {
            val array = JSONArray(existing)
            return buildList {
                for (i in 0 until array.length()) {
                    val id = array.getString(i)
                    if (allTurkishLessons.any { it.id == id }) add(id)
                }
            }
        }

        val remaining = allTurkishLessons.filter { !isLessonComplete(it) }
        val chosen = remaining.take(target).map { it.id }
        prefs.edit().putString(key, JSONArray(chosen).toString()).apply()
        return chosen
    }

    private fun isLessonCompleteAfterChange(lessonId: String, changedSection: Int): Boolean =
        turkishLessonSectionsIndices().all { index ->
            if (index == changedSection) true else isSectionDone(lessonId, index)
        }

    private fun turkishLessonSectionsIndices(): IntRange = 0..5
}
