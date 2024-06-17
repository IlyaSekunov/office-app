package ru.ilyasekunov.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import ru.ilyasekunov.ui.components.BasicDropdownMenu
import ru.ilyasekunov.ui.components.MenuItemInfo
import ru.ilyasekunov.officeapp.core.ui.R as coreUiResources

@Composable
private fun rememberIdeaPostMenuItemsState(
    isAuthorPostCurrentUser: Boolean,
    isIdeaSuggestedToMyOffice: Boolean,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    onNavigateToAuthorClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
): List<MenuItemInfo> {
    val suggestIdeaToMyOffice = stringResource(coreUiResources.string.core_ui_suggest_idea_to_my_office)
    val navigateToAuthor = stringResource(coreUiResources.string.core_ui_navigate_to_author)
    val editPost = stringResource(coreUiResources.string.core_ui_edit)
    val deletePost = stringResource(coreUiResources.string.core_ui_delete)

    return remember(
        isAuthorPostCurrentUser,
        onSuggestIdeaToMyOfficeClick,
        onNavigateToAuthorClick,
        onEditClick,
        onDeleteClick
    ) {
        val optionsList = mutableListOf<MenuItemInfo>()
        if (!isIdeaSuggestedToMyOffice) {
            optionsList += MenuItemInfo(
                text = suggestIdeaToMyOffice,
                onClick = onSuggestIdeaToMyOfficeClick
            )
        }
        if (isAuthorPostCurrentUser) {
            optionsList += MenuItemInfo(
                text = editPost,
                onClick = onEditClick
            )
            optionsList += MenuItemInfo(
                text = deletePost,
                onClick = onDeleteClick
            )
        } else {
            optionsList += MenuItemInfo(
                text = navigateToAuthor,
                onClick = onNavigateToAuthorClick
            )
        }
        optionsList
    }
}

@Composable
fun IdeaPostDropdownMenu(
    expanded: Boolean,
    isIdeaSuggestedToMyOffice: Boolean,
    onDismissClick: () -> Unit,
    isAuthorPostCurrentUser: Boolean,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    onNavigateToAuthorClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit,
    shape: CornerBasedShape,
    containerColor: Color,
    textStyle: TextStyle,
    menuItemPaddings: PaddingValues,
    borderStroke: BorderStroke,
    modifier: Modifier = Modifier
) {
    val ideaPostMenuItemsState = rememberIdeaPostMenuItemsState(
        isAuthorPostCurrentUser = isAuthorPostCurrentUser,
        isIdeaSuggestedToMyOffice = isIdeaSuggestedToMyOffice,
        onSuggestIdeaToMyOfficeClick = onSuggestIdeaToMyOfficeClick,
        onNavigateToAuthorClick = onNavigateToAuthorClick,
        onEditClick = onEditClick,
        onDeleteClick = onDeleteClick
    )
    BasicDropdownMenu(
        expanded = expanded,
        menuItems = ideaPostMenuItemsState,
        onDismissClick = onDismissClick,
        shape = shape,
        containerColor = containerColor,
        textStyle = textStyle,
        menuItemPaddings = menuItemPaddings,
        borderStroke = borderStroke,
        modifier = modifier
    )
}