package ru.ilyasekunov.officeapp.util

import android.content.ContentResolver
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import java.io.ByteArrayOutputStream

fun Uri?.toBitmap(contentResolver: ContentResolver): Bitmap? =
    this?.let {
        if (Build.VERSION.SDK_INT < 28) {
            MediaStore.Images.Media.getBitmap(contentResolver, it)
        } else {
            val source = ImageDecoder.createSource(contentResolver, it)
            ImageDecoder.decodeBitmap(source)
        }
    }

fun Bitmap?.toByteArray(): ByteArray? =
    this?.let { bitmap ->
        ByteArrayOutputStream().use {
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
            it.toByteArray()
        }
    }