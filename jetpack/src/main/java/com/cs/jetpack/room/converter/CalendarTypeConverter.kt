package com.cs.jetpack.room.converter

import androidx.room.TypeConverter
import java.util.*

class CalendarTypeConverter {

    @TypeConverter
    fun calendarToDateStamp(calendar: Calendar): Long = calendar.timeInMillis


    @TypeConverter
    fun calendarToDateStamp(value: Long): Calendar =
        Calendar.getInstance().apply { timeInMillis = value }
}