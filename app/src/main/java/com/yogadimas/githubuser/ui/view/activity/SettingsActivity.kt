package com.yogadimas.githubuser.ui.view.activity

import android.os.Bundle
import android.widget.CompoundButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.datastore.ObjectDatastore.dataStore
import com.yogadimas.githubuser.data.datastore.SettingPreferences
import com.yogadimas.githubuser.databinding.ActivitySettingsBinding
import com.yogadimas.githubuser.ui.helper.userInterfaceThemeState
import com.yogadimas.githubuser.ui.viewmodel.SettingsViewModel
import com.yogadimas.githubuser.ui.viewmodel.factory.SettingsViewModelFactory


class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding


    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance(SettingPreferences.getInstance(dataStore))

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // val pref = SettingPreferences.getInstance(dataStore)


        binding.toolbar.apply {
            title = getString(R.string.settings)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { finish() }
        }

        settingsViewModel.getThemeSettings().observe(this) {
            userInterfaceThemeState(it, binding.switchTheme)
        }

        binding.switchTheme.setOnCheckedChangeListener { _: CompoundButton?, isChecked: Boolean ->
            settingsViewModel.saveThemeSettings(isChecked)
        }

    }

}