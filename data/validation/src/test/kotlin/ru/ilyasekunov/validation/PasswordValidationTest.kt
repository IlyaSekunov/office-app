package ru.ilyasekunov.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import kotlin.test.assertTrue

class PasswordValidationTest {
    @Test
    fun whenPasswordIsBlank_validationErrorBlankIsReturned() {
        val blankPassword = ""

        val validationResult = validatePassword(blankPassword)

        assertTrue(validationResult is PasswordValidationResult.Failure)
        assertThat(validationResult.error).isEqualTo(PasswordValidationError.BLANK)
    }

    @Test
    fun whenPasswordIsTooShort_validationErrorTooShortIsReturned() {
        val tooShortPassword1 = "123!"
        val tooShortPassword2 = "Qwerty@"

        val validationResult1 = validatePassword(tooShortPassword1)
        val validationResult2 = validatePassword(tooShortPassword2)

        assertTrue(validationResult1 is PasswordValidationResult.Failure)
        assertTrue(validationResult2 is PasswordValidationResult.Failure)

        assertThat(validationResult1.error).isEqualTo(PasswordValidationError.TOO_SHORT)
        assertThat(validationResult2.error).isEqualTo(PasswordValidationError.TOO_SHORT)
    }

    @Test
    fun whenPasswordNotContainsSpecSymbol_validationErrorNoSpecSymbolsIsReturned() {
        val passwordWithoutSpecSymbol = "Abcd1234"

        val validationResult = validatePassword(passwordWithoutSpecSymbol)

        assertTrue(validationResult is PasswordValidationResult.Failure)
        assertThat(validationResult.error).isEqualTo(PasswordValidationError.NO_SPEC_SYMBOLS)
    }

    @Test
    fun whenPasswordNotContainsCapitalLetter_validationErrorNoCapitalLetterIsReturned() {
        val passwordWithoutCapitalLetter = "abcde123@"

        val validationResult = validatePassword(passwordWithoutCapitalLetter)

        assertTrue(validationResult is PasswordValidationResult.Failure)
        assertThat(validationResult.error).isEqualTo(PasswordValidationError.NO_CAPITAL_LETTER)
    }

    @Test
    fun whenPasswordIsCorrect_validationSuccessIsReturned() {
        val correctPassword = "Abcd123@"

        val validationResult = validatePassword(correctPassword)

        assertTrue(validationResult is PasswordValidationResult.Success)
    }
}