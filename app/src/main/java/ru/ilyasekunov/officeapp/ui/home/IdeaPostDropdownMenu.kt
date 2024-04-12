package ru.ilyasekunov.officeapp.ui.home

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.components.BasicDropdownMenu
import ru.ilyasekunov.officeapp.ui.components.MenuItemInfo

@Composable
private fun rememberIdeaPostMenuItemsState(
    isAuthorPostCurrentUser: Boolean,
    onSuggestIdeaToMyOfficeClick: () -> Unit,
    onNavigateToAuthorClick: () -> Unit,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
): List<MenuItemInfo> {
    val suggestIdeaToMyOffice = stringResource(R.string.suggest_idea_to_my_office)
    val navigateToAuthor = stringResource(R.string.navigate_to_author)
    val editPost = stringResource(R.string.edit)
    val deletePost = stringResource(R.string.delete)
    return remember(
        isAuthorPostCurrentUser,
        onSuggestIdeaToMyOfficeClick,
        onNavigateToAuthorClick,
        onEditClick,
        onDeleteClick
    ) {
        if (isAuthorPostCurrentUser)
            listOf(
                MenuItemInfo(
                    text = suggestIdeaToMyOffice,
                    onClick = onSuggestIdeaToMyOfficeClick
                ),
                MenuItemInfo(
                    text = editPost,
                    onClick = onEditClick
                ),
                MenuItemInfo(
                    text = deletePost,
                    onClick = onDeleteClick
                )
            )

        else {
            listOf(
                MenuItemInfo(
                    text = suggestIdeaToMyOffice,
                    onClick = onSuggestIdeaToMyOfficeClick
                ),
                MenuItemInfo(
                    text = navigateToAuthor,
                    onClick = onNavigateToAuthorClick
                )
            )
        }
    }
}

@Composable
fun IdeaPostDropdownMenu(
    expanded: Boolean,
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