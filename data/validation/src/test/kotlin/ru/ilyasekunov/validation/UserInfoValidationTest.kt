package ru.ilyasekunov.validation

import assertk.assertThat
import assertk.assertions.isEqualTo
import org.junit.Test
import kotlin.test.assertTrue

class UserInfoValidationTest {
    @Test
    fun whenUserInfoIsBlank_validationErrorBlankIsReturned() {
        val blankUserInfo = ""

        val validationResult = validateUserInfo(blankUserInfo)

        assertTrue(validationResult is UserInfoValidationResult.Failure)
        assertThat(validationResult.error).isEqualTo(UserInfoValidationError.BLANK)
    }

    @Test
    fun whenUserInfoIsCorrect_validationSuccessIsReturned() {
        val correctUserInfo = "user"

        val validationResult = validateUserInfo(correctUserInfo)

        assertTrue(validationResult is UserInfoValidationResult.Success)
    }
}