package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class PresetThemeColor(
    val id: String,
    val name: String,
    val description: String,
    val swatch: Color,
    val lightScheme: ColorScheme,
    val darkScheme: ColorScheme
)

fun blendColors(c1: Color, c2: Color, ratio: Float): Color {
    val inv = 1f - ratio
    val r = (c1.red * inv + c2.red * ratio).coerceIn(0f, 1f)
    val g = (c1.green * inv + c2.green * ratio).coerceIn(0f, 1f)
    val b = (c1.blue * inv + c2.blue * ratio).coerceIn(0f, 1f)
    return Color(r, g, b, 1f)
}

fun generateCustomColorScheme(customColor: Color, isDark: Boolean): ColorScheme {
    val luminance = 0.2126f * customColor.red + 0.7152f * customColor.green + 0.0722f * customColor.blue
    return if (isDark) {
        val darkPrimary = blendColors(customColor, Color.White, 0.35f)
        val onPrimary = if (luminance > 0.6f) Color(0xFF00150A) else Color(0xFF001B24)
        val darkContainer = blendColors(customColor, Color.Black, 0.45f)
        val onContainer = blendColors(customColor, Color.White, 0.75f)
        darkColorScheme(
            primary = darkPrimary,
            onPrimary = onPrimary,
            primaryContainer = darkContainer,
            onPrimaryContainer = onContainer,
            secondary = blendColors(customColor, Color(0xFF818CF8), 0.4f),
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = blendColors(customColor, Color.Black, 0.6f),
            onSecondaryContainer = Color(0xFFE2E8F0),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = BackgroundDark,
            surface = SurfaceDark,
            surfaceVariant = SurfaceVariantDark,
            onSurface = OnSurfaceDark,
            onSurfaceVariant = OnSurfaceVariantDark,
            outline = OutlineDark,
            outlineVariant = Color(0xFF2E3842)
        )
    } else {
        val onPrimary = if (luminance > 0.55f) Color(0xFF0F172A) else Color(0xFFFFFFFF)
        val lightContainer = blendColors(customColor, Color.White, 0.8f)
        val onContainer = blendColors(customColor, Color.Black, 0.45f)
        val lightVariant = blendColors(customColor, Color(0xFFF1F5F9), 0.92f)
        lightColorScheme(
            primary = customColor,
            onPrimary = onPrimary,
            primaryContainer = lightContainer,
            onPrimaryContainer = onContainer,
            secondary = blendColors(customColor, Color(0xFF334155), 0.5f),
            onSecondary = Color.White,
            secondaryContainer = blendColors(customColor, Color(0xFFF1F5F9), 0.7f),
            onSecondaryContainer = Color(0xFF0F172A),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color.White,
            surfaceVariant = lightVariant,
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        )
    }
}

val PRESET_THEMES = listOf(
    // 1. Emerald Mint (Default)
    PresetThemeColor(
        id = "emerald",
        name = "Emerald Mint",
        description = "Fresh & financial",
        swatch = Color(0xFF006D4D),
        lightScheme = lightColorScheme(
            primary = EmeraldPrimaryLight,
            onPrimary = EmeraldOnPrimaryLight,
            primaryContainer = EmeraldPrimaryContainerLight,
            onPrimaryContainer = EmeraldOnPrimaryContainerLight,
            secondary = CyanSecondaryLight,
            onSecondary = CyanOnSecondaryLight,
            secondaryContainer = CyanSecondaryContainerLight,
            onSecondaryContainer = CyanOnSecondaryContainerLight,
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = BackgroundLight,
            surface = SurfaceLight,
            surfaceVariant = SurfaceVariantLight,
            onSurface = OnSurfaceLight,
            onSurfaceVariant = OnSurfaceVariantLight,
            outline = OutlineLight,
            outlineVariant = OutlineVariantLight
        ),
        darkScheme = darkColorScheme(
            primary = EmeraldPrimaryDark,
            onPrimary = EmeraldOnPrimaryDark,
            primaryContainer = EmeraldPrimaryContainerDark,
            onPrimaryContainer = EmeraldOnPrimaryContainerDark,
            secondary = CyanSecondaryDark,
            onSecondary = CyanOnSecondaryDark,
            secondaryContainer = CyanSecondaryContainerDark,
            onSecondaryContainer = CyanOnSecondaryContainerDark,
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = BackgroundDark,
            surface = SurfaceDark,
            surfaceVariant = SurfaceVariantDark,
            onSurface = OnSurfaceDark,
            onSurfaceVariant = OnSurfaceVariantDark,
            outline = OutlineDark,
            outlineVariant = OutlineVariantDark
        )
    ),

    // 2. Ocean Blue
    PresetThemeColor(
        id = "ocean",
        name = "Ocean Blue",
        description = "Classic & reliable",
        swatch = Color(0xFF00639B),
        lightScheme = lightColorScheme(
            primary = Color(0xFF00639B),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFCEE5FF),
            onPrimaryContainer = Color(0xFF001D33),
            secondary = Color(0xFF51606F),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFD4E4F6),
            onSecondaryContainer = Color(0xFF0D1D2A),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF96CCFF),
            onPrimary = Color(0xFF003353),
            primaryContainer = Color(0xFF004A76),
            onPrimaryContainer = Color(0xFFCEE5FF),
            secondary = Color(0xFFB8C8D9),
            onSecondary = Color(0xFF23323F),
            secondaryContainer = Color(0xFF394957),
            onSecondaryContainer = Color(0xFFD4E4F6),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = Color(0xFF101418),
            surface = Color(0xFF12171B),
            surfaceVariant = Color(0xFF222830),
            onSurface = Color(0xFFE2E2E6),
            onSurfaceVariant = Color(0xFFC2C7CE),
            outline = Color(0xFF8C9199),
            outlineVariant = Color(0xFF2E3842)
        )
    ),

    // 3. Royal Violet
    PresetThemeColor(
        id = "violet",
        name = "Royal Violet",
        description = "Modern & elegant",
        swatch = Color(0xFF6750A4),
        lightScheme = lightColorScheme(
            primary = Color(0xFF6750A4),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFE9DDFF),
            onPrimaryContainer = Color(0xFF22005D),
            secondary = Color(0xFF625B71),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE8DEF8),
            onSecondaryContainer = Color(0xFF1E192B),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFCFBCFF),
            onPrimary = Color(0xFF381E72),
            primaryContainer = Color(0xFF4F378B),
            onPrimaryContainer = Color(0xFFE9DDFF),
            secondary = Color(0xFFCBC2DB),
            onSecondary = Color(0xFF332D41),
            secondaryContainer = Color(0xFF4A4458),
            onSecondaryContainer = Color(0xFFE8DEF8),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = Color(0xFF141218),
            surface = Color(0xFF18151D),
            surfaceVariant = Color(0xFF282430),
            onSurface = Color(0xFFE6E1E5),
            onSurfaceVariant = Color(0xFFCAC4D0),
            outline = Color(0xFF948F99),
            outlineVariant = Color(0xFF2E3842)
        )
    ),

    // 4. Crimson Ruby
    PresetThemeColor(
        id = "ruby",
        name = "Crimson Ruby",
        description = "Luxe & vibrant",
        swatch = Color(0xFFB32656),
        lightScheme = lightColorScheme(
            primary = Color(0xFFB32656),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFD9E2),
            onPrimaryContainer = Color(0xFF3F0017),
            secondary = Color(0xFF75565F),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFFD9E2),
            onSecondaryContainer = Color(0xFF2B151C),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFFFB1C7),
            onPrimary = Color(0xFF65002C),
            primaryContainer = Color(0xFF8E093E),
            onPrimaryContainer = Color(0xFFFFD9E2),
            secondary = Color(0xFFE5BDC7),
            onSecondary = Color(0xFF432931),
            secondaryContainer = Color(0xFF5B3F47),
            onSecondaryContainer = Color(0xFFFFD9E2),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = Color(0xFF191113),
            surface = Color(0xFF1E1517),
            surfaceVariant = Color(0xFF2E2125),
            onSurface = Color(0xFFEBE0E1),
            onSurfaceVariant = Color(0xFFD5C2C6),
            outline = Color(0xFF9E8C90),
            outlineVariant = Color(0xFF2E3842)
        )
    ),

    // 5. Sunset Amber
    PresetThemeColor(
        id = "amber",
        name = "Sunset Amber",
        description = "Warm & gold",
        swatch = Color(0xFF8A5100),
        lightScheme = lightColorScheme(
            primary = Color(0xFF8A5100),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFFFDCBE),
            onPrimaryContainer = Color(0xFF2C1600),
            secondary = Color(0xFF725A42),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFFEDDBE),
            onSecondaryContainer = Color(0xFF291806),
            tertiary = Color(0xFF006D4D),
            onTertiary = Color.White,
            tertiaryContainer = Color(0xFF75F8C6),
            onTertiaryContainer = Color(0xFF002114),
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFFFFB870),
            onPrimary = Color(0xFF4A2800),
            primaryContainer = Color(0xFF693C00),
            onPrimaryContainer = Color(0xFFFFDCBE),
            secondary = Color(0xFFE0C1A3),
            onSecondary = Color(0xFF402D18),
            secondaryContainer = Color(0xFF59432C),
            onSecondaryContainer = Color(0xFFFEDDBE),
            tertiary = Color(0xFF56DBAB),
            onTertiary = Color(0xFF003825),
            tertiaryContainer = Color(0xFF005139),
            onTertiaryContainer = Color(0xFF75F8C6),
            background = Color(0xFF18120C),
            surface = Color(0xFF1D1710),
            surfaceVariant = Color(0xFF2D2319),
            onSurface = Color(0xFFECE0D8),
            onSurfaceVariant = Color(0xFFD4C4B5),
            outline = Color(0xFF9D8E81),
            outlineVariant = Color(0xFF2E3842)
        )
    ),

    // 6. Nordic Teal
    PresetThemeColor(
        id = "teal",
        name = "Nordic Teal",
        description = "Balanced & arctic",
        swatch = Color(0xFF006A6A),
        lightScheme = lightColorScheme(
            primary = Color(0xFF006A6A),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFF9CF1F0),
            onPrimaryContainer = Color(0xFF002020),
            secondary = Color(0xFF4A6363),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFCCE8E7),
            onSecondaryContainer = Color(0xFF051F1F),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF80D5D4),
            onPrimary = Color(0xFF003737),
            primaryContainer = Color(0xFF004F4F),
            onPrimaryContainer = Color(0xFF9CF1F0),
            secondary = Color(0xFFB1CCCC),
            onSecondary = Color(0xFF1C3535),
            secondaryContainer = Color(0xFF324B4B),
            onSecondaryContainer = Color(0xFFCCE8E7),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = Color(0xFF0E1515),
            surface = Color(0xFF131A1A),
            surfaceVariant = Color(0xFF202A2A),
            onSurface = Color(0xFFDEE4E4),
            onSurfaceVariant = Color(0xFFBEC8C8),
            outline = Color(0xFF899392),
            outlineVariant = Color(0xFF2E3842)
        )
    ),

    // 7. Slate Charcoal
    PresetThemeColor(
        id = "charcoal",
        name = "Slate Charcoal",
        description = "Minimalist & sleek",
        swatch = Color(0xFF475569),
        lightScheme = lightColorScheme(
            primary = Color(0xFF475569),
            onPrimary = Color(0xFFFFFFFF),
            primaryContainer = Color(0xFFCBD5E1),
            onPrimaryContainer = Color(0xFF0F172A),
            secondary = Color(0xFF64748B),
            onSecondary = Color(0xFFFFFFFF),
            secondaryContainer = Color(0xFFE2E8F0),
            onSecondaryContainer = Color(0xFF1E293B),
            tertiary = AmberTertiaryLight,
            onTertiary = AmberOnTertiaryLight,
            tertiaryContainer = AmberTertiaryContainerLight,
            onTertiaryContainer = AmberOnTertiaryContainerLight,
            background = Color(0xFFF8FAFC),
            surface = Color(0xFFFFFFFF),
            surfaceVariant = Color(0xFFF1F5F9),
            onSurface = Color(0xFF0F172A),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFF94A3B8),
            outlineVariant = Color(0xFFE2E8F0)
        ),
        darkScheme = darkColorScheme(
            primary = Color(0xFF94A3B8),
            onPrimary = Color(0xFF0F172A),
            primaryContainer = Color(0xFF334155),
            onPrimaryContainer = Color(0xFFE2E8F0),
            secondary = Color(0xFFCBD5E1),
            onSecondary = Color(0xFF1E293B),
            secondaryContainer = Color(0xFF475569),
            onSecondaryContainer = Color(0xFFF1F5F9),
            tertiary = AmberTertiaryDark,
            onTertiary = AmberOnTertiaryDark,
            tertiaryContainer = AmberTertiaryContainerDark,
            onTertiaryContainer = AmberOnTertiaryContainerDark,
            background = Color(0xFF0F172A),
            surface = Color(0xFF1E293B),
            surfaceVariant = Color(0xFF334155),
            onSurface = Color(0xFFF8FAFC),
            onSurfaceVariant = Color(0xFFCBD5E1),
            outline = Color(0xFF64748B),
            outlineVariant = Color(0xFF2E3842)
        )
    )
)
