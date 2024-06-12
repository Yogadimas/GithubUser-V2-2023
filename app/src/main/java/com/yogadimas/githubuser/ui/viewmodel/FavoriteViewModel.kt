package com.yogadimas.githubuser.ui.viewmodel

import androidx.lifecycle.ViewModel
import com.yogadimas.githubuser.data.UserRepository

class FavoriteViewModel(private val userRepository: UserRepository) : ViewModel() {

    fun getFavoriteUser() = userRepository.getFavoriteUser()


}