package com.example.chatapp.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

fun getMessageDay(timestamp:Long):String {
    val messageDate = Date(timestamp)  // كائن يحول الارقام الي تاريخ

    val messageCalendar = Calendar.getInstance() // تقويم يحلل التاريخ الي يوم شهر سنة ساعة دقائق
    messageCalendar.time = messageDate

    val todayCalendar = Calendar.getInstance() // تقويم يرجع تاريخ الوقت الحالي

    val isSameDay =  // مقارنة تاريخ الرسالة بتاريخ اليوم اذا صح يرجع today
        messageCalendar.get(Calendar.YEAR) == todayCalendar.get(Calendar.YEAR) && messageCalendar.get(Calendar.DAY_OF_YEAR) == todayCalendar.get(Calendar.DAY_OF_YEAR)
    if (isSameDay) {
        return "Today"
    }
    val yesterdayCalendar = Calendar.getInstance() // تقويم يرجع تاريخ امس
    yesterdayCalendar.add(Calendar.DAY_OF_YEAR, -1)

    val isYesterday =
        messageCalendar.get(Calendar.YEAR) == yesterdayCalendar.get(Calendar.YEAR) && messageCalendar.get(Calendar.DAY_OF_YEAR) == yesterdayCalendar.get(Calendar.DAY_OF_YEAR)
    if (isYesterday) {
        return "Yesterday"
    }
    val format = SimpleDateFormat("dd MMM yyyy" , Locale.ENGLISH)
    return format.format(messageDate) // اذا الرسالة ليست من اليوم ولا من امس ارجع تاريخها الاصلي بصيغه  12 8 2026
}