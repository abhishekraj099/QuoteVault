package com.example.quotevault.core.di

import android.content.Context
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.preferencesDataStoreFile
import com.example.quotevault.data.local.datastore.UserPreferencesManager
import com.example.quotevault.core.notification.NotificationScheduler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

private const val TAG = "QV_AppModule"

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideDataStore(
        @ApplicationContext context: Context
    ): DataStore<Preferences> {
        Log.d(TAG, "provideDataStore called - creating singleton DataStore")
        return PreferenceDataStoreFactory.create {
            context.preferencesDataStoreFile("user_prefs")
        }.also {
            Log.d(TAG, "DataStore created successfully: $it")
        }
    }

    @Provides
    @Singleton
    fun provideUserPreferencesManager(
        dataStore: DataStore<Preferences>
    ): UserPreferencesManager {
        Log.d(TAG, "provideUserPreferencesManager called - dataStore: $dataStore")
        return UserPreferencesManager(dataStore).also {
            Log.d(TAG, "UserPreferencesManager created: $it")
        }
    }

    @Provides
    @Singleton
    fun provideNotificationScheduler(
        @ApplicationContext context: Context
    ): NotificationScheduler {
        Log.d(TAG, "provideNotificationScheduler called")
        return NotificationScheduler(context).also {
            Log.d(TAG, "NotificationScheduler created: $it")
        }
    }
}
