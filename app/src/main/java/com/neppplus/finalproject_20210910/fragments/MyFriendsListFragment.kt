package com.neppplus.finalproject_20210910.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.adapters.MyFriendsRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.FragmentMyFriendsListBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyFriendsListFragment : BaseFragment() {

    companion object {
        private var frag : MyFriendsListFragment? = null
        fun getFrag() : MyFriendsListFragment {
            if (frag == null) {
                frag = MyFriendsListFragment()
            }

            return frag!!
        }
    }

    lateinit var binding: FragmentMyFriendsListBinding

    val mMyFriendsList = ArrayList<UserData>()
    lateinit var mFriendAdapter: MyFriendsRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_friends_list, container, false)
        return  binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setupEvents()
        setValues()

    }

    override fun setupEvents() {

    }

    override fun setValues() {

        mFriendAdapter = MyFriendsRecyclerAdapter(mContext, mMyFriendsList)
        binding.myFriendsRecyclerView.adapter = mFriendAdapter

        binding.myFriendsRecyclerView.layoutManager = LinearLayoutManager(mContext)

    }

    override fun onResume() {
        super.onResume()
        getMyFriendsListFromServer()
    }

    fun getMyFriendsListFromServer() {

        Log.d("내친구목록", "서버에서 받아오기")

        apiService.getRequestFriendList("my").enqueue(object : Callback<BasicResponse> {
            override fun onResponse(call: Call<BasicResponse>, response: Response<BasicResponse>) {

                if (response.isSuccessful) {

                    val basicResponse = response.body()!!

                    mMyFriendsList.clear()
                    mMyFriendsList.addAll(basicResponse.data.friends)
                    mFriendAdapter.notifyDataSetChanged()
                }

            }

            override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

            }

        })

    }


}