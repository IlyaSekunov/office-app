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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Office(
    office: Office,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier
) {
    val officeImageModifier = if (isSelected)
        modifier
            .clip(MaterialTheme.shapes.large)
            .clickable { onClick() }
            .border(
                width = 4.dp,
                shape = MaterialTheme.shapes.large,
                color = MaterialTheme.colorScheme.primary
            )
    else modifier
        .clip(MaterialTheme.shapes.large)
        .clickable { onClick() }

    val officeAddressColor =
        if (isSelected) MaterialTheme.colorScheme.primary else Color.Unspecified

    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        AsyncImage(
            model = office.imageUrl,
            contentDescription = "office_image",
            contentScale = ContentScale.Crop,
            modifier = officeImageModifier
        )
        Spacer(modifier = Modifier.height(7.dp))
        Text(
            text = office.address,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 14.sp,
            maxLines = 1,
            color = officeAddressColor,
            modifier = Modifier.basicMarquee()
        )
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
        val contentPadding = Dp(LocalConfiguration.current.screenWidthDp.toFloat()) / 2 - officeWidth / 2
        val officePagerState = rememberPagerState(
            initialPage = officeList.indexOf(initialSelectedOffice),
            pageCount = { officeList.size }
        )
        LaunchedEffect(officePagerState) {
            snapshotFlow { officePagerState.currentPage }.collect {
                onOfficeChange(officeList[it])
            }
        }
        HorizontalPager(
            state = officePagerState,
            contentPadding = PaddingValues(horizontal = contentPadding),
            pageSpacing = 30.dp,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Office(
                office = officeList[page],
                isSelected = officePagerState.currentPage == page,
                onClick = {
                    coroutineScope.launch {
                        officePagerState.animateScrollToPage(
                            page = page,
                            animationSpec = tween(durationMillis = 300)
                        )
                    }
                },
                modifier = Modifier.size(width = officeWidth, height = officeHeight)
            )
        }
    }
}