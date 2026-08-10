package com.example.chatapp.c_listChatUser.menuToolbar_ListChat.setting

import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import java.util.Locale

object AppSetting {
    fun applyTheme(theme: String) {
        when (theme) {
            "System", "الوضع الافتراضي لنظام" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }

            "Light", "فاتح" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }

            "Dark", "داكن" -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
        }
    }

    fun applyLanguage(language: String) {
        val locale = when (language) {
            "العربية" ->
                Locale("ar")

            else -> Locale("en")
        }
        AppCompatDelegate.setApplicationLocales(LocaleListCompat.create(locale))
    }
}