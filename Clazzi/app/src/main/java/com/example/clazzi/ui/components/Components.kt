package com.example.clazzi.ui.components

import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat


@Composable
private fun PermissionPickerLauncher(
    permission: String,
    rationale: String,
    onLaunchPicker: () -> Unit,
    onResult: (Boolean) -> Unit = {}
) {
    val context = LocalContext.current
    var showRational by remember { mutableStateOf(false) }
    var launched by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
    ) { isGranted ->
        if (isGranted) {
            onLaunchPicker()
        } else {
            showRational = true
        }
        onResult(isGranted)

    }

    LaunchedEffect(Unit) {
        if (!launched) {
            launched = true
            if (ContextCompat.checkSelfPermission(
                    context,
                    permission
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                onLaunchPicker()
                onResult(true)
            } else {
                permissionLauncher.launch(permission)
            }
        }

    }
    if (showRational) {
        AlertDialog(
            onDismissRequest = { showRational = false },
            title = { Text("권한 요청 없음") },
            text = { Text(rationale) },
            confirmButton = {
                TextButton(onClick = {
                    showRational = false
                }) {
                    Text("확인")
                }
            },

            )
    }


}

@Composable
fun ImagePickerWithPermission(
    onImagePicked: (Uri?) -> Unit
) {
    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent(),
    ) { uri: Uri? ->
        onImagePicked(uri)
    }
    val imagePermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        android.Manifest.permission.READ_MEDIA_IMAGES
    } else {
        android.Manifest.permission.READ_EXTERNAL_STORAGE
    }
    PermissionPickerLauncher(
        permission = imagePermission,
        rationale = "이미지를 선택하려면 갤러리 접근 권한이 필요합니다.",
        onLaunchPicker = { galleryLauncher.launch("image/*") }
    )
}


