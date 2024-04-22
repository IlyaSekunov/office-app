package ru.ilyasekunov.officeapp.ui.filters

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.ui.ErrorScreen
import ru.ilyasekunov.officeapp.ui.LoadingScreen
import ru.ilyasekunov.officeapp.ui.LocalCurrentNavigationBarScreen
import ru.ilyasekunov.officeapp.ui.components.AsyncImageWithLoading
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.rememberCircleClickEffectIndication
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.SortingFiltersUiState
import ru.ilyasekunov.officeapp.ui.home.sortingCategoryName
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun FiltersScreen(
    filtersUiState: FiltersUiState,
    onSortingCategoryClick: (SortingCategory) -> Unit,
    onOfficeFilterClick: (OfficeFilterUiState) -> Unit,
    onResetClick: () -> Unit,
    onShowClick: () -> Unit,
    onRetryLoad: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    when {
        filtersUiState.isLoading -> LoadingScreen()
        else -> FiltersScreenContent(
            filtersUiState = filtersUiState,
            onSortingCategoryClick = onSortingCategoryClick,
            onOfficeFilterClick = onOfficeFilterClick,
            onResetClick = onResetClick,
            onShowClick = onShowClick,
            onRetryLoad = onRetryLoad,
            navigateToHomeScreen = navigateToHomeScreen,
            navigateToFavouriteScreen = navigateToFavouriteScreen,
            navigateToMyOfficeScreen = navigateToMyOfficeScreen,
            navigateToProfileScreen = navigateToProfileScreen,
            navigateBack = navigateBack,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FiltersScreenContent(
    filtersUiState: FiltersUiState,
    onSortingCategoryClick: (SortingCategory) -> Unit,
    onOfficeFilterClick: (OfficeFilterUiState) -> Unit,
    onResetClick: () -> Unit,
    onShowClick: () -> Unit,
    onRetryLoad: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        topBar = {
            FiltersTopAppBar(
                navigateBack = navigateBack,
                onResetClick = onResetClick,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = LocalCurrentNavigationBarScreen.current,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        },
        modifier = modifier
    ) { paddingValues ->
        if (filtersUiState.isErrorWhileLoading) {
            ErrorScreen(
                message = stringResource(R.string.error_connecting_to_server),
                onRetryButtonClick = onRetryLoad
            )
        } else {
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(top = 5.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OfficeFiltersSection(
                    officeList = filtersUiState.officeFiltersUiState,
                    onOfficeClick = onOfficeFilterClick,
                    officeFilterSize = DpSize(width = 345.dp, height = 80.dp),
                    modifier = Modifier.padding(start = 20.dp, end = 20.dp)
                )
                Spacer(modifier = Modifier.height(22.dp))
                SortingFiltersSection(
                    sortingFiltersUiState = filtersUiState.sortingFiltersUiState,
                    onFilterClick = onSortingCategoryClick
                )
                Spacer(modifier = Modifier.height(46.dp))
                ApplyFiltersButton(
                    onClick = onShowClick,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersTopAppBar(
    navigateBack: () -> Unit,
    onResetClick: () -> Unit,
    colors: TopAppBarColors,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = {
            Text(
                text = stringResource(R.string.search_for_best_ideas),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp
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
        actions = {
            Text(
                text = stringResource(R.string.discard),
                style = MaterialTheme.typography.titleLarge,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier
                    .padding(end = 20.dp)
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = rememberCircleClickEffectIndication(),
                        onClick = onResetClick
                    )
            )
        },
        colors = colors,
        modifier = modifier
    )
}

@Composable
private fun OfficeFiltersSection(
    officeList: List<OfficeFilterUiState>,
    onOfficeClick: (OfficeFilterUiState) -> Unit,
    officeFilterSize: DpSize,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(18.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.offices),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 20.dp, bottom = 7.dp)
        )
        officeList.forEach {
            OfficeFilter(
                officeFilterUiState = it,
                onOfficeClick = { onOfficeClick(it) },
                modifier = Modifier.size(
                    width = officeFilterSize.width,
                    height = officeFilterSize.height
                )
            )
        }
    }
}

@Composable
private fun OfficeFilter(
    officeFilterUiState: OfficeFilterUiState,
    onOfficeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color = if (officeFilterUiState.isSelected)
        MaterialTheme.colorScheme.primary
    else
        MaterialTheme.colorScheme.surfaceVariant
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                shape = MaterialTheme.shapes.medium,
                color = color
            )
            .background(color.copy(alpha = 0.15f))
            .clickable(onClick = onOfficeClick)
    ) {
        AsyncImageWithLoading(
            model = officeFilterUiState.office.imageUrl,
            modifier = Modifier
                .fillMaxHeight()
                .aspectRatio(1f / 1f)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = officeFilterUiState.office.address,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp,
            modifier = Modifier.weight(0.85f)
        )
        Spacer(modifier = Modifier.weight(0.15f))
        Checkbox(
            checked = officeFilterUiState.isSelected,
            onCheckedChange = null,
            colors = CheckboxDefaults.colors(
                checkmarkColor = Color.White,
                checkedColor = MaterialTheme.colorScheme.primary,
                uncheckedColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            modifier = Modifier.padding(end = 20.dp)
        )
    }
}

@Composable
private fun SortingFiltersSection(
    sortingFiltersUiState: SortingFiltersUiState,
    onFilterClick: (SortingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.sort_by),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 40.dp)
        )
        Spacer(modifier = Modifier.height(14.dp))
        SortingFilters(
            sortingFiltersUiState = sortingFiltersUiState,
            onFilterClick = onFilterClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp)
        )
    }
}

@Composable
private fun SortingFilters(
    sortingFiltersUiState: SortingFiltersUiState,
    onFilterClick: (SortingCategory) -> Unit,
    modifier: Modifier
) {
    Box {
        val filters = sortingFiltersUiState.filters
        val currentSelectedFilter = sortingFiltersUiState.selected
        val density = LocalDensity.current
        var sortingFilterWidth by remember { mutableStateOf(0.dp) }
        Column {
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
                    .onSizeChanged { size ->
                        sortingFilterWidth =
                            calculateSortingFilterWidth(size, filters.size, density)
                    }
            ) {
                filters.forEach {
                    SortingFilter(
                        sortingCategory = it,
                        isSelected = currentSelectedFilter == it,
                        modifier = Modifier
                            .clickable { onFilterClick(it) }
                            .fillMaxHeight()
                            .weight(1f)
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        }
        CurrentSelectedFilterDivider(
            width = sortingFilterWidth,
            filters = filters,
            currentSelectedFilter = currentSelectedFilter,
            modifier = Modifier.offset(y = (-1).dp)
        )
    }
}

@Composable
private fun CurrentSelectedFilterDivider(
    width: Dp,
    filters: List<SortingCategory>,
    currentSelectedFilter: SortingCategory?,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val pickedCategoryDividerOffset = remember(filters, currentSelectedFilter, width) {
        (filters.indexOf(currentSelectedFilter) * width.value).dp
    }
    val animatedPickedCategoryDividerOffset by animateIntOffsetAsState(
        targetValue = IntOffset(
            x = with(density) { pickedCategoryDividerOffset.toPx() }.toInt(),
            y = 0
        ),
        animationSpec = tween(durationMillis = 100),
        label = "horizontal_divider_offset"
    )
    HorizontalDivider(
        modifier = modifier
            .width(width)
            .offset { animatedPickedCategoryDividerOffset },
        thickness = 3.dp,
        color = MaterialTheme.colorScheme.primary
    )
}

@Composable
private fun SortingFilter(
    sortingCategory: SortingCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    val sortingCategoryName = sortingCategoryName(sortingCategory)
    val sortingFilterColor =
        if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline
    Text(
        text = sortingCategoryName,
        style = MaterialTheme.typography.bodyMedium,
        fontWeight = FontWeight.Bold,
        fontSize = 14.sp,
        textAlign = TextAlign.Center,
        color = sortingFilterColor,
        modifier = modifier.wrapContentSize(align = Alignment.Center)
    )
}

@Composable
private fun ApplyFiltersButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        shape = MaterialTheme.shapes.large,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        contentPadding = PaddingValues(12.dp),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.show),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

private fun calculateSortingFilterWidth(
    rowSize: IntSize,
    actualFiltersCount: Int,
    density: Density
): Dp {
    val rowWidthPx = rowSize.width
    val filtersCount = actualFiltersCount.coerceAtLeast(1)
    val oneFilterWidthPx = rowWidthPx / filtersCount
    return with(density) { oneFilterWidthPx.toDp() }
}

@Preview
@Composable
private fun OfficeFilterPreview() {
    OfficeAppTheme {
        OfficeFilter(
            officeFilterUiState = OfficeFilterUiState(
                office = Office(
                    id = 0,
                    imageUrl = "",
                    address = "Пушкинская"
                )
            ),
            onOfficeClick = {},
            modifier = Modifier.size(width = 325.dp, height = 80.dp)
        )
    }
}