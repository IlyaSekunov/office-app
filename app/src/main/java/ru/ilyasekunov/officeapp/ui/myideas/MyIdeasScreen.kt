package ru.ilyasekunov.officeapp.ui.myideas

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.paging.compose.LazyPagingItems
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.IdeaPost
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.components.BothDirectedPullToRefreshContainer
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.LazyPagingItemsVerticalGrid
import ru.ilyasekunov.officeapp.ui.favouriteideas.FavouriteIdea
import ru.ilyasekunov.officeapp.ui.modifiers.shadow

@Composable
fun MyIdeasScreen(
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: CoroutineScope.() -> Job,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToProfileScreen = navigateToProfileScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen
            )
        },
        topBar = {
            MyIdeasTopAppBar(
                navigateBack = navigateBack,
                modifier = Modifier.fillMaxWidth()
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        MyIdeasScreenContent(
            ideas = ideas,
            onPullToRefresh = onPullToRefresh,
            navigateToIdeaDetailsScreen = navigateToIdeaDetailsScreen,
            navigateToSuggestIdeaScreen = navigateToSuggestIdeaScreen,
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        )
    }
}

@Composable
private fun MyIdeasScreenContent(
    ideas: LazyPagingItems<IdeaPost>,
    onPullToRefresh: CoroutineScope.() -> Job,
    navigateToIdeaDetailsScreen: (Long) -> Unit,
    navigateToSuggestIdeaScreen: () -> Unit,
    modifier: Modifier = Modifier,
    ideaSize: Dp = 100.dp,
) {
    BothDirectedPullToRefreshContainer(
        onRefreshTrigger = onPullToRefresh,
        modifier = modifier
    ) { isRefreshing ->
        LazyPagingItemsVerticalGrid(
            items = ideas,
            isPullToRefreshActive = isRefreshing,
            columns = GridCells.Adaptive(minSize = ideaSize),
            itemKey = { it.id },
            itemsEmptyComposable = {
                NoIdeas(onSuggestIdeaButtonClick = navigateToSuggestIdeaScreen)
            },
            itemComposable = { idea ->
                FavouriteIdea(
                    ideaPost = idea,
                    onClick = { navigateToIdeaDetailsScreen(idea.id) },
                    modifier = Modifier
                        .fillMaxSize()
                        .size(ideaSize)
                )
            },
            itemsAppendComposable = {
                LoadingScreen(
                    modifier = Modifier
                        .size(ideaSize)
                        .wrapContentSize(Alignment.Center)
                )
            },
            errorWhileRefreshComposable = {
                ErrorScreen(
                    message = stringResource(R.string.error_while_ideas_loading),
                    onRetryButtonClick = ideas::retry
                )
            },
            errorWhileAppendComposable = {
                ErrorWhileAppending(
                    message = stringResource(R.string.error_while_ideas_loading),
                    modifier = Modifier.size(ideaSize)
                )
            },
            horizontalArrangement = Arrangement.spacedBy(2.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ErrorWhileAppending(
    message: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = message,
        fontSize = 12.sp,
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.surfaceVariant,
        textAlign = TextAlign.Center,
        modifier = modifier.wrapContentSize(Alignment.Center)
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MyIdeasTopAppBar(
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.my_ideas),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 20.sp
            )
        },
        navigationIcon = {
            IconButton(onClick = navigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                    contentDescription = "back_arrow",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(30.dp)
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier.shadow(blurRadius = 4.dp)
    )
}

@Composable
private fun NoIdeas(
    onSuggestIdeaButtonClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = stringResource(R.string.my_ideas_list_is_empty),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 20.sp,
            color = MaterialTheme.colorScheme.surfaceVariant
        )
        SuggestIdeaButton(onClick = onSuggestIdeaButtonClick)
    }
}

@Composable
private fun SuggestIdeaButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedScale by animateFloatAsState(
        targetValue = if (isPressed) 0.92f else 1f,
        animationSpec = tween(durationMillis = 100),
        label = "animated_scale"
    )
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
            .selectable(
                selected = isPressed,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .scale(animatedScale)
            .clip(MaterialTheme.shapes.medium)
            .background(MaterialTheme.colorScheme.primary)
            .padding(vertical = 6.dp, horizontal = 10.dp)
    ) {
        Text(
            text = stringResource(R.string.suggest_an_idea),
            style = MaterialTheme.typography.labelLarge,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onPrimary
        )
    }
}