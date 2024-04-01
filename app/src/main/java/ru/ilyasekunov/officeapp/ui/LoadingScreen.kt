package ru.ilyasekunov.officeapp.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LoadingScreen() {
    CircularProgressIndicator(
        color = MaterialTheme.colorScheme.primary,
        strokeWidth = 3.dp,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .size(40.dp)
    )
}