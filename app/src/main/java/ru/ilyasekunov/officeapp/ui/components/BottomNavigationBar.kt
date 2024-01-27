package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.size
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.navigation.bottomNavigationDestinations
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun BottomNavigationBar(
    selectedScreen: BottomNavigationScreen,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = modifier.navigationBarsPadding()
    ) {
        bottomNavigationDestinations.forEach {
            val onClick = when (it) {
                BottomNavigationScreen.Home -> navigateToHomeScreen
                BottomNavigationScreen.Favourite -> navigateToFavouriteScreen
                BottomNavigationScreen.MyOffice -> navigateToMyOfficeScreen
                BottomNavigationScreen.Profile -> navigateToProfileScreen
            }
            val label = stringResource(it.labelId)
            BottomNavigationItem(
                selected = selectedScreen == it,
                onClick = onClick,
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
                selectedContentColor = MaterialTheme.colorScheme.primary,
                unselectedContentColor = MaterialTheme.colorScheme.surfaceVariant
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    OfficeAppTheme {
        BottomNavigationBar(
            selectedScreen = BottomNavigationScreen.Home,
            navigateToHomeScreen = {},
            navigateToFavouriteScreen = {},
            navigateToMyOfficeScreen = {},
            navigateToProfileScreen = {}
        )
    }
}