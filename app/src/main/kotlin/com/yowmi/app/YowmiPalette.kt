package com.yowmi.app

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * FEMININE VIBRANT DESIGN SYSTEM — V1.0
 * Exact source HEX tokens. Shared across every screen to keep semantics consistent.
 */
internal object YowmiPalette {
    // Foundation
    val Primary = Color(0xFF49365F)
    val Secondary = Color(0xFF14B8B1)
    val Accent = Color(0xFFF04B9A)
    val GrammarBlue = Color(0xFF7180F7)
    val MintGreen = Color(0xFF0DBD96)
    val OrchidPurple = Color(0xFFA16AF5)
    val PeachOrange = Color(0xFFFF9161)
    val SkyCyan = Color(0xFF27BDD0)
    val CoralPink = Color(0xFFF65E85)
    val WarmYellow = Color(0xFFFFBE42)
    val SoftRed = Color(0xFFE85B68)
    val Emerald = Color(0xFF19AB83)

    // Backgrounds, surfaces and legible foregrounds
    val Canvas = Color(0xFFF9F7FF)
    val SecondaryCanvas = Color(0xFFF0EAF9)
    val Surface = Color(0xFFFFFFFF)
    val PinkSurface = Color(0xFFFFF0F7)
    val PurpleSurface = Color(0xFFF2EAFE)
    val MintSurface = Color(0xFFE3F9F0)
    val PeachSurface = Color(0xFFFFF0E5)
    val BlueSurface = Color(0xFFEAF0FF)
    val LearningSurface = Color(0xFFE6F8FB)
    val HomeSurface = Color(0xFFFFF6D9)
    val Text = Color(0xFF302640)
    val SecondaryText = Color(0xFF756B85)
    val MutedText = Color(0xFF958BA4)
    val Border = Color(0xFFEAE4F3)
    val Divider = Color(0xFFF0EBF5)

    // Semantic categories: use labels and icons alongside color, never color alone.
    val NormalTask = GrammarBlue
    val Goal = OrchidPurple
    val Habit = MintGreen
    val Fitness = PeachOrange
    val Learning = SkyCyan
    val Personal = Accent
    val Home = WarmYellow
    val Completed = Emerald

    // Backward-compatible aliases consumed by existing reusable components.
    val Berry = Primary
    val Pink = Accent
    val Lavender = GrammarBlue
    val Lilac = OrchidPurple
    val Coral = PeachOrange
    val Gold = WarmYellow
    val Mint = MintGreen
    val Sky = GrammarBlue
    val Aqua = SkyCyan
    val Success = Emerald
    val Danger = SoftRed
    val Muted = SecondaryText
    val RoseWash = PinkSurface
    val LilacWash = PurpleSurface
    val PeachWash = PeachSurface

    // Only the four featured headers use a very light pastel gradient.
    // Dark copy remains accessible over each gradient.
    val MainHero = Brush.horizontalGradient(listOf(PinkSurface, PurpleSurface))
    val GoalsHero = Brush.horizontalGradient(listOf(PurpleSurface, PinkSurface))
    val TurkishHero = Brush.horizontalGradient(listOf(BlueSurface, PurpleSurface))
    val FitnessHero = Brush.horizontalGradient(listOf(PeachSurface, PinkSurface))
    val AddButton = Brush.linearGradient(listOf(Accent, OrchidPurple))
}
