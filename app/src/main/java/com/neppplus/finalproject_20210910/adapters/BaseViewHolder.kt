package com.neppplus.finalproject_20210910.adapters

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.neppplus.finalproject_20210910.utils.FontChanger

abstract class BaseViewHolder(context: Context, itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        FontChanger.setGlobalFont(context, itemView)
    }
}