package ru.ilyasekunov.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import java.time.LocalDateTime

class LocalDateTimeToStringTest {
    @Test
    fun localDateTimeToRussianStringTest() {
        val date1 = LocalDateTime.of(2024, 8, 5, 6, 5, 4)
        val date2 = LocalDateTime.of(2024, 11, 19, 11, 32, 48)

        assertThat(date1.toRussianString()).isEqualTo("5 Авг 2024 в 06:05")
        assertThat(date2.toRussianString()).isEqualTo("19 Нояб 2024 в 11:32")
    }
}