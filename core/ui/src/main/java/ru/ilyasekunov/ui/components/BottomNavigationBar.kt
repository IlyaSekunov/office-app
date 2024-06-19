package ru.ilyasekunov.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.navigation.BottomNavigationScreen
import ru.ilyasekunov.navigation.bottomNavigationDestinations
import ru.ilyasekunov.ui.theme.OfficeAppTheme

@Composable
fun BottomNavigationBar(
    selectedScreen: BottomNavigationScreen,
    modifier: Modifier = Modifier,
    navigateToHomeScreen: (() -> Unit)? = null,
    navigateToFavouriteScreen: (() -> Unit)? = null,
    navigateToMyOfficeScreen: (() -> Unit)? = null,
    navigateToProfileScreen: (() -> Unit)? = null
) {
    val borderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
    NavigationBar(
        containerColor = Color.Transparent,
        modifier = modifier
            .drawBehind {
                val strokeWidth = 1.dp.toPx()
                drawLine(
                    color = borderColor,
                    start = Offset(x = 0f, y = -strokeWidth),
                    end = Offset(x = size.width, y = -strokeWidth),
                    strokeWidth = strokeWidth
                )
            }
    ) {
        bottomNavigationDestinations.forEach {
            val onClick = when (it) {
                BottomNavigationScreen.Home -> navigateToHomeScreen
                BottomNavigationScreen.Favourite -> navigateToFavouriteScreen
                BottomNavigationScreen.MyOffice -> navigateToMyOfficeScreen
                BottomNavigationScreen.Profile -> navigateToProfileScreen
            }
            val label = stringResource(it.labelId)

            NavigationBarItem(
                selected = it == selectedScreen,
                onClick = { onClick?.invoke() },
                icon = {
                    Icon(
                        painter = painterResource(it.iconId),
                        contentDescription = label,
                        modifier = Modifier.size(30.dp)
                    )
                },
                label = {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.labelLarge,
                        fontSize = 10.sp
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    indicatorColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    unselectedIconColor = MaterialTheme.colorScheme.surfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.surfaceVariant
                )
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    OfficeAppTheme {
        Surface {
            BottomNavigationBar(
                selectedScreen = BottomNavigationScreen.Home,
                navigateToHomeScreen = {},
                navigateToFavouriteScreen = {},
                navigateToMyOfficeScreen = {},
                navigateToProfileScreen = {}
            )
        }
    }
}