package io.github.nfdz.cryptool.ui.common

import androidx.annotation.DrawableRes
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp

@Composable
fun IconTextButton(modifier: Modifier = Modifier, label: String, @DrawableRes iconId: Int, onClick: () -> Unit) {
    TextButton(
        modifier = modifier,
        onClick = onClick,
    ) {
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(painter = painterResource(iconId), modifier = Modifier.size(24.dp), contentDescription = null)
            Spacer(modifier = Modifier.size(16.dp))
            Text(label)
        }
    }
}