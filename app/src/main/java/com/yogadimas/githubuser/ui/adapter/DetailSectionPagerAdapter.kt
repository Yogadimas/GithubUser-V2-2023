package com.yogadimas.githubuser.ui.adapter

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.yogadimas.githubuser.ui.view.fragment.FollowFragment

class DetailSectionPagerAdapter(activity: AppCompatActivity) : FragmentStateAdapter(activity) {

    var login: String = ""

    override fun createFragment(position: Int): Fragment {

        val fragment = FollowFragment()

        fragment.arguments = Bundle().apply {

            putInt(FollowFragment.ARG_POSITION, position + 1)
            putString(FollowFragment.ARG_USERNAME, login)


        }
        return fragment
    }

    override fun getItemCount(): Int = 2

}