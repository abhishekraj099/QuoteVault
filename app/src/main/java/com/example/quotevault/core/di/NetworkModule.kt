package com.example.quotevault.core.di

import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.data.remote.api.CollectionService
import com.example.quotevault.data.remote.api.FavoriteService
import com.example.quotevault.data.remote.api.QuoteService
import com.example.quotevault.data.remote.api.SupabaseClientProvider
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideSupabaseClient(): SupabaseClientProvider {
        return SupabaseClientProvider()
    }

    @Provides
    @Singleton
    fun provideAuthService(
        supabaseClient: SupabaseClientProvider
    ): AuthService {
        return AuthService(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideQuoteService(
        supabaseClient: SupabaseClientProvider
    ): QuoteService {
        return QuoteService(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideFavoriteService(
        supabaseClient: SupabaseClientProvider
    ): FavoriteService {
        return FavoriteService(supabaseClient)
    }

    @Provides
    @Singleton
    fun provideCollectionService(
        supabaseClient: SupabaseClientProvider
    ): CollectionService {
        return CollectionService(supabaseClient)
    }
}
