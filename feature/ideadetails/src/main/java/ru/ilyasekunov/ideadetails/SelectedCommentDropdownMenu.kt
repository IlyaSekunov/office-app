package ru.ilyasekunov.ideadetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.ui.components.BasicDropdownMenu
import ru.ilyasekunov.ui.components.BasicDropdownMenuDefaults
import ru.ilyasekunov.ui.components.MenuItemInfo
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources

@Composable
fun rememberSelectedCommentMenuState(
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
): List<MenuItemInfo> {
    val edit = stringResource(coreUiResources.string.core_ui_edit)
    val delete = stringResource(coreUiResources.string.core_ui_delete)

    return remember(onEditClick, onDeleteClick) {
        listOf(
            MenuItemInfo(
                text = edit,
                onClick = onEditClick,
                leadingIcon = coreUiResources.drawable.core_ui_outline_create_24
            ),
            MenuItemInfo(
                text = delete,
                onClick = onDeleteClick,
                leadingIcon = coreUiResources.drawable.core_ui_outline_delete_24
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