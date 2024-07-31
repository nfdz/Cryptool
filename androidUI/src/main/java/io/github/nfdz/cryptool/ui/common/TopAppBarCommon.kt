package io.github.nfdz.cryptool.ui.common

import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import io.github.nfdz.cryptool.ui.Router
import io.github.nfdz.cryptool.ui.topAppBar

@Composable
fun TopAppBarCommon(
    title: String,
    router: Router?,
    actions: @Composable RowScope.() -> Unit = {},
) {
    TopAppBar(
        navigationIcon = {
            if (router != null) {
                IconButton(onClick = { router.popBackStack() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, stringResource(android.R.string.cancel))
                }
            }
        },
        title = {
            Text(
                title,
                style = MaterialTheme.typography.topAppBar,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        },
        actions = actions,
    )
}