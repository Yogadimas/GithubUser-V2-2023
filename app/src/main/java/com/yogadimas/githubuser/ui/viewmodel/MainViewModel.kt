package com.yogadimas.githubuser.ui.viewmodel

import android.annotation.SuppressLint
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogadimas.githubuser.data.UserRepository
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.view.activity.MainActivity
import kotlinx.coroutines.launch

class MainViewModel(private val userRepository: UserRepository) : ViewModel() {

    // @SuppressLint("StaticFieldLeak")
    // private lateinit var activity: MainActivity

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _listUsers = MutableLiveData<ArrayList<ItemsItem?>?>()
    val listUsers: LiveData<ArrayList<ItemsItem?>?> = _listUsers

    private val _isError = MutableLiveData<Boolean>()
    val error: LiveData<Boolean> = _isError

    companion object {
        const val USER_LOGIN = "yogadimas"
    }

    init {
        findUser()
    }

    // fun getContext(activity: MainActivity) {
    //     this.activity = activity
    // }

    fun findUser(keyword: String? = null) {
        viewModelScope.launch {
            userRepository.findUser(keyword ?: USER_LOGIN, _listUsers, _isLoading, _isError)
        }

    }


}