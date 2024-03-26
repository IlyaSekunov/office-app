package ru.ilyasekunov.officeapp.data.serialize

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.Locale

class JsonLocalDateTimeDeserializer : JsonDeserializer<LocalDateTime> {
    override fun deserialize(
        json: JsonElement?,
        typeOfT: Type?,
        context: JsonDeserializationContext?
    ): LocalDateTime {
        return LocalDateTime.parse(
            json!!.asString,
            DateTimeFormatter
                .ISO_DATE_TIME
                .withLocale(Locale.getDefault())
        )
    }
}