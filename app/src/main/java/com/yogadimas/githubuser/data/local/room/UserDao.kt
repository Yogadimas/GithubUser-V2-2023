package com.yogadimas.githubuser.data.local.room

import androidx.lifecycle.LiveData
import androidx.room.*
import com.yogadimas.githubuser.data.local.entity.*


@Dao
interface UserDao {

    @Query("SELECT * FROM fav_user")
    fun getFavUser(): LiveData<List<FavoriteUserEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavUser(user: FavoriteUserEntity)

    @Delete
    suspend fun deleteFavUser(user: FavoriteUserEntity)

    @Query("SELECT EXISTS(SELECT * FROM fav_user WHERE login = :login)")
    suspend fun isUserFav(login: String): Boolean

    @Query("SELECT * FROM fav_user WHERE login = :login")
    fun getFavoriteUserByLogin(login: String): LiveData<FavoriteUserEntity>

}