package com.neppplus.finalproject_20210910

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.neppplus.finalproject_20210910.adapters.SearchUserRecyclerAdapter
import com.neppplus.finalproject_20210910.databinding.ActivityAddFriendBinding
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.UserData
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddFriendActivity : BaseActivity() {

    lateinit var binding: ActivityAddFriendBinding

    val mSearchedUserList = ArrayList<UserData>()

    lateinit var mSearchedUserAdapter :  SearchUserRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_friend)
        setupEvents()
        setValues()
    }

    override fun setupEvents() {

        binding.searchBtn.setOnClickListener {

//            검색어를 뭐라고 쳤는가
            val inputKeyword = binding.keywordEdt.text.toString()

//            validation - 2자이상

            if (inputKeyword.length < 2) {
                Toast.makeText(mContext, "검색어는 2자 이상 입력해주세요.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            apiService.getRequestSearchUser(inputKeyword).enqueue( object : Callback<BasicResponse> {
                override fun onResponse(
                    call: Call<BasicResponse>,
                    response: Response<BasicResponse>
                ) {
                    if (response.isSuccessful) {
                        val basicResponse = response.body()!!

//                        기존의 검색결과 삭제.
                        mSearchedUserList.clear()
                        mSearchedUserList.addAll( basicResponse.data.users )
                        mSearchedUserAdapter.notifyDataSetChanged()
                    }
                }

                override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                }

            })


        }

    }

    override fun setValues() {
        mSearchedUserAdapter = SearchUserRecyclerAdapter(mContext, mSearchedUserList)
        binding.searchUserRecyclerView.adapter = mSearchedUserAdapter
        binding.searchUserRecyclerView.layoutManager = LinearLayoutManager(mContext)
    }
}