package ru.ilyasekunov.officeapp.ui.ideadetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.components.BasicDropdownMenu
import ru.ilyasekunov.officeapp.ui.components.BasicDropdownMenuDefaults
import ru.ilyasekunov.officeapp.ui.components.MenuItemInfo

@Composable
fun rememberSelectedCommentMenuState(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
): List<MenuItemInfo> {
    val edit = stringResource(R.string.edit)
    val delete = stringResource(R.string.delete)
    return remember(onEditClick, onDeleteClick) {
        listOf(
            MenuItemInfo(
                text = edit,
                onClick = onEditClick,
                leadingIcon = R.drawable.outline_create_24
            ),
            MenuItemInfo(
                text = delete,
                onClick = onDeleteClick,
                leadingIcon = R.drawable.outline_delete_24
            )
        )
    }
}

@Composable
fun SelectedCommentDropdownMenu(
    expanded: Boolean,
    onDismiss: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedCommentMenuState = rememberSelectedCommentMenuState(
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    )
    BasicDropdownMenu(
        expanded = expanded,
        menuItems = selectedCommentMenuState,
        onDismissClick = onDismiss,
        shape = MaterialTheme.shapes.medium,
        containerColor = MaterialTheme.colorScheme.background,
        textStyle = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
        menuItemPaddings = PaddingValues(13.dp),
        borderStroke = BorderStroke(
            width = 1.dp,
            color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f)
        ),
        colors = BasicDropdownMenuDefaults.colors(
            unselectedLeadingIconColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    )
}