package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer

@Composable
fun AsyncImageWithLoading(
    model: Any?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop
) {
    val asyncImagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(model)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    when (asyncImagePainter.state) {
        is AsyncImagePainter.State.Success -> {
            Image(
                painter = asyncImagePainter,
                contentDescription = "image",
                contentScale = contentScale,
                modifier = modifier
            )
        }

        else -> ImageLoading(modifier)
    }
}

@Composable
private fun ImageLoading(modifier: Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .shimmer()
    )
}