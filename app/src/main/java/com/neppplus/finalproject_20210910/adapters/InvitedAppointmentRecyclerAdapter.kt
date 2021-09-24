package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.ViewAppointmentDetailActivity
import com.neppplus.finalproject_20210910.ViewMapActivity
import com.neppplus.finalproject_20210910.datas.AppointmentData
import java.text.SimpleDateFormat

class InvitedAppointmentRecyclerAdapter(
    val mContext: Context,
    val mList:List<AppointmentData>) : RecyclerView.Adapter<InvitedAppointmentRecyclerAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTxt = view.findViewById<TextView>(R.id.titleTxt)
        val dateTimeTxt = view.findViewById<TextView>(R.id.dateTimeTxt)
        val placeNameTxt = view.findViewById<TextView>(R.id.placeNameTxt)
        val viewPlaceMapBtn = view.findViewById<ImageView>(R.id.viewPlaceMapBtn)
        val rootLayout = view.findViewById<LinearLayout>(R.id.rootLayout)
        val userProfileImg = view.findViewById<ImageView>(R.id.userProfileImg)
        val userNicknametxt = view.findViewById<TextView>(R.id.userNicknametxt)

        fun bind( context: Context, data: AppointmentData ) {
            titleTxt.text = data.title

//            약속일시 : Date형태로 파싱됨. => String으로 가공. SimpleDateFormat 사용.

            dateTimeTxt.text = data.getFormattedDateTime()
            placeNameTxt.text = data.placeName


//            약속 만든 사람 정보 표시
            Glide.with(context).load(data.user.profileImgURL).into(userProfileImg)

            userNicknametxt.text = data.user.nickName


//            이벤트 처리들
            viewPlaceMapBtn.setOnClickListener {
                val myIntent = Intent(context, ViewMapActivity::class.java)
                myIntent.putExtra("appointment", data)
                context.startActivity(myIntent)
            }

            rootLayout.setOnClickListener {
                val myIntent = Intent(context, ViewAppointmentDetailActivity::class.java)
                myIntent.putExtra("appointment",  data)
                context.startActivity(myIntent)
            }



        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.invited_appointment_list_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        val data = mList[position]

        holder.bind(mContext, data)



    }

    override fun getItemCount() = mList.size

}