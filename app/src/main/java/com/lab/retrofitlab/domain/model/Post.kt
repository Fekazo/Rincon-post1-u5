package com.lab.retrofitlab.domain.model

data class Post(
    val id: Int,
    val userId: Int,
    val title: String,
    val excerpt: String
)