package com.meadowlandapps.simplelists.db

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value: Long): Calendar {
        return Calendar.getInstance().apply {
            this.timeInMillis = value
        }
    }

    @TypeConverter
    fun toTimeStamp(value: Calendar): Long {
        return value.timeInMillis
    }
}