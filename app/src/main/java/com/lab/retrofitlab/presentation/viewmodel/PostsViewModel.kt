package com.lab.retrofitlab.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.lab.retrofitlab.data.repository.PostRepositoryImpl
import com.lab.retrofitlab.di.NetworkModule
import com.lab.retrofitlab.domain.error.toAppError
import com.lab.retrofitlab.domain.error.toMessage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PostsViewModel : ViewModel() {

    private val repo = PostRepositoryImpl(NetworkModule.postApi)

    private val _uiState = MutableStateFlow<PostsUiState>(PostsUiState.Loading)
    val uiState: StateFlow<PostsUiState> = _uiState.asStateFlow()

    private var currentPage = 1
    private val allPosts = mutableListOf<com.lab.retrofitlab.domain.model.Post>()

    init { loadPosts() }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = PostsUiState.Loading
            repo.getPosts(currentPage)
                .onSuccess { posts ->
                    allPosts.addAll(posts)
                    _uiState.value = if (allPosts.isEmpty())
                        PostsUiState.Empty
                    else
                        PostsUiState.Success(allPosts.toList())
                }
                .onFailure { error ->
                    _uiState.value = PostsUiState.Error(error.toAppError().toMessage())
                }
        }
    }

    fun loadNextPage() {
        currentPage++
        viewModelScope.launch {
            repo.getPosts(currentPage).onSuccess { newPosts ->
                allPosts.addAll(newPosts)
                _uiState.value = PostsUiState.Success(allPosts.toList())
            }
        }
    }
}