package ru.ilyasekunov.officeapp.ui.home.filters

import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CornerSize
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.model.SortingCategory
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.home.FiltersUiState
import ru.ilyasekunov.officeapp.ui.home.OfficeFilterUiState
import ru.ilyasekunov.officeapp.ui.home.SortingFiltersUiState
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FiltersScreen(
    filtersUiState: FiltersUiState,
    onSortingCategoryClick: (SortingCategory) -> Unit,
    onOfficeFilterClick: (OfficeFilterUiState) -> Unit,
    onResetClick: () -> Unit,
    onShowClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit,
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
                selectedScreen = BottomNavigationScreen.Home,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
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
            Button(
                onClick = onShowClick,
                shape = MaterialTheme.shapes.large,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                contentPadding = PaddingValues(12.dp),
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth()
            ) {
                Text(
                    text = stringResource(R.string.show),
                    style = MaterialTheme.typography.bodyLarge,
                    fontSize = 16.sp
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
                    .clickable { onResetClick() }
            )
        },
        colors = colors,
        modifier = modifier
    )
}

@Composable
fun OfficeFiltersSection(
    officeList: List<OfficeFilterUiState>,
    onOfficeClick: (OfficeFilterUiState) -> Unit,
    officeFilterSize: DpSize,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = stringResource(R.string.offices),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.padding(start = 20.dp)
        )
        Spacer(modifier = Modifier.height(25.dp))
        officeList.forEachIndexed { index, office ->
            OfficeFilter(
                officeFilterUiState = office,
                onOfficeClick = { onOfficeClick(office) },
                modifier = Modifier.size(
                    width = officeFilterSize.width,
                    height = officeFilterSize.height
                )
            )
            if (index != officeList.lastIndex) {
                Spacer(modifier = Modifier.height(18.dp))
            }
        }
    }
}

@Composable
fun OfficeFilter(
    officeFilterUiState: OfficeFilterUiState,
    onOfficeClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val color =
        if (officeFilterUiState.isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant
    val shape = MaterialTheme.shapes.medium
    val density = LocalDensity.current.density
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(MaterialTheme.shapes.medium)
            .border(
                width = 1.dp,
                shape = shape,
                color = color
            )
            .background(color.copy(alpha = 0.15f))
            .clickable { onOfficeClick() }
    ) {
        val imageShape = shape.copy(topEnd = CornerSize(0.dp), bottomEnd = CornerSize(0.dp))
        var imageWidth by remember { mutableStateOf(0.dp) }
        Image(
            painter = rememberAsyncImagePainter(officeFilterUiState.office.imageUrl),
            contentDescription = "office_image",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxHeight()
                .width(imageWidth)
                .clip(imageShape)
                .onGloballyPositioned {
                    imageWidth = (it.size.height / density).dp
                }
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
fun SortingFiltersSection(
    sortingFiltersUiState: SortingFiltersUiState,
    onFilterClick: (SortingCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
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
fun SortingFilters(
    sortingFiltersUiState: SortingFiltersUiState,
    onFilterClick: (SortingCategory) -> Unit,
    modifier: Modifier
) {
    val filters = sortingFiltersUiState.filters
    val selectedCategory = sortingFiltersUiState.selected
    Box {
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )

        val density = LocalDensity.current.density
        var pickedCategoryDividerWidth by remember { mutableStateOf(0.dp) }
        val pickedCategoryDividerOffset =
            (filters.indexOf(selectedCategory) * pickedCategoryDividerWidth.value).dp
        val animatedPickedCategoryDividerOffset by animateIntOffsetAsState(
            targetValue = IntOffset(
                x = (pickedCategoryDividerOffset.value * density).toInt(),
                y = 0
            ),
            animationSpec = tween(
                durationMillis = 100
            ),
            label = "picked_horizontal_divider_offset"
        )
        HorizontalDivider(
            modifier = Modifier
                .width(pickedCategoryDividerWidth)
                .offset(y = (-1).dp)
                .offset { animatedPickedCategoryDividerOffset },
            thickness = 3.dp,
            color = MaterialTheme.colorScheme.primary
        )

        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = modifier
            ) {
                filters.forEach {
                    SortingFilter(
                        sortingCategory = it,
                        isSelected = selectedCategory == it,
                        modifier = Modifier
                            .clickable { onFilterClick(it) }
                            .fillMaxHeight()
                            .weight(1f)
                            .onGloballyPositioned { coordinates ->
                                pickedCategoryDividerWidth = (coordinates.size.width / density).dp
                            }
                    )
                }
            }
            HorizontalDivider(
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Composable
fun SortingFilter(
    sortingCategory: SortingCategory,
    isSelected: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier
    ) {
        if (isSelected) {
            Text(
                text = sortingCategory.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary
            )
        } else {
            Text(
                text = sortingCategory.name,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}

@Preview
@Composable
fun OfficeFilterPreview() {
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