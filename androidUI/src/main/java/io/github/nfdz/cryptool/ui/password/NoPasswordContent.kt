package io.github.nfdz.cryptool.ui.password

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
import io.github.nfdz.cryptool.ui.AppTheme
import io.github.nfdz.cryptool.ui.R

@Composable
@Preview
private fun NoPasswordContentPreview() {
    AppTheme {
        NoPasswordContent()
    }
}

@Composable
fun NoPasswordContent() {
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
            contentDescription = null
        )
        Spacer(Modifier.height(8.dp))
        Text(
            stringResource(R.string.password_empty_label),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}