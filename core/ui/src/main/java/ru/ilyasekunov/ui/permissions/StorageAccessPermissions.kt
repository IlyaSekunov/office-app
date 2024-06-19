package ru.ilyasekunov.ui.permissions

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.core.content.ContextCompat

fun currentStorageAccessPermissions(): Array<String> =
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE -> {
            arrayOf(
                Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED,
                Manifest.permission.READ_MEDIA_IMAGES
            )
        }

        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU -> {
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        }

        else -> arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
    }

fun isPermissionGranted(context: Context, permission: String) =
    ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

fun storageAccessPermissionsGranted(context: Context): Boolean {
    val storageAccessPermissions = currentStorageAccessPermissions()
    return storageAccessPermissions.all { permission -> isPermissionGranted(context, permission) }
}

@Composable
fun rememberStorageAccessPermissionRequest(onResult: (Boolean) -> Unit): () -> Unit {
    val activityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissionsResults ->
        val allPermissionsGranted = permissionsResults.all { it.value }
        onResult(allPermissionsGranted)
    }
    return remember(activityResultLauncher, onResult) {
        {
            val permissionsToRequest = currentStorageAccessPermissions()
            activityResultLauncher.launch(permissionsToRequest)
        }
    }
}