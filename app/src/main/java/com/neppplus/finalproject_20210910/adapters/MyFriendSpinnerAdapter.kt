package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.ViewMapActivity
import com.neppplus.finalproject_20210910.datas.AppointmentData
import com.neppplus.finalproject_20210910.datas.PlaceData
import com.neppplus.finalproject_20210910.datas.UserData
import org.json.JSONObject
import java.text.SimpleDateFormat

class MyFriendSpinnerAdapter(
    val mContext:Context,
    resId: Int,
    val mList: List<UserData>) : ArrayAdapter<UserData>(mContext, resId, mList) {

    val mInflater = LayoutInflater.from(mContext)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var row = convertView
        if (row == null) {
            row = mInflater.inflate(R.layout.friend_list_item, null)
        }
        row!!

        val data = mList[position]



        return row
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(position, convertView, parent)
    }


}











