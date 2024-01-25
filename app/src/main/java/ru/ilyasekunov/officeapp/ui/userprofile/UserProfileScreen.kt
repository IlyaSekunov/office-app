package ru.ilyasekunov.officeapp.ui.userprofile

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.preview.userInfoUiStatePreview
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun UserProfileScreen(
    userInfoUiState: UserInfoUiState,
    onManageAccountClick: () -> Unit,
    onMyOfficeClick: () -> Unit,
    onMyIdeasClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        UserInfoSection(
            userInfoUiState = userInfoUiState,
            modifier = Modifier.fillMaxWidth()
        )
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Option(
                icon = painterResource(R.drawable.outline_manage_accounts_24),
                text = stringResource(R.string.manage_account),
                onClick = onManageAccountClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.outline_person_pin_circle_24),
                text = stringResource(R.string.my_office),
                onClick = onMyOfficeClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Option(
                icon = painterResource(R.drawable.outline_lightbulb_24),
                text = stringResource(R.string.my_ideas),
                onClick = onMyIdeasClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 20.dp, top = 10.dp, bottom = 10.dp)
            )
            Divider(
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(start = 16.dp, end = 16.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.log_out),
                style = MaterialTheme.typography.bodyMedium,
                fontSize = 15.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .padding(start = 16.dp)
                    .clickable { onLogoutClick() }
            )
        }
    }
}

@Composable
fun UserInfoSection(
    userInfoUiState: UserInfoUiState,
    modifier: Modifier = Modifier
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(
                RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
            .shadow(
                elevation = 2.dp,
                shape = RoundedCornerShape(
                    bottomStart = 20.dp,
                    bottomEnd = 20.dp
                )
            )
    ) {
        AsyncImage(
            model = userInfoUiState.photo,
            contentDescription = "user_photo",
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .statusBarsPadding()
                .padding(top = 5.dp)
                .size(width = 145.dp, height = 145.dp)
                .clip(MaterialTheme.shapes.extraLarge)
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = MaterialTheme.shapes.extraLarge
                )
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = "${userInfoUiState.name} ${userInfoUiState.surname}",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = userInfoUiState.job,
            style = MaterialTheme.typography.titleMedium,
            fontSize = 15.sp
        )
        Spacer(modifier = Modifier.height(10.dp))
    }
}

@Composable
fun Option(
    icon: Painter,
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clickable { onClick() }
            .then(modifier)
    ) {
        Icon(
            painter = icon,
            contentDescription = "icon",
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(30.dp)
        )
        Spacer(modifier = Modifier.width(15.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun UserInfoSectionPreview() {
    OfficeAppTheme {
        Surface {
            UserInfoSection(
                userInfoUiState = userInfoUiStatePreview
            )
        }
    }
}

@Preview
@Composable
fun UserProfileScreenPreview() {
    OfficeAppTheme {
        Surface {
            UserProfileScreen(
                userInfoUiState = UserInfoUiState(
                    name = "Дмитрий",
                    surname = "Комарницкий",
                    job = "Сотрудник Tinkoff"
                ),
                onManageAccountClick = {},
                onMyOfficeClick = {},
                onMyIdeasClick = {},
                onLogoutClick = {}
            )
        }
    }
}