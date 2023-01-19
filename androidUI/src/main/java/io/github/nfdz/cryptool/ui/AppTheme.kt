package io.github.nfdz.cryptool.ui

import android.os.Build
import androidx.annotation.ChecksSdkIntAtLeast
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val Primary = Color(0xff55619A)
private val PrimaryContainer = Color(0xff222f67)
private val Secondary = Color(0xff006064)
private val SecondaryContainer = Color(0xff003739)

internal val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color.White,
)
internal val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimaryContainer,
    onPrimaryContainer = Color.White,
    secondary = Secondary,
    onSecondary = Color.White,
    secondaryContainer = SecondaryContainer,
    onSecondaryContainer = Color.White,
)

internal val ColorScheme.selectedBackground: Color
    get() = onBackground.copy(alpha = 0.25f)

// Dynamic color is available on Android 12+
@ChecksSdkIntAtLeast(api = Build.VERSION_CODES.S)
private val dynamicColor = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S

@Composable
fun customColorScheme(): ColorScheme {
    return when {
        dynamicColor && isSystemInDarkTheme() -> dynamicDarkColorScheme(LocalContext.current)
        dynamicColor && !isSystemInDarkTheme() -> dynamicLightColorScheme(LocalContext.current)
        isSystemInDarkTheme() -> DarkColorScheme
        else -> LightColorScheme
    }
}

private val Nunito = FontFamily(
    Font(R.font.nunito)
)

private val CustomTypography = Typography().let {
    it.copy(
        headlineLarge = it.headlineLarge.copy(fontFamily = Nunito, fontSize = 30.sp),
        headlineMedium = it.headlineMedium.copy(fontFamily = Nunito, fontSize = 22.sp),
        headlineSmall = it.headlineSmall.copy(fontFamily = Nunito, fontSize = 14.sp),
        titleLarge = it.titleLarge.copy(fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
        titleMedium = it.titleMedium.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
        titleSmall = it.titleSmall.copy(fontSize = 12.5.sp, fontWeight = FontWeight.SemiBold),
        bodyLarge = it.bodyLarge.copy(fontSize = 16.sp),
        bodyMedium = it.bodyLarge.copy(fontSize = 13.5.sp),
        bodySmall = it.bodyLarge.copy(fontSize = 11.sp),
//         labelLarge = TypographyTokens.LabelLarge,
//     labelMedium = TypographyTokens.LabelMedium,
        labelSmall = it.labelSmall.copy(fontSize = 8.sp),
    )
}
//
//val Typography.bodyTiny
//    get() = body1.copy(fontSize = 8.sp)

@Composable
fun AppTheme(
    colorScheme: ColorScheme = customColorScheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = colorScheme,
        typography = CustomTypography,
        content = {
            Surface(content = content)
        }
    )
}