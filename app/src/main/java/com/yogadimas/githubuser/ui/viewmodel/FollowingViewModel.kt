package com.yogadimas.githubuser.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogadimas.githubuser.data.UserRepository
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.event.Event
import kotlinx.coroutines.launch

class FollowingViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listFollowing = MutableLiveData<ArrayList<ItemsItem?>?>()
    val listFollowing: LiveData<ArrayList<ItemsItem?>?> = _listFollowing

    private val _snackBarText = MutableLiveData<Event<String>>()

    private val _isError = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _isError

    private val isNotDetail: Boolean = true

    var login: String = ""
        set(value) {
            field = value
            getFollowing()
        }

    private fun getFollowing() {
        viewModelScope.launch {
            userRepository.getFollowing(login,
                _listFollowing,
                _isLoading,
                _snackBarText,
                _isError,
                isNotDetail)
        }
    }


}