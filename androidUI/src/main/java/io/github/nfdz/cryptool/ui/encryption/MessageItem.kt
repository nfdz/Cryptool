package io.github.nfdz.cryptool.ui.encryption

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.rounded.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.shared.message.entity.Message
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.extension.isToday
import io.github.nfdz.cryptool.ui.selectedBackground
import java.text.DateFormat.getDateTimeInstance
import java.text.DateFormat.getTimeInstance
import java.util.*

private const val contentTopSpacing = 12
private const val timestampTopPadding = 2
private const val timestampBottomPadding = 8
private const val contentRoundCorner = 10
private const val contentHorizontalPadding = 16

@Composable
fun MyMessageItem(
    message: Message,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    visibility: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCopyPlainClick: () -> Unit,
    onCopyEncryptedClick: () -> Unit,
) {
    MessageSurface(
        modifier = modifier,
        horizontalArrangement = Arrangement.End,
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        MessageActions(onCopyPlainClick = onCopyPlainClick, onCopyEncryptedClick = onCopyEncryptedClick)
        MyMessageContent(Modifier.weight(1f), message, visibility)
    }
}

@Composable
fun OtherMessageItem(
    message: Message,
    modifier: Modifier = Modifier,
    isSelected: Boolean,
    visibility: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onCopyPlainClick: () -> Unit,
    onCopyEncryptedClick: () -> Unit,
) {
    MessageSurface(
        modifier = modifier,
        horizontalArrangement = Arrangement.Start,
        isSelected = isSelected,
        onClick = onClick,
        onLongClick = onLongClick,
    ) {
        OtherMessageContent(Modifier.weight(1f), message, visibility)
        MessageActions(onCopyPlainClick = onCopyPlainClick, onCopyEncryptedClick = onCopyEncryptedClick)
    }
}

@Composable
private fun MessageSurface(
    modifier: Modifier,
    horizontalArrangement: Arrangement.Horizontal,
    isSelected: Boolean,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    content: @Composable RowScope.() -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .combinedClickable(
                onClick = onClick,
                onLongClick = onLongClick,
            )
            .background(if (isSelected) MaterialTheme.colorScheme.selectedBackground else Color.Transparent)
            .padding(
                vertical = 4.dp,
                horizontal = 2.5.dp,
            ),
        horizontalArrangement = horizontalArrangement,
        verticalAlignment = Alignment.CenterVertically,
        content = content,
    )
}

@Composable
private fun MyMessageContent(modifier: Modifier, message: Message, visibility: Boolean) {
    Column(
        modifier = modifier
            .wrapContentWidth(align = Alignment.End)
            .clip(RoundedCornerShape(contentRoundCorner.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = contentHorizontalPadding.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.End,
    ) {
        Spacer(Modifier.height(contentTopSpacing.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSecondary,
            text = if (visibility) message.message else message.encryptedMessage,
        )
        Text(
            modifier = Modifier.padding(
                top = timestampTopPadding.dp,
                bottom = timestampBottomPadding.dp,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSecondary,
            text = message.timestampInMillis.formatMessageTime(),
            maxLines = 1,
        )
        if (message.isFavorite) {
            FavoriteIcon(
                tint = MaterialTheme.colorScheme.secondary,
                background = MaterialTheme.colorScheme.background,
            )
            Spacer(Modifier.height(timestampBottomPadding.dp))
        }
    }
}

@Composable
private fun OtherMessageContent(modifier: Modifier, message: Message, visibility: Boolean) {
    Column(
        modifier = modifier
            .wrapContentWidth(align = Alignment.Start)
            .clip(RoundedCornerShape(contentRoundCorner.dp))
            .background(MaterialTheme.colorScheme.primary)
            .padding(horizontal = contentHorizontalPadding.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start,
    ) {
        Spacer(Modifier.height(contentTopSpacing.dp))
        Text(
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onPrimary,
            text = if (visibility) message.message else message.encryptedMessage,
        )
        Text(
            modifier = Modifier.padding(
                top = timestampTopPadding.dp,
                bottom = timestampBottomPadding.dp,
            ),
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onPrimary,
            text = message.timestampInMillis.formatMessageTime(),
            maxLines = 1,
        )
        if (message.isFavorite) {
            FavoriteIcon(
                tint = MaterialTheme.colorScheme.primary,
                background = MaterialTheme.colorScheme.background,
            )
            Spacer(Modifier.height(timestampBottomPadding.dp))
        }
    }
}

@Composable
private fun FavoriteIcon(tint: Color, background: Color) {
    Icon(
        imageVector = Icons.Default.Star,
        tint = tint,
        modifier = Modifier
            .clip(CircleShape)
            .background(background)
            .padding(3.dp)
            .size(12.dp),
        contentDescription = stringResource(R.string.encryption_message_favorite_icon_description)
    )
}

@Composable
private fun MessageActions(onCopyPlainClick: () -> Unit, onCopyEncryptedClick: () -> Unit) {
    Box(contentAlignment = Alignment.Center) {
        CopyIcon(onCopyPlainClick = onCopyPlainClick, onCopyEncryptedClick = onCopyEncryptedClick)
    }
}

@Composable
private fun CopyIcon(onCopyPlainClick: () -> Unit, onCopyEncryptedClick: () -> Unit) {
    var showMenu by remember { mutableStateOf(false) }

    IconButton(onClick = {
        showMenu = !showMenu
    }) {
        Icon(
            imageVector = Icons.Rounded.ContentCopy,
            modifier = Modifier
                .size(28.dp)
                .padding(3.dp)
                .alpha(0.6f),
            tint = MaterialTheme.colorScheme.primary,
            contentDescription = stringResource(R.string.encryption_message_copy_icon_description)
        )
    }
    DropdownMenu(
        expanded = showMenu,
        onDismissRequest = {
            showMenu = false
        }
    ) {
        DropdownMenuItem(
            text = { Text(stringResource(R.string.encryption_message_copy_encrypted)) },
            onClick = {
                onCopyEncryptedClick()
                showMenu = false
            },
        )
        DropdownMenuItem(
            text = { Text(stringResource(R.string.encryption_message_copy_plain)) },
            onClick = {
                onCopyPlainClick()
                showMenu = false
            },
        )
    }
}

private fun Long.formatMessageTime(): String {
    val formatter = if (isToday()) getTimeInstance() else getDateTimeInstance()
    return formatter.format(Date(this))
}

@Composable
fun SystemMessageItem(
    message: Message,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 8.dp,
                horizontal = 18.dp,
            ),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = modifier
                .wrapContentWidth(align = Alignment.Start)
                .clip(RoundedCornerShape(contentRoundCorner.dp))
                .background(MaterialTheme.colorScheme.onBackground.copy(alpha = 0.5f))
                .padding(vertical = 4.dp, horizontal = contentHorizontalPadding.dp),
            contentAlignment = Alignment.Center,
        ) {
            Text(
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onPrimary,
                text = message.message,
            )
        }
    }
}
