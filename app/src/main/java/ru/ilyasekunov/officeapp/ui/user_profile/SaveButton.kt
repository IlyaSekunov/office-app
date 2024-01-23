package ru.ilyasekunov.officeapp.ui.user_profile

import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import ru.ilyasekunov.officeapp.R
import ru.ilyasekunov.officeapp.ui.theme.OfficeAppTheme

@Composable
fun SaveButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        modifier = modifier
    ) {
        Text(
            text = stringResource(R.string.save),
            style = MaterialTheme.typography.bodyLarge,
            fontSize = 16.sp
        )
    }
}

@Preview
@Composable
fun SaveButtonPreview() {
    OfficeAppTheme {
        SaveButton(
            onClick = {}
        )
    }
}