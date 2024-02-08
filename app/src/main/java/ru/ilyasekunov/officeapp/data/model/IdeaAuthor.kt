package ru.ilyasekunov.officeapp.data.model

data class IdeaAuthor(
    val id: Long,
    val name: String,
    val surname: String,
    val job: String,
    val photo: ByteArray? = null
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as IdeaAuthor

        if (name != other.name) return false
        if (surname != other.surname) return false
        if (job != other.job) return false
        if (photo != null) {
            if (other.photo == null) return false
            if (!photo.contentEquals(other.photo)) return false
        } else if (other.photo != null) return false

        return true
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + surname.hashCode()
        result = 31 * result + job.hashCode()
        return result
    }
}