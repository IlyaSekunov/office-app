package ru.ilyasekunov.officeapp.ui.comments

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.itemKey
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.valentinilk.shimmer.shimmer
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Comment
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.DislikeButton
import ru.ilyasekunov.officeapp.ui.components.LikeButton
import ru.ilyasekunov.officeapp.util.toRussianString

fun LazyListScope.comments(
    comments: LazyPagingItems<Comment>,
    onRetryCommentsLoad: () -> Unit,
    onCommentLikeClick: (comment: Comment, isPressed: Boolean) -> Unit,
    onCommentDislikeClick: (comment: Comment, isPressed: Boolean) -> Unit,
    navigateToIdeaAuthorScreen: (authorId: Long) -> Unit
) {
    when {
        comments.loadState.refresh == LoadState.Loading -> item { LoadingScreen() }
        comments.loadState.hasError -> {
            item {
                ErrorScreen(
                    message = stringResource(R.string.error_while_loading),
                    onRetryButtonClick = onRetryCommentsLoad
                )
            }
        }

        comments.itemCount == 0 -> item { CommentsListIsEmpty() }
        else -> {
            items(
                count = comments.itemCount,
                key = comments.itemKey { it.id }
            ) {
                val comment = comments[it]!!
                Comment(
                    comment = comment,
                    onLikeClick = {
                        onCommentLikeClick(comment, !comment.isLikePressed)
                    },
                    onDislikeClick = {
                        onCommentDislikeClick(comment, !comment.isDislikePressed)
                    },
                    navigateToIdeaAuthorScreen = navigateToIdeaAuthorScreen
                )
            }
            if (comments.loadState.append == LoadState.Loading) {
                item {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary,
                        strokeWidth = 3.dp,
                        modifier = Modifier
                            .fillMaxWidth()
                            .size(20.dp)
                            .wrapContentWidth(Alignment.CenterHorizontally)
                    )
                }
            }
        }
    }
}

@Composable
fun Comment(
    comment: Comment,
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
            .clip(MaterialTheme.shapes.medium)
            .clickable { }
            .padding(10.dp)
    ) {
        when (authorImagePainter.state) {
            is AsyncImagePainter.State.Loading -> CommentIsLoading(authorImageModifier)
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

            else -> {}
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
        text = stringResource(R.string.comments_list_is_empty),
        style = MaterialTheme.typography.bodyLarge,
        fontSize = 20.sp,
        color = MaterialTheme.colorScheme.surfaceVariant,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    )
}

@Composable
fun LikesAndDislikesSection(
    isLikePressed: Boolean,
    likesCount: Int,
    onLikeClick: () -> Unit,
    isDislikePressed: Boolean,
    dislikesCount: Int,
    onDislikeClick: () -> Unit,
    spaceBetweenCategories: Dp,
    likesIconSize: Dp,
    dislikesIconSize: Dp,
    textSize: TextUnit,
    buttonsWithBackground: Boolean,
    buttonsWithRippleEffect: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        LikeButton(
            onClick = onLikeClick,
            iconSize = likesIconSize,
            textSize = textSize,
            isPressed = isLikePressed,
            count = likesCount,
            withBackground = buttonsWithBackground,
            withRippleEffect = buttonsWithRippleEffect
        )
        Spacer(modifier = Modifier.width(spaceBetweenCategories))
        DislikeButton(
            onClick = onDislikeClick,
            iconSize = dislikesIconSize,
            textSize = textSize,
            isPressed = isDislikePressed,
            count = dislikesCount,
            withBackground = buttonsWithBackground,
            withRippleEffect = buttonsWithRippleEffect
        )
    }
}