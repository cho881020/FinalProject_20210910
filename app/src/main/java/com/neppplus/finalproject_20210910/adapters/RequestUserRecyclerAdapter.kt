package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.finalproject_20210910.AddFriendActivity
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.ViewMyFriendsListActivity
import com.neppplus.finalproject_20210910.datas.BasicResponse
import com.neppplus.finalproject_20210910.datas.UserData
import com.neppplus.finalproject_20210910.fragments.RequestedUserListFragment
import com.neppplus.finalproject_20210910.web.ServerAPI
import com.neppplus.finalproject_20210910.web.ServerAPIService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RequestUserRecyclerAdapter(
    val mContext: Context,
    val mList:List<UserData>) : RecyclerView.Adapter<RequestUserRecyclerAdapter.UserViewHolder>() {


    inner class UserViewHolder(view: View) : BaseViewHolder(mContext, view) {
        val profileImg = view.findViewById<ImageView>(R.id.profileImg)
        val nicknameTxt = view.findViewById<TextView>(R.id.nicknameTxt)
        val socialLoginImg = view.findViewById<ImageView>(R.id.socialLoginImg)
        val acceptBtn = view.findViewById<Button>(R.id.acceptBtn)
        val refuseBtn = view.findViewById<Button>(R.id.refuseBtn)


        fun bind(context: Context, data: UserData) {

            Glide.with(context).load(data.profileImgURL).into(profileImg)
            nicknameTxt.text = data.nickName
            when (data.provider) {
                "facebook" -> {
                    socialLoginImg.visibility = View.VISIBLE
                    socialLoginImg.setImageResource(R.drawable.facebook_login_icon)
                }
                "kakao" -> {
                    socialLoginImg.visibility = View.VISIBLE
                    socialLoginImg.setImageResource(R.drawable.kakao_login_icon)
                }
                else -> {
                    socialLoginImg.visibility = View.GONE
                }
            }

//            ??????/?????? ?????? ?????? ?????? ?????? ??????. => type??? ????????? ?????? ?????????.
//             ????????? ?????? ????????? ???????????? => ????????? ?????? ????????? ??????.

            val sendOkOrNoToServer = object : View.OnClickListener {
                override fun onClick(p0: View?) {

                    val okOrNo = p0!!.tag.toString()

//                    ??????????????? API ????????? ?????????.
//                    1) ?????? ????????? (!)
//                    2) ?????? (context) ??? ????????? ??????. => ??????????????? ??????????????? ?????? ??????.

                    val apiService = ServerAPI.getRetrofit(context).create(ServerAPIService::class.java)

                    apiService.putRequestSendOkOrNoFriend(data.id, okOrNo).enqueue(object : Callback<BasicResponse> {
                        override fun onResponse(
                            call: Call<BasicResponse>,
                            response: Response<BasicResponse>
                        ) {

//                            ?????????????????? ???????????? ?????? ???????????? ????????? ???????
//                            ????????? -> ???????????? ?????? : context ?????? ??????.

//                            ????????? -> ???????????? -> ViewPager????????? -> 1??????Fragment -> ????????????Frag??? ?????? -> ?????? ??????.
                            ((context as ViewMyFriendsListActivity).mFPA.getItem(1) as RequestedUserListFragment)
                                .getRequestUserListFromServer()

                        }

                        override fun onFailure(call: Call<BasicResponse>, t: Throwable) {

                        }

                    })


                }

            }
            acceptBtn.setOnClickListener(sendOkOrNoToServer)
            refuseBtn.setOnClickListener(sendOkOrNoToServer)


        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.request_user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

}