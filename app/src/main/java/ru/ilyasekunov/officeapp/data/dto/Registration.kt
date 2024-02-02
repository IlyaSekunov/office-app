package ru.ilyasekunov.officeapp.data.dto

data class RegistrationForm(
    val email: String,
    val password: String,
    val userInfo: UserInfoForm
)

data class UserInfoForm(
    val name: String,
    val surname: String,
    val job: String,
    val photo: ByteArray? = null,
    val officeId: Int
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as UserInfoForm

        if (name != other.name) return false
        if (surname != other.surname) return false
        if (job != other.job) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        return officeId == other.officeId
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + job.hashCode()
        result = 31 * result + officeId
        return result
    }
}