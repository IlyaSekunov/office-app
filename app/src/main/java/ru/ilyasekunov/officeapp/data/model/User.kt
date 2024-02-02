package ru.ilyasekunov.officeapp.data.model

data class User(
    val id: Long,
    val email: String,
    val password: String,
    val name: String,
    val surname: String,
    val job: String,
    val photo: ByteArray? = null,
    val office: Office
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as User

        if (id != other.id) return false
        if (email != other.email) return false
        if (password != other.password) return false
        if (name != other.name) return false
        if (surname != other.surname) return false
        if (job != other.job) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false
        return office == other.office
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + email.hashCode()
        result = 31 * result + password.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + job.hashCode()
        result = 31 * result + office.hashCode()
        return result
    }
}