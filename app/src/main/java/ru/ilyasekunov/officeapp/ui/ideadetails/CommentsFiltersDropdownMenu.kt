package ru.ilyasekunov.officeapp.ui.ideadetails

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import ru.ilyasekunov.officeapp.data.model.CommentsSortingFilters
import ru.ilyasekunov.officeapp.ui.components.BasicDropdownMenu
import ru.ilyasekunov.officeapp.ui.components.MenuItemInfo

@Composable
fun rememberCommentsFiltersState(
    currentCommentsFilter: CommentsSortingFilters,
    onFilterClick: (CommentsSortingFilters) -> Unit
): List<MenuItemInfo> {
    val commentsNewFilter = stringCommentsFilter(CommentsSortingFilters.NEW)
    val commentsOldFilter = stringCommentsFilter(CommentsSortingFilters.OLD)
    val commentsPopularFilter = stringCommentsFilter(CommentsSortingFilters.POPULAR)
    val commentsUnpopularFilter = stringCommentsFilter(CommentsSortingFilters.UNPOPULAR)
    return remember(currentCommentsFilter, onFilterClick) {
        CommentsSortingFilters.entries.map {
            val text = when (it) {
                CommentsSortingFilters.NEW -> commentsNewFilter
                CommentsSortingFilters.OLD -> commentsOldFilter
                CommentsSortingFilters.POPULAR -> commentsPopularFilter
                CommentsSortingFilters.UNPOPULAR -> commentsUnpopularFilter
            }
            MenuItemInfo(
                text = text,
                onClick = { onFilterClick(it) },
                isSelected = currentCommentsFilter == it
            )
        }
    }
}

@Composable
fun CommentsFiltersDropdownMenu(
    currentCommentsFilter: CommentsSortingFilters,
    onFilterClick: (CommentsSortingFilters) -> Unit,
    expanded: Boolean,
    onDismissClick: () -> Unit,
    shape: CornerBasedShape,
    containerColor: Color,
    textStyle: TextStyle,
    menuItemPaddings: PaddingValues,
    borderStroke: BorderStroke,
    modifier: Modifier = Modifier
) {
    val commentsFiltersState = rememberCommentsFiltersState(
        currentCommentsFilter = currentCommentsFilter,
        onFilterClick = onFilterClick
    )
    BasicDropdownMenu(
        expanded = expanded,
        menuItems = commentsFiltersState,
        onDismissClick = onDismissClick,
        shape = shape,
        containerColor = containerColor,
        textStyle = textStyle,
        menuItemPaddings = menuItemPaddings,
        borderStroke = borderStroke,
        modifier = modifier
    )
}