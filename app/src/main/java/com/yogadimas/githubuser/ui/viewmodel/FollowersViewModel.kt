package com.yogadimas.githubuser.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogadimas.githubuser.data.UserRepository
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.event.Event
import kotlinx.coroutines.launch

class FollowersViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listFollowers = MutableLiveData<ArrayList<ItemsItem?>?>()
    val listFollowers: LiveData<ArrayList<ItemsItem?>?> = _listFollowers

    private val _snackBarText = MutableLiveData<Event<String>>()

    private val _isError = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _isError

    private val isNotDetail: Boolean = true

    var login: String = ""
        set(value) {
            field = value
            getFollowers()
        }


    private fun getFollowers() {
        viewModelScope.launch {
            userRepository.getFollowers(login,
                _listFollowers,
                _isLoading,
                _snackBarText,
                _isError,
                isNotDetail)
        }
    }


}