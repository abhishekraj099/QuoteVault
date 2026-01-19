package com.example.quotevault.presentation.settings

data class SettingsState(
    val isDarkMode: Boolean = false,
    val isNotificationsEnabled: Boolean = true,
    val fontSize: Int = 100, // percentage: 80-120
    val language: String = "English",
    val notificationTime: String = "09:00",
    val autoSync: Boolean = true,
    val theme: String = "Default"
)
