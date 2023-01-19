package io.github.nfdz.cryptool.ui.test

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material3.ColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.ui.AppTheme

@Composable
internal fun TestEntry(
    colorScheme: ColorScheme,
    content: @Composable () -> Unit,
) {
    Box(
        Modifier
            .heightIn(max = 700.dp)
            .widthIn(max = 380.dp)
    ) {
        AppTheme(colorScheme = colorScheme, content = content)
    }
}