package io.github.nfdz.cryptool.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.ui.AppTheme

@Composable
internal fun PickEncryptionDialog(
    incomingData: String,
    encryptions: List<Encryption>,
    callback: (Encryption?) -> Unit
) {
    Dialog(onDismissRequest = { callback(null) }, content = {
        PickEncryptionDialogContent(incomingData, encryptions, callback)
    })
}

@Composable
@Preview
private fun PickEncryptionDialogPreview() {
    AppTheme {
        PickEncryptionDialogContent(
            "-99Vk91QTKxp8M91puXQjqwJ5Mw.fDcHFT5r2nFIpS0nq28PQQ.128.rbbgZVnc9fHyghE7KHTV2L_t_pMgmulmWLYl9HrCqmV8IhdnXiIUmA",
            listOf(
                Encryption("", "Conversation 1", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
                Encryption("", "Conversation 2", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
                Encryption("", "Conversation 3", "", AlgorithmVersion.V2, null, false, 0, "", 0L),
            )
        ) {}
    }
}

@Composable
internal fun PickEncryptionDialogContent(
    incomingData: String,
    encryptions: List<Encryption>,
    callback: (Encryption?) -> Unit
) {
    Column(
        modifier = Modifier.background(MaterialTheme.colorScheme.background, shape = MaterialTheme.shapes.medium),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            modifier = Modifier.padding(16.dp),
            style = MaterialTheme.typography.labelSmall,
            text = incomingData,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
        )
        Icon(
            Icons.Default.ArrowDownward,
            contentDescription = null,
        )
        LazyColumn {
            items(encryptions) {
                Row(
                    Modifier
                        .clickable { callback(it) }
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 24.dp)) {
                    Text(
                        modifier = Modifier.weight(1f),
                        style = MaterialTheme.typography.titleMedium,
                        text = it.name,
                    )
                }
            }
        }
    }
}
