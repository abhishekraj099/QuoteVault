package com.example.quotevault.core.di

import com.example.quotevault.data.remote.api.AuthService
import com.example.quotevault.data.remote.api.CollectionService
import com.example.quotevault.data.remote.api.FavoriteService
import com.example.quotevault.data.remote.api.QuoteService
import com.example.quotevault.data.repository.AuthRepositoryImpl
import com.example.quotevault.data.repository.CollectionRepositoryImpl
import com.example.quotevault.data.repository.FavoriteRepositoryImpl
import com.example.quotevault.data.repository.QuoteRepositoryImpl
import com.example.quotevault.domain.repository.AuthRepository
import com.example.quotevault.domain.repository.CollectionRepository
import com.example.quotevault.domain.repository.FavoriteRepository
import com.example.quotevault.domain.repository.QuoteRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun provideAuthRepository(
        authService: AuthService
    ): AuthRepository {
        return AuthRepositoryImpl(authService)
    }

    @Provides
    @Singleton
    fun provideQuoteRepository(
        quoteService: QuoteService
    ): QuoteRepository {
        return QuoteRepositoryImpl(quoteService)
    }

    @Provides
    @Singleton
    fun provideFavoriteRepository(
        favoriteService: FavoriteService,
        authService: AuthService
    ): FavoriteRepository {
        return FavoriteRepositoryImpl(favoriteService, authService)
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(
        collectionService: CollectionService,
        authService: AuthService
    ): CollectionRepository {
        return CollectionRepositoryImpl(collectionService, authService)
    }
}
