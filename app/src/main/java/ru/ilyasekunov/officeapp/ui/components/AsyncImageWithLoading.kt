package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.background
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import coil.compose.AsyncImage
import com.valentinilk.shimmer.shimmer
import ru.ilyasekunov.officeapp.ui.modifiers.conditional

@Composable
fun AsyncImageWithLoading(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    var shouldShowShimmer by rememberSaveable { mutableStateOf(false) }
    val shimmerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)

    AsyncImage(
        model = model,
        contentScale = contentScale,
        contentDescription = "image",
        onSuccess = { shouldShowShimmer = false },
        modifier = modifier
            .conditional(shouldShowShimmer) {
                background(shimmerColor)
                shimmer()
            }
    )
}