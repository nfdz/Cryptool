package io.github.nfdz.cryptool.ui.common

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.ui.R

@Composable
fun LargeAppIcon() {
    Box(
        modifier = Modifier
            .padding(18.dp)
            .fillMaxWidth(),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .size(90.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.onBackground),
        )
        Icon(
            painter = painterResource(R.drawable.ic_launcher_foreground),
            modifier = Modifier.size(120.dp),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.background,
        )
    }
}