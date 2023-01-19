package io.github.nfdz.cryptool.ui.about

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.nfdz.cryptool.shared.core.constant.AppUrl
import io.github.nfdz.cryptool.shared.encryption.entity.AlgorithmVersion
import io.github.nfdz.cryptool.ui.*
import io.github.nfdz.cryptool.ui.R
import io.github.nfdz.cryptool.ui.common.IconTextButton
import io.github.nfdz.cryptool.ui.common.TopAppBarCommon

@Composable
internal fun AlgorithmsScreen(router: Router) {
    val snackbar = remember { SnackbarHostState() }
    AppMessagesEffect(snackbar)
    AlgorithmsScreenContent(snackbar, router)
}

@Composable
@Preview
private fun AlgorithmsScreenPreview() {
    AppTheme {
        AlgorithmsScreenContent(SnackbarHostState(), EmptyRouter)
    }
}

@Composable
internal fun AlgorithmsScreenContent(snackbar: SnackbarHostState, router: Router) {
    var showV1 by remember { mutableStateOf(false) }
    var showV2 by remember { mutableStateOf(false) }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        topBar = {
            TopAppBarCommon(stringResource(R.string.algorithms_topbar_title), router)
        },
        content = { padding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(PaddingValues(28.dp)),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(stringResource(R.string.algorithms_introduction))

                    Spacer(modifier = Modifier.size(12.dp))
                    CryptographySection(AlgorithmVersion.V1.description, showV1) {
                        showV1 = !showV1
                    }
                    if (showV1) {
                        Spacer(modifier = Modifier.size(12.dp))
                        CryptographyV1Description(router)
                        Spacer(modifier = Modifier.size(12.dp))
                    }

                    CryptographySection(AlgorithmVersion.V2.description, showV2) {
                        showV2 = !showV2
                    }
                    if (showV2) {
                        Spacer(modifier = Modifier.size(12.dp))
                        CryptographyV2Description(router)
                        Spacer(modifier = Modifier.size(12.dp))
                    }
                }
            }
        },
    )
}

@Composable
private fun CryptographySection(title: String, state: Boolean, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .clickable(onClick = onClick)
            .padding(12.dp)
    ) {
        if (state) {
            Icon(Icons.Filled.ArrowDropDown, null)
        } else {
            Icon(Icons.Filled.ArrowRight, null)
        }
        Text(
            title,
            style = MaterialTheme.typography.titleLarge,
        )
    }
}

@Composable
private fun CryptographyV1Description(router: Router) {
    Text(stringResource(R.string.algorithms_v1_description))
    Spacer(modifier = Modifier.size(12.dp))
    IconTextButton(
        label = stringResource(R.string.algorithms_see_source_code_action),
        iconId = R.drawable.ic_web_icon,
        onClick = {
            router.navigateToUrl(AppUrl.sourceCodeAlgorithmV1)
        },
    )
}

@Composable
private fun CryptographyV2Description(router: Router) {
    Text(stringResource(R.string.algorithms_v2_description))
    Spacer(modifier = Modifier.size(16.dp))
    Text(
        stringResource(R.string.algorithms_v2_components),
        fontFamily = FontFamily.Monospace,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold
    )
    Spacer(modifier = Modifier.size(12.dp))
    IconTextButton(
        label = stringResource(R.string.algorithms_see_source_code_action),
        iconId = R.drawable.ic_web_icon,
        onClick = {
            router.navigateToUrl(AppUrl.sourceCodeAlgorithmV2)
        },
    )
}