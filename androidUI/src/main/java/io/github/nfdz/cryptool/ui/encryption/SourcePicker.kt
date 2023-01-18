package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import io.github.nfdz.cryptool.ui.R
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.ui.AppTheme

@Composable
@Preview
private fun SourcePickerPreview() {
    AppTheme {
        SourcePicker {}
    }
}

@Composable
fun SourcePicker(modifier: Modifier = Modifier, onPick: (MessageSource) -> Unit) {
    val sources = listOf(
        SourceOptionData(
            title = stringResource(R.string.encryption_source_manual_title),
            description = stringResource(R.string.encryption_source_manual_description),
            icon = Icons.Filled.TouchApp,
            type = MessageSource.MANUAL,
        ),
        SourceOptionData(
            enabled = false,
            title = stringResource(R.string.encryption_source_sms_title),
            description = stringResource(R.string.encryption_source_sms_description),
            icon = Icons.Filled.Message,
            type = MessageSource.MANUAL,
        ),
    )
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        if (maxWidth < 600.dp) {
            SourcePickerColumn(sources, onPick)
        } else {
            SourcePickerGrid(sources, onPick)
        }
    }
}

@Composable
private fun SourcePickerColumn(sources: List<SourceOptionData>, onPick: (MessageSource) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sources.forEach {
            SourceOption(
                modifier = Modifier.weight(1f),
                data = it,
                onPick = onPick,
            )
        }
    }
}

@Composable
private fun SourcePickerGrid(sources: List<SourceOptionData>, onPick: (MessageSource) -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val sourcesRows = sources.chunked(2)
        sourcesRows.forEach {
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                SourceOption(
                    modifier = Modifier.weight(1f),
                    data = it.first(),
                    onPick = onPick,
                )
                SourceOption(
                    modifier = Modifier.weight(1f),
                    data = it.last(),
                    onPick = onPick,
                )
            }
        }
    }
}

@Composable
private fun SourceOption(
    modifier: Modifier,
    data: SourceOptionData,
    onPick: (MessageSource) -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .run {
                if (data.enabled) {
                    clickable {
                        onPick(data.type)
                    }
                } else {
                    alpha(0.4f)
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            data.icon,
            modifier = Modifier
                .size(55.dp)
                .alpha(0.7f),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(data.title, style = MaterialTheme.typography.titleLarge)
            Text(
                data.description,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(220.dp),
            )
        }
    }
}

private class SourceOptionData(
    val enabled: Boolean = true,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val type: MessageSource,
)