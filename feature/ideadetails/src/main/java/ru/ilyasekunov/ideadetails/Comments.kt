package ru.ilyasekunov.ideadetails

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer
import ru.ilyasekunov.model.Comment
import ru.ilyasekunov.officeapp.core.ui.R
import ru.ilyasekunov.ui.ErrorScreen
import ru.ilyasekunov.ui.LoadingScreen
import ru.ilyasekunov.ui.RetryButton
import ru.ilyasekunov.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.ui.components.LikesAndDislikesSection
import ru.ilyasekunov.ui.isAppending
import ru.ilyasekunov.ui.isEmpty
import ru.ilyasekunov.ui.isErrorWhileAppending
import ru.ilyasekunov.ui.isErrorWhileRefreshing
import ru.ilyasekunov.ui.isRefreshing
import ru.ilyasekunov.util.toRussianString

fun LazyListScope.comments(
    comments: LazyPagingItems<Comment>,
    onCommentClick: (Comment) -> Unit,
    isPullToRefreshActive: Boolean,
    onCommentLikeClick: (Comment) -> Unit,
    onCommentDislikeClick: (Comment) -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit
) {
    when {
        comments.isRefreshing() && !isPullToRefreshActive -> item {
            LoadingScreen(
                indicatorSize = 24.dp,
                modifier = Modifier.padding(20.dp)
            )
        }

        comments.isErrorWhileRefreshing() -> {
            item {
                ErrorScreen(
                    message = stringResource(R.string.core_ui_error_while_loading),
                    onRetryButtonClick = comments::retry
                )
            }
        }

        comments.isEmpty() && !comments.isRefreshing() -> item {
            CommentsListIsEmpty(modifier = Modifier.padding(10.dp))
        }

        else -> {
            items(
                count = comments.itemCount,
                key = comments.itemKey { it.id }
            ) {
                val comment = comments[it]!!
                Comment(
                    comment = comment,
                    onClick = { onCommentClick(comment) },
                    onLikeClick = { onCommentLikeClick(comment) },
                    onDislikeClick = { onCommentDislikeClick(comment) },
                    navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen
                )
            }
            if (comments.isAppending()) {
                item { LoadingScreen(indicatorSize = 24.dp) }
            }
            if (comments.isErrorWhileAppending()) {
                item {
                    ErrorWhileAppending(
                        message = stringResource(R.string.core_ui_error_while_ideas_loading),
                        onRetryButtonClick = comments::retry,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorWhileAppending(
    message: String,
    onRetryButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        RetryButton(onClick = onRetryButtonClick)
    }
}

@Composable
fun Comment(
    comment: Comment,
    onClick: () -> Unit,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val authorImagePainter = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current)
            .data(comment.author.photo)
            .size(coil.size.Size.ORIGINAL)
            .build()
    )
    val authorImageModifier = Modifier
        .size(50.dp)
        .clip(CircleShape)
        .clickable(
            interactionSource = remember { MutableInteractionSource() },
            indication = null
        ) { navigateToIdeaAuthorScreen(comment.author.id) }

    Row(
        horizontalArrangement = Arrangement.spacedBy(15.dp),
        modifier = modifier
            .clickable(onClick = onClick)
            .padding(10.dp)
    ) {
        when (authorImagePainter.state) {
            is AsyncImagePainter.State.Success -> {
                Image(
                    painter = authorImagePainter,
                    contentDescription = "author_photo",
                    contentScale = ContentScale.Crop,
                    modifier = authorImageModifier
                )
                CommentContent(
                    comment = comment,
                    onLikeClick = onLikeClick,
                    onDislikeClick = onDislikeClick,
                    attachedImageSize = DpSize(width = 125.dp, height = 100.dp),
                    navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen
                )
            }

            else -> CommentIsLoading(authorImageModifier)
        }
    }
}

@Composable
private fun CommentContent(
    comment: Comment,
    onLikeClick: () -> Unit,
    onDislikeClick: () -> Unit,
    attachedImageSize: DpSize,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "${comment.author.name} ${comment.author.surname}",
            fontSize = 12.sp,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier.clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                navigateToIdeaAuthorScreen(comment.author.id)
            }
        )
        Spacer(modifier = Modifier.height(10.dp))
        if (comment.attachedImage != null) {
            AsyncImageWithLoading(
                model = comment.attachedImage,
                modifier = Modifier
                    .size(attachedImageSize)
                    .clip(MaterialTheme.shapes.small)
            )
            Spacer(modifier = Modifier.height(6.dp))
        }
        Text(
            text = comment.content,
            fontSize = 14.sp,
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = comment.date.toRussianString(),
                fontSize = 10.sp,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.surfaceVariant
            )
            LikesAndDislikesSection(
                isLikePressed = comment.isLikePressed,
                likesCount = comment.likesCount,
                onLikeClick = onLikeClick,
                isDislikePressed = comment.isDislikePressed,
                dislikesCount = comment.dislikesCount,
                onDislikeClick = onDislikeClick,
                likesIconSize = 12.dp,
                dislikesIconSize = 12.dp,
                textSize = 10.sp,
                spaceBetweenCategories = 12.dp,
                buttonsWithBackground = false,
                buttonsWithRippleEffect = false
            )
        }
    }
}

@Composable
private fun CommentIsLoading(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            .shimmer()
    )
    Column {
        Box(
            modifier = Modifier
                .size(width = 50.dp, height = 10.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .shimmer()
        )
        Spacer(modifier = Modifier.height(10.dp))
        Box(
            modifier = Modifier
                .size(width = 70.dp, height = 14.dp)
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                .shimmer()
        )
    }
}

@Composable
private fun CommentsListIsEmpty(modifier: Modifier = Modifier) {
    Text(
        text = stringResource(R.string.core_ui_comments_list_is_empty),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}