package com.yogadimas.githubuser.ui.view.fragment

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.yogadimas.githubuser.R
import com.yogadimas.githubuser.data.remote.model.ItemsItem
import com.yogadimas.githubuser.databinding.FragmentFollowBinding
import com.yogadimas.githubuser.databinding.LayoutHandleDataConnectionBinding
import com.yogadimas.githubuser.ui.adapter.UserAdapter
import com.yogadimas.githubuser.ui.helper.checkDataSize
import com.yogadimas.githubuser.ui.helper.failedToConnectView
import com.yogadimas.githubuser.ui.helper.goToDetail
import com.yogadimas.githubuser.ui.helper.showLoading
import com.yogadimas.githubuser.ui.interfaces.OnCallbackReceived
import com.yogadimas.githubuser.ui.interfaces.OnItemClickCallback
import com.yogadimas.githubuser.ui.view.activity.DetailActivity
import com.yogadimas.githubuser.ui.viewmodel.FollowersViewModel
import com.yogadimas.githubuser.ui.viewmodel.FollowingViewModel
import com.yogadimas.githubuser.ui.viewmodel.factory.ViewModelFactory


class FollowFragment : Fragment() {

    companion object {
        const val ARG_POSITION = "arg_position"
        const val ARG_USERNAME = "arg_username"
    }

    private var _binding: FragmentFollowBinding? = null
    private val binding get() = _binding!!

    private var _bindingHandle: LayoutHandleDataConnectionBinding? = null
    private val bindingHandle get() = _bindingHandle!!

    private val followersViewModel: FollowersViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }
    private val followingViewModel: FollowingViewModel by viewModels {
        ViewModelFactory.getInstance(requireActivity())
    }

    var login: String = ""
    private var position = 0

    private lateinit var mCallback: OnCallbackReceived


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mCallback = activity as OnCallbackReceived

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentFollowBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _bindingHandle = binding.viewHandle

        bindingHandle.viewFailedConnect.btnRefresh.visibility = View.GONE

        val layoutManager = LinearLayoutManager(requireActivity())
        binding.rvFollows.layoutManager = layoutManager

        arguments?.let {
            position = it.getInt(ARG_POSITION, 0)
            login = it.getString(ARG_USERNAME).orEmpty()
        }

        if (position == 1) {
            loadFollowersData()
        } else {
            loadFollowingData()
        }

    }


    private fun loadFollowersData() {
        if (followersViewModel.login.isEmpty()) {
            followersViewModel.login = login
        }
        followersViewModel.isLoading.observe(viewLifecycleOwner) {
            mCallback.fragmentIsLoading(it)
            showLoading(binding.progressBar, it)
        }
        followersViewModel.listFollowers.observe(viewLifecycleOwner) {
            mCallback.refreshAmountUserFollow(true)
            setUsersData(it, getString(R.string.no_followers), false)
        }
        followersViewModel.error.observe(viewLifecycleOwner) {
            mCallback.updateSelectedTab(DetailActivity.EXTRA_FOLLOWER)
            setErrorView(it)
        }


    }


    private fun loadFollowingData() {
        if (followingViewModel.login.isEmpty()) {
            followingViewModel.login = login
        }

        followingViewModel.isLoading.observe(viewLifecycleOwner) {
            mCallback.fragmentIsLoading(it)
            showLoading(binding.progressBar, it)

        }

        followingViewModel.listFollowing.observe(viewLifecycleOwner) {
            mCallback.refreshAmountUserFollow(true)
            setUsersData(it, getString(R.string.no_following), false)
        }

        followingViewModel.error.observe(viewLifecycleOwner) {
            mCallback.updateSelectedTab(DetailActivity.EXTRA_FOLLOWING)
            setErrorView(it)
        }


    }

    private fun setErrorView(it: Boolean) {
        if (it) {
            setUsersData(ArrayList(), getString(R.string.failed_to_connect), true)
        }
    }

    private fun setUsersData(
        it: ArrayList<ItemsItem?>?,
        text: String,
        isFailed: Boolean,
    ) {

        failedToConnectView(isFailed, bindingHandle.viewFailedConnect.root)
        checkDataSize(it,
            bindingHandle.viewNoData.tvNoData,
            bindingHandle.viewNoData.root,
            text,
            isFailed)

        val adapter = UserAdapter(it)
        binding.rvFollows.adapter = adapter

        adapter.setOnItemClickCallback(object : OnItemClickCallback {
            override fun onItemClicked(user: ItemsItem?) {
                startActivity(goToDetail(requireActivity(), user?.login))
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()

        _bindingHandle = null
        _binding = null
    }


}