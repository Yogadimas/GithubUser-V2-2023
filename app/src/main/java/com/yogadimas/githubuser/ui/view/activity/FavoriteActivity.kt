package com.yogadimas.githubuser.ui.view.activity

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.local.entity.FavoriteUserEntity
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.databinding.ActivityFavoriteBinding
import com.yogadimas.githubuser.ui.adapter.UserAdapter
import com.yogadimas.githubuser.ui.helper.checkDataSize
import com.yogadimas.githubuser.ui.helper.goToDetail
import com.yogadimas.githubuser.ui.interfaces.OnItemClickCallback
import com.yogadimas.githubuser.ui.viewmodel.FavoriteViewModel
import com.yogadimas.githubuser.ui.viewmodel.factory.ViewModelFactory

class FavoriteActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFavoriteBinding

    private val favoriteViewModel: FavoriteViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityFavoriteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.toolbar.apply {
            title = getString(R.string.title_favorite_user)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { finish() }
        }

        val layoutManager = LinearLayoutManager(this)
        binding.rvUsers.layoutManager = layoutManager

        loadData()
    }

    private fun loadData() {
        favoriteViewModel.getFavoriteUser().observe(this) {
            setUsersData(it, getString(R.string.no_favorite))
        }
    }

    private fun setUsersData(
        it: List<FavoriteUserEntity>,
        text: String,
    ) {

        val items = arrayListOf<ItemsItem?>()
        it.map {
            val item = ItemsItem(login = it.login, avatarUrl = it.avatarUrl)
            items.add(item)
        }
        val adapter = UserAdapter(items)
        binding.rvUsers.adapter = adapter

        checkDataSize(items, binding.viewNoData.tvNoData, binding.viewNoData.root, text, false)

        adapter.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(user: ItemsItem?) {
                startActivity(goToDetail(this@FavoriteActivity, user?.login))
            }
        })
    }

}