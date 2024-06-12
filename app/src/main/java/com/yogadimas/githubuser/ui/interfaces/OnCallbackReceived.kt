package com.yogadimas.githubuser.ui.interfaces

interface OnCallbackReceived {
    fun updateSelectedTab(data: String)
    fun refreshAmountUserFollow(isRefresh : Boolean)
    fun fragmentIsLoading(isLoading: Boolean)
}
