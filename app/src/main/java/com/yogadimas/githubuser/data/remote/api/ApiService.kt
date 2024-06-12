package com.yogadimas.githubuser.data.remote.api

import com.yogadimas.githubuser.data.remote.model.DetailUserResponse
import com.yogadimas.githubuser.data.remote.model.GithubResponse
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("search/users")
    suspend fun getListUsers(@Query("q") q: String): GithubResponse

    @GET("users/{login}")
    suspend fun getDetailUser(@Path("login") login: String): DetailUserResponse

    @GET("users/{login}/followers")
    suspend fun getFollowers(@Path("login") login: String): ArrayList<ItemsItem?>?

    @GET("users/{login}/following")
    suspend fun getFollowing(@Path("login") login: String): ArrayList<ItemsItem?>?

}