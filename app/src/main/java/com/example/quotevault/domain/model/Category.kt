package com.example.quotevault.domain.model

enum class Category(val displayName: String) {
    ALL("All"),
    MOTIVATION("Motivation"),
    LOVE("Love"),
    SUCCESS("Success"),
    WISDOM("Wisdom"),
    HUMOR("Humor");

    companion object {
        fun fromString(value: String): Category {
            return values().find {
                it.displayName.equals(value, ignoreCase = true)
            } ?: ALL
        }
    }
}
