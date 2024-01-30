package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.snapping.rememberSnapFlingBehavior
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.center
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.util.findNearestToCenterOfScreenItem

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OfficePicker(
    officeList: List<Office>,
    onOfficeChange: (Office) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current.density
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

        val officeScrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val nearestToScreenCenterItem by remember {
            derivedStateOf {
                officeScrollState.findNearestToCenterOfScreenItem()
            }
        }
        var layoutCenterDp by remember { mutableStateOf(0.dp) }
        val officeWidthDp = 160.dp
        LazyRow(
            contentPadding = PaddingValues(start = layoutCenterDp, end = layoutCenterDp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            state = officeScrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = officeScrollState),
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned {
                    layoutCenterDp = (it.size.center.x / density).dp - officeWidthDp / 2
                }
        ) {
            items(
                count = officeList.size,
                key = { officeList[it].id }
            ) {
                val isSelected = it == nearestToScreenCenterItem
                if (isSelected) {
                    onOfficeChange(officeList[it])
                }
                Office(
                    office = officeList[it],
                    onClick = {
                        coroutineScope.launch {
                            officeScrollState.animateScrollToItem(it)
                        }
                    },
                    isSelected = isSelected,
                    modifier = Modifier.size(width = officeWidthDp, height = 170.dp)
                )
            }
        }
    }
}

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
            color = officeAddressColor
        )
    }
}