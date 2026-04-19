package com.lab.retrofitlab.domain.repository

import com.lab.retrofitlab.domain.model.Post

interface PostRepository {
    suspend fun getPosts(page: Int): Result<List<Post>>
}