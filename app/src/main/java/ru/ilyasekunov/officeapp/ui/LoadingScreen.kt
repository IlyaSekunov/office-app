package ru.ilyasekunov.officeapp.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp

@Composable
fun LoadingScreen(
    circularProgressingColor: Color,
    circularProgressingWidth: Dp,
    circularProgressingSize: Dp,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = circularProgressingColor,
            strokeWidth = circularProgressingWidth,
            modifier = Modifier.size(circularProgressingSize)
        )
    }
}