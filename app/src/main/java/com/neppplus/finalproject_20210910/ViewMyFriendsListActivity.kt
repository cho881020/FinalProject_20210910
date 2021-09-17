package com.neppplus.finalproject_20210910

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import com.neppplus.finalproject_20210910.adapters.FriendPagerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityViewMyFriendsListBinding
import com.neppplus.finalproject_20210910.fragments.MyFriendsListFragment
import com.neppplus.finalproject_20210910.fragments.RequestedUserListFragment

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

        binding.friendsViewPager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {

//                Log.d("오프셋값", positionOffset.toString())

            }

            override fun onPageSelected(position: Int) {

                Log.d("선택된페이지", position.toString())

//                각 페이지에 맞는 프래그먼트의 새로고침 실행.

                when (position) {
                    0 -> {
                        (mFPA.getItem(position) as MyFriendsListFragment).getMyFriendsListFromServer()
                    }
                    else -> {
                        (mFPA.getItem(position) as RequestedUserListFragment).getRequestUserListFromServer()
                    }
                }

            }

            override fun onPageScrollStateChanged(state: Int) {

            }

        })


        addBtn.setOnClickListener {
            val myIntent = Intent(mContext, AddFriendActivity::class.java)
            startActivity(myIntent)
        }

    }

    override fun setValues() {

        titleTxt.text = "친구 관리"
        addBtn.visibility = View.VISIBLE

        mFPA = FriendPagerAdapter(supportFragmentManager)
        binding.friendsViewPager.adapter = mFPA

        binding.friendsTabLayout.setupWithViewPager(binding.friendsViewPager)

    }
}