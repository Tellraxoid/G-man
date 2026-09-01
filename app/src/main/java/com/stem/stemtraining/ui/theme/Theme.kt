package com.stem.stemtraining.ui.theme
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
private val LightColors = lightColorScheme(primary=Ink,onPrimary=Color.White,primaryContainer=StemGreen,onPrimaryContainer=Ink,secondary=StemGreenDark,secondaryContainer=Color(0xFFE6F7C8),onSecondaryContainer=Ink,background=Canvas,onBackground=Ink,surface=CardLight,onSurface=Ink,surfaceVariant=Color(0xFFEEF0E9),onSurfaceVariant=InkSoft,outline=LineLight,error=Color(0xFFBA1A1A))
private val DarkColors = darkColorScheme(primary=StemGreen,onPrimary=Ink,primaryContainer=Color(0xFF334D0C),onPrimaryContainer=Color(0xFFE3FFC0),secondary=StemGreen,secondaryContainer=Color(0xFF293718),onSecondaryContainer=Color(0xFFE3FFC0),background=DarkCanvas,onBackground=Color(0xFFF0F2EA),surface=DarkCard,onSurface=Color(0xFFF0F2EA),surfaceVariant=Color(0xFF252922),onSurfaceVariant=Color(0xFFC4C9BC),outline=DarkLine)
@Composable fun STEMTrainingTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) { MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, typography = Typography, shapes = Shapes(small = RoundedCornerShape(12.dp), medium = RoundedCornerShape(20.dp), large = RoundedCornerShape(28.dp)), content = content) }
