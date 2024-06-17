package ru.ilyasekunov.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import ru.ilyasekunov.officeapp.core.ui.R

@Composable
fun CloseIconButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.clickable(onClick = onClick)
    ) {
        Icon(
            painter = painterResource(R.drawable.core_ui_baseline_close_24),
            contentDescription = "close_icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier
                .padding(4.dp)
                .fillMaxSize()
        )
    }
}