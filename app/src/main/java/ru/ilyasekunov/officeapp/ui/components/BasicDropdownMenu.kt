package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.shape.CornerBasedShape
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle

data class MenuItemInfo(
    val text: String,
    val onClick: () -> Unit
)

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
    modifier: Modifier = Modifier
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
                                style = textStyle
                            )
                        },
                        contentPadding = menuItemPaddings,
                        onClick = it.onClick
                    )
                }
            }
        }
    }
}