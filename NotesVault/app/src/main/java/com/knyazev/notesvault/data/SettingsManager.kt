package com.knyazev.notesvault.data

import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class SettingsManager(context: Context) {

    private val sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    // Ключ для хранения темы
    companion object {
        private const val PREFS_NAME = "app_settings"
        private const val THEME_KEY = "theme_mode"
        const val THEME_LIGHT = "light"
        const val THEME_DARK = "dark"
    }

    // Получить текущую тему
    fun getThemeMode(): String {
        return sharedPreferences.getString(THEME_KEY, THEME_LIGHT) ?: THEME_LIGHT // По умолчанию выводим светлую тему
    }

    // Установить тему
    fun setThemeMode(themeMode: String) {
        sharedPreferences.edit().putString(THEME_KEY, themeMode).apply()
        applyTheme(themeMode) // Применить тему сразу
    }

    // Применить тему приложения
    private fun applyTheme(mode: String) {
        when (mode) {
            THEME_LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // Светлая тема
            THEME_DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES) // Темная тема
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM) //  По умолчанию
        }
    }

    // Инициализация  выбранной темы при запуске приложения
    fun applyCurrentTheme() {
        applyTheme(getThemeMode())
    }
}