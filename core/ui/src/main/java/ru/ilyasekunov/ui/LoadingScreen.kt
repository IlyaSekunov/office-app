package ru.ilyasekunov.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition
import ru.ilyasekunov.officeapp.core.ui.R

@Composable
fun AnimatedLoadingScreen() {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.RawRes(R.raw.loading_animation_sand_clocks)
    )
    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .size(80.dp)
    )
}

@Composable
fun LoadingScreen(
    modifier: Modifier = Modifier,
    indicatorStrokeWidth: Dp = 3.dp,
    indicatorColor: Color = MaterialTheme.colorScheme.primary,
    indicatorSize: Dp = 30.dp
) {
    CircularProgressIndicator(
        color = indicatorColor,
        strokeWidth = indicatorStrokeWidth,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
            .requiredSize(indicatorSize)
    )
}