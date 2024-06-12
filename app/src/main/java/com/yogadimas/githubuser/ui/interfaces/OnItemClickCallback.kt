package com.yogadimas.githubuser.ui.interfaces

import com.yogadimas.githubuser.data.remote.model.ItemsItem

interface OnItemClickCallback {

    fun onItemClicked(user: ItemsItem?)
}