package com.example.flowapp.ui.theme

import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color

// ---------------------------------------------------------------------------
// Brand colours (unchanged from the original theme)
// ---------------------------------------------------------------------------
val BluePrimaryLight = Color(44, 120, 239, 255)
val BluePrimaryDark = Color(57, 120, 227, 255)

// ---------------------------------------------------------------------------
// Light palette
// ---------------------------------------------------------------------------
val Ink = Color(0xFF1B2540)          // main text / icon colour
val PaleBlue = Color(0xFFDCEBFB)     // highlight / primary container
val FieldFill = Color(0xFFE9EDF8)    // text fields, dropdowns
val TicketFill = Color(0xFFD9E2F7)   // ticket / card surfaces

private val LightBackground = Color(0xFFF7F9FE)
private val LightOnSurfaceVariant = Color(0xFF44506E)
private val LightOutline = Color(0xFF7A86A6)
private val LightOutlineVariant = Color(0xFFC3CCE3)

// ---------------------------------------------------------------------------
// Dark palette (dark counterparts of the four colours above)
// ---------------------------------------------------------------------------
val InkDark = Color(0xFFE2E7F5)
val PaleBlueDark = Color(0xFF1E3A6B)
val FieldFillDark = Color(0xFF212A44)
val TicketFillDark = Color(0xFF2B3760)

private val DarkBackground = Color(0xFF0E1322)
private val DarkOnSurfaceVariant = Color(0xFFB4BDD6)
private val DarkOutline = Color(0xFF8791AE)
private val DarkOutlineVariant = Color(0xFF3A4463)

// ---------------------------------------------------------------------------
// Material colour schemes
// ---------------------------------------------------------------------------
val LightColorScheme = lightColorScheme(
    primary = BluePrimaryLight,
    onPrimary = Color.White,
    primaryContainer = PaleBlue,
    onPrimaryContainer = Ink,

    secondary = Color.DarkGray,
    onSecondary = Color.White,
    secondaryContainer = TicketFill,
    onSecondaryContainer = Ink,

    background = LightBackground,
    onBackground = Ink,
    surface = LightBackground,
    onSurface = Ink,
    surfaceVariant = FieldFill,
    onSurfaceVariant = LightOnSurfaceVariant,

    surfaceContainerLowest = Color.White,
    surfaceContainerLow = Color(0xFFF1F4FB),
    surfaceContainer = Color(0xFFEEF1FA),
    surfaceContainerHigh = FieldFill,
    surfaceContainerHighest = TicketFill,

    outline = LightOutline,
    outlineVariant = LightOutlineVariant
)

val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryDark,
    onPrimary = Color.White,
    primaryContainer = PaleBlueDark,
    onPrimaryContainer = PaleBlue,

    secondary = Color.LightGray,
    onSecondary = Color.Black,
    secondaryContainer = TicketFillDark,
    onSecondaryContainer = InkDark,

    background = DarkBackground,
    onBackground = InkDark,
    surface = DarkBackground,
    onSurface = InkDark,
    surfaceVariant = FieldFillDark,
    onSurfaceVariant = DarkOnSurfaceVariant,

    surfaceContainerLowest = Color(0xFF0A0E1A),
    surfaceContainerLow = Color(0xFF121830),
    surfaceContainer = Color(0xFF161D35),
    surfaceContainerHigh = FieldFillDark,
    surfaceContainerHighest = TicketFillDark,

    outline = DarkOutline,
    outlineVariant = DarkOutlineVariant
)

// ---------------------------------------------------------------------------
// Extra app colours that have no direct Material 3 role
// Access with: MaterialTheme.flowColors.fieldFill
// ---------------------------------------------------------------------------
@Immutable
data class FlowExtraColors(
    val ink: Color,
    val paleBlue: Color,
    val fieldFill: Color,
    val ticketFill: Color
)

val LightExtraColors = FlowExtraColors(
    ink = Ink,
    paleBlue = PaleBlue,
    fieldFill = FieldFill,
    ticketFill = TicketFill
)

val DarkExtraColors = FlowExtraColors(
    ink = InkDark,
    paleBlue = PaleBlueDark,
    fieldFill = FieldFillDark,
    ticketFill = TicketFillDark
)

val LocalFlowExtraColors = staticCompositionLocalOf { LightExtraColors }