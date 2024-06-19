package ru.ilyasekunov.ui.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle

data class MenuItemInfo(
    val text: String,
    val onClick: () -> Unit,
    val isSelected: Boolean = false,
    @DrawableRes val leadingIcon: Int? = null
)

data class BasicDropdownMenuColors(
    val selectedTextColor: Color,
    val selectedLeadingIconColor: Color,
    val unselectedTextColor: Color,
    val unselectedLeadingIconColor: Color
)

object BasicDropdownMenuDefaults {
    @Composable
    fun colors(
        selectedTextColor: Color = MaterialTheme.colorScheme.primary,
        selectedLeadingIconColor: Color = MaterialTheme.colorScheme.primary,
        unselectedTextColor: Color = Color.Unspecified,
        unselectedLeadingIconColor: Color = Color.Unspecified
    ) = BasicDropdownMenuColors(
        selectedTextColor = selectedTextColor,
        selectedLeadingIconColor = selectedLeadingIconColor,
        unselectedTextColor = unselectedTextColor,
        unselectedLeadingIconColor = unselectedLeadingIconColor
    )
}

@Composable
fun BasicDropdownMenu(
    expanded: Boolean,
    menuItems: List<MenuItemInfo>,
    onDismissClick: () -> Unit,
    shape: CornerBasedShape,
    containerColor: Color,
    textStyle: TextStyle,
    menuItemPaddings: PaddingValues,
    borderStroke: BorderStroke,
    modifier: Modifier = Modifier,
    colors: BasicDropdownMenuColors = BasicDropdownMenuDefaults.colors()
) {
    Box(modifier = modifier) {
        MaterialTheme(shapes = MaterialTheme.shapes.copy(extraSmall = shape)) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = onDismissClick,
                modifier = modifier
                    .background(containerColor)
                    .border(borderStroke, shape)
            ) {
                menuItems.forEach {
                    DropdownMenuItem(
                        text = {
                            Text(
                                text = it.text,
                                style = textStyle,
                                color = if (it.isSelected) colors.selectedTextColor
                                else colors.unselectedTextColor
                            )
                        },
                        leadingIcon = it.leadingIcon?.let { icon ->
                            {
                                Icon(
                                    painter = painterResource(icon),
                                    contentDescription = "item_leading_icon",
                                    tint = if (it.isSelected) colors.selectedLeadingIconColor
                                    else colors.unselectedLeadingIconColor
                                )
                            }
                        },
                        contentPadding = menuItemPaddings,
                        onClick = it.onClick
                    )
                }
            }
        }
    }
}