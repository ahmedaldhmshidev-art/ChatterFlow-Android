package com.example.chatapp.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun styleTime(time:Long):String {
    val sdf = SimpleDateFormat("h:mm a", Locale.US)
    return sdf.format(Date(time))
}

fun formatFullDate(time: Long):String{
    val formatter = SimpleDateFormat(
        "EEEE, DD MMMM yyyy - hh:mm a" ,Locale("ar")
    )
    return formatter.format(Date(time))
}