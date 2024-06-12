package com.yogadimas.githubuser.ui.view.activity

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.local.entity.FavoriteUserEntity
import com.yogadimas.githubuser.data.remote.model.DetailUserResponse
import com.yogadimas.githubuser.databinding.ActivityDetailBinding
import com.yogadimas.githubuser.databinding.LayoutDetailContentBinding
import com.yogadimas.githubuser.ui.adapter.DetailSectionPagerAdapter
import com.yogadimas.githubuser.ui.event.Event
import com.yogadimas.githubuser.ui.helper.SingleToast
import com.yogadimas.githubuser.ui.helper.failedToConnectView
import com.yogadimas.githubuser.ui.helper.showLoading
import com.yogadimas.githubuser.ui.interfaces.OnCallbackReceived
import com.yogadimas.githubuser.ui.viewmodel.DetailViewModel
import com.yogadimas.githubuser.ui.viewmodel.factory.ViewModelFactory
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class DetailActivity : AppCompatActivity(), OnCallbackReceived, TabLayout.OnTabSelectedListener {

    companion object {

        const val EXTRA_KEY = "extra_key"
        const val EXTRA_FOLLOWER = "Follower"
        const val EXTRA_FOLLOWING = "Following"

        private const val STATE_TAB_POSITION = "state_tab_position"

        private const val STATE_AMOUNT_FOLLOWER = "state_amount_following"
        private const val STATE_AMOUNT_FOLLOWING = "state_amount_follower"

        private val TAB_TITLES =
            intArrayOf(R.string.tab_title_followers, R.string.tab_title_following)

    }

    private lateinit var binding: ActivityDetailBinding

    private lateinit var bindingInclude: LayoutDetailContentBinding

    private val detailViewModel: DetailViewModel by viewModels {
        ViewModelFactory.getInstance(application)
    }


    private var loginUser = ""

    private var isFavorite = false
    private var isDetailError = false

    private var position = 0
    private var currentPosition: Int? = null

    private var amountFollower = 0
    private var amountFollowing = 0

    private var currentAmountFollower: Int? = null
    private var currentAmountFollowing: Int? = null


    private var fragmentIsLoading = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        bindingInclude = binding.includeDetailContent

        binding.toolbar.apply {
            title = getString(R.string.title_detail_github)
            setNavigationIcon(R.drawable.ic_back)
            setNavigationOnClickListener { finish() }
        }

        loginUser = intent.getStringExtra(EXTRA_KEY).orEmpty()

        loadDetailData()

        binding.tabs.addOnTabSelectedListener(this)

        binding.swipeRefresh.setOnRefreshListener {
            currentPosition = position
            setFollowData(loginUser)
            binding.swipeRefresh.isRefreshing = false
        }

        if (savedInstanceState != null) {
            currentPosition = savedInstanceState.getInt(STATE_TAB_POSITION)
            currentAmountFollower = savedInstanceState.getInt(STATE_AMOUNT_FOLLOWER)
            currentAmountFollowing = savedInstanceState.getInt(STATE_AMOUNT_FOLLOWING)
            setAmountUserFollows(currentAmountFollower, currentAmountFollowing)
        }

    }

    private fun selectPage(pageIndex: Int) {
        binding.tabs.setScrollPosition(pageIndex, 0f, true)
        binding.viewPager.currentItem = pageIndex
    }

    private var isLoadingDetail = false

    private fun loadDetailData() {

        if (detailViewModel.login.isEmpty()) {
            detailViewModel.login = loginUser
        }

        detailViewModel.isLoading.observe(this) {
            isLoadingDetail = it
            showLoading(binding.progressBar, it)
        }


        detailViewModel.amountFollower.observe(this) { followerAmount ->
            amountFollower = followerAmount
            detailViewModel.isSuccess.observe(this) {
                if (it) {
                    setAmountUserFollows(amountFollower, amountFollowing)
                }
            }
        }


        detailViewModel.amountFollowing.observe(this) { followingAmount ->
            amountFollowing = followingAmount
            detailViewModel.isSuccess.observe(this) {
                if (it) {
                    setAmountUserFollows(amountFollower, amountFollowing)
                }
            }
        }


        detailViewModel.user.observe(this) {
             setUserData(it, false)
        }

        detailViewModel.getFavoriteUserByLogin().observe(this) {
            try {
                if (!it.equals(null)) {
                    isFavorite = true
                    setFavoriteState(R.drawable.ic_fav_filled, R.color.red)
                }
            } catch (e: NullPointerException) {
                isFavorite = false
                setFavoriteState(R.drawable.ic_fav_border, R.color.dark_600)
            }
        }

        detailViewModel.errorDetail.observe(this) {
            if (it) {
                isDetailError = true
                binding.coordinator.visibility = View.GONE
                setUserData(isFailed = true)
                SingleToast.show(this, getString(R.string.failed_to_connect), Toast.LENGTH_LONG)
            } else {
                isDetailError = false
                binding.coordinator.visibility = View.VISIBLE
                setUserData(isFailed = false)
            }
            binding.fabAdd.visibility = if (it) View.GONE else View.VISIBLE
        }

    }


    private fun setFavoriteState(drawable: Int, color: Int) {
        binding.fabAdd.setImageDrawable(ContextCompat.getDrawable(this, drawable))
        binding.fabAdd.setColorFilter(ContextCompat.getColor(applicationContext, color),
            android.graphics.PorterDuff.Mode.SRC_IN)
    }


    private fun setUserData(user: DetailUserResponse? = null, isFailed: Boolean) {

        failedToConnectView(isFailed, binding.viewFailedConnect.root)

        if (user != null) {

            bindingInclude.tvDetailUsername.text =
                user.name ?: getString(R.string.no_detail_user_data)
            bindingInclude.tvDetailLogin.text = user.login

            if (isLoadingDetail) {
                setAmountUserFollows(user.followers ?: 0, user.following ?: 0)
            }


            Glide.with(this@DetailActivity).load(user.avatarUrl)
                .apply(RequestOptions.placeholderOf(R.drawable.ic_loading)
                    .error(R.drawable.ic_error)).into(bindingInclude.imgDetailAvatar)


            binding.fabAdd.setOnClickListener {
                val userFavorite = FavoriteUserEntity(
                    user.login!!,
                    user.avatarUrl,
                )
                if ((isFavorite)) {
                    detailViewModel.deleteFavoriteUser(userFavorite)
                } else {
                    detailViewModel.insertFavoriteUser(userFavorite)
                }
            }
            setFollowData(user.login)
        }
        binding.viewFailedConnect.btnRefresh.setOnClickListener {
            detailViewModel.login = ""
            detailViewModel.login = loginUser
        }


    }


    private fun setFollowData(userLogin: String?) {


        val detailSectionPagerAdapter = DetailSectionPagerAdapter(this)

        detailSectionPagerAdapter.login = userLogin!!

        binding.viewPager.adapter = detailSectionPagerAdapter

        TabLayoutMediator(binding.tabs, binding.viewPager) { tab, position ->
            tab.text = resources.getString(TAB_TITLES[position])
        }.attach()

        selectPage(currentPosition ?: 0)


    }

    override fun updateSelectedTab(data: String) {

        when (data) {
            EXTRA_FOLLOWER -> {
                if (detailViewModel.userFollowerValue.isEmpty()) {
                    detailViewModel.userFollowerValue = EXTRA_FOLLOWER
                }
            }
            EXTRA_FOLLOWING -> {
                if (detailViewModel.userFollowingValue.isEmpty()) {
                    detailViewModel.userFollowingValue = EXTRA_FOLLOWING
                }
            }
        }
        if (!isDetailError) {
            detailViewModel.snackBarText.observe(this) {
                showLoading(binding.progressBar, false)
                setSnackBar(it)
            }
        }

    }

    override fun refreshAmountUserFollow(isRefresh: Boolean) {
        if (isRefresh) {
            if (fragmentIsLoading) {
                detailViewModel.loginAmountUserFollow = ""
                detailViewModel.loginAmountUserFollow = loginUser
            }
        }
    }


    override fun fragmentIsLoading(isLoading: Boolean) {
        fragmentIsLoading = isLoading
    }

    private var amountFollowerValue: Int? = null
    private var amountFollowingValue: Int? = null
    private fun setAmountUserFollows(amountFollower: Int?, amountFollowing: Int?) {

        lifecycleScope.launch {
            delay(1000)
            val textAmountFollower =
                bindingInclude.tvDetailAmountFollowers.text.toString().split("\n").toTypedArray()[0]
            val textAmountFollowing =
                bindingInclude.tvDetailAmountFollowing.text.toString().split("\n").toTypedArray()[0]
            amountFollowerValue = textAmountFollower.toInt()
            amountFollowingValue = textAmountFollowing.toInt()
        }

        bindingInclude.tvDetailAmountFollowers.text =
            getString(R.string.amount_followers, amountFollower)
        bindingInclude.tvDetailAmountFollowing.text =
            getString(R.string.amount_following, amountFollowing)
    }

    private fun setSnackBar(it: Event<String>) {
        it.getContentIfNotHandled()?.let { snackBarText ->
            Snackbar.make(window.decorView.rootView,
                getString(R.string.failed_to_load, snackBarText),
                Snackbar.LENGTH_LONG).setAnchorView(binding.fabAdd).show()
        }
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        position = tab?.position ?: 0
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
        return
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
        return
    }


    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt(STATE_TAB_POSITION, currentPosition ?: 0)
        outState.putInt(STATE_AMOUNT_FOLLOWER, amountFollowerValue ?: 0)
        outState.putInt(STATE_AMOUNT_FOLLOWING, amountFollowingValue ?: 0)


    }


}