package com.yogadimas.githubuser.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yogadimas.githubuser.data.UserRepository
import com.yogadimas.githubuser.data.local.entity.FavoriteUserEntity
import com.yogadimas.githubuser.data.remote.model.DetailUserResponse
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.event.Event
import kotlinx.coroutines.launch

class DetailViewModel(private val userRepository: UserRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _isLoadingAmount = MutableLiveData<Boolean>()
    private val _isSuccess = MutableLiveData<Boolean>()
    val isSuccess: LiveData<Boolean> = _isSuccess

    private val _user = MutableLiveData<DetailUserResponse?>()
    val user: LiveData<DetailUserResponse?> = _user

    private val _amountFollower = MutableLiveData<Int>()
    val amountFollower: LiveData<Int> = _amountFollower

    private val _amountFollowing = MutableLiveData<Int>()
    val amountFollowing: LiveData<Int> = _amountFollowing

    private val _listFollowers = MutableLiveData<ArrayList<ItemsItem?>?>()
    private val _listFollowing = MutableLiveData<ArrayList<ItemsItem?>?>()

    private val _snackBarText = MutableLiveData<Event<String>>()
    val snackBarText: LiveData<Event<String>> = _snackBarText

    private val _isErrorDetail = MutableLiveData<Boolean>()
    val errorDetail: LiveData<Boolean> = _isErrorDetail

    private val _isErrorFollow = MutableLiveData<Boolean>()

    private val isNotDetail: Boolean = false

    var login: String = ""
        set(value) {
            field = value
            detailUser()
            getFavoriteUserByLogin()
        }


    private fun detailUser() {
        viewModelScope.launch {
            userRepository.getDetailUser(login, _user, _isLoading, _isErrorDetail)
        }
    }


    var loginAmountUserFollow: String = ""
        set(value) {
            field = value
            getDetailUserAmountUserFollow()
        }

    private fun getDetailUserAmountUserFollow() {
        viewModelScope.launch {
            userRepository.getDetailUserAmountUserFollow(loginAmountUserFollow,
                _amountFollower,
                _amountFollowing,
                _isLoadingAmount,
                _isErrorDetail,
                _isSuccess)
        }
    }

    var userFollowerValue: String = ""
        set(value) {
            field = value
            getFollowers()
        }

    var userFollowingValue: String = ""
        set(value) {
            field = value
            getFollowing()
        }


    private fun getFollowers() {
        viewModelScope.launch {
            userRepository.getFollowers(login,
                _listFollowers,
                _isLoading,
                _snackBarText,
                _isErrorFollow,
                isNotDetail)
        }
    }

    private fun getFollowing() {
        viewModelScope.launch {
            userRepository.getFollowing(login,
                _listFollowing,
                _isLoading,
                _snackBarText,
                _isErrorFollow,
                isNotDetail)
        }
    }

    fun getFavoriteUserByLogin() = userRepository.getFavoriteUserByLogin(login)

    fun insertFavoriteUser(user: FavoriteUserEntity) {
        viewModelScope.launch {
            userRepository.insertFavoriteUser(user)
        }

    }

    fun deleteFavoriteUser(user: FavoriteUserEntity) {
        viewModelScope.launch {
            userRepository.deleteFavoriteUser(user)
        }
    }

}