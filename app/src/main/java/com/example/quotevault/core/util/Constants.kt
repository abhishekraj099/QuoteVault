package com.example.quotevault.core.util

object Constants {


    const val TABLE_QUOTES = "quotes"
    const val TABLE_FAVORITES = "user_favorites"
    const val TABLE_COLLECTIONS = "collections"
    const val TABLE_COLLECTION_QUOTES = "collection_quotes"

    val CATEGORIES = listOf("All", "Motivation", "Love", "Success", "Wisdom", "Humor")
    const val PAGE_SIZE = 20

    const val PREFS_NAME = "quote_vault_prefs"
    const val KEY_USER_ID = "user_id"
    const val KEY_IS_LOGGED_IN = "is_logged_in"
}