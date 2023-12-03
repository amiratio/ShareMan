package com.application.shareman.remote.model

data class Post(
    val type: String,
    val media: String,
    val content: String,
    val lat: String,
    val lon: String,
    val comments: Boolean,
    val name: String,
    val username: String,
    val key: String,
)