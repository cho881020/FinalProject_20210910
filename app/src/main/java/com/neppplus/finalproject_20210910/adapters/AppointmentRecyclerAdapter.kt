package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.ViewMapActivity
import com.neppplus.finalproject_20210910.datas.AppointmentData
import java.text.SimpleDateFormat

class AppointmentRecyclerAdapter(
    val mContext: Context,
    val mList:List<AppointmentData>) : RecyclerView.Adapter<AppointmentRecyclerAdapter.AppointmentViewHolder>() {

    class AppointmentViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val titleTxt = view.findViewById<TextView>(R.id.titleTxt)
        val dateTimeTxt = view.findViewById<TextView>(R.id.dateTimeTxt)
        val placeNameTxt = view.findViewById<TextView>(R.id.placeNameTxt)
        val viewPlaceMapBtn = view.findViewById<ImageView>(R.id.viewPlaceMapBtn)

        val dateTimeSDF = SimpleDateFormat("M/d a h:mm")

        fun bind( data: AppointmentData ) {
            titleTxt.text = data.title

//            약속일시 : Date형태로 파싱됨. => String으로 가공. SimpleDateFormat 사용.

            dateTimeTxt.text = dateTimeSDF.format( data.datetime )
            placeNameTxt.text = data.placeName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AppointmentViewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.appointment_list_item, parent, false)
        return AppointmentViewHolder(view)
    }

    override fun onBindViewHolder(holder: AppointmentViewHolder, position: Int) {

        val data = mList[position]

        holder.bind(data)

        holder.viewPlaceMapBtn.setOnClickListener {
            val myIntent = Intent(mContext, ViewMapActivity::class.java)
            myIntent.putExtra("appointment", data)
            mContext.startActivity(myIntent)
        }

    }

    override fun getItemCount() = mList.size

}