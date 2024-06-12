package com.yogadimas.githubuser.di

import android.content.Context
import com.yogadimas.githubuser.data.UserRepository
import com.yogadimas.githubuser.data.local.room.UserDatabase
import com.yogadimas.githubuser.data.remote.api.ApiConfig

object Injection {

    fun provideRepository(context: Context): UserRepository {
        val apiService = ApiConfig.getApiService()
        val database = UserDatabase.getInstance(context)
        val dao = database.userDao()
        return UserRepository.getInstance(apiService, dao)
    }

}