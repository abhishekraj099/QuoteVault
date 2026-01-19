package com.example.quotevault.data.remote.mapper

import com.example.quotevault.data.remote.dto.UserDto
import com.example.quotevault.domain.model.User
import kotlinx.serialization.json.jsonPrimitive

fun UserDto.toUser(): User {
    val name = user_metadata?.get("name")?.jsonPrimitive?.content
    val avatarUrl = user_metadata?.get("avatar_url")?.jsonPrimitive?.content

    return User(
        id = id,
        email = email,
        name = name,
        avatarUrl = avatarUrl
    )
}
