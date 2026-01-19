package com.example.quotevault.data.local.datastore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPreferencesManager @Inject constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val DARK_MODE_KEY = booleanPreferencesKey("dark_mode")
        private val NOTIFICATIONS_ENABLED_KEY = booleanPreferencesKey("notifications_enabled")
        private val FONT_SIZE_KEY = intPreferencesKey("font_size")
        private val LANGUAGE_KEY = stringPreferencesKey("language")
        private val NOTIFICATION_TIME_KEY = stringPreferencesKey("notification_time")
        private val NOTIF_HOUR_KEY = intPreferencesKey("notif_hour")
        private val NOTIF_MINUTE_KEY = intPreferencesKey("notif_minute")
        private val AUTO_SYNC_KEY = booleanPreferencesKey("auto_sync")
        private val THEME_KEY = stringPreferencesKey("theme")

        @Volatile
        private var INSTANCE: UserPreferencesManager? = null

        fun getInstance(context: Context): UserPreferencesManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: create(context).also { INSTANCE = it }
            }
        }

        fun create(context: Context): UserPreferencesManager {
            val dataStore = PreferenceDataStoreFactory.create {
                context.applicationContext.preferencesDataStoreFile("user_prefs")
            }
            return UserPreferencesManager(dataStore)
        }
    }

    val isDarkModeFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[DARK_MODE_KEY] ?: false
    }

    val isNotificationsEnabledFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_ENABLED_KEY] ?: true
    }

    val fontSizeFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[FONT_SIZE_KEY] ?: 100
    }

    val languageFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[LANGUAGE_KEY] ?: "English"
    }

    val notificationTimeFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[NOTIFICATION_TIME_KEY] ?: "09:00"
    }

    val notifHourFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[NOTIF_HOUR_KEY] ?: 9
    }

    val notifMinuteFlow: Flow<Int> = dataStore.data.map { prefs ->
        prefs[NOTIF_MINUTE_KEY] ?: 0
    }

    val autoSyncFlow: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[AUTO_SYNC_KEY] ?: true
    }

    val themeFlow: Flow<String> = dataStore.data.map { prefs ->
        prefs[THEME_KEY] ?: "Default"
    }

    suspend fun setDarkMode(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[DARK_MODE_KEY] = enabled
        }
    }

    suspend fun setNotificationsEnabled(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_ENABLED_KEY] = enabled
        }
    }

    suspend fun setFontSize(size: Int) {
        dataStore.edit { prefs ->
            prefs[FONT_SIZE_KEY] = size
        }
    }

    suspend fun setLanguage(language: String) {
        dataStore.edit { prefs ->
            prefs[LANGUAGE_KEY] = language
        }
    }

    suspend fun setNotificationTime(time: String) {
        dataStore.edit { prefs ->
            prefs[NOTIFICATION_TIME_KEY] = time
        }
    }

    suspend fun setNotificationTime(hour: Int, minute: Int) {
        android.util.Log.d("QV_PrefsManager", "💾 onTimePicked(hour=$hour, minute=$minute)")
        dataStore.edit { prefs ->
            prefs[NOTIF_HOUR_KEY] = hour
            prefs[NOTIF_MINUTE_KEY] = minute
            prefs[NOTIFICATION_TIME_KEY] = String.format("%02d:%02d", hour, minute)
        }
        android.util.Log.d("QV_PrefsManager", "✅ afterSave(savedHour=$hour, savedMinute=$minute)")
    }

    suspend fun getNotificationHour(): Int {
        var hour = 9
        dataStore.data.map { prefs ->
            hour = prefs[NOTIF_HOUR_KEY] ?: 9
        }.collect { }
        return hour
    }

    suspend fun getNotificationMinute(): Int {
        var minute = 0
        dataStore.data.map { prefs ->
            minute = prefs[NOTIF_MINUTE_KEY] ?: 0
        }.collect { }
        return minute
    }

    suspend fun setAutoSync(enabled: Boolean) {
        dataStore.edit { prefs ->
            prefs[AUTO_SYNC_KEY] = enabled
        }
    }

    suspend fun setTheme(themeName: String) {
        dataStore.edit { prefs ->
            prefs[THEME_KEY] = themeName
        }
    }
}
