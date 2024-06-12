package com.yogadimas.githubuser.ui.view.activity

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.datastore.ObjectDatastore.dataStore
import com.yogadimas.githubuser.data.datastore.SettingPreferences
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.databinding.ActivityMainBinding
import com.yogadimas.githubuser.ui.adapter.UserAdapter
import com.yogadimas.githubuser.ui.helper.*
import com.yogadimas.githubuser.ui.interfaces.OnItemClickCallback
import com.yogadimas.githubuser.ui.viewmodel.MainViewModel
import com.yogadimas.githubuser.ui.viewmodel.SettingsViewModel
import com.yogadimas.githubuser.ui.viewmodel.factory.SettingsViewModelFactory
import com.yogadimas.githubuser.ui.viewmodel.factory.ViewModelFactory

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private val mainViewModel: MainViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory.getInstance(SettingPreferences.getInstance(dataStore))
    }

    private var queryUser: String = MainViewModel.USER_LOGIN

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        settingsViewModel.getThemeSettings().observe(this) {
            userInterfaceThemeState(it)
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager

        loadData()

        binding.viewFailedConnect.btnRefresh.setOnClickListener {
            mainViewModel.findUser(queryUser)
        }


    }


    private fun loadData() {

        // mainViewModel.getContext(this)

        mainViewModel.isLoading.observe(this) {
            showLoading(binding.progressBar, it)
        }

        mainViewModel.listUsers.observe(this) { items ->
            setUsersData(items, getString(R.string.no_user), false)
        }

        mainViewModel.error.observe(this) {
            if (it) {
                val items = ArrayList<ItemsItem?>()
                setUsersData(items, getString(R.string.failed_to_connect), true)
                SingleToast.show(this, getString(R.string.failed_to_connect), Toast.LENGTH_SHORT)

            }
        }


    }

    private fun setUsersData(
        it: ArrayList<ItemsItem?>?,
        text: String,
        isFailed: Boolean,
    ) {

        failedToConnectView(isFailed, binding.viewFailedConnect.root)
        checkDataSize(
            it,
            binding.viewNoData.tvNoData,
            binding.viewNoData.root,
            text, isFailed)

        val adapter = UserAdapter(it)
        binding.rvUsers.adapter = adapter

        adapter.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(user: ItemsItem?) {
                startActivity(goToDetail(this@MainActivity, user?.login))
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.main_menu, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu.findItem(R.id.action_search)?.actionView as SearchView

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))

        searchView.queryHint = resources.getString(R.string.search_hint)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                queryUser = query
                mainViewModel.findUser(query)
                searchView.clearFocus()
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })

        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_favorite -> {
                startActivity(Intent(this@MainActivity, FavoriteActivity::class.java))
            }
            R.id.action_setting -> {
                startActivity(Intent(this@MainActivity, SettingsActivity::class.java))
            }
        }
        return super.onOptionsItemSelected(item)
    }


}