package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.data.model.Office
import ru.ilyasekunov.officeapp.data.officeList
import ru.ilyasekunov.officeapp.navigation.BottomNavigationScreen
import ru.ilyasekunov.officeapp.preview.userInfoPreview
import ru.ilyasekunov.officeapp.ui.components.BottomNavigationBar
import ru.ilyasekunov.officeapp.ui.components.OfficePicker
import ru.ilyasekunov.officeapp.ui.components.PhotoPicker
import ru.ilyasekunov.officeapp.ui.components.UserInfoTextField
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserManageAccountScreen(
    userInfoUiState: UserInfoUiState,
    officeList: List<Office>,
    onPhotoPickerClick: () -> Unit,
    onNameValueChange: (String) -> Unit,
    onSurnameValueChange: (String) -> Unit,
    onJobValueChange: (String) -> Unit,
    onOfficeChange: (Office) -> Unit,
    onSaveButtonClick: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    navigateToFavouriteScreen: () -> Unit,
    navigateToMyOfficeScreen: () -> Unit,
    navigateToProfileScreen: () -> Unit,
    navigateBack: () -> Unit
) {
    val topAppBarScrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        state = rememberTopAppBarState(),
        snapAnimationSpec = spring(
            dampingRatio = Spring.DampingRatioNoBouncy,
            stiffness = Spring.StiffnessMediumLow,
            visibilityThreshold = null
        )
    )
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.manage_account),
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
                scrollBehavior = topAppBarScrollBehavior,
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    scrolledContainerColor = MaterialTheme.colorScheme.background
                )
            )
        },
        bottomBar = {
            BottomNavigationBar(
                selectedScreen = BottomNavigationScreen.Profile,
                navigateToHomeScreen = navigateToHomeScreen,
                navigateToFavouriteScreen = navigateToFavouriteScreen,
                navigateToMyOfficeScreen = navigateToMyOfficeScreen,
                navigateToProfileScreen = navigateToProfileScreen
            )
        },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .nestedScroll(topAppBarScrollBehavior.nestedScrollConnection)
    ) { paddingValues ->  
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(paddingValues)
        ) {
            PhotoPicker(
                selectedPhoto = userInfoUiState.photo,
                onPhotoPickerClick = onPhotoPickerClick,
                modifier = Modifier.size(150.dp)
            )
            Spacer(modifier = Modifier.height(22.dp))
            UserInfoTextField(
                value = userInfoUiState.name,
                label = "Имя",
                placeholder = "Ваше имя",
                onValueChange = onNameValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserInfoTextField(
                value = userInfoUiState.surname,
                label = "Фамилия",
                placeholder = "Ваша фамилия",
                onValueChange = onSurnameValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            UserInfoTextField(
                value = userInfoUiState.job,
                label = "Должность",
                placeholder = "Ваша должность",
                onValueChange = onJobValueChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp, end = 12.dp)
            )
            Spacer(modifier = Modifier.height(20.dp))
            OfficePicker(
                officeList = officeList,
                onOfficeChange = onOfficeChange
            )
            Spacer(modifier = Modifier.height(45.dp))
            SaveButton(
                onClick = onSaveButtonClick,
                modifier = Modifier.size(width = 200.dp, height = 40.dp)
            )
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.save),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun UserManageAccountScreenPreview() {
    OfficeAppTheme {
        UserManageAccountScreen(
            userInfoUiState = userInfoPreview.toUserInfoUiState(),
            officeList = officeList,
            onPhotoPickerClick = {},
            onNameValueChange = {},
            onSurnameValueChange = {},
            onJobValueChange = {},
            onOfficeChange = {},
            onSaveButtonClick = {},
            navigateToHomeScreen = {},
            navigateToFavouriteScreen = {},
            navigateToMyOfficeScreen = {},
            navigateToProfileScreen = {},
            navigateBack = {}
        )
    }
}