package com.yowmi.app

import android.content.Context
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

internal enum class FitnessDayType { STRENGTH, OPTIONAL, REST }

internal data class FitnessExercise(
    val id: String,
    val name: String,
    val arabicName: String,
    val baseSets: Int,
    val reps: String,
    val instructions: List<String>,
    val caution: String = "",
    val alternative: String = "",
    val resistanceAllowed: Boolean = false,
    val required: Boolean = true
)

internal data class FitnessDayPlan(
    val day: DayOfWeek,
    val title: String,
    val subtitle: String,
    val duration: String,
    val type: FitnessDayType,
    val warmup: String = "",
    val cooldown: String = "",
    val exercises: List<FitnessExercise> = emptyList()
)

internal data class FitnessPhase(
    val weeks: IntRange,
    val title: String,
    val subtitle: String,
    val rule: String
)

internal data class FitnessProgramProgress(
    val completed: Int,
    val target: Int,
    val xp: Int,
    val week: Int,
    val phaseIndex: Int,
    val streak: Int
) {
    val ratio: Float
        get() = if (target <= 0) 0f else (completed.toFloat() / target).coerceIn(0f, 1f)
}

internal val fitnessPhases = listOf(
    FitnessPhase(
        1..2,
        "تعلّم الحركة",
        "الهدف هو التكنيك والثبات، مو التعب.",
        "مجموعتان لكل تمرين وبدون أوزان. استراحة 60–90 ثانية."
    ),
    FitnessPhase(
        3..4,
        "زيادة المجموعات",
        "نرفع حجم التمرين تدريجيًا.",
        "طبّقي عدد المجموعات الأصلي؛ التمارين الأساسية للأرداف تصل إلى 3 مجموعات."
    ),
    FitnessPhase(
        5..6,
        "زيادة المقاومة",
        "نضيف مقاومة فقط لما تصير الحركة سهلة.",
        "حقيبة خفيفة أو مطاط مقاومة عند الحاجة مع بقاء التكنيك ثابت."
    ),
    FitnessPhase(
        7..8,
        "التقدّم التدريجي",
        "آخر مرحلة من البرنامج.",
        "زيدي التكرارات قليلًا أو المقاومة؛ لا تزيدي الاثنين بنفس الوقت."
    )
)

private fun ex(
    id: String,
    name: String,
    ar: String,
    sets: Int,
    reps: String,
    instructions: List<String>,
    caution: String = "",
    alternative: String = "",
    resistanceAllowed: Boolean = false,
    required: Boolean = true
) = FitnessExercise(id, name, ar, sets, reps, instructions, caution, alternative, resistanceAllowed, required)

internal val homeFitnessPlan = listOf(
    FitnessDayPlan(
        DayOfWeek.SATURDAY,
        "الأرداف + خلف الفخذ",
        "Glutes & Hamstrings",
        "35–45 دقيقة",
        FitnessDayType.STRENGTH,
        warmup = "5 دقائق: مشي بمكانك دقيقتين + 10 جلوس ووقوف من كرسي + تحريك الوركين بلطف.",
        cooldown = "بعد آخر تمرين: مشي خفيف وتمطيط لطيف 3 دقائق.",
        exercises = listOf(
            ex(
                "fit8_sat_bridge", "Glute Bridge", "جسر الأرداف", 3, "15",
                listOf(
                    "استلقي على ظهرك واثني الركبتين والقدمين على الأرض.",
                    "شدي الأرداف وارفعي الحوض حتى يصير الجسم تقريبًا خط مستقيم.",
                    "اثبتي ثانية بالأعلى ثم انزلي ببطء."
                ),
                "لا تقوّسي أسفل الظهر لزيادة ارتفاع الحوض.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_sat_rdl", "Romanian Deadlift", "انحناء الورك", 3, "12",
                listOf(
                    "القدمين بعرض الورك والركبتان مثنيتان قليلًا.",
                    "رجّعي المؤخرة للخلف وميلي بالجذع للأمام مع ظهر ثابت.",
                    "ارجعي للوقوف باستخدام عضلات الأرداف."
                ),
                "الحركة من مفصل الورك، مو من تدوير الظهر.",
                "ابدئي بدون وزن ثم استخدمي حقيبة خفيفة.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_sat_reverse_lunge", "Reverse Lunge", "اندفاع للخلف", 2, "10 لكل رجل",
                listOf(
                    "وقفي مستقيمة وممكن تستندي لكرسي للتوازن.",
                    "رجّعي رجل للخلف واثني الركبتين لحد مريح.",
                    "ادفعي بقدم الرجل الأمامية وارجعي للوقوف ثم بدّلي."
                ),
                "الركبة الأمامية باتجاه أصابع القدم وما تنهار للداخل.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_sat_side_leg_raise", "Side-Lying Leg Raise", "رفع الرجل جانبياً", 2, "15 لكل جهة",
                listOf(
                    "نامي على جنبك والرجلان فوق بعض.",
                    "ارفعي الرجل العليا مع بقاء الحوض ثابت.",
                    "انزلي ببطء ثم بدّلي الجهة."
                ),
                "لا تلفّي الحوض للخلف."
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.SUNDAY,
        "البطن العميقة + الظهر والجزء العلوي",
        "Core, Back & Upper Body",
        "25–35 دقيقة",
        FitnessDayType.STRENGTH,
        warmup = "إحماء خفيف 5 دقائق قبل التمارين.",
        exercises = listOf(
            ex(
                "fit8_sun_dead_bug", "Dead Bug", "الحشرة الميتة", 3, "8 لكل جهة",
                listOf(
                    "استلقي وارفعِي الركبتين 90° والذراعين للأعلى.",
                    "مدّي الذراع اليمنى والرجل اليسرى ببطء.",
                    "ارجعي وبدّلي الجهة."
                ),
                "أسفل الظهر يضل ثابت بدون تقوّس زائد."
            ),
            ex(
                "fit8_sun_bird_dog", "Bird Dog", "مدّ الذراع والرجل المعاكستين", 2, "10 لكل جهة",
                listOf(
                    "ارتكزي على الكفين والركبتين.",
                    "مدّي اليد اليمنى والرجل اليسرى.",
                    "اثبتي ثانيتين ثم بدّلي."
                ),
                "لا ترفعي الرجل فوق مستوى الحوض ولا تلفّي الظهر."
            ),
            ex(
                "fit8_sun_side_plank", "Modified Side Plank", "البلانك الجانبي المعدّل", 2, "20 ثانية لكل جهة",
                listOf(
                    "نامي على جنبك والركبتان مثنيتان.",
                    "الكوع تحت الكتف والجسم على الساعد.",
                    "ارفعي الحوض بخط مستقيم من الكتف للركبتين مع تنفس طبيعي."
                )
            ),
            ex(
                "fit8_sun_wall_pushup", "Wall Push-up", "الضغط على الحائط", 2, "10",
                listOf(
                    "وقفي مقابل الحائط والكفان بعرض الكتفين.",
                    "اثني الكوعين واقتربي بجسم مستقيم.",
                    "ادفعي الحائط وارجعي."
                )
            ),
            ex(
                "fit8_sun_backpack_row", "Backpack Row", "سحب حقيبة للظهر", 2, "12",
                listOf(
                    "امسكي حقيبة خفيفة وميلي من الوركين مع ظهر مستقيم.",
                    "اسحبي الحقيبة باتجاه أسفل البطن.",
                    "نزليها ببطء."
                ),
                "استخدمي حقيبة متينة ومحكمة الإغلاق.",
                resistanceAllowed = true
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.MONDAY,
        "مشي خفيف أو راحة",
        "Recovery",
        "20–30 دقيقة اختياري",
        FitnessDayType.OPTIONAL,
        exercises = listOf(
            ex(
                "fit8_mon_walk", "Comfortable Walk", "مشي بسرعة مريحة", 1, "20–30 دقيقة",
                listOf("امشي بسرعة مريحة إذا جسمك متعافي.", "إذا العضلات تعبانة خدي راحة كاملة."),
                required = false
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.TUESDAY,
        "الأرداف + جوانب الورك",
        "Glutes & Hip Sides",
        "35–45 دقيقة",
        FitnessDayType.STRENGTH,
        exercises = listOf(
            ex(
                "fit8_tue_hip_thrust", "Hip Thrust", "دفع الحوض", 3, "12",
                listOf(
                    "اسندي أعلى الظهر على كنبة ثابتة وغير منزلقة.",
                    "القدمان على الأرض والركبتان مثنيتان.",
                    "ارفعي الحوض بشد الأرداف ثم انزلي ببطء."
                ),
                "لا تبالغي بتقويس أسفل الظهر.",
                "إذا الكنبة غير ثابتة استخدمي Glute Bridge.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_tue_split_squat", "Split Squat", "سكوات برجلين متباعدتين", 3, "10 لكل رجل",
                listOf(
                    "رجل قدام والثانية ورا بمسافة مريحة.",
                    "انزلي عموديًا بثني الركبتين.",
                    "اطلعي بدفع القدم الأمامية."
                ),
                "استندي لكرسي ثابت إذا احتجتي توازن.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_tue_clamshell", "Clamshell", "فتح الركبة جانبياً", 2, "15 لكل جهة",
                listOf(
                    "نامي على جنبك واثني الركبتين.",
                    "خلي القدمين متلامستين وارفعِي الركبة العليا.",
                    "انزلي ببطء مع تثبيت الحوض."
                ),
                "لا ترجّعي الجسم للخلف لفتح الركبة أكثر."
            ),
            ex(
                "fit8_tue_band_walk", "Lateral Band Walk", "المشي الجانبي بالمطاط", 2, "12 خطوة لكل اتجاه",
                listOf(
                    "حطي مطاط مقاومة خفيف فوق الركبتين.",
                    "اثني الركبتين قليلًا وخدي خطوات صغيرة للجانب.",
                    "حافظي على شد خفيف وكرري بالعكس."
                ),
                alternative = "إذا ما عندك مطاط اعملي Side-Lying Leg Raise.",
                resistanceAllowed = true
            ),
            ex(
                "fit8_tue_single_rdl", "Single-Leg Romanian Deadlift", "انحناء على رجل واحدة", 2, "10 لكل رجل",
                listOf(
                    "امسكي كرسي ثابت بيدك للتوازن.",
                    "ميلي من الورك وارفعِي الرجل الثانية للخلف.",
                    "ارجعي للوقوف بشد الأرداف."
                ),
                "ابدئي بمدى حركة صغير ومن دون أوزان.",
                resistanceAllowed = true
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.WEDNESDAY,
        "البطن العميقة + الخصر + التنفس",
        "Deep Core & Waist Control",
        "20–30 دقيقة",
        FitnessDayType.STRENGTH,
        exercises = listOf(
            ex(
                "fit8_wed_dead_bug", "Dead Bug", "الحشرة الميتة", 2, "10 لكل جهة",
                listOf("نفس تمرين الأحد مع التركيز على ثبات أسفل الظهر والتنفس.")
            ),
            ex(
                "fit8_wed_heel_slides", "Heel Slides", "مدّ الكعب", 2, "12 لكل رجل",
                listOf(
                    "استلقي والركبتان مثنيتان.",
                    "مدّي كعب رجل ببطء على الأرض حتى تستقيم تقريبًا.",
                    "ارجعي وبدّلي مع ثبات الحوض."
                )
            ),
            ex(
                "fit8_wed_pelvic_tilt", "Pelvic Tilt", "إمالة الحوض", 2, "12",
                listOf(
                    "استلقي مع ثني الركبتين.",
                    "لفّي الحوض قليلًا للخلف حتى يقترب أسفل الظهر من الأرض.",
                    "ارخي وكرري بدون رفع الأرداف."
                )
            ),
            ex(
                "fit8_wed_side_plank", "Modified Side Plank", "البلانك الجانبي المعدّل", 2, "20 ثانية لكل جهة",
                listOf("كرري البلانك الجانبي المعدّل مع تنفس طبيعي.")
            ),
            ex(
                "fit8_wed_vacuum", "Abdominal Vacuum", "فاكيوم البطن الخفيف", 4, "5–10 ثوانٍ",
                listOf(
                    "خدي شهيق طبيعي.",
                    "طلّعي الهواء ببطء واسحبي أسفل البطن للداخل بلطف.",
                    "حافظي على تنفس طبيعي ثم ارخي."
                ),
                "لا تحبسي النفس طويلًا. فائدته التحكم بالعضلات العميقة وليس حرق دهون موضعي."
            ),
            ex(
                "fit8_wed_walk", "Easy Walk", "مشي مريح بعد التمرين", 1, "15 دقيقة اختياري",
                listOf("مشي مريح بعد الحصة إذا عندك طاقة."),
                required = false
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.THURSDAY,
        "مشي خفيف اختياري",
        "Optional Recovery Walk",
        "20–30 دقيقة",
        FitnessDayType.OPTIONAL,
        exercises = listOf(
            ex(
                "fit8_thu_walk", "Easy Walk", "مشي خفيف", 1, "20–30 دقيقة",
                listOf("امشي إذا عندك طاقة، أو خدي راحة."),
                required = false
            )
        )
    ),
    FitnessDayPlan(
        DayOfWeek.FRIDAY,
        "راحة كاملة",
        "Full Rest",
        "تعافي",
        FitnessDayType.REST
    )
)

internal class FitnessJourneyStore(context: Context) {
    private val prefs = context.getSharedPreferences("fitness_journey_v2", Context.MODE_PRIVATE)

    init {
        if (!prefs.contains("program_start")) {
            val start = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
            prefs.edit().putString("program_start", start.toString()).apply()
        }
    }

    fun startDate(): LocalDate =
        runCatching { LocalDate.parse(prefs.getString("program_start", LocalDate.now().toString())) }
            .getOrDefault(LocalDate.now())

    fun restartFromNextSaturday() {
        val start = LocalDate.now().with(TemporalAdjusters.nextOrSame(DayOfWeek.SATURDAY))
        prefs.edit().putString("program_start", start.toString()).apply()
    }

    fun weekFor(date: LocalDate = LocalDate.now()): Int {
        val days = ChronoUnit.DAYS.between(startDate(), date)
        return when {
            days < 0 -> 1
            days >= 56 -> 8
            else -> (days / 7).toInt() + 1
        }
    }

    fun phaseFor(week: Int): FitnessPhase =
        fitnessPhases.first { week in it.weeks }

    fun phaseIndex(week: Int): Int =
        fitnessPhases.indexOfFirst { week in it.weeks }.coerceAtLeast(0)

    fun weekStart(week: Int): LocalDate =
        startDate().plusDays(((week.coerceIn(1, 8) - 1) * 7).toLong())

    fun planFor(day: DayOfWeek): FitnessDayPlan =
        homeFitnessPlan.first { it.day == day }

    fun recommendedSets(exercise: FitnessExercise, week: Int): Int =
        if (week <= 2) exercise.baseSets.coerceAtMost(2) else exercise.baseSets

    fun progressionNote(exercise: FitnessExercise, week: Int): String = when {
        week <= 2 -> "بدون أوزان؛ الأولوية للتكنيك."
        week <= 4 -> "طبّقي المجموعات الأصلية مع تكنيك ثابت."
        week <= 6 && exercise.resistanceAllowed -> "إذا صار سهل: حقيبة خفيفة أو مطاط مقاومة."
        week <= 6 -> "ثبتي الحركة والتحكم؛ ما في داعي تضيفي مقاومة."
        exercise.resistanceAllowed -> "زيدي التكرارات قليلًا أو المقاومة، مو الاثنين."
        else -> "زيدي التكرارات أو مدة الثبات قليلًا إذا صار التمرين سهل."
    }

    fun isDone(date: LocalDate, exerciseId: String): Boolean =
        prefs.getBoolean("done_${date}_$exerciseId", false)

    fun setDone(date: LocalDate, exerciseId: String, done: Boolean) {
        prefs.edit().putBoolean("done_${date}_$exerciseId", done).apply()
    }

    fun exerciseImageUri(exerciseId: String): String? =
        prefs.getString("exercise_image_$exerciseId", null)

    fun setExerciseImageUri(exerciseId: String, uri: String) {
        prefs.edit().putString("exercise_image_$exerciseId", uri).apply()
    }

    fun clearExerciseImageUri(exerciseId: String) {
        prefs.edit().remove("exercise_image_$exerciseId").apply()
    }

    fun dayProgress(date: LocalDate): Pair<Int, Int> {
        val required = planFor(date.dayOfWeek).exercises.filter { it.required }
        return required.count { isDone(date, it.id) } to required.size
    }

    fun completedRequired(): Int {
        var count = 0
        val start = startDate()
        repeat(56) { offset ->
            val date = start.plusDays(offset.toLong())
            val plan = planFor(date.dayOfWeek)
            count += plan.exercises.filter { it.required }.count { isDone(date, it.id) }
        }
        return count
    }

    fun totalRequired(): Int {
        var total = 0
        val start = startDate()
        repeat(56) { offset ->
            total += planFor(start.plusDays(offset.toLong()).dayOfWeek).exercises.count { it.required }
        }
        return total
    }

    fun streak(): Int {
        val start = startDate()
        var date = if (LocalDate.now().isBefore(start)) start else LocalDate.now()
        var streak = 0
        repeat(70) {
            if (date.isBefore(start)) return streak
            val (_, total) = dayProgress(date)
            if (total == 0) {
                date = date.minusDays(1)
            } else {
                val (done, required) = dayProgress(date)
                if (done == required) {
                    streak++
                    date = date.minusDays(1)
                } else {
                    return streak
                }
            }
        }
        return streak
    }

    fun progress(): FitnessProgramProgress {
        val completed = completedRequired()
        val target = totalRequired()
        val week = weekFor()
        return FitnessProgramProgress(
            completed = completed,
            target = target,
            xp = completed * 20,
            week = week,
            phaseIndex = phaseIndex(week),
            streak = streak()
        )
    }

    fun asGoalOverview(): GoalOverview {
        val p = progress()
        val stage = when {
            p.ratio >= 1f -> 4
            p.ratio >= .75f -> 3
            p.ratio >= .50f -> 2
            p.ratio >= .25f -> 1
            else -> 0
        }
        return GoalOverview(
            completed = p.completed,
            target = p.target,
            xp = p.xp,
            level = (p.xp / 100) + 1,
            streak = p.streak,
            stageIndex = stage
        )
    }
}
