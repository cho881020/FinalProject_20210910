package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.neppplus.finalproject_20210910.adapters.FriendPagerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityViewMyFriendsListBinding

class ViewMyFriendsListActivity : BaseActivity() {

    lateinit var binding: ActivityViewMyFriendsListBinding

    lateinit var mFPA : FriendPagerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding =  DataBindingUtil.setContentView(this, R.layout.activity_view_my_friends_list)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mFPA = FriendPagerAdapter(supportFragmentManager)
        binding.friendsViewPager.adapter = mFPA

        binding.friendsTabLayout.setupWithViewPager(binding.friendsViewPager)

    }
}