package ru.ilyasekunov.util

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test

class IntToThousandsStringTest {
    @Test
    fun whenNumberIsLessThanThousand_sameNumberIsReturned() {
        val lessThanThousand = 345

        assertThat(lessThanThousand.toThousandsString()).isEqualTo("345")
    }

    @Test
    fun whenNumberIsGreaterThanThousand_firstThreeDigitsWithSuffixAreReturned() {
        val greaterThanThousand1 = 2131
        val greaterThanThousand2 = 117_341_109

        assertThat(greaterThanThousand1.toThousandsString()).isEqualTo("2.1к")
        assertThat(greaterThanThousand2.toThousandsString()).isEqualTo("117к")
    }
}