package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.shared.message.entity.MessageOwnership
import io.github.nfdz.cryptool.ui.AppTheme

@Composable
@Preview
private fun MessageListPreview() {
    val messages = (1..4).map {
        Message(
            id = "$it",
            encryptionId = "11",
            message = "Hello $it, ".let { text ->
                val bld = StringBuilder(text)
                (0..it).forEach { _ ->
                    bld.append(text)
                }
                bld.toString()
            },
            encryptedMessage = "fwofwfwofewjof3944ufb3fojfb3asfd;klsdfsdjf;4flknwnkllwfklr",
            timestampInMillis = 987688696768,
            ownership = if (it == 1) MessageOwnership.OTHER else MessageOwnership.OWN,
            isFavorite = it == 1 || it == 3
        )
    }.toMutableList().apply {
        add(
            2,
            Message(
                id = "00000",
                encryptionId = "11",
                message = "Name: 'Lorem' ➡️ 'Ipsum'",
                encryptedMessage = "",
                timestampInMillis = 987688696768,
                ownership = MessageOwnership.SYSTEM,
                isFavorite = false,
            )
        )
    }
    val selectedMessageIds = setOf(
        messages[1].id
    )
    AppTheme {
        MessageList(
            modifier = Modifier,
            messages = messages,
            visibility = true,
            favoritesOnly = false,
            selectedMessageIds = selectedMessageIds,
            searchResultMessageIds = emptySet(),
            onClick = {},
            onLongClick = {},
            onCopyClick = {},
        )
    }
}

@Composable
fun MessageList(
    modifier: Modifier = Modifier,
    messages: List<Message>,
    selectedMessageIds: Set<String>,
    searchResultMessageIds: Set<String>?,
    visibility: Boolean,
    favoritesOnly: Boolean,
    onClick: (Message) -> Unit,
    onLongClick: (Message) -> Unit,
    onCopyClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = modifier,
        reverseLayout = true,
    ) {
        val content = when {
            favoritesOnly -> messages.filter { it.isFavorite }
            searchResultMessageIds != null -> messages.filter { searchResultMessageIds.contains(it.id) }
            else -> messages
        }
        itemsIndexed(content) { _, message ->
            val onCopyEncryptedClick: () -> Unit = {
                onCopyClick(message.encryptedMessage)
            }
            val onCopyPlainClick: () -> Unit = {
                onCopyClick(message.message)
            }
            when (message.ownership) {
                MessageOwnership.OWN -> MyMessageItem(
                    message,
                    isSelected = selectedMessageIds.contains(message.id),
                    visibility = visibility,
                    onClick = { onClick(message) },
                    onLongClick = { onLongClick(message) },
                    onCopyEncryptedClick = onCopyEncryptedClick,
                    onCopyPlainClick = onCopyPlainClick,
                )

                MessageOwnership.OTHER -> OtherMessageItem(
                    message,
                    isSelected = selectedMessageIds.contains(message.id),
                    visibility = visibility,
                    onClick = { onClick(message) },
                    onLongClick = { onLongClick(message) },
                    onCopyEncryptedClick = onCopyEncryptedClick,
                    onCopyPlainClick = onCopyPlainClick,
                )

                MessageOwnership.SYSTEM -> SystemMessageItem(
                    message,
                )
            }
        }
    }
}
