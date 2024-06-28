package ru.ilyasekunov.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class EmailValidationTest {
    @Test
    fun whenEmailIsBlank_validationErrorBlankIsReturned() {
        val blankEmail = ""

        val validationResult = validateEmail(blankEmail)

        assertTrue(validationResult is EmailValidationResult.Failure)
        assertEquals(validationResult.error, EmailValidationError.BLANK)
    }

    @Test
    fun whenEmailIsNotEmailPattern_validationErrorNotEmailPatternIsReturned() {
        val emailThatNotMatchesPattern1 = "abc123"
        val emailThatNotMatchesPattern2 = "abc123@mdasda"

        val validationResult1 = validateEmail(emailThatNotMatchesPattern1)
        val validationResult2 = validateEmail(emailThatNotMatchesPattern2)

        assertTrue(validationResult1 is EmailValidationResult.Failure)
        assertTrue(validationResult2 is EmailValidationResult.Failure)

        assertThat(validationResult1.error).isEqualTo(EmailValidationError.NOT_EMAIL_PATTERN)
        assertThat(validationResult2.error).isEqualTo(EmailValidationError.NOT_EMAIL_PATTERN)
    }

    @Test
    fun whenEmailIsCorrect_validationSuccessIsReturned() {
        val correctEmail = "abc123@mail.ru"

        val validationResult = validateEmail(correctEmail)

        assertTrue(validationResult is EmailValidationResult.Success)
    }
}