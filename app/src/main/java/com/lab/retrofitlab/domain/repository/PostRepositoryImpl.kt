package com.lab.retrofitlab.data.repository

import com.lab.retrofitlab.data.remote.api.PostApi
import com.lab.retrofitlab.data.remote.dto.toDomain
import com.lab.retrofitlab.domain.model.Post
import com.lab.retrofitlab.domain.repository.PostRepository

class PostRepositoryImpl(private val api: PostApi) : PostRepository {
    override suspend fun getPosts(page: Int): Result<List<Post>> =
        runCatching { api.getPosts(page = page).map { it.toDomain() } }
}