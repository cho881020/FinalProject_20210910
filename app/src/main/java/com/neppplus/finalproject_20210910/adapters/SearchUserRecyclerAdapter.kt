package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.content.DialogInterface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.datas.UserData

class SearchUserRecyclerAdapter(
    val mContext: Context,
    val mList:List<UserData>) : RecyclerView.Adapter<SearchUserRecyclerAdapter.UserViewHolder>() {


    class UserViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val profileImg = view.findViewById<ImageView>(R.id.profileImg)
        val nicknameTxt = view.findViewById<TextView>(R.id.nicknameTxt)
        val socialLoginImg = view.findViewById<ImageView>(R.id.socialLoginImg)
        val addFriendBtn = view.findViewById<Button>(R.id.addFriendBtn)

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

//            친구추가 버튼 눌림 이벤트 처리
            addFriendBtn.setOnClickListener {
//                ~~님을 친구로 추가하겠습니까?
                val alert = AlertDialog.Builder(context)
                alert.setMessage("${data.nickName}님을 친구로 추가하겠습니까?")
                alert.setPositiveButton("확인", DialogInterface.OnClickListener { dialogInterface, i ->

                })
                alert.setNegativeButton("취소", null)
                alert.show()
            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.search_user_list_item, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        holder.bind(mContext, mList[position])
    }

    override fun getItemCount() = mList.size

}