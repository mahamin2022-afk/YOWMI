package com.yowmi.app

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * YOWMI's expressive feminine palette. One design language across the routine,
 * monthly calendar, Turkish learning, Quran goals, and home fitness journeys.
 * Keep text and status colours legible; use pastel fills only on surfaces.
 */
internal object YowmiPalette {
    val Text = Color(0xFF4B2946)
    val Muted = Color(0xFF806B80)
    val Canvas = Color(0xFFFFF7FC)
    val Surface = Color(0xFFFFFFFF)
    val Border = Color(0xFFF0E0EB)
    val RoseWash = Color(0xFFFFEAF4)
    val LilacWash = Color(0xFFF2EAFE)
    val PeachWash = Color(0xFFFFEFE9)

    val Berry = Color(0xFFC2377B)
    val Pink = Color(0xFFE64D94)
    val Lavender = Color(0xFF8463D5)
    val Lilac = Color(0xFFA782E5)
    val Coral = Color(0xFFD56B61)
    val Gold = Color(0xFFF1AC46)
    val Mint = Color(0xFF229E88)
    val Sky = Color(0xFF6D8CE1)
    val Aqua = Color(0xFF26A8B1)
    val Success = Color(0xFF218F74)
    val Danger = Color(0xFFC54962)

    val MainHero = Brush.horizontalGradient(
        listOf(Color(0xFFC2377B), Color(0xFFA54AB1))
    )
    val GoalsHero = Brush.horizontalGradient(
        listOf(Color(0xFFAC429D), Color(0xFF7961C9))
    )
    val TurkishHero = Brush.horizontalGradient(
        listOf(Color(0xFF7B57C8), Color(0xFFA35BCA))
    )
    val FitnessHero = Brush.horizontalGradient(
        listOf(Color(0xFFC34D72), Color(0xFFCA695F))
    )
    val AddButton = Brush.linearGradient(
        listOf(Color(0xFFE94B95), Color(0xFFBE3D95))
    )
}
