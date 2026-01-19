package com.example.quotevault.presentation.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.quotevault.data.local.datastore.UserPreferencesManager
import com.example.quotevault.core.notification.NotificationScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "QV_SettingsVM"

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefs: UserPreferencesManager,
    private val notificationScheduler: NotificationScheduler
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsState())
    val uiState: StateFlow<SettingsState> = _uiState.asStateFlow()

    init {
        Log.d(TAG, "SettingsViewModel init started - prefs: $prefs, scheduler: $notificationScheduler")
        initPreferences()
        Log.d(TAG, "SettingsViewModel init completed")
    }

    private fun initPreferences() {
        try {
            Log.d(TAG, "Starting preference flows collection")

            prefs.isDarkModeFlow
                .catch { e -> Log.e(TAG, "Error in isDarkModeFlow", e) }
                .onEach { enabled ->
                    Log.d(TAG, "isDarkMode updated: $enabled")
                    _uiState.value = _uiState.value.copy(isDarkMode = enabled)
                }
                .launchIn(viewModelScope)

            prefs.isNotificationsEnabledFlow
                .catch { e -> Log.e(TAG, "Error in isNotificationsEnabledFlow", e) }
                .onEach { enabled ->
                    Log.d(TAG, "isNotificationsEnabled updated: $enabled")
                    _uiState.value = _uiState.value.copy(isNotificationsEnabled = enabled)
                }
                .launchIn(viewModelScope)

            prefs.fontSizeFlow
                .catch { e -> Log.e(TAG, "Error in fontSizeFlow", e) }
                .onEach { size ->
                    Log.d(TAG, "fontSize updated: $size")
                    _uiState.value = _uiState.value.copy(fontSize = size)
                }
                .launchIn(viewModelScope)

            prefs.languageFlow
                .catch { e -> Log.e(TAG, "Error in languageFlow", e) }
                .onEach { lang ->
                    Log.d(TAG, "language updated: $lang")
                    _uiState.value = _uiState.value.copy(language = lang)
                }
                .launchIn(viewModelScope)

            prefs.notificationTimeFlow
                .catch { e -> Log.e(TAG, "Error in notificationTimeFlow", e) }
                .onEach { time ->
                    Log.d(TAG, "notificationTime updated: $time")
                    _uiState.value = _uiState.value.copy(notificationTime = time)
                }
                .launchIn(viewModelScope)

            prefs.autoSyncFlow
                .catch { e -> Log.e(TAG, "Error in autoSyncFlow", e) }
                .onEach { enabled ->
                    Log.d(TAG, "autoSync updated: $enabled")
                    _uiState.value = _uiState.value.copy(autoSync = enabled)
                }
                .launchIn(viewModelScope)

            prefs.themeFlow
                .catch { e -> Log.e(TAG, "Error in themeFlow", e) }
                .onEach { theme ->
                    Log.d(TAG, "theme updated: $theme")
                    _uiState.value = _uiState.value.copy(theme = theme)
                }
                .launchIn(viewModelScope)

            Log.d(TAG, "All preference flows launched successfully")
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing preferences", e)
        }
    }

    fun toggleDarkMode() {
        Log.d(TAG, "toggleDarkMode called")
        val newValue = !_uiState.value.isDarkMode
        _uiState.value = _uiState.value.copy(isDarkMode = newValue)
        viewModelScope.launch {
            try {
                prefs.setDarkMode(newValue)
                Log.d(TAG, "Dark mode set to: $newValue")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting dark mode", e)
            }
        }
    }

    fun toggleNotifications() {
        Log.d(TAG, "toggleNotifications called")
        val newValue = !_uiState.value.isNotificationsEnabled
        setNotificationsEnabled(newValue)
    }

    fun enableNotifications() {
        Log.d(TAG, "enableNotifications called")
        setNotificationsEnabled(true)
    }

    fun disableNotifications() {
        Log.d(TAG, "disableNotifications called")
        setNotificationsEnabled(false)
    }

    private fun setNotificationsEnabled(enabled: Boolean) {
        Log.d(TAG, "setNotificationsEnabled: $enabled")
        _uiState.value = _uiState.value.copy(isNotificationsEnabled = enabled)
        viewModelScope.launch {
            try {
                prefs.setNotificationsEnabled(enabled)
                if (enabled) {
                    // Read saved hour/minute from DataStore
                    val hour = prefs.getNotificationHour()
                    val minute = prefs.getNotificationMinute()
                    Log.d(TAG, "📅 whenScheduling(hour=$hour, minute=$minute) [from saved prefs]")
                    notificationScheduler.scheduleQuoteNotification(hour, minute)
                } else {
                    Log.d(TAG, "❌ Cancelling notifications")
                    notificationScheduler.cancelQuoteNotification()
                }
                Log.d(TAG, "✅ Notifications set to: $enabled")
            } catch (e: Exception) {
                Log.e(TAG, "❌ Error setting notifications", e)
            }
        }
    }

    fun setFontSize(size: Int) {
        Log.d(TAG, "setFontSize called: $size")
        _uiState.value = _uiState.value.copy(fontSize = size)
        viewModelScope.launch {
            try {
                prefs.setFontSize(size)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting font size", e)
            }
        }
    }

    fun setLanguage(language: String) {
        Log.d(TAG, "setLanguage called: $language")
        _uiState.value = _uiState.value.copy(language = language)
        viewModelScope.launch {
            try {
                prefs.setLanguage(language)
            } catch (e: Exception) {
                Log.e(TAG, "Error setting language", e)
            }
        }
    }

    fun setNotificationTime(time: String) {
        Log.d(TAG, "=== setNotificationTime START ===")
        Log.d(TAG, "Received time string: $time")

        _uiState.value = _uiState.value.copy(notificationTime = time)
        viewModelScope.launch {
            try {
                prefs.setNotificationTime(time)
                Log.d(TAG, "Time saved to preferences: $time")

                if (_uiState.value.isNotificationsEnabled) {
                    val (hour, minute) = parseTime(time)
                    Log.d(TAG, "Parsed time - Hour: $hour, Minute: $minute")
                    Log.d(TAG, "Notifications are ENABLED, calling scheduler...")
                    notificationScheduler.scheduleQuoteNotification(hour, minute)
                    Log.d(TAG, "Scheduler called successfully")
                } else {
                    Log.d(TAG, "Notifications are DISABLED, not scheduling")
                }
                Log.d(TAG, "=== setNotificationTime END ===")
            } catch (e: Exception) {
                Log.e(TAG, "ERROR in setNotificationTime", e)
            }
        }
    }

    fun setNotificationTime(hour: Int, minute: Int) {
        val timeString = String.format("%02d:%02d", hour, minute)
        Log.d(TAG, "🎯 onTimePicked(hour=$hour, minute=$minute)")

        // Update UI immediately for responsiveness
        _uiState.value = _uiState.value.copy(notificationTime = timeString)

        viewModelScope.launch {
            try {
                // Save to DataStore (hour and minute separately)
                prefs.setNotificationTime(hour, minute)
                Log.d(TAG, "��� afterSave(savedHour=$hour, savedMinute=$minute)")

                // Only schedule AFTER save completes
                if (_uiState.value.isNotificationsEnabled) {
                    Log.d(TAG, "📅 whenScheduling(hour=$hour, minute=$minute)")
                    notificationScheduler.scheduleQuoteNotification(hour, minute)
                    Log.d(TAG, "✅ Scheduler called successfully")
                } else {
                    Log.d(TAG, "⏸️ Notifications disabled, not scheduling")
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ ERROR in setNotificationTime", e)
            }
        }
    }

    fun toggleAutoSync() {
        Log.d(TAG, "toggleAutoSync called")
        val newValue = !_uiState.value.autoSync
        _uiState.value = _uiState.value.copy(autoSync = newValue)
        viewModelScope.launch {
            try {
                prefs.setAutoSync(newValue)
                Log.d(TAG, "Auto sync set to: $newValue")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting auto sync", e)
            }
        }
    }

    fun setTheme(theme: String) {
        Log.d(TAG, "setTheme called: $theme")
        _uiState.value = _uiState.value.copy(theme = theme)
        viewModelScope.launch {
            try {
                prefs.setTheme(theme)
                Log.d(TAG, "Theme set to: $theme")
            } catch (e: Exception) {
                Log.e(TAG, "Error setting theme", e)
            }
        }
    }


    private fun parseTime(timeStr: String): Pair<Int, Int> {
        return try {
            val parts = timeStr.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: 9
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: 0
            Pair(hour, minute)
        } catch (e: Exception) {
            Log.e(TAG, "Error parsing time: $timeStr", e)
            Pair(9, 0) // Default to 9:00 AM
        }
    }
}
