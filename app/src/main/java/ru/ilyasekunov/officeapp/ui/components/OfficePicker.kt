package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.basicMarquee
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.ui.modifiers.conditional

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Office(
    office: Office,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val shape = MaterialTheme.shapes.large
    val selectedColor = MaterialTheme.colorScheme.primary
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImageWithLoading(
            model = office.imageUrl,
            modifier = modifier
                .padding(bottom = 7.dp)
                .clip(shape)
                .clickable(onClick = onClick)
                .conditional(isSelected) {
                    border(
                        width = 4.dp,
                        shape = shape,
                        color = selectedColor
                    )
                }
        )
        if (isSelected) {
            Text(
                text = office.address,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                maxLines = 1,
                color = selectedColor,
                modifier = Modifier.basicMarquee()
            )
        } else {
            Text(
                text = office.address,
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 14.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OfficePicker(
    officeList: List<Office>,
    initialSelectedOffice: Office,
    officeWidth: Dp,
    officeHeight: Dp,
    onOfficeChange: (Office) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.office),
            style = MaterialTheme.typography.titleLarge,
            fontSize = 20.sp
        )
        Spacer(modifier = Modifier.height(36.dp))

        val coroutineScope = rememberCoroutineScope()
        val contentPadding = LocalConfiguration.current.screenWidthDp.dp / 2 - officeWidth / 2

        val pageCount = 100_000
        val itemsCount = officeList.size
        val startPage = pageCount / 2
        val officePagerState = rememberPagerState(
            initialPage = officeList.indexOf(initialSelectedOffice) + startPage,
            pageCount = { pageCount }
        )
        HorizontalPager(
            state = officePagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = 30.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            val officeIndex = page % itemsCount
            Office(
                office = officeList[officeIndex],
                isSelected = officePagerState.currentPage == page,
                onClick = {
                    coroutineScope.launch {
                        officePagerState.animateScrollToPage(
                            page = page,
                            animationSpec = tween()
                        )
                    }
                },
                modifier = Modifier.size(width = officeWidth, height = officeHeight)
            )
        }

        LaunchedEffect(officePagerState) {
            snapshotFlow { officePagerState.currentPage % itemsCount }.collect {
                onOfficeChange(officeList[it])
            }
        }
    }
}