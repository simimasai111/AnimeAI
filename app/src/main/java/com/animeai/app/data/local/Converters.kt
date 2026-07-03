package com.animeai.app.data.local

import androidx.room.TypeConverter
import com.animeai.app.data.model.MessageContentType
import com.animeai.app.data.model.MessageRole
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromMessageRole(role: MessageRole): String = role.name

    @TypeConverter
    fun toMessageRole(value: String): MessageRole = MessageRole.valueOf(value)

    @TypeConverter
    fun fromMessageContentType(type: MessageContentType): String = type.name

    @TypeConverter
    fun toMessageContentType(value: String): MessageContentType = MessageContentType.valueOf(value)

    @TypeConverter
    fun fromStringList(value: List<String>?): String? {
        return value?.let { gson.toJson(it) }
    }

    @TypeConverter
    fun toStringList(value: String?): List<String>? {
        return value?.let {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(it, type)
        }
    }
}
