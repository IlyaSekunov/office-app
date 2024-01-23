package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun UserInfoTextField(
    value: String,
    label: String,
    placeholder: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        singleLine = true,
        label = {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        },
        placeholder = {
            Text(
                text = placeholder,
                style = MaterialTheme.typography.labelMedium,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.outline
            )
        },
        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
        shape = MaterialTheme.shapes.small,
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedLabelColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
            unfocusedLabelColor = MaterialTheme.colorScheme.outline,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun UserInfoTextFieldPreview() {
    OfficeAppTheme {
        UserInfoTextField(
            value = "",
            label = "Имя",
            placeholder = "Ваше имя",
            onValueChange = {}
        )
    }
}