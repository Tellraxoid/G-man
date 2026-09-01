package com.stem.stemtraining.ui.theme
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
private val font=FontFamily.SansSerif
val Typography = Typography(
    displaySmall = TextStyle(fontFamily=font,fontWeight=FontWeight.Black,fontSize=38.sp,lineHeight=42.sp,letterSpacing=(-1).sp),
    headlineMedium = TextStyle(fontFamily=font,fontWeight=FontWeight.Black,fontSize=30.sp,lineHeight=34.sp,letterSpacing=(-0.6).sp),
    titleLarge = TextStyle(fontFamily=font,fontWeight=FontWeight.Bold,fontSize=22.sp,lineHeight=28.sp),
    titleMedium = TextStyle(fontFamily=font,fontWeight=FontWeight.Bold,fontSize=17.sp,lineHeight=22.sp),
    bodyLarge = TextStyle(fontFamily=font,fontWeight=FontWeight.Normal,fontSize=16.sp,lineHeight=24.sp),
    bodyMedium = TextStyle(fontFamily=font,fontWeight=FontWeight.Normal,fontSize=14.sp,lineHeight=20.sp),
    labelLarge = TextStyle(fontFamily=font,fontWeight=FontWeight.Bold,fontSize=14.sp,lineHeight=18.sp,letterSpacing=0.2.sp),
    labelSmall = TextStyle(fontFamily=font,fontWeight=FontWeight.SemiBold,fontSize=11.sp,lineHeight=15.sp,letterSpacing=0.4.sp)
)
