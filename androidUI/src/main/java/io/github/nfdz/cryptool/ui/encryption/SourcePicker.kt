package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileOpen
import androidx.compose.material.icons.filled.Message
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R

@Composable
@Preview
private fun SourcePickerPreview() {
    AppTheme {
        SourcePicker {}
    }
}

@Composable
fun SourcePicker(modifier: Modifier = Modifier, onPick: (MessageSource) -> Unit) {
    var showSmsDialog by remember { mutableStateOf(false) }
    if (showSmsDialog) {
        SmsSourceDialog { source ->
            showSmsDialog = false
            if (source != null) {
                onPick(source)
            }
        }
    }
    var showFileDialog by remember { mutableStateOf(false) }
    if (showFileDialog) {
        FileSourceDialog { source ->
            showFileDialog = false
            if (source != null) {
                onPick(source)
            }
        }
    }

    val sources = listOf(
        SourceOptionEntry(
            title = stringResource(R.string.encryption_source_manual_title),
            description = stringResource(R.string.encryption_source_manual_description),
            icon = Icons.Filled.TouchApp,
        ) {
            onPick(MessageSource.Manual)
        },
        SourceOptionEntry(
            title = stringResource(R.string.encryption_source_sms_title),
            description = stringResource(R.string.encryption_source_sms_description),
            icon = Icons.Filled.Message,
        ) {
            showSmsDialog = true
        },
        SourceOptionEntry(
            title = "TODO File",
            description = "TODO SMS",
            icon = Icons.Filled.FileOpen,
        ) {
            showFileDialog = true
        },
    )
    BoxWithConstraints(modifier = modifier, contentAlignment = Alignment.Center) {
        if (maxWidth < 600.dp) {
            SourcePickerColumn(sources)
        } else {
            SourcePickerGrid(sources)
        }
    }
}

@Composable
private fun SourcePickerColumn(sources: List<SourceOptionEntry>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        sources.forEach {
            SourceOption(
                modifier = Modifier.weight(1f),
                entry = it,
            )
        }
    }
}

@Composable
private fun SourcePickerGrid(sources: List<SourceOptionEntry>) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val sourcesRows = sources.chunked(3)
        sourcesRows.forEach { row ->
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                val left = row[0]
                SourceOption(
                    modifier = Modifier.weight(1f),
                    entry = left,
                )
                row.getOrNull(1)?.let { right ->
                    SourceOption(
                        modifier = Modifier.weight(1f),
                        entry = right,
                    )
                }
            }
        }
    }
}

@Composable
private fun SourceOption(
    modifier: Modifier,
    entry: SourceOptionEntry,
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .run {
                if (entry.enabled) {
                    clickable {
                        entry.onPick()
                    }
                } else {
                    alpha(0.4f)
                }
            },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            entry.icon,
            modifier = Modifier
                .size(55.dp)
                .alpha(0.7f),
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
        )
        Spacer(Modifier.width(8.dp))
        Column {
            Text(entry.title, style = MaterialTheme.typography.titleLarge)
            Text(
                entry.description,
                style = MaterialTheme.typography.labelMedium,
                modifier = Modifier.width(220.dp),
            )
        }
    }
}

private class SourceOptionEntry(
    val enabled: Boolean = true,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val onPick: () -> Unit,
)