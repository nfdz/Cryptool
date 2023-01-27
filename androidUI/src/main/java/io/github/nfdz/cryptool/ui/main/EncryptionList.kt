package io.github.nfdz.cryptool.ui.main

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.shared.encryption.entity.Encryption
import io.github.nfdz.cryptool.shared.encryption.entity.MessageSource
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.isToday
import io.github.nfdz.cryptool.ui.selectedBackground
import java.text.DateFormat.getDateInstance
import java.text.DateFormat.getTimeInstance
import java.util.*

@Composable
@Preview
private fun EncryptionListPreview() {
    val encryption = (1..4).map {
        Encryption(
            id = "$it",
            name = "My conversation $it",
            password = "123",
            algorithm = AlgorithmVersion.V1,
            MessageSource.Manual,
            unreadMessagesCount = (it - 1) * 4,
            lastMessage = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.",
            lastMessageTimestamp = 987688696768,
            isFavorite = it == 1
        )
    }
    val selectedEncryptions = setOf(
        encryption[1].id
    )
    AppTheme {
        EncryptionList(
            modifier = Modifier,
            encryptions = encryption,
            selectedEncryptionIds = selectedEncryptions,
            onClick = {},
            onLongClick = {}
        )
    }
}

@Composable
fun EncryptionList(
    modifier: Modifier,
    encryptions: List<Encryption>,
    selectedEncryptionIds: Set<String>,
    onClick: (Encryption) -> Unit,
    onLongClick: (Encryption) -> Unit
) {
    LazyColumn(
        modifier = modifier,
    ) {
        itemsIndexed(encryptions) { _, encryption ->
            EncryptionItem(
                encryption,
                isSelected = selectedEncryptionIds.contains(encryption.id),
                onClick = { onClick(encryption) },
                onLongClick = { onLongClick(encryption) }
            )
        }
    }
}

@Composable
private fun EncryptionItem(
    encryption: Encryption,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {
    Row(
        modifier
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .background(if (isSelected) MaterialTheme.colorScheme.selectedBackground else Color.Transparent)
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                style = MaterialTheme.typography.titleMedium,
                text = encryption.name,
            )
            Text(
                style = MaterialTheme.typography.labelMedium,
                text = encryption.lastMessage,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(4.dp))
        Column(
            Modifier.sizeIn(minHeight = 36.dp),
            horizontalAlignment = Alignment.End,
        ) {
            if (encryption.lastMessageTimestamp > 0) {
                Text(
                    style = MaterialTheme.typography.labelSmall,
                    text = encryption.lastMessageTimestamp.formatConversationTime(),
                    maxLines = 1,
                )
            }
            Spacer(Modifier.weight(1f))
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (encryption.unreadMessagesCount > 0) {
                    Text(
                        text = encryption.unreadMessagesCount.toString(),
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        style = MaterialTheme.typography.labelMedium,
                        modifier = Modifier
                            .background(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = CircleShape
                            )
                            .padding(
                                vertical = 1.dp,
                                horizontal = 5.dp
                            )
                    )
                }
                if (encryption.isFavorite) {
                    Icon(
                        Icons.Default.Star,
                        modifier = Modifier.size(16.dp),
                        contentDescription = stringResource(R.string.main_encryption_favorite_icon_description)
                    )
                }
            }
        }
    }
}

private fun Long.formatConversationTime(): String {
    val formatter = if (isToday()) getTimeInstance() else getDateInstance()
    return formatter.format(Date(this))
}
