package com.yogadimas.githubuser.data

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.yogadimas.githubuser.data.local.entity.FavoriteUserEntity
import com.yogadimas.githubuser.data.local.room.UserDao
import com.yogadimas.githubuser.data.remote.api.ApiService
import com.yogadimas.githubuser.data.remote.model.DetailUserResponse
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.ui.event.Event
import com.yogadimas.githubuser.ui.view.activity.DetailActivity
import kotlinx.coroutines.delay
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

class UserRepository private constructor(
    private val apiService: ApiService,
    private val userDao: UserDao,
) {

    suspend fun findUser(
        keyword: String,
        _listUsers: MutableLiveData<ArrayList<ItemsItem?>?>,
        _isLoading: MutableLiveData<Boolean>,
        _isError: MutableLiveData<Boolean>,
    ) {
        _isLoading.value = true
        try {
            val response = apiService.getListUsers(keyword)
            _isError.value = false
            _listUsers.value = response.items
            _isLoading.value = false
        } catch (e: Exception) {
            _isLoading.value = false
            _isError.value = true
        }
    }

    suspend fun getDetailUser(
        login: String,
        _user: MutableLiveData<DetailUserResponse?>,
        _isLoading: MutableLiveData<Boolean>,
        _isError: MutableLiveData<Boolean>,
    ) {
        _isLoading.value = true
        try {
            val response = apiService.getDetailUser(login)
            _isError.value = false
            _user.value = response
            _isLoading.value = false
        } catch (e: UnknownHostException) {
            _isError.value = true
            _isLoading.value = false
        } catch (e: HttpException) {
            _isLoading.value = true
        } catch (e: SocketTimeoutException) {
            _isError.value = true
            _isLoading.value = false
        }

    }

    suspend fun getDetailUserAmountUserFollow(
        login: String,
        _amountFollower: MutableLiveData<Int>,
        _amountFollowing: MutableLiveData<Int>,
        _isLoading: MutableLiveData<Boolean>,
        _isError: MutableLiveData<Boolean>,
        _isSuccess: MutableLiveData<Boolean>,
    ) {
        _isLoading.value = true
        delay(2000)
        try {
            _isSuccess.value = false
            val response = apiService.getDetailUser(login)
            _amountFollower.value = response.followers
            _amountFollowing.value = response.following
            _isLoading.value = false
            _isSuccess.value = true
        } catch (e: UnknownHostException) {
            _isError.value = true
            _isLoading.value = false
            _isSuccess.value = false
        } catch (e: HttpException) {
            _isLoading.value = true
        } catch (e: SocketTimeoutException) {
            _isError.value = true
            _isLoading.value = false
        }

    }

    suspend fun getFollowers(
        login: String,
        _listFollowers: MutableLiveData<ArrayList<ItemsItem?>?>,
        _isLoading: MutableLiveData<Boolean>,
        _message: MutableLiveData<Event<String>>,
        _isError: MutableLiveData<Boolean>,
        isNotDetail: Boolean
    ) {
        getUser(login,
            _listFollowers,
            _isLoading,
            _message,
            _isError,
            DetailActivity.EXTRA_FOLLOWER,
            isNotDetail)
    }


    suspend fun getFollowing(
        login: String,
        _listFollowing: MutableLiveData<ArrayList<ItemsItem?>?>,
        _isLoading: MutableLiveData<Boolean>,
        _message: MutableLiveData<Event<String>>,
        _isError: MutableLiveData<Boolean>,
        isNotDetail: Boolean
    ) {
        getUser(login,
            _listFollowing,
            _isLoading,
            _message,
            _isError,
            DetailActivity.EXTRA_FOLLOWING,
            isNotDetail)
    }

    private suspend fun getUser(
        login: String,
        _listUser: MutableLiveData<ArrayList<ItemsItem?>?>,
        _isLoading: MutableLiveData<Boolean>,
        _message: MutableLiveData<Event<String>>,
        _isError: MutableLiveData<Boolean>,
        category: String,
        isNotDetail: Boolean
    ) {
        _isLoading.value = isNotDetail
        try {
            when (category) {
                DetailActivity.EXTRA_FOLLOWER -> {
                    val response = apiService.getFollowers(login)
                    _isError.value = false
                    _listUser.value = response
                    _isLoading.value = false
                }
                DetailActivity.EXTRA_FOLLOWING -> {
                    val response = apiService.getFollowing(login)
                    _isError.value = false
                    _listUser.value = response
                    _isLoading.value = false
                }
            }
        } catch (e: UnknownHostException) {
            _isError.value = true
            _isLoading.value = false
            _message.value = Event(category)
        } catch (e: Exception) {
            _isLoading.value = true
            _isError.value = true
            _isLoading.value = false
            _message.value = Event(category)
        }
    }

    fun getFavoriteUser(): LiveData<List<FavoriteUserEntity>> = userDao.getFavUser()

    fun getFavoriteUserByLogin(login: String): LiveData<FavoriteUserEntity> =
        userDao.getFavoriteUserByLogin(login)

    suspend fun insertFavoriteUser(users: FavoriteUserEntity) {
        userDao.insertFavUser(users)
    }

    suspend fun deleteFavoriteUser(users: FavoriteUserEntity) {
        userDao.deleteFavUser(users)
    }

    companion object {

        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            apiService: ApiService,
            newsDao: UserDao,
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(apiService, newsDao)
            }.also { instance = it }
    }
}