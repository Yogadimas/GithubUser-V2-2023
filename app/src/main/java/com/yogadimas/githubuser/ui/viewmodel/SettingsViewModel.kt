package com.yogadimas.githubuser.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.yogadimas.githubuser.data.datastore.SettingPreferences
import kotlinx.coroutines.launch

class SettingsViewModel(private val preferences: SettingPreferences?) : ViewModel() {

    fun saveThemeSettings(isDarkMode: Boolean) {
        viewModelScope.launch {
            preferences?.saveThemeSetting(isDarkMode)
        }
    }

    fun getThemeSettings(): LiveData<Boolean> = preferences?.getThemeSetting()!!.asLiveData()

}