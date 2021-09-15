package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.finalproject_20210910.R
import com.neppplus.finalproject_20210910.datas.PlaceData

class MyPlaceRecyclerAdapter(
    val mContext: Context,
    val mList: List<PlaceData>) : RecyclerView.Adapter<MyPlaceRecyclerAdapter.MyViewHolder>() {

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val placeNameTxt = view.findViewById<TextView>(R.id.placeNameTxt)
        val isPrimaryTxt = view.findViewById<TextView>(R.id.isPrimaryTxt)
        val viewPlaceMapBtn = view.findViewById<ImageView>(R.id.viewPlaceMapBtn)
        val backgroundLayout = view.findViewById<LinearLayout>(R.id.backgroundLayout)

        fun setRealData( data: PlaceData ) {
            placeNameTxt.text = data.name

            if (data.isPrimary) {
                isPrimaryTxt.visibility = View.VISIBLE
            }
            else {
                isPrimaryTxt.visibility = View.GONE
            }

//            이벤트처리

//            viewPlaceMapBtn.setOnClickListener {
//                Toast.makeText(mco, "", Toast.LENGTH_SHORT).show()
//            }

        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        val view = LayoutInflater.from(mContext).inflate(R.layout.my_place_list_item, parent, false)
        return  MyViewHolder(view)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.setRealData(mList[position])


//        이벤트 처리는 mContext 변수때문에, 이 onBindViewHolder에서 처리하자.
        holder.viewPlaceMapBtn.setOnClickListener {

            Toast.makeText(mContext, "지도 버튼을 눌렀습니다.", Toast.LENGTH_SHORT).show()

        }

        holder.backgroundLayout.setOnClickListener {

            Toast.makeText(mContext, "${mList[position].name}을 클릭.", Toast.LENGTH_SHORT).show()

        }

    }

    override fun getItemCount() = mList.size

}