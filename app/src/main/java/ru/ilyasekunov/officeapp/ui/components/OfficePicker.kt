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
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme
import ru.ilyasekunov.officeapp.util.findNearestToCenterOfScreenItem

val officeList = listOf(
    Office(
        id = 0,
        imageUrl = "https://arearent.ru/upload/resize_cache/iblock/7e7/1250_600_1/ed79s3ywxiqn6myw26f70ndbkw23hc85.jpg",
        "ул. Гагарина 6"
    ),
    Office(
        id = 1,
        imageUrl = "https://cdn.inmyroom.ru/uploads/photo/file/a8/a8e4/base_a8e4096a-9249-4fbe-870d-b08d03a78f24.jpg",
        "ул.Коминтерна, д.164"
    ),
    Office(
        id = 2,
        imageUrl = "https://interiorizm.com/wp-content/uploads/2021/02/rabochee-mesto-s-vidom-na-more-interiorizm-05.jpg",
        "ул.Большая Печерская, 5/9"
    ),
    Office(
        id = 3,
        imageUrl = "https://cdn.inmyroom.ru/uploads/photo/file/d5/d5c9/photos_show_big_d5c9a014-519c-4f3d-bd44-bfb7a8d0e52a.jpg",
        "ул.Ковалихинская, д.8"
    ),
    Office(
        id = 4,
        imageUrl = "https://archello.s3.eu-central-1.amazonaws.com/images/2018/02/03/DFBridgesIISHOT07G20-0.1517653034.5896.jpg",
        "ул.Горького, д.146"
    )
)

data class Office(
    val id: Int,
    val imageUrl: String,
    val address: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OfficePicker(
    officeList: List<Office>,
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

        val officeScrollState = rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val nearestToScreenCenterItem by remember {
            derivedStateOf {
                officeScrollState.findNearestToCenterOfScreenItem()
            }
        }
        LazyRow(
            contentPadding = PaddingValues(start = 130.dp, end = 130.dp),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            state = officeScrollState,
            flingBehavior = rememberSnapFlingBehavior(lazyListState = officeScrollState),
            modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.size(width = 160.dp, height = 170.dp)
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

@Preview
@Composable
fun OfficePreview() {
    OfficeAppTheme {
        Surface {
            Office(
                office = officeList[0],
                isSelected = false,
                onClick = {},
                modifier = Modifier.size(width = 160.dp, height = 170.dp)
            )
        }
    }
}

@Preview
@Composable
fun OfficePickerPreview() {
    OfficeAppTheme {
        Surface {
            OfficePicker(
                officeList = officeList,
                onOfficeChange = {}
            )
        }
    }
}