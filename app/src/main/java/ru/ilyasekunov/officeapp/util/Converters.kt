package ru.ilyasekunov.officeapp.util

import android.content.ContentResolver
import android.net.Uri
import java.io.File
import java.io.FileOutputStream
import java.time.LocalDateTime

fun LocalDateTime.toRussianString(): String {
    val month = when (monthValue) {
        1 -> "Янв"
        2 -> "Фев"
        3 -> "Мар"
        4 -> "Апр"
        5 -> "Мая"
        6 -> "Июн"
        7 -> "Июл"
        8 -> "Авг"
        9 -> "Сен"
        10 -> "Окт"
        11 -> "Нояб"
        12 -> "Дек"
        else -> throw RuntimeException("Incorrect month value - $monthValue")
    }
    val hour = if (hour < 10) "0$hour" else hour
    val minute = if (minute < 10) "0$minute" else minute
    return "$dayOfMonth $month $year в $hour:$minute"
}

fun Int.toThousandsString(): String =
    if (this >= 1000) {
        val result = (this / 1000f).toString().substring(startIndex = 0, endIndex = 3)
        "${result}к"
    } else {
        this.toString()
    }

fun copyUriContentToFile(source: Uri, destination: File, contentResolver: ContentResolver) {
    contentResolver.openInputStream(source)?.use { input ->
        val outputStream = FileOutputStream(destination)
        outputStream.use { output ->
            val buffer = ByteArray(4 * 1024) // buffer size
            while (true) {
                val byteCount = input.read(buffer)
                if (byteCount < 0) break
                output.write(buffer, 0, byteCount)
            }
            output.flush()
        }
    }
}