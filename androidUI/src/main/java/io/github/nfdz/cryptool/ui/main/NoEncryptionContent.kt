package io.github.nfdz.cryptool.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.AppTheme

@Composable
@Preview
private fun NoEncryptionContentPreview() {
    AppTheme {
        NoEncryptionContent()
    }
}

@Composable
fun NoEncryptionContent() {
    Column(
        modifier = Modifier
            .padding(34.dp)
            .fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_drawer_empty),
            modifier = Modifier.size(90.dp),
            contentDescription = null,
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.main_empty_label),
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
        )
    }
}