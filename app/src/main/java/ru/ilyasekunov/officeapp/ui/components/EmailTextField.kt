package ru.ilyasekunov.officeapp.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material3.Icon
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
fun EmailTextField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        leadingIcon = {
            Icon(
                imageVector = Icons.Outlined.Email,
                contentDescription = "email_icon"
            )
        },
        textStyle = MaterialTheme.typography.bodySmall.copy(fontSize = 16.sp),
        singleLine = true,
        placeholder = {
            Text(
                text = "Email",
                style = MaterialTheme.typography.bodySmall,
                fontSize = 16.sp
            )
        },
        colors = OutlinedTextFieldDefaults.colors(
            cursorColor = MaterialTheme.colorScheme.primary,
            focusedBorderColor = MaterialTheme.colorScheme.primary,
            focusedLeadingIconColor = MaterialTheme.colorScheme.primary,
            focusedPlaceholderColor = MaterialTheme.colorScheme.primary,
            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.6f),
            unfocusedLeadingIconColor = MaterialTheme.colorScheme.outline,
            unfocusedPlaceholderColor = MaterialTheme.colorScheme.outline
        ),
        modifier = modifier
    )
}

@Preview
@Composable
fun EmailTextFieldPreview() {
    OfficeAppTheme {
        EmailTextField(value = "", onValueChange = {})
    }
}