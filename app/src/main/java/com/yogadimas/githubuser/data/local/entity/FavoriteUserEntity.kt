package com.yogadimas.githubuser.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "fav_user")
data class FavoriteUserEntity(

    @ColumnInfo
    @PrimaryKey(autoGenerate = false)
    var login: String = "",

    @ColumnInfo
    var avatarUrl: String? = null,

    )
